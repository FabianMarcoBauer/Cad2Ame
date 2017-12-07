import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import CAD.CADConnection;
import CAD.CADContainer;
import CAD.CADEntry;
import CAD.CADFactory;
import CAD.CADPackage;
import CAD.CADPrimitive;
import CAD.CADPrimitiveComment;
import CAD.CADPrimitiveParameter;
import CAD.CADPrimitiveParameterValue;
import CAD.CADPrimitivePort;

public class XMLParser {

	public static void main(String[] args) throws Exception {
		XMLParser xmlParser = new XMLParser(args[0]);
		xmlParser.parse();
		xmlParser.save();
	}

	private String filename;
	private CADContainer cadContainer;
	private Map<Integer, Map<Integer, CADPrimitivePort>> ports = new HashMap<>();

	public XMLParser(String filename) {
		this.filename = filename;
	}

	public void parse() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(filename + ".cadmd"));

		cadContainer = CADFactory.eINSTANCE.createCADContainer();
		Element xmlContainer = (Element) doc.getChildNodes().item(0);
		cadContainer.setVersion(xmlContainer.getAttribute("Version"));

		addPrimitives(xmlContainer);
		addConnections(xmlContainer);
	}

	private void save() throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(CADPackage.eNS_URI, CADPackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource resource = rs.createResource(URI.createFileURI(new File(filename + "_parsed.xmi").getAbsolutePath()));
		resource.getContents().add(cadContainer);
		resource.save(null);
	}

	private Stream<Element> createElementStream(NodeList nodeList) {
		return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item).filter(Element.class::isInstance)
				.map(Element.class::cast);
	}

	private <T> void addNodesFromChildren(Element element, String children, EList<T> list, String filter,
			Function<Element, T> factory) {
		createElementStream(element.getElementsByTagName(children)).map(e -> e.getChildNodes())
				.forEach(nl -> addNodes(nl, list, filter, factory));
	}

	private <T> void addNodes(NodeList nodeList, EList<T> list, String filter, Function<Element, T> factory) {
		createElementStream(nodeList).filter(e -> filter.equals(e.getNodeName())).map(factory).forEach(list::add);
	}

	private void addConnections(Element element) {
		addNodesFromChildren(element, "Connections", cadContainer.getConnections(), "Connection",
				this::createCadConnection);
	}

	private void addPrimitives(Element element) {
		addNodesFromChildren(element, "Primitives", cadContainer.getPrimitives(), "Primitive",
				this::createCadPrimitive);
	}

	private void addPorts(Element element, CADPrimitive primitive) {
		addNodesFromChildren(element, "Ports", primitive.getPorts(), "Port", item -> {
			CADPrimitivePort port = this.createCadPrimitivePort(item);
			ports.get(primitive.getUId()).put(port.getUId(), port);
			return port;
		});
	}

	private void addParameters(Element element, CADPrimitive primitive) {
		addNodesFromChildren(element, "Parameters", primitive.getParameters(), "Parameter",
				this::createCadPrimitiveParameter);
	}

	private CADPrimitive createCadPrimitive(Element item) {
		CADPrimitive primitive = CADFactory.eINSTANCE.createCADPrimitive();
		fillCadEntry(primitive, item);
		primitive.setDomainId(item.getAttribute("DomainId"));
		primitive.setDomainInstance(item.getAttribute("DomainInstance"));

		ports.put(primitive.getUId(), new HashMap<>());
		addPorts(item, primitive);
		addParameters(item, primitive);
		primitive.setComment(createElementStream(item.getElementsByTagName("Comment")).findAny()
				.map(this::createCadPrimitiveComment)
				.orElseGet(CADFactory.eINSTANCE::createCADPrimitiveComment));

		return primitive;
	}

	private CADConnection createCadConnection(Element item) {
		CADConnection connection = CADFactory.eINSTANCE.createCADConnection();

		int fpr = Integer.parseInt(item.getAttribute("FirstPrimitive"));
		int fpo = Integer.parseInt(item.getAttribute("FirstPort"));
		int spr = Integer.parseInt(item.getAttribute("SecondPrimitive"));
		int spo = Integer.parseInt(item.getAttribute("SecondPort"));
		connection.setFirstPort(ports.get(fpr).get(fpo));
		connection.setSecondPort(ports.get(spr).get(spo));
		System.out.println(((CADPrimitive)connection.getFirstPort().eContainer()).getUId()+" "+connection.getFirstPort().getUId()+" "+((CADPrimitive)connection.getSecondPort().eContainer()).getUId()+" "+connection.getSecondPort().getUId());
		
		return connection;
	}

	private CADPrimitivePort createCadPrimitivePort(Element item) {
		CADPrimitivePort port = CADFactory.eINSTANCE.createCADPrimitivePort();
		fillCadEntry(port, item);

		port.setXPosition(Double.parseDouble(item.getAttribute("XPosition")));
		port.setYPosition(Double.parseDouble(item.getAttribute("YPosition")));

		return port;
	}

	private CADPrimitiveParameter createCadPrimitiveParameter(Element item) {
		CADPrimitiveParameter parameter = CADFactory.eINSTANCE.createCADPrimitiveParameter();
		fillCadEntry(parameter, item);

		parameter.setUnit(item.getAttribute("Unit"));
		parameter.setValue(createElementStream(item.getElementsByTagName("Value")).findAny()
				.map(this::createCadPrimitiveParameterValue)
				.orElseGet(CADFactory.eINSTANCE::createCADPrimitiveParameterValue));

		return parameter;
	}

	private CADPrimitiveParameterValue createCadPrimitiveParameterValue(Element element) {
		CADPrimitiveParameterValue value = CADFactory.eINSTANCE.createCADPrimitiveParameterValue();

		value.setValue(element.getTextContent());

		return value;
	}
	
	private CADPrimitiveComment createCadPrimitiveComment(Element element) {
		CADPrimitiveComment value = CADFactory.eINSTANCE.createCADPrimitiveComment();

		return value;
	}

	private void fillCadEntry(CADEntry entry, Element item) {
		entry.setId(item.getAttribute("Id"));
		entry.setTitle(item.getAttribute("Title"));
		entry.setUId(Integer.parseInt(item.getAttribute("UId")));
	}

}
