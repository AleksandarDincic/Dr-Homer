package imageEditor;

import java.util.ArrayList;

public class Layer {
	private ArrayList<ArrayList<Pixel>> pixels;
	private int opacity = 100;
	private boolean active = true, visible = true;
	private String path;

	public Layer(int width, int height, String path) {
		pixels = new ArrayList<ArrayList<Pixel>>();
		for (int i = 0; i < height; i++) {
			ArrayList<Pixel> row = new ArrayList<Pixel>();
			for (int j = 0; j < width; j++) {
				row.add(new Pixel());
			}
			pixels.add(row);
		}
		this.path = path;
	}

	public int getOpacity() {
		return opacity;
	}

	public boolean getActive() {
		return active;
	}

	public boolean getVisible() {
		return visible;
	}

	public int getWidth() {
		return pixels.get(0).size();
	}

	public int getHeight() {
		return pixels.size();
	}

	public Pixel getPixel(int x, int y) {
		return pixels.get(y).get(x);
	}

	public void setPixel(Pixel pixel, int x, int y) {
		pixels.get(y).set(x, pixel);
	}

	public void setOpacity(int val) {
		opacity = val < 0 ? 0 : (val > 100 ? 100 : val);
	}

	public void setActive(boolean val) {
		active = val;
	}

	public void setVisible(boolean val) {
		visible = val;
	}

	public void resize(int width, int height) {
		if (width > 0) {
			for (ArrayList<Pixel> row : pixels) {
				while (row.size() != width) {
					row.add(new Pixel());
				}
			}
		}
		if (height > 0) {
			while (pixels.size() != height) {
				ArrayList<Pixel> row = new ArrayList<Pixel>();
				for (int j = 0; j < getWidth(); j++) {
					row.add(new Pixel());
				}
				pixels.add(row);
			}
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
