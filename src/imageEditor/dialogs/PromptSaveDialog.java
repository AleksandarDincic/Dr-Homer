package imageEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import imageEditor.MainWindow;

@SuppressWarnings("serial")
public class PromptSaveDialog extends EditorDialog {

	private Button saveButton;
	private Button exportButton;
	private Button quitButton;

	public PromptSaveDialog(MainWindow owner) {
		super(owner, "Unsaved changes", 480, 120);
	}

	@Override
	protected void addComponents() {
		Panel buttonPanel = new Panel();
		saveButton = new Button("Save");
		exportButton = new Button("Export");
		quitButton = new Button("Don't save");
		buttonPanel.add(saveButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(quitButton);

		Label dialogLabel = new Label("Save changes before closing?");
		dialogLabel.setAlignment(Label.CENTER);

		add(dialogLabel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.CENTER);
	}

	@Override
	protected void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});

		saveButton.addActionListener(e -> {
			mwOwner.showSave();
			setVisible(false);
		});

		exportButton.addActionListener(e -> {
			mwOwner.showExport();
			setVisible(false);
		});

		quitButton.addActionListener(e -> {
			setVisible(false);
		});
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

}