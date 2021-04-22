package imageEditor;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BasicOperation extends Operation implements Cloneable {

	private ArrayList<Double> params = new ArrayList<Double>();

	public BasicOperation(String name, int numOfParams) {
		super(name);
		for (int i = 0; i < numOfParams; i++)
			params.add(0.0);
	}

	public int getNumOfParams() {
		return params.size();
	}

	public void setParam(int index, double value) {
		if (index >= 0 && index < params.size())
			params.set(index, value);
	}

	public BasicOperation clone() {
		try {
			return (BasicOperation) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public void appendParamsXML(Document doc, Element currentElement) {
		for (Double param : params) {
			Element paramElement = doc.createElement("param");
			paramElement.appendChild(doc.createTextNode(param.toString()));
			currentElement.appendChild(paramElement);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());

		for (int i = 0; i < params.size(); i++) {
			if (i == 0) {
				sb.append(" (");
			}
			sb.append(params.get(i));
			if (i == params.size() - 1)
				sb.append(")");
			else
				sb.append(", ");
		}
		return sb.toString();
	}
}
