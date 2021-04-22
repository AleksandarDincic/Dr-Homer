package imageEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import imageEditor.EditorException;
import imageEditor.MainWindow;

@SuppressWarnings("serial")
public class OperateDialog extends EditorDialog {

	private boolean started = false;

	public OperateDialog(MainWindow owner) {
		super(owner, "Operation in progress", 480, 120);
	}

	@Override
	protected void addComponents() {
		Label dialogLabel = new Label("Operation in progress.");
		dialogLabel.setAlignment(Label.CENTER);

		Label dialogCenterLabel = new Label("Please wait...");
		dialogCenterLabel.setAlignment(Label.CENTER);

		add(dialogLabel, BorderLayout.NORTH);
		add(dialogCenterLabel, BorderLayout.NORTH);
	}

	@Override
	protected void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});

		addComponentListener(new ComponentAdapter() {

			public void componentShown(ComponentEvent e) {
				if (!started) {
					started = true;
					try {
						image.operate();
						mwOwner.refreshImage();
					} catch (EditorException ex) {
						mwOwner.showException(ex);
					}
					mwOwner.updateOperationsBuffer();
					setVisible(false);
				}
			}
		});
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

}
