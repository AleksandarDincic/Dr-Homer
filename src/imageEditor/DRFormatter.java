package imageEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class DRFormatter extends ProjectFormatter {

	private void appendXMLRectangle(Rectangle rectangle, Document doc, Element currentElement) {
		Element rectangleElement = doc.createElement("rectangle");

		Element xElement = doc.createElement("x");
		xElement.appendChild(doc.createTextNode(Integer.toString(rectangle.getX())));
		rectangleElement.appendChild(xElement);

		Element yElement = doc.createElement("y");
		yElement.appendChild(doc.createTextNode(Integer.toString(rectangle.getY())));
		rectangleElement.appendChild(yElement);

		Element widthElement = doc.createElement("width");
		widthElement.appendChild(doc.createTextNode(Integer.toString(rectangle.getWidth())));
		rectangleElement.appendChild(widthElement);

		Element heightElement = doc.createElement("height");
		heightElement.appendChild(doc.createTextNode(Integer.toString(rectangle.getHeight())));
		rectangleElement.appendChild(heightElement);

		currentElement.appendChild(rectangleElement);
	}

	private void appendXMLSelection(String name, Selection selection, Document doc, Element currentElement) {
		Element selectionElement = doc.createElement("selection");

		Element nameElement = doc.createElement("name");
		nameElement.appendChild(doc.createTextNode(name));
		selectionElement.appendChild(nameElement);

		Element activeElement = doc.createElement("active");
		activeElement.appendChild(doc.createTextNode(selection.isActive() ? "1" : "0"));
		selectionElement.appendChild(activeElement);

		for (Rectangle r : selection) {
			appendXMLRectangle(r, doc, selectionElement);
		}

		currentElement.appendChild(selectionElement);
	}

	private void appendXMLLayer(Layer l, Document doc, Element currentElement) {
		Element layerElement = doc.createElement("layer");

		Element pathElement = doc.createElement("path");
		pathElement.appendChild(doc.createTextNode(l.getPath()));
		layerElement.appendChild(pathElement);

		Element activeElement = doc.createElement("active");
		activeElement.appendChild(doc.createTextNode(l.getActive() ? "1" : "0"));
		layerElement.appendChild(activeElement);

		Element visibleElement = doc.createElement("visible");
		visibleElement.appendChild(doc.createTextNode(l.getVisible() ? "1" : "0"));
		layerElement.appendChild(visibleElement);

		Element opacityElement = doc.createElement("opacity");
		opacityElement.appendChild(doc.createTextNode(Integer.toString(l.getOpacity())));
		layerElement.appendChild(opacityElement);

		Element doneElement = doc.createElement("doneOperations");
		layerElement.appendChild(doneElement);

		currentElement.appendChild(layerElement);
	}

	private Layer parseXMLToLayer(Element currentLayer, int width, int height) throws EditorException {
		Layer newLayer;

		String layerPath = currentLayer.getElementsByTagName("path").item(0).getTextContent();

		if (layerPath.equals(""))
			newLayer = new Layer(width, height, "");
		else
			newLayer = Image.getImage().createLayer(layerPath);

		newLayer.setActive(Integer.parseInt(currentLayer.getElementsByTagName("active").item(0).getTextContent()) != 0);

		newLayer.setVisible(
				Integer.parseInt(currentLayer.getElementsByTagName("visible").item(0).getTextContent()) != 0);
		newLayer.setOpacity(Integer.parseInt(currentLayer.getElementsByTagName("opacity").item(0).getTextContent()));

		return newLayer;
	}

	private Rectangle parseXMLToRectangle(Element currentRectangle) {
		int x = Integer.parseInt(currentRectangle.getElementsByTagName("x").item(0).getTextContent());
		int y = Integer.parseInt(currentRectangle.getElementsByTagName("y").item(0).getTextContent());
		int width = Integer.parseInt(currentRectangle.getElementsByTagName("width").item(0).getTextContent());
		int height = Integer.parseInt(currentRectangle.getElementsByTagName("height").item(0).getTextContent());

		return new Rectangle(x, y, width, height);
	}

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
	public void load(String path) throws EditorException {
		try {
			Image image = Image.getImage();

			File inputFile = new File(path);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
			doc.getDocumentElement().normalize();

			Element rootElement = doc.getDocumentElement();

			int width = Integer.parseInt(rootElement.getElementsByTagName("width").item(0).getTextContent());
			int height = Integer.parseInt(rootElement.getElementsByTagName("height").item(0).getTextContent());

			NodeList layersList = ((Element) rootElement.getElementsByTagName("layers").item(0))
					.getElementsByTagName("layer");

			for (int i = 0; i < layersList.getLength(); i++) {
				image.addLayerBottom(parseXMLToLayer((Element) layersList.item(i), width, height));
			}

			NodeList selectionsList = ((Element) rootElement.getElementsByTagName("selections").item(0))
					.getElementsByTagName("selection");

			for (int i = 0; i < selectionsList.getLength(); i++) {
				Selection newSelection = new Selection();

				String name = ((Element) selectionsList.item(i)).getElementsByTagName("name").item(0).getTextContent();

				newSelection.setActive(Integer.parseInt(((Element) selectionsList.item(i))
						.getElementsByTagName("active").item(0).getTextContent()) != 0);

				NodeList rectanglesList = ((Element) selectionsList.item(i)).getElementsByTagName("rectangle");
				for (int j = 0; j < rectanglesList.getLength(); j++) {
					newSelection.addRectangle(parseXMLToRectangle((Element) rectanglesList.item(j)));
				}

				image.addSelection(name, newSelection);
			}

			Element operationsList = ((Element) rootElement.getElementsByTagName("compositeOperations").item(0));

			for (Node node = operationsList.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					CompositeOperation newOperation = (CompositeOperation) parseXMLToOperation((Element) node);
					image.saveGivenCompositeOperation(newOperation.getName(), newOperation);
				}
			}

		} catch (EditorException e) {
			throw new EditorException(e.getMessage());
		} catch (Exception e) {
			throw new EditorException("Load failed.");
		}
	}

	@Override
	public void save(String path) throws EditorException {
		try {

			Image image = Image.getImage();

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element rootElement = doc.createElement("image");

			Element widthElement = doc.createElement("width");
			widthElement.appendChild(doc.createTextNode(Integer.toString(image.getWidth())));
			rootElement.appendChild(widthElement);

			Element heightElement = doc.createElement("height");
			heightElement.appendChild(doc.createTextNode(Integer.toString(image.getHeight())));
			rootElement.appendChild(heightElement);

			Element layersElement = doc.createElement("layers");

			ArrayList<Layer> layers = image.getLayers();
			for (Layer l : layers) {
				appendXMLLayer(l, doc, layersElement);
			}
			rootElement.appendChild(layersElement);

			Element selectionsElement = doc.createElement("selections");
			HashMap<String, Selection> selections = image.getSelections();
			for (String s : selections.keySet()) {
				appendXMLSelection(s, selections.get(s), doc, selectionsElement);
			}
			rootElement.appendChild(selectionsElement);

			Element compositeOperationsElement = doc.createElement("compositeOperations");
			Collection<Operation> compositeOperations = image.getCompositeOperations();
			for (Operation o : compositeOperations) {
				o.appendOperationXML(doc, compositeOperationsElement);
			}
			rootElement.appendChild(compositeOperationsElement);

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
