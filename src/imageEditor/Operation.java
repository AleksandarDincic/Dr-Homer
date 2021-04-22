package imageEditor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Operation {

	protected String name;

	public Operation(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Operation clone() throws CloneNotSupportedException {
		return (Operation) super.clone();
	}

	public void appendOperationXML(Document doc, Element currentElement) {
		Element operationElement = doc.createElement("operation");

		Element nameElement = doc.createElement("name");
		nameElement.appendChild(doc.createTextNode(name));
		operationElement.appendChild(nameElement);

		appendParamsXML(doc, operationElement);

		currentElement.appendChild(operationElement);
	}

	public abstract void appendParamsXML(Document doc, Element currentElement);

	public String toString() {
		return name;
	}
}
