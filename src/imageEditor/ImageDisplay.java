package imageEditor;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

@SuppressWarnings("serial")
public class ImageDisplay extends Canvas {

	private Image image = Image.getImage();
	private BufferedImage bufImg;
	private Selection newSelection = new Selection();
	private boolean firstPress, selecting;
	private int firstCoordX, firstCoordY;

	public ImageDisplay() {
		setBackground(Color.LIGHT_GRAY);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				if (selecting) {
					double widthRadio = (double) image.getWidth() / getWidth();
					int imageX = (int) (widthRadio * ev.getX());

					double heightRadio = (double) image.getHeight() / getHeight();
					int imageY = (int) (heightRadio * (getHeight() - 1 - ev.getY()));

					if (!firstPress) {
						firstCoordX = imageX;
						firstCoordY = imageY;
						firstPress = true;
					} else {
						int rectX = 0, rectY = 0, rectW = 0, rectH = 0;
						if (firstCoordX <= imageX) {
							rectX = firstCoordX;
							rectW = imageX - firstCoordX + 1;
						} else {
							rectX = imageX;
							rectW = firstCoordX - imageX + 1;
						}
						if (firstCoordY <= imageY) {
							rectY = imageY;
							rectH = imageY - firstCoordY + 1;
						} else {
							rectY = firstCoordY;
							rectH = firstCoordY - imageY + 1;
						}
						newSelection.addRectangle(new Rectangle(rectX, rectY, rectW, rectH));
						firstPress = false;
						repaint();
					}
				}
			}
		});
	}

	public void refreshImage() {
		if (image.getWidth() > 0 && image.getHeight() > 0) {
			bufImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			for (int i = 0; i < image.getHeight(); i++) {
				for (int j = 0; j < image.getWidth(); j++) {
					Pixel pixel = image.getPixel(j, i);
					int rgba = pixel.getB() + (pixel.getG() << 8) + (pixel.getR() << 16) + (pixel.getA() << 24);
					bufImg.setRGB(j, image.getHeight() - 1 - i, rgba);
				}
			}
		} else
			bufImg = null;
		repaint();
	}

	public void startSelecting() {
		if (!selecting) {
			selecting = true;
			repaint();
		}
	}

	public void addNewSelection(String name) throws EditorException {
		if (selecting) {
			image.addSelection(name, newSelection);
			selecting = false;
			repaint();
			newSelection = new Selection();
		}
	}

	public boolean getSelecting() {
		return selecting;
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (bufImg != null) {
			g.drawImage(bufImg, 0, 0, getWidth(), getHeight(), null);

			g.setColor(Color.BLACK);
			Graphics2D g2d = (Graphics2D) g.create();
			Stroke stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
			g2d.setStroke(stroke);

			double widthRadio = (double) getWidth() / image.getWidth();
			double heightRadio = (double) getHeight() / image.getHeight();

			if (selecting) {
				for (Rectangle r : newSelection) {
					int drawX = (int) (widthRadio * r.getX());
					int drawY = getHeight() - (int) (heightRadio * (r.getY() + 1));
					int drawW = (int) (widthRadio * r.getWidth());
					int drawH = (int) (heightRadio * r.getHeight());

					g2d.drawRect(drawX, drawY, drawW, drawH);
				}
			} else {
				List<Selection> activeSelections = image.getActiveSelections();
				for (Selection s : activeSelections) {
					for (Rectangle r : s) {
						int drawX = (int) (widthRadio * r.getX());
						int drawY = getHeight() - (int) (heightRadio * (r.getY() + 1));
						int drawW = (int) (widthRadio * r.getWidth());
						int drawH = (int) (heightRadio * r.getHeight());

						g2d.drawRect(drawX, drawY, drawW, drawH);
					}
				}
			}
		}
	}

}
