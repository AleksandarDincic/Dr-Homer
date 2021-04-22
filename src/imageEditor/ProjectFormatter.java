package imageEditor;

import java.util.HashMap;

public abstract class ProjectFormatter {

	private static HashMap<String, ProjectFormatter> formatMap = new HashMap<String, ProjectFormatter>();

	public static void addFormat(String fileType, ProjectFormatter formatter) throws EditorException {
		if (formatMap.containsKey(fileType))
			throw new EditorException("Project formatter of that type already exists.");
		formatMap.put(fileType, formatter);
	}

	public static ProjectFormatter getFormatter(String fileType) throws EditorException {
		if (!formatMap.containsKey(fileType))
			throw new EditorException("Operation formatter of that type doesn't exist.");
		return formatMap.get(fileType);
	}

	static {

		try {
			addFormat("dr", new DRFormatter());
		} catch (EditorException e) {
			// nece se desiti
		}

	}

	public abstract void load(String path) throws EditorException;

	public abstract void save(String path) throws EditorException;

}
