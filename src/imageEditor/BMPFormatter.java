package imageEditor;

import java.io.*;

public class BMPFormatter extends ImageFormatter {

	@Override
	public Layer load(String path) throws EditorException {
		FileInputStream istream = null;
		try {
			File file = new File(path);
			istream = new FileInputStream(file);
			byte header[] = new byte[14];
			byte dibStart[] = new byte[4];

			ImageFormatter.readBytes(istream, header, 14);
			ImageFormatter.readBytes(istream, dibStart, 4);

			int dibSize = ((((int) dibStart[0]) << 24) >>> 24) + ((((int) dibStart[1]) << 24) >>> 16)
					+ ((((int) dibStart[2]) << 24) >>> 8) + (((int) dibStart[3]) << 24);

			byte dib[] = new byte[dibSize - 4];

			ImageFormatter.readBytes(istream, dib, dibSize - 4);

			int imageWidth = ((((int) dib[0]) << 24) >>> 24) + ((((int) dib[1]) << 24) >>> 16)
					+ ((((int) dib[2]) << 24) >>> 8) + (((int) dib[3]) << 24);

			int imageHeight = ((((int) dib[4]) << 24) >>> 24) + ((((int) dib[5]) << 24) >>> 16)
					+ ((((int) dib[6]) << 24) >>> 8) + (((int) dib[7]) << 24);

			int pixelSize = ((((int) dib[10]) << 24) >>> 24) + ((((int) dib[11]) << 24) >>> 16);

			if (pixelSize != 24 && pixelSize != 32) {
				System.out.print("NE VALJA.");
				istream.close();
				throw new EditorException("Unsupported BMP format. Only 24-bit and 32-bit RGB formats are supported.");
			}

			Layer l = new Layer(imageWidth, imageHeight, path);

			int pixelByteSize = pixelSize / 8;
			long rowSize = (pixelSize * imageWidth + 31) / 32 * 4l;

			int compression = ((((int) dib[12]) << 24) >>> 24) + ((((int) dib[13]) << 24) >>> 16)
					+ ((((int) dib[14]) << 24) >>> 8) + (((int) dib[15]) << 24);

			byte pixelBuffer[] = new byte[pixelByteSize];

			if (compression == 3) {
				int redMask = ((((int) dib[36]) << 24) >>> 24) + ((((int) dib[37]) << 24) >>> 16)
						+ ((((int) dib[38]) << 24) >>> 8) + (((int) dib[39]) << 24);
				int greenMask = ((((int) dib[40]) << 24) >>> 24) + ((((int) dib[41]) << 24) >>> 16)
						+ ((((int) dib[42]) << 24) >>> 8) + (((int) dib[43]) << 24);
				int blueMask = ((((int) dib[44]) << 24) >>> 24) + ((((int) dib[45]) << 24) >>> 16)
						+ ((((int) dib[46]) << 24) >>> 8) + (((int) dib[47]) << 24);
				int alphaMask = ((((int) dib[48]) << 24) >>> 24) + ((((int) dib[49]) << 24) >>> 16)
						+ ((((int) dib[50]) << 24) >>> 8) + (((int) dib[51]) << 24);

				for (int i = 0; i < imageHeight; i++) {
					for (int j = 0; j < imageWidth; j++) {
						int tempMask;
						// istream.read(pixelBuffer);
						ImageFormatter.readBytes(istream, pixelBuffer, pixelByteSize);

						int readPixel = ((((int) pixelBuffer[0]) << 24) >>> 24)
								+ ((((int) pixelBuffer[1]) << 24) >>> 16) + ((((int) pixelBuffer[2]) << 24) >>> 8)
								+ (((int) pixelBuffer[3]) << 24);

						int readRed = readPixel & redMask;
						tempMask = redMask;
						while ((tempMask & 1) == 0) {
							readRed >>>= 1;
							tempMask >>>= 1;
						}

						int readGreen = readPixel & greenMask;
						tempMask = greenMask;
						while ((tempMask & 1) == 0) {
							readGreen >>>= 1;
							tempMask >>>= 1;
						}

						int readBlue = readPixel & blueMask;
						tempMask = blueMask;
						while ((tempMask & 1) == 0) {
							readBlue >>>= 1;
							tempMask >>>= 1;
						}

						int readAlpha = readPixel & alphaMask;
						tempMask = alphaMask;
						while ((tempMask & 1) == 0) {
							readAlpha >>>= 1;
							tempMask >>>= 1;
						}

						l.setPixel(new Pixel(readRed, readGreen, readBlue, readAlpha), j, i);
					}
					for (long j = 0; j < rowSize - pixelByteSize * imageWidth; j++) {
						// istream.read(pixelBuffer, 0, 1);
						ImageFormatter.readBytes(istream, pixelBuffer, 1);
					}
				}
			} else {
				for (int i = 0; i < imageHeight; i++) {
					for (int j = 0; j < imageWidth; j++) {
						// istream.read(pixelBuffer);
						ImageFormatter.readBytes(istream, pixelBuffer, pixelByteSize);
						l.setPixel(new Pixel((((int) pixelBuffer[2]) << 24) >>> 24,
								(((int) pixelBuffer[1]) << 24) >>> 24, (((int) pixelBuffer[0]) << 24) >>> 24, 255), j,
								i);
					}
					for (long j = 0; j < rowSize - pixelByteSize * imageWidth; j++) {
						// istream.read(pixelBuffer, 0, 1);
						ImageFormatter.readBytes(istream, pixelBuffer, 1);
					}
				}
			}
			return l;
		} catch (FileNotFoundException e) {
			throw new EditorException("File not found.");
		} catch (IOException e) {
			throw new EditorException("File reading failed. This isn't supposed to happen...");
		} finally {
			if (istream != null)
				try {
					istream.close();
				} catch (IOException e) {
				}
		}
	}

	@Override
	public void save(String path) throws EditorException {
		FileOutputStream ostream = null;
		try {
			File file = new File(path);
			ostream = new FileOutputStream(file);
			Image image = Image.getImage();

			long bmpSize = image.getWidth() * image.getHeight() * 4 + 70;

			byte header[] = new byte[14];
			// BM
			header[0] = 66;
			header[1] = 77;
			// velicina bitmape u bajtovima
			header[2] = (byte) bmpSize;
			header[3] = (byte) (bmpSize >> 8);
			header[4] = (byte) (bmpSize >> 16);
			header[5] = (byte) (bmpSize >> 24);

			// djubre
			header[6] = header[7] = header[8] = header[9] = 0;
			// offset gde pocinju pikseli -> fiksno
			header[10] = 70;
			header[11] = header[12] = header[13] = 0;

			ostream.write(header);

			byte dib[] = new byte[56];

			// velicina DIB -> fiksno
			dib[0] = 56;
			dib[1] = dib[2] = dib[3] = 0;

			// sirina slike
			dib[4] = (byte) image.getWidth();
			dib[5] = (byte) (image.getWidth() >> 8);
			dib[6] = (byte) (image.getWidth() >> 16);
			dib[7] = (byte) (image.getWidth() >> 24);

			// visina slike
			dib[8] = (byte) image.getHeight();
			dib[9] = (byte) (image.getHeight() >> 8);
			dib[10] = (byte) (image.getHeight() >> 16);
			dib[11] = (byte) (image.getHeight() >> 24);

			// plane neki -> fiksno
			dib[12] = 1;
			dib[13] = 0;

			// broj bita po pikselu
			dib[14] = 32;
			dib[15] = 0;

			// BI_BITFIELDS
			dib[16] = 3;
			dib[17] = dib[18] = dib[19] = 0;

			// velicina dela s bitovima
			dib[20] = (byte) (bmpSize - 70);
			dib[21] = (byte) ((bmpSize - 70) >> 8);
			dib[22] = (byte) ((bmpSize - 70) >> 16);
			dib[23] = (byte) ((bmpSize - 70) >> 24);

			// rezolucija neka -> fiksno
			dib[24] = 0x13;
			dib[25] = 0x0B;
			dib[26] = dib[27] = 0;

			// rezolucija opet
			dib[28] = 0x13;
			dib[29] = 0x0B;
			dib[30] = dib[31] = 0;

			// neke boje -> fiksno
			dib[32] = dib[33] = dib[34] = dib[35] = 0;

			// neke boje bitne -> fiksno
			dib[36] = dib[37] = dib[38] = dib[39] = 0;

			// red maska
			dib[40] = dib[41] = 0;
			dib[42] = (byte) 0xFF;
			dib[43] = 0;

			// green maska
			dib[44] = 0;
			dib[45] = (byte) 0xFF;
			dib[46] = dib[47] = 0;

			// blue maska
			dib[48] = (byte) 0xFF;
			dib[49] = dib[50] = dib[51] = 0;

			// alpha maska
			dib[52] = dib[53] = dib[54] = 0;
			dib[55] = (byte) 0xFF;

			ostream.write(dib);

			byte pixelBuffer[] = new byte[4];

			for (int i = 0; i < image.getHeight(); i++) {
				for (int j = 0; j < image.getWidth(); j++) {
					Pixel tempPixel = image.getPixel(j, i);
					pixelBuffer[0] = (byte) tempPixel.getB();
					pixelBuffer[1] = (byte) tempPixel.getG();
					pixelBuffer[2] = (byte) tempPixel.getR();
					pixelBuffer[3] = (byte) tempPixel.getA();

					ostream.write(pixelBuffer);
				}
			}
		} catch (FileNotFoundException e) {
			throw new EditorException("Cannot open file for writing.");
		} catch (IOException e) {
			throw new EditorException("Error while writing to the file. This shouldn't happen...");
		} finally {
			if (ostream != null)
				try {
					ostream.close();
				} catch (IOException e) {
				}
		}
	}
}
