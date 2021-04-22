package imageEditor;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CompositeOperation extends Operation implements Cloneable {

	private ArrayList<Operation> operations = new ArrayList<Operation>();

	public CompositeOperation(String name) {
		super(name);
	}

	public void addOperation(Operation o) {
		operations.add(o);
	}

	public CompositeOperation clone() {
		try {
			return (CompositeOperation) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public void appendParamsXML(Document doc, Element currentElement) {
		Element operationsElement = doc.createElement("operations");

		for (Operation o : operations)
			o.appendOperationXML(doc, operationsElement);

		currentElement.appendChild(operationsElement);
	}
}
