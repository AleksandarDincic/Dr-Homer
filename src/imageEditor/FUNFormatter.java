package imageEditor;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;

public class FUNFormatter extends OperationFormatter {

	private Operation parseXMLToOperation(Element currentOperation) throws EditorException {
		Operation newOperation = null;
		Node currentNode = null;
		for (currentNode = currentOperation.getFirstChild(); currentNode != null
				&& currentNode.getNodeType() != Node.ELEMENT_NODE; currentNode = currentNode.getNextSibling())
			;

		String name = currentNode.getTextContent();
		for (currentNode = currentNode.getNextSibling(); currentNode != null
				&& currentNode.getNodeType() != Node.ELEMENT_NODE; currentNode = currentNode.getNextSibling())
			;
		if (currentNode != null && currentNode.getNodeName() == "operations") {
			newOperation = new CompositeOperation(name);
			for (currentNode = currentNode.getFirstChild(); currentNode != null; currentNode = currentNode
					.getNextSibling()) {
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					Operation innerOperation = parseXMLToOperation((Element) currentNode);
					((CompositeOperation) newOperation).addOperation(innerOperation);
				}
			}
		} else {
			newOperation = ((BasicOperation) Image.getImage().getOperation(name)).clone();

			if (currentNode != null) {
				int cnt = 0;
				for (; currentNode != null; currentNode = currentNode.getNextSibling()) {
					if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
						((BasicOperation) newOperation).setParam(cnt++,
								Double.parseDouble(currentNode.getTextContent()));
					}
				}
			}

		}
		return newOperation;
	}

	@Override
	public CompositeOperation load(String path) throws EditorException {
		try {
			File inputFile = new File(path);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
			doc.getDocumentElement().normalize();

			Element rootElement = doc.getDocumentElement();

			CompositeOperation newOperation = new CompositeOperation(
					rootElement.getElementsByTagName("name").item(0).getTextContent());

			Node operationsNode = rootElement.getElementsByTagName("operations").item(0);

			for (Node node = operationsNode.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					newOperation.addOperation(parseXMLToOperation((Element) node));
				}
			}

			return newOperation;

		} catch (EditorException e) {
			throw new EditorException(e.getMessage());
		} catch (Exception e) {
			throw new EditorException("Load failed.");
		}
	}

	@Override
	public void save(CompositeOperation operation, String path) throws EditorException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element rootElement = doc.createElement("compositeOperation");

			Element nameElement = doc.createElement("name");
			nameElement.appendChild(doc.createTextNode(operation.getName()));
			rootElement.appendChild(nameElement);

			operation.appendParamsXML(doc, rootElement);

			doc.appendChild(rootElement);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			transformer.transform(source, result);

		} catch (Exception e) {
			throw new EditorException("Save failed.");
		}
	}

}
