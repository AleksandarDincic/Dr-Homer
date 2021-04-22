package imageEditor;

import java.io.*;

public class PAMFormatter extends ImageFormatter {

	@Override
	public Layer load(String path) throws EditorException {
		FileInputStream istream = null;
		try {
			File file = new File(path);
			istream = new FileInputStream(file);
			byte HDR[] = new byte[9];

			ImageFormatter.readBytes(istream, HDR, 3);
			ImageFormatter.readBytes(istream, HDR, 6);

			int imageWidth = 0;
			char numChar;

			ImageFormatter.readBytes(istream, HDR, 1);
			numChar = (char) (((int) HDR[0]) << 24 >>> 24);
			while (numChar != 10) {
				imageWidth *= 10;
				imageWidth += numChar - '0';
				ImageFormatter.readBytes(istream, HDR, 1);
				numChar = (char) (((int) HDR[0]) << 24 >>> 24);
			}

			ImageFormatter.readBytes(istream, HDR, 7);

			int imageHeight = 0;

			ImageFormatter.readBytes(istream, HDR, 1);
			numChar = (char) (((int) HDR[0]) << 24 >>> 24);
			while (numChar != 10) {
				imageHeight *= 10;
				imageHeight += numChar - '0';
				ImageFormatter.readBytes(istream, HDR, 1);
				numChar = (char) (((int) HDR[0]) << 24 >>> 24);
			}

			ImageFormatter.readBytes(istream, HDR, 6);

			// DEPTH -> beskorisno
			ImageFormatter.readBytes(istream, HDR, 1);
			numChar = (char) (((int) HDR[0]) << 24 >>> 24);

			ImageFormatter.readBytes(istream, HDR, 1);

			ImageFormatter.readBytes(istream, HDR, 7);

			int maxval = 0;

			ImageFormatter.readBytes(istream, HDR, 1);
			numChar = (char) (((int) HDR[0]) << 24 >>> 24);
			while (numChar != 10) {
				maxval *= 10;
				maxval += numChar - '0';
				ImageFormatter.readBytes(istream, HDR, 1);
				numChar = (char) (((int) HDR[0]) << 24 >>> 24);
			}

			if (maxval > 255) {
				throw new EditorException(
						"Unsupported format. Only PAM files with color representation of 255 and less are allowed.");
			}

			ImageFormatter.readBytes(istream, HDR, 9);

			StringBuilder tupleSb = new StringBuilder();

			ImageFormatter.readBytes(istream, HDR, 1);
			numChar = (char) (((int) HDR[0]) << 24 >>> 24);
			while (numChar != 10) {
				tupleSb.append(numChar);
				ImageFormatter.readBytes(istream, HDR, 1);
				numChar = (char) (((int) HDR[0]) << 24 >>> 24);
			}

			ImageFormatter.readBytes(istream, HDR, 7);

			Layer l = new Layer(imageWidth, imageHeight, path);

			if (tupleSb.toString().equals("RGB_ALPHA")) {
				int r, g, b, a;
				for (int i = imageHeight - 1; i >= 0; i--) {
					for (int j = 0; j < imageWidth; j++) {
						ImageFormatter.readBytes(istream, HDR, 4);
						r = ((int) HDR[0]) << 24 >>> 24;
						g = ((int) HDR[1]) << 24 >>> 24;
						b = ((int) HDR[2]) << 24 >>> 24;
						a = ((int) HDR[3]) << 24 >>> 24;

						l.setPixel(new Pixel(r, g, b, a), j, i);
					}
				}
			} else if (tupleSb.toString().equals("RGB")) {
				int r, g, b;
				for (int i = imageHeight - 1; i >= 0; i--) {
					for (int j = 0; j < imageWidth; j++) {
						ImageFormatter.readBytes(istream, HDR, 3);
						r = ((int) HDR[0]) << 24 >>> 24;
						g = ((int) HDR[1]) << 24 >>> 24;
						b = ((int) HDR[2]) << 24 >>> 24;

						l.setPixel(new Pixel(r, g, b, 255), j, i);
					}
				}
			} else
				System.out.println("opaa");
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

			byte HDR[] = new byte[25];

			// P7\n
			HDR[0] = 80;
			HDR[1] = 55;
			HDR[2] = 10;
			ostream.write(HDR, 0, 3);

			int width = image.getWidth();
			int height = image.getHeight();

			// WIDTH_
			HDR[0] = 87;
			HDR[1] = 73;
			HDR[2] = 68;
			HDR[3] = 84;
			HDR[4] = 72;
			HDR[5] = 32;
			ostream.write(HDR, 0, 6);

			int digitCounter = 0;

			while (width > 0) {
				HDR[digitCounter++] = (byte) ('0' + width % 10);
				width /= 10;
			}
			for (int i = 0, j = digitCounter - 1; i < j; i++, j--) {
				byte t = HDR[i];
				HDR[i] = HDR[j];
				HDR[j] = t;
			}

			ostream.write(HDR, 0, digitCounter);

			HDR[0] = 10;
			ostream.write(HDR, 0, 1);

			// HEIGHT_
			HDR[0] = 72;
			HDR[1] = 69;
			HDR[2] = 73;
			HDR[3] = 71;
			HDR[4] = 72;
			HDR[5] = 84;
			HDR[6] = 32;
			ostream.write(HDR, 0, 7);

			digitCounter = 0;

			while (height > 0) {
				HDR[digitCounter++] = (byte) ('0' + height % 10);
				height /= 10;
			}
			for (int i = 0, j = digitCounter - 1; i < j; i++, j--) {
				byte t = HDR[i];
				HDR[i] = HDR[j];
				HDR[j] = t;
			}

			ostream.write(HDR, 0, digitCounter);

			HDR[0] = 10;
			ostream.write(HDR, 0, 1);

			// DEPTH_4\n
			HDR[0] = 68;
			HDR[1] = 69;
			HDR[2] = 80;
			HDR[3] = 84;
			HDR[4] = 72;
			HDR[5] = 32;
			HDR[6] = '0' + 4;
			HDR[7] = 10;
			ostream.write(HDR, 0, 8);

			// MAXVAL_255\n
			HDR[0] = 77;
			HDR[1] = 65;
			HDR[2] = 88;
			HDR[3] = 86;
			HDR[4] = 65;
			HDR[5] = 76;
			HDR[6] = 32;
			HDR[7] = '0' + 2;
			HDR[8] = '0' + 5;
			HDR[9] = '0' + 5;
			HDR[10] = 10;
			ostream.write(HDR, 0, 11);

			// TUPLTYPE_RGB_ALPHA\n
			HDR[0] = 84;
			HDR[1] = 85;
			HDR[2] = 80;
			HDR[3] = 76;
			HDR[4] = 84;
			HDR[5] = 89;
			HDR[6] = 80;
			HDR[7] = 69;
			HDR[8] = 32;
			HDR[9] = 82;
			HDR[10] = 71;
			HDR[11] = 66;
			HDR[12] = 95;
			HDR[13] = 65;
			HDR[14] = 76;
			HDR[15] = 80;
			HDR[16] = 72;
			HDR[17] = 65;
			HDR[18] = 10;
			ostream.write(HDR, 0, 19);

			// ENDHDR\n
			HDR[0] = 69;
			HDR[1] = 78;
			HDR[2] = 68;
			HDR[3] = 72;
			HDR[4] = 68;
			HDR[5] = 82;
			HDR[6] = 10;
			ostream.write(HDR, 0, 7);

			for (int i = image.getHeight() - 1; i >= 0; i--) {
				for (int j = 0; j < image.getWidth(); j++) {
					Pixel tempPixel = image.getPixel(j, i);
					HDR[0] = (byte) tempPixel.getR();
					HDR[1] = (byte) tempPixel.getG();
					HDR[2] = (byte) tempPixel.getB();
					HDR[3] = (byte) tempPixel.getA();

					ostream.write(HDR, 0, 4);
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
