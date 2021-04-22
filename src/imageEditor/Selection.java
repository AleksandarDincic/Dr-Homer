package imageEditor;

import java.util.ArrayList;
import java.util.Iterator;

public class Selection implements Iterable<Rectangle> {

	private ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
	private boolean active = true;

	public void addRectangle(Rectangle r) {
		rectangles.add(r);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Iterator<Rectangle> iterator() {
		return new Iterator<Rectangle>() {

			private int i = 0;

			public boolean hasNext() {
				return i < rectangles.size();
			}

			public Rectangle next() {
				return rectangles.get(i++);
			}

		};
	}

}
