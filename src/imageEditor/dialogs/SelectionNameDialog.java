package imageEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import imageEditor.EditorException;
import imageEditor.MainWindow;

@SuppressWarnings("serial")
public class SelectionNameDialog extends EditorDialog {
	private TextField nameField;
	private Button addButton;

	public SelectionNameDialog(MainWindow owner) {
		super(owner, "Add selection", 480, 120);
	}

	@Override
	protected void addComponents() {
		Panel pathPanel = new Panel();
		nameField = new TextField(35);
		pathPanel.add(nameField);

		Label dialogLabel = new Label("Enter new selection name:");
		dialogLabel.setAlignment(Label.CENTER);

		Panel buttonPanel = new Panel();
		addButton = new Button("Add");
		buttonPanel.add(addButton);

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
				mwOwner.addSelection(nameField.getText());
				setVisible(false);
			} catch (EditorException e1) {
				mwOwner.showException(e1);
			}
		});
	}

	@Override
	public void showDialog() {
		nameField.setText("");
		setVisible(true);
	}

}
