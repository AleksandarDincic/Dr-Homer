package imageEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import imageEditor.EditorException;
import imageEditor.MainWindow;

@SuppressWarnings("serial")
public class AddLayerDialog extends EditorDialog {

	private TextField pathField;
	private Button addButton;

	private Panel entryPanel;
	private Panel pathPanel;

	private Panel dimensionsPanel;
	private TextField widthField;
	private TextField heightField;

	private CheckboxGroup layerTypes;
	private Checkbox emptyLayer;
	private Checkbox imageLayer;

	@Override
	protected void addComponents() {
		dimensionsPanel = new Panel();
		Label widthLabel = new Label("Width:");
		Label heightLabel = new Label("Height:");
		widthField = new TextField("640");
		heightField = new TextField("480");
		dimensionsPanel.add(widthLabel);
		dimensionsPanel.add(widthField);
		dimensionsPanel.add(heightLabel);
		dimensionsPanel.add(heightField);

		pathPanel = new Panel();
		pathField = new TextField(35);
		pathPanel.add(pathField);

		entryPanel = new Panel(new CardLayout());
		entryPanel.add(pathPanel);
		entryPanel.add(dimensionsPanel);

		Panel buttonPanel = new Panel();
		addButton = new Button("Add");
		buttonPanel.add(addButton);

		Panel selectionPanel = new Panel(new GridLayout(1, 0));
		Label selectionLabel = new Label("Layer type:");
		layerTypes = new CheckboxGroup();
		emptyLayer = new Checkbox("Empty", layerTypes, false);
		imageLayer = new Checkbox("From image", layerTypes, true);
		Panel choicesPanel = new Panel(new GridLayout(0, 1));
		choicesPanel.add(emptyLayer);
		choicesPanel.add(imageLayer);
		selectionPanel.add(selectionLabel);
		selectionPanel.add(choicesPanel);

		Label dialogLabel = new Label("Enter path to image file:");
		dialogLabel.setAlignment(Label.CENTER);

		add(dialogLabel, BorderLayout.NORTH);
		add(selectionPanel, BorderLayout.WEST);
		add(entryPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (addButton.isEnabled())
					setVisible(false);
			}
		});

		addButton.addActionListener(e -> {
			try {
				addButton.setEnabled(false);

				if (layerTypes.getSelectedCheckbox().equals(imageLayer)) {
					image.addLayer(image.createLayer(pathField.getText().toLowerCase()));
					mwOwner.addLayer(pathField.getText());
				} else {
					int w = Integer.parseInt(widthField.getText());
					int h = Integer.parseInt(heightField.getText());
					if (w < 1 || h < 1)
						throw new EditorException("Invalid numbers entered.");
					image.addLayer(image.createLayer(w, h));
					mwOwner.addLayer("Empty layer (" + w + " x " + h + ")");
				}
				mwOwner.refreshImage();
				setVisible(false);
			} catch (EditorException e1) {
				mwOwner.showException(e1);
			} catch (OutOfMemoryError e1) {
				mwOwner.showException(new EditorException("Maximum image size exceeded. Calm down."));
			} catch (NumberFormatException ex) {
				mwOwner.showException(new EditorException("Please enter numbers."));
			} finally {
				addButton.setEnabled(true);
			}
		});

		emptyLayer.addItemListener(e -> {
			CardLayout c = (CardLayout) (entryPanel.getLayout());
			c.next(entryPanel);
		});
		imageLayer.addItemListener(e -> {
			CardLayout c = (CardLayout) (entryPanel.getLayout());
			c.next(entryPanel);
		});
	}

	public AddLayerDialog(MainWindow owner) {
		super(owner, "Add layer", 480, 120);
	}

	@Override
	public void showDialog() {
		pathField.setText("");
		widthField.setText("640");
		heightField.setText("480");
		setVisible(true);
	}
}