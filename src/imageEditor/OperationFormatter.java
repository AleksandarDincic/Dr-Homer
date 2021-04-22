package imageEditor;

import java.util.HashMap;

public abstract class OperationFormatter {

	private static HashMap<String, OperationFormatter> formatMap = new HashMap<String, OperationFormatter>();

	public static void addFormat(String fileType, OperationFormatter formatter) throws EditorException {
		if (formatMap.containsKey(fileType))
			throw new EditorException("Operation formatter of that type already exists.");
		formatMap.put(fileType, formatter);
	}

	static {
		try {
			addFormat("fun", new FUNFormatter());
		} catch (EditorException e) {
			// nece se desiti
		}
	}

	public static OperationFormatter getFormatter(String fileType) throws EditorException {
		if (!formatMap.containsKey(fileType))
			throw new EditorException("Operation formatter of that type doesn't exist.");
		return formatMap.get(fileType);
	}

	public abstract CompositeOperation load(String path) throws EditorException;

	public abstract void save(CompositeOperation operation, String path) throws EditorException;
}
