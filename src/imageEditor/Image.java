package imageEditor;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Image {

	private static Image image;
	private static HashMap<String, Operation> basicOperations = new HashMap<String, Operation>();

	private static void addBasicOperation(String name, int numOfParams) {
		basicOperations.put(name, new BasicOperation(name, numOfParams));
	}

	static {
		addBasicOperation("+", 3);
		addBasicOperation("-", 3);
		addBasicOperation("i-", 3);
		addBasicOperation("*", 3);
		addBasicOperation("/", 3);
		addBasicOperation("i/", 3);
		addBasicOperation("pow", 3);
		addBasicOperation("log", 3);
		addBasicOperation("abs", 0);
		addBasicOperation("min", 3);
		addBasicOperation("max", 3);
		addBasicOperation("fill", 4);
		addBasicOperation("invert", 0);
		addBasicOperation("greyscale", 0);
		addBasicOperation("blackwhite", 0);
		addBasicOperation("median", 0);
	}

	private HashMap<String, Selection> selections = new HashMap<String, Selection>();
	private HashMap<String, Operation> compositeOperations = new HashMap<String, Operation>();

	private ArrayList<Operation> operationsBuffer = new ArrayList<Operation>();

	private int width = 0, height = 0;
	private ArrayList<Layer> layers = new ArrayList<Layer>();

	private boolean unsaved;

	private static String getExtension(String path) throws EditorException {
		Pattern p = Pattern.compile("[^\\.]*\\.(.*)");
		Matcher m = p.matcher(path);

		if (m.matches())
			return m.group(1);
		else
			throw new EditorException("File doesn't have an extension.");
	}

	public static Image getImage() {
		if (image == null)
			image = new Image();
		return image;
	}

	private void resize(Layer l) {
		if (height < l.getHeight() || width < l.getWidth()) {
			for (Layer ll : layers) {
				ll.resize(width < l.getWidth() ? l.getWidth() : -1, height < l.getHeight() ? l.getHeight() : -1);
			}
			height = height < l.getHeight() ? l.getHeight() : height;
			width = width < l.getWidth() ? l.getWidth() : width;
		}
		l.resize(width > l.getWidth() ? width : -1, height > l.getHeight() ? height : -1);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isUnsaved() {
		return unsaved;
	}

	public Layer createLayer(String path) throws EditorException {
		String extension = getExtension(path);
		ImageFormatter formatter = ImageFormatter.getFormatter(extension);

		return formatter.load(path);
	}

	public Layer createLayer(int width, int height) {
		return new Layer(width, height, "");
	}

	public void addLayer(Layer l) throws EditorException {
		if (l != null) {
			resize(l);
			layers.add(0, l);
			unsaved = true;
		} else
			throw new EditorException("Null layer is being added. This should never happen...");
	}

	public void addLayerBottom(Layer l) throws EditorException {
		if (l != null) {
			resize(l);
			layers.add(l);
			unsaved = true;
		} else
			throw new EditorException("Null layer is being added. This should never happen...");
	}

	public void removeLayer(int index) {
		layers.remove(index);
		if (layers.size() == 0)
			width = height = 0;
		unsaved = true;
	}

	public void exportImage(String path) throws EditorException {
		String extension = getExtension(path);
		ImageFormatter formatter = ImageFormatter.getFormatter(extension);

		formatter.save(path);
		unsaved = false;
	}

	public Pixel getPixel(int x, int y) {
		int tempRed = 0, tempGreen = 0, tempBlue = 0;
		double tempAlpha = 0;
		for (Layer l : layers) {
			if (l.getVisible()) {
				double opacity = l.getOpacity() / 100.0;
				Pixel tempPixel = l.getPixel(x, y);
				double doubleA = tempPixel.getA() * opacity / 255.0;
				tempAlpha += (1 - tempAlpha) * doubleA;
			}
		}
		double temperAlpha = 0;
		for (Layer l : layers) {
			if (l.getVisible()) {
				double opacity = l.getOpacity() / 100.0;
				Pixel tempPixel = l.getPixel(x, y);
				double doubleA = tempPixel.getA() * opacity / 255.0;
				tempRed += (1 - temperAlpha) * doubleA / tempAlpha * tempPixel.getR();
				tempGreen += (1 - temperAlpha) * doubleA / tempAlpha * tempPixel.getG();
				tempBlue += (1 - temperAlpha) * doubleA / tempAlpha * tempPixel.getB();
				temperAlpha += (1 - temperAlpha) * doubleA;
			}
		}
		return new Pixel(tempRed, tempGreen, tempBlue, (int) (tempAlpha * 255));
	}

	public Layer getLayer(int i) {
		return layers.get(i);
	}

	public void clearImage() {
		layers.clear();
		selections.clear();
		operationsBuffer.clear();
		compositeOperations.clear();
		width = height = 0;
		unsaved = false;
	}

	public void addSelection(String name, Selection selection) throws EditorException {
		if (selections.get(name) == null) {
			selections.put(name, selection);
			unsaved = true;
		} else
			throw new EditorException("Selection with that name already exists");
	}

	public void removeSelection(String name) {
		if (selections.get(name) != null) {
			selections.remove(name);
			unsaved = true;
		}
	}

	public List<Selection> getActiveSelections() {
		return selections.values().stream().filter(s -> s.isActive()).collect(Collectors.toList());
	}

	public boolean getSelectionActive(String name) {
		if (selections.containsKey(name))
			return selections.get(name).isActive();
		return false;
	}

	public void setSelectionActive(String name, boolean active) {
		if (selections.containsKey(name)) {
			selections.get(name).setActive(active);
			unsaved = true;
		}
	}

	public Operation getOperation(String name) throws EditorException {
		if (basicOperations.containsKey(name))
			return basicOperations.get(name);
		if (compositeOperations.containsKey(name))
			return compositeOperations.get(name);
		throw new EditorException("Requested operation not found.");
	}

	public List<String> getAvailableOperations() {
		return Stream.concat(basicOperations.keySet().stream(), compositeOperations.keySet().stream())
				.collect(Collectors.toList());
	}

	public List<String> getAvailableCompositeOperations() {
		return compositeOperations.keySet().stream().collect(Collectors.toList());
	}

	public void addOperation(Operation o) {
		operationsBuffer.add(o);
	}

	public void clearOperations() {
		operationsBuffer.clear();
	}

	public void saveAsCompositeOperation(String name) throws EditorException {
		if (basicOperations.containsKey(name) || compositeOperations.containsKey(name))
			throw new EditorException("Composite operation with that name already exists.");
		CompositeOperation newOperation = new CompositeOperation(name);

		for (Operation o : operationsBuffer)
			newOperation.addOperation(o);

		compositeOperations.put(name, newOperation);

		operationsBuffer.clear();
		unsaved = true;
	}

	public void saveGivenCompositeOperation(String name, CompositeOperation operation) throws EditorException {
		if (basicOperations.containsKey(name) || compositeOperations.containsKey(name))
			throw new EditorException("Composite operation with that name already exists.");
		compositeOperations.put(name, operation);
		unsaved = true;
	}

	public String getOperationString() {
		StringBuilder sb = new StringBuilder();
		for (Operation o : operationsBuffer) {
			sb.append(o + "\n");
		}
		return sb.toString();
	}

	public void exportOperation(String operationName, String path) throws EditorException {
		if (!compositeOperations.containsKey(operationName))
			throw new EditorException("Composite operation with that name doesn't exist.");

		String extension = getExtension(path);
		OperationFormatter formatter = OperationFormatter.getFormatter(extension);

		formatter.save((CompositeOperation) compositeOperations.get(operationName), path);
	}

	public void importOperation(String path) throws EditorException {

		String extension = getExtension(path);
		OperationFormatter formatter = OperationFormatter.getFormatter(extension);

		CompositeOperation newOperation = formatter.load(path);

		if (compositeOperations.containsKey(newOperation.getName()))
			throw new EditorException("Composite operation with that name already exists.");
		compositeOperations.put(newOperation.getName(), newOperation);
	}

	public ArrayList<Layer> getLayers() {
		return layers;
	}

	public HashMap<String, Selection> getSelections() {
		return selections;
	}

	public Collection<Operation> getCompositeOperations() {
		return compositeOperations.values();
	}

	public void saveImage(String path) throws EditorException {
		String extension = getExtension(path);
		ProjectFormatter formatter = ProjectFormatter.getFormatter(extension);
		formatter.save(path);
		unsaved = false;
	}

	public void loadImage(String path) throws EditorException {

		String extension = getExtension(path);
		ProjectFormatter formatter = ProjectFormatter.getFormatter(extension);
		clearImage();
		formatter.load(path);
		unsaved = false;

	}

	public void operate() throws EditorException {

		File tempFolder = new File("temp");

		tempFolder.mkdir();

		ArrayList<Boolean> layerVisibleStates = new ArrayList<Boolean>();
		ArrayList<String> layerPaths = new ArrayList<String>();

		CompositeOperation operation = new CompositeOperation("jesusalmighty");

		for (Operation o : operationsBuffer) {
			operation.addOperation(o);
		}

		OperationFormatter.getFormatter("fun").save(operation, "temp/operation.fun");

		for (Layer l : layers) {
			layerVisibleStates.add(l.getVisible());
			layerPaths.add(l.getPath());
			l.setVisible(false);
		}

		int cnt = 0;
		for (Layer l : layers) {
			int tempOpacity = l.getOpacity();

			l.setVisible(true);
			l.setOpacity(100);
			exportImage("temp/layer" + cnt + ".bmp");
			l.setOpacity(tempOpacity);
			l.setPath("temp/layer" + (cnt++) + ".bmp");
			l.setVisible(false);
		}

		saveImage("temp/image.dr");

		Runtime runtime = Runtime.getRuntime();

		try {
			Process process = runtime.exec("POOP_Projekat.exe temp/image.dr temp/operation.fun");
			process.waitFor();
			if (process.exitValue() != 0)
				throw new EditorException("Operation failed.");

			loadImage("temp/image.dr");

			unsaved = true;
		} catch (IOException | InterruptedException e) {
			throw new EditorException("Operation failed.");
		} finally {
			cnt = 0;
			for (Layer l : layers) {
				l.setVisible(layerVisibleStates.get(cnt));
				l.setPath(layerPaths.get(cnt++));
			}
			operationsBuffer.clear();

			File[] files = tempFolder.listFiles();
			for (File f : files) {
				f.delete();
			}
			tempFolder.delete();

		}

	}
}
