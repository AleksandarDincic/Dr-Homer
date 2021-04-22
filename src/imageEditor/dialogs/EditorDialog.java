package imageEditor.dialogs;

import java.awt.Dialog;

import imageEditor.Image;
import imageEditor.MainWindow;

@SuppressWarnings("serial")
public abstract class EditorDialog extends Dialog {

	protected Image image = Image.getImage();
	protected MainWindow mwOwner;

	protected abstract void addComponents();

	protected abstract void addListeners();

	public abstract void showDialog();

	public EditorDialog(MainWindow owner, String name, int width, int height) {
		super(owner, name, true);
		this.mwOwner = owner;
		setSize(width, height);
		setResizable(false);
		addComponents();
		addListeners();
	}
}