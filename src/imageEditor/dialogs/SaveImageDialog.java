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
public class SaveImageDialog extends EditorDialog {
	private TextField pathField;
	private Button addButton;

	public SaveImageDialog(MainWindow owner) {
		super(owner, "Save image", 480, 120);
	}

	@Override
	protected void addComponents() {
		Panel pathPanel = new Panel();
		pathField = new TextField(35);
		pathPanel.add(pathField);

		Label dialogLabel = new Label("Enter path where to save image:");
		dialogLabel.setAlignment(Label.CENTER);

		Panel buttonPanel = new Panel();
		addButton = new Button("Save");
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
				image.saveImage(pathField.getText());
				setVisible(false);
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
