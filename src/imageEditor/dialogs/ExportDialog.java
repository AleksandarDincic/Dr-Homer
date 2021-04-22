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
public class ExportDialog extends EditorDialog {

	private TextField pathField;
	private Button exportButton;

	public ExportDialog(MainWindow owner) {
		super(owner, "Export image", 480, 120);
	}

	@Override
	protected void addComponents() {
		Panel pathPanel = new Panel();
		pathField = new TextField(35);
		pathPanel.add(pathField);

		Label dialogLabel = new Label("Enter export path:");
		dialogLabel.setAlignment(Label.CENTER);

		Panel buttonPanel = new Panel();
		exportButton = new Button("Export");
		buttonPanel.add(exportButton);

		add(dialogLabel, BorderLayout.NORTH);
		add(pathPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (exportButton.isEnabled())
					setVisible(false);
			}
		});

		exportButton.addActionListener(e -> {
			try {
				exportButton.setEnabled(false);
				image.exportImage(pathField.getText());
				setVisible(false);
			} catch (EditorException e1) {
				mwOwner.showException(e1);
			} finally {
				exportButton.setEnabled(true);
			}
		});
	}

	@Override
	public void showDialog() {
		pathField.setText("");
		setVisible(true);
	}

}