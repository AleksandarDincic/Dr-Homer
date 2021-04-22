package imageEditor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public abstract class ImageFormatter {

	private static HashMap<String, ImageFormatter> formatMap = new HashMap<String, ImageFormatter>();

	public static void addFormat(String fileType, ImageFormatter formatter) throws EditorException {
		if (formatMap.containsKey(fileType))
			throw new EditorException("Image format with that extension already exists.");
		formatMap.put(fileType, formatter);
	}

	public static ImageFormatter getFormatter(String fileType) throws EditorException {
		if (!formatMap.containsKey(fileType))
			throw new EditorException("Image formatter of that type doesn't exist.");
		return formatMap.get(fileType);
	}

	static {
		try {
			addFormat("bmp", new BMPFormatter());
			addFormat("pam", new PAMFormatter());
		} catch (EditorException e) {
			// nece se desiti
		}
	}

	public static void readBytes(FileInputStream fistream, byte[] buffer, int count)
			throws EditorException, IOException {
		int ret = fistream.read(buffer, 0, count);
		if (ret < count)
			throw new EditorException("File reading failed. Perhaps the file is corrupted.");
	}

	public abstract Layer load(String path) throws EditorException;

	public abstract void save(String path) throws EditorException;
}
