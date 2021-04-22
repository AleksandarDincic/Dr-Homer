package imageEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import imageEditor.EditorException;
import imageEditor.MainWindow;

@SuppressWarnings("serial")
public class ExceptionDialog extends EditorDialog {
	Label errorLabel;
	private Button okButton;

	@Override
	protected void addComponents() {
		errorLabel = new Label("");

		Panel buttonPanel = new Panel();
		okButton = new Button("OK");
		buttonPanel.add(okButton);

		errorLabel.setAlignment(Label.CENTER);
		add(errorLabel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.CENTER);
	}

	public ExceptionDialog(MainWindow owner) {
		super(owner, "Exception", 320, 80);
	}

	@Override
	protected void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});
		okButton.addActionListener(e -> {
			setVisible(false);
		});
	}

	@Override
	public void showDialog() {
		errorLabel.setText("Please don't use this.");
		setVisible(true);
	}

	public void showDialog(EditorException e) {
		errorLabel.setText(e.getMessage());
		setVisible(true);
	}
}
