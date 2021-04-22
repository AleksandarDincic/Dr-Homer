package imageEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import imageEditor.EditorException;
import imageEditor.MainWindow;

@SuppressWarnings("serial")
public class ExportOperationDialog extends EditorDialog {
	private TextField pathField;
	private Button addButton;
	private List operationsList;

	public ExportOperationDialog(MainWindow owner) {
		super(owner, "Export operation", 480, 120);
	}

	@Override
	protected void addComponents() {
		Panel pathPanel = new Panel();
		pathField = new TextField(35);
		pathPanel.add(pathField);

		Label dialogLabel = new Label("Select a composite operation and enter path where to save it:");
		dialogLabel.setAlignment(Label.CENTER);

		Panel buttonPanel = new Panel();
		addButton = new Button("Export");
		buttonPanel.add(addButton);

		operationsList = new List();
		operationsList.setMultipleMode(false);
		java.util.List<String> availableOperations = image.getAvailableCompositeOperations();

		for (String s : availableOperations)
			operationsList.add(s);

		add(operationsList, BorderLayout.WEST);
		add(dialogLabel, BorderLayout.NORTH);
		add(pathPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});

		addButton.addActionListener(e -> {
			try {
				if (operationsList.getSelectedItem() != null) {
					image.exportOperation(operationsList.getSelectedItem(), pathField.getText());
					setVisible(false);
				}
			} catch (EditorException e1) {
				mwOwner.showException(e1);
			}
		});
	}

	@Override
	public void showDialog() {
		pathField.setText("");
		setVisible(true);
	}

}
