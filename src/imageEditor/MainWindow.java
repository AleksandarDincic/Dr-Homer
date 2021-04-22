package imageEditor;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import imageEditor.dialogs.*;

@SuppressWarnings("serial")
public class MainWindow extends Frame {

	private Image image = Image.getImage();

	// prikaz slike
	private ImageDisplay imageDisplay = new ImageDisplay();

	// selekcije
	private List selectionsList = new List();
	private Checkbox selectionsActive = new Checkbox("Active", false);
	private Button selectionsAdd = new Button("Add");
	private Button selectionsRemove = new Button("Remove");

	// slojevi
	private List layersList = new List();
	private TextField layersOpacity = new TextField("100");
	private Checkbox layersActive = new Checkbox("Active", false);
	private Checkbox layersVisible = new Checkbox("Visible", false);
	private Button layersAdd = new Button("Add");
	private Button layersRemove = new Button("Remove");

	// operacije
	private TextArea operationsBuffer = new TextArea("");
	private Button operationsAdd = new Button("Add");
	private Button operationsClear = new Button("Clear");
	private Button operationsImport = new Button("Import");
	private Button operationsExport = new Button("Export");
	private Button operationsApply = new Button("Apply");
	private Button operationsToComposite = new Button("To Composite");

	private void newImage() {
		try {
			promptSelecting();
			promptUnsaved();

			layersList.removeAll();
			selectionsList.removeAll();
			image.clearImage();
			refreshImage();
		} catch (EditorException e) {
			showException(e);
		}
	}

	private void openImage() {
		try {
			promptSelecting();
			promptUnsaved();

			new OpenImageDialog(this).showDialog();
		} catch (EditorException e) {
			showException(e);
		}
	}

	private void quit() {
		try {
			promptSelecting();
			promptUnsaved();
			dispose();
		} catch (EditorException e) {
			showException(e);
		}
	}

	private void promptSelecting() throws EditorException {
		if (imageDisplay.getSelecting())
			throw new EditorException("Please finish selecting first.");
	}

	private void removeLayer() {
		try {
			promptSelecting();
			if (layersList.getSelectedIndex() != -1) {
				int selected = layersList.getSelectedIndex();
				layersList.remove(selected);
				Image.getImage().removeLayer(selected);
				refreshImage();
			}
		} catch (EditorException e) {
			showException(e);
		}
	}

	private void promptUnsaved() {
		if (image.isUnsaved()) {
			new PromptSaveDialog(this).showDialog();
		}
	}

	private void showAddLayerDialog() {
		try {
			promptSelecting();
			new AddLayerDialog(this).showDialog();
		} catch (EditorException e) {
			showException(e);
		}
	}

	private void addComponents() {

		// SELEKCIJE
		Panel selectionsPanel = new Panel(new GridLayout(0, 1, 10, 10));

		Label selectionsLabel = new Label("Selections:");
		selectionsLabel.setFont(new Font(null, Font.BOLD, 18));
		selectionsList.setMultipleMode(false);
		Panel selectionsButtons = new Panel(new GridLayout(0, 1, 10, 10));

		selectionsButtons.add(selectionsAdd);
		selectionsButtons.add(selectionsRemove);

		selectionsPanel.add(selectionsLabel);
		selectionsPanel.add(selectionsList);
		selectionsPanel.add(selectionsActive);
		selectionsPanel.add(selectionsButtons);
		// SELEKCIJE

		// SLOJEVI
		Panel layersPanel = new Panel(new GridLayout(0, 1, 10, 10));

		Label layersLabel = new Label("Layers:");
		layersLabel.setFont(new Font(null, Font.BOLD, 18));
		layersList.setMultipleMode(false);
		Panel layersOpacityPanel = new Panel();
		Label layersOpacityLabel = new Label("Opacity:");
		layersOpacityPanel.add(layersOpacityLabel);
		layersOpacityPanel.add(layersOpacity);
		Panel layersButtons = new Panel(new GridLayout(0, 1, 10, 10));
		layersButtons.add(layersAdd);
		layersButtons.add(layersRemove);

		layersPanel.add(layersLabel);
		layersPanel.add(layersList);
		layersPanel.add(layersOpacityPanel);
		layersPanel.add(layersActive);
		layersPanel.add(layersVisible);
		layersPanel.add(layersButtons);
		// SLOJEVI

		// OPERACIJE
		Panel operationsPanel = new Panel(new GridLayout(1, 0, 10, 10));

		Label operationsLabel = new Label("Operations:");
		operationsLabel.setFont(new Font(null, Font.BOLD, 18));
		operationsBuffer.setPreferredSize(new Dimension(100, 50));
		operationsBuffer.setEditable(false);
		Panel operationsButtons1 = new Panel(new GridLayout(0, 1, 10, 10));
		operationsButtons1.add(operationsAdd);
		operationsButtons1.add(operationsClear);
		operationsButtons1.add(operationsImport);
		Panel operationsButtons2 = new Panel(new GridLayout(0, 1, 10, 10));
		operationsButtons2.add(operationsApply);
		operationsButtons2.add(operationsToComposite);
		operationsButtons2.add(operationsExport);

		operationsPanel.add(operationsLabel);
		operationsPanel.add(operationsBuffer);
		operationsPanel.add(operationsButtons1);
		operationsPanel.add(operationsButtons2);
		// OPERACIJE

		Panel centerPanel = new Panel(new BorderLayout());

		centerPanel.add(imageDisplay, BorderLayout.CENTER);
		centerPanel.add(operationsPanel, BorderLayout.SOUTH);

		add(selectionsPanel, BorderLayout.WEST);
		add(layersPanel, BorderLayout.EAST);
		add(centerPanel, BorderLayout.CENTER);
	}

	private void addMenu() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");

		MenuItem newImage = new MenuItem("New image", new MenuShortcut('N'));
		newImage.addActionListener(e -> {
			newImage();
		});

		MenuItem openImage = new MenuItem("Open image", new MenuShortcut('O'));
		openImage.addActionListener(e -> {
			openImage();
		});
		MenuItem saveImage = new MenuItem("Save image", new MenuShortcut('S'));
		saveImage.addActionListener(e -> {
			showSave();
		});
		MenuItem exportImage = new MenuItem("Export image", new MenuShortcut('E'));
		exportImage.addActionListener(e -> {
			showExport();
		});

		MenuItem quit = new MenuItem("Quit", new MenuShortcut('Q'));
		quit.addActionListener(e -> {
			quit();
		});

		fileMenu.add(newImage);
		fileMenu.add(openImage);
		fileMenu.add(saveImage);
		fileMenu.add(exportImage);
		fileMenu.add(quit);

		menuBar.add(fileMenu);

		setMenuBar(menuBar);
	}

	private void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});

		layersList.addItemListener(e -> {
			if (layersList.getSelectedIndex() != -1) {

				Layer selectedLayer = image.getLayer(layersList.getSelectedIndex());
				layersOpacity.setText(Integer.toString(selectedLayer.getOpacity()));
				layersActive.setState(selectedLayer.getActive());
				layersVisible.setState(selectedLayer.getVisible());
			}
		});

		layersAdd.addActionListener(e -> {
			showAddLayerDialog();
		});

		layersRemove.addActionListener(e -> {
			removeLayer();
		});

		layersOpacity.addActionListener(e -> {
			if (layersList.getSelectedIndex() != -1) {
				try {
					int newOpacity = Integer.parseInt(layersOpacity.getText());
					image.getLayer(layersList.getSelectedIndex()).setOpacity(newOpacity);
					refreshImage();
				} catch (NumberFormatException ex) {

				}
			}
		});

		layersVisible.addItemListener(e -> {
			if (layersList.getSelectedIndex() != -1) {
				image.getLayer(layersList.getSelectedIndex()).setVisible(layersVisible.getState());
				refreshImage();
			}
		});

		layersActive.addItemListener(e -> {
			if (layersList.getSelectedIndex() != -1) {
				image.getLayer(layersList.getSelectedIndex()).setActive(layersActive.getState());
			}
		});

		selectionsAdd.addActionListener(e -> {
			if (image.getWidth() > 0 && image.getHeight() > 0) {
				switch (selectionsAdd.getLabel()) {
				case "Add":
					imageDisplay.startSelecting();
					selectionsAdd.setLabel("Done");
					break;
				case "Done":
					new SelectionNameDialog(this).showDialog();
					break;
				}
			}
		});

		selectionsRemove.addActionListener(e -> {
			if (selectionsList.getSelectedItem() != null) {
				image.removeSelection(selectionsList.getSelectedItem());
				selectionsList.remove(selectionsList.getSelectedIndex());
				imageDisplay.repaint();
			}
		});

		selectionsActive.addItemListener(e -> {
			if (selectionsList.getSelectedItem() != null) {
				image.setSelectionActive(selectionsList.getSelectedItem(), selectionsActive.getState());
				imageDisplay.repaint();
			}
		});

		selectionsList.addItemListener(e -> {
			if (selectionsList.getSelectedItem() != null) {
				selectionsActive.setState(image.getSelectionActive(selectionsList.getSelectedItem()));
			}
		});

		operationsAdd.addActionListener(e -> {
			new AddOperationDialog(this).showDialog();
		});

		operationsClear.addActionListener(e -> {
			image.clearOperations();
			operationsBuffer.setText("");
		});

		operationsToComposite.addActionListener(e -> {
			new CompositeOperationNameDialog(this).showDialog();
		});

		operationsImport.addActionListener(e -> {
			new ImportOperationDialog(this).showDialog();
		});

		operationsExport.addActionListener(e -> {
			new ExportOperationDialog(this).showDialog();
		});

		operationsApply.addActionListener(e -> {
			new OperateDialog(this).showDialog();
		});
	}

	public MainWindow() {
		super("Dr. Homer Image Editor");
		setSize(1280, 720);
		addComponents();
		addMenu();
		addListeners();
		setVisible(true);
	}

	public void refreshImage() {
		imageDisplay.refreshImage();
	}

	public void addLayer(String path) {
		layersList.add(path, 0);
	}

	public void addSelection(String name) throws EditorException {
		imageDisplay.addNewSelection(name);
		selectionsList.add(name);
		selectionsAdd.setLabel("Add");
	}

	public void addCompositeOperation(String name) throws EditorException {
		image.saveAsCompositeOperation(name);
		operationsBuffer.setText("");
	}

	public void showException(EditorException e) {
		new ExceptionDialog(this).showDialog(e);
	}

	public void showExport() {
		new ExportDialog(this).showDialog();
	}

	public void showSave() {
		new SaveImageDialog(this).showDialog();
	}

	public void updateOperationsBuffer() {
		operationsBuffer.setText(image.getOperationString());
	}

	public void updateAll() {
		layersList.removeAll();
		selectionsList.removeAll();

		ArrayList<Layer> layers = image.getLayers();
		for (Layer l : layers) {
			if (l.getPath().equals(""))
				layersList.add("Empty layer (" + l.getWidth() + " x " + l.getHeight() + ")");
			else
				layersList.add(l.getPath());
		}

		HashMap<String, Selection> selections = image.getSelections();

		for (String s : selections.keySet()) {
			selectionsList.add(s);
		}

		updateOperationsBuffer();
		refreshImage();
	}

	public static void main(String[] args) {
		new MainWindow();
	}
}