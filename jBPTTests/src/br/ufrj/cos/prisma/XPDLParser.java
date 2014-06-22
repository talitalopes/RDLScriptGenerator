package br.ufrj.cos.prisma;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jbpt.pm.Activity;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.ufrj.cos.prisma.utils.Constants;

public class XPDLParser {
	ProcessModel model;
	Document doc;
	Map<String,FlowNode> flowNodesMap;
	
	public XPDLParser(String file) {
		if (file == null) {
			System.out.println("Error: invalid file path");
			return;
		}
		flowNodesMap = new HashMap<String,FlowNode>();
		model = new ProcessModel();
		parseModelFromFile(file);
	}
	
	public Document getDocument() {
		return doc;
	}
	
	public ProcessModel getProcessModel() {
		return model;
	}
	
	private void parseModelFromFile(String file) {
		doc = getDomObject(file);
		createActivitiesAndGateways();
		createControlFlows();
		System.out.println(model);
	}
	
	private Document getDomObject(String file) {
		File fXmlFile = new File(file);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return doc;
	}
	
	private void createActivitiesAndGateways() {
		NodeList nodes = doc.getElementsByTagName(Constants.XPDL_ACTIVITY_TAG);

		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node node = nodes.item(temp);
			if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element element = (Element) node;
			String id = element.getAttribute(Constants.XPDL_ATTRIBUTE_ID);
			String name = element.getAttribute(Constants.XPDL_ATTRIBUTE_NAME);
			
			if (name.equals(Constants.EXCLUSIVE_GATEWAY)) {
				addXORGateway(id, name);
			} else {
				addProcessActivity(id, name);
			}
		}
	}
	
	private void addProcessActivity(String id, String name) {
		Activity activity = new Activity(name);
		String description = String.format("%s | %s", id, name);
		activity.setDescription(description);
		
		flowNodesMap.put(id, activity);
		model.addFlowNode(activity);
	}
	
	private void addXORGateway(String id, String name) {
		XorGateway gateway = new XorGateway(name);
		String description = String.format("%s | %s", id, name);
		gateway.setDescription(description);
		
		flowNodesMap.put(id, gateway);
		model.addFlowNode(gateway);
	}
	
	private void createControlFlows() {
		NodeList nodes = doc.getElementsByTagName(Constants.XPDL_TRANSITION_TAG);
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element element = (Element) node;
			String source = element.getAttribute(Constants.XPDL_ATTRIBUTE_SOURCE);
			String target = element.getAttribute(Constants.XPDL_ATTRIBUTE_TARGET);
			
			FlowNode sourceNode = flowNodesMap.get(source);
			FlowNode targetNode = flowNodesMap.get(target);
			
			model.addControlFlow(sourceNode, targetNode);
		}
	}
	
}
