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
public class OpenImageDialog extends EditorDialog {
	private TextField pathField;
	private Button addButton;

	public OpenImageDialog(MainWindow owner) {
		super(owner, "Open image", 480, 120);
	}

	@Override
	protected void addComponents() {
		Panel pathPanel = new Panel();
		pathField = new TextField(35);
		pathPanel.add(pathField);

		Label dialogLabel = new Label("Enter path to image:");
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
				if (addButton.isEnabled())
					setVisible(false);
			}
		});

		addButton.addActionListener(e -> {
			try {
				addButton.setEnabled(false);
				image.loadImage(pathField.getText());
				mwOwner.updateAll();
				setVisible(false);
			} catch (EditorException e1) {
				mwOwner.updateAll();
				mwOwner.showException(e1);
			} finally {
				addButton.setEnabled(true);
			}
		});
	}

	@Override
	public void showDialog() {
		pathField.setText("");
		setVisible(true);
	}

}
