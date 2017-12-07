import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import CAD.CADConnection;
import CAD.CADContainer;
import CAD.CADEntry;
import CAD.CADPackage;
import CAD.CADPrimitive;
import CAD.CADPrimitiveComment;
import CAD.CADPrimitiveParameter;
import CAD.CADPrimitivePort;

public class XMLGenerator {

	public static void main(String[] args) throws Exception {
		XMLGenerator xmlParser = new XMLGenerator(args[0]);
		xmlParser.generate();
		xmlParser.save();
	}

	private String filename;
	private Document doc;

	public XMLGenerator(String filename) {
		this.filename = filename;
	}

	public void generate() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.newDocument();

		CADContainer cadContainer = load();
		Element xmlContainer = doc.createElement("Cad2Ame");
		xmlContainer.setAttribute("Version", cadContainer.getVersion());
		doc.appendChild(xmlContainer);

		addList(xmlContainer, cadContainer.getPrimitives(), "Primitives", this::generateCadPrimitive);
		addList(xmlContainer, cadContainer.getConnections(), "Connections", this::generateCadConnection);
		// addConnections(xmlContainer);
	}

	private void save() throws IOException, TransformerFactoryConfigurationError, TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File(filename + "_parsed.cadmd"));
		Source input = new DOMSource(doc);

		transformer.transform(input, output);

	}

	private CADContainer load() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(CADPackage.eNS_URI, CADPackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource resource = rs.getResource(URI.createFileURI(new File(filename + ".xmi").getAbsolutePath()), true);
		return (CADContainer) resource.getContents().get(0);
	}

	private <T> void addList(Element parent, EList<T> entries, String listname, Function<T, Element> factory) {
		Element container = doc.createElement(listname);
		parent.appendChild(container);
		entries.stream().map(factory).forEach(container::appendChild);
	}

	private Element generateCadPrimitive(CADPrimitive primitive) {
		Element item = doc.createElement("Primitive");
		fillCadEntry(item, primitive);
		item.setAttribute("DomainId", primitive.getDomainId());
		item.setAttribute("DomainInstance", primitive.getDomainInstance());

		addList(item, primitive.getPorts(), "Ports", this::generateCadPrimitivePort);
		addList(item, primitive.getParameters(), "Parameters", this::generateCadPrimitiveParameter);
		if (primitive.getComment() != null)
			item.appendChild(generateCadPrimitiveComment(primitive.getComment()));

		return item;
	}

	private Element generateCadConnection(CADConnection connection) {
		Element item = doc.createElement("Connection");

		int fpr = ((CADPrimitive) connection.getFirstPort().eContainer()).getUId();
		int fpo = connection.getFirstPort().getUId();
		int spr = ((CADPrimitive) connection.getSecondPort().eContainer()).getUId();
		int spo = connection.getSecondPort().getUId();
		item.setAttribute("FirstPrimitive", String.valueOf(fpr));
		item.setAttribute("FirstPort", String.valueOf(fpo));
		item.setAttribute("SecondPrimitive", String.valueOf(spr));
		item.setAttribute("SecondPort", String.valueOf(spo));
		System.out.println(((CADPrimitive) connection.getFirstPort().eContainer()).getUId() + " "
				+ connection.getFirstPort().getUId() + " "
				+ ((CADPrimitive) connection.getSecondPort().eContainer()).getUId() + " "
				+ connection.getSecondPort().getUId());

		return item;
	}

	private Element generateCadPrimitivePort(CADPrimitivePort port) {
		Element item = doc.createElement("Port");
		fillCadEntry(item, port);
		item.setAttribute("XPosition", String.valueOf(port.getXPosition()));
		item.setAttribute("YPosition", String.valueOf(port.getYPosition()));

		return item;
	}

	private Element generateCadPrimitiveParameter(CADPrimitiveParameter parameter) {
		Element item = doc.createElement("Parameter");
		fillCadEntry(item, parameter);
		item.setAttribute("Unit", parameter.getUnit());
		Element value = doc.createElement("Value");
		value.setTextContent(parameter.getValue().getValue());
		item.appendChild(value);

		return item;
	}

	private Element generateCadPrimitiveComment(CADPrimitiveComment comment) {
		Element item = doc.createElement("Comment");

		return item;
	}

	private void fillCadEntry(Element item, CADEntry entry) {
		item.setAttribute("Id", entry.getId());
		item.setAttribute("Title", entry.getTitle());
		item.setAttribute("UId", String.valueOf(entry.getUId()));
	}

}
