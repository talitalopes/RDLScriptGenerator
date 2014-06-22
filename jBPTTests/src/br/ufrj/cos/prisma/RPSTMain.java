package br.ufrj.cos.prisma;

import org.jbpt.graph.DirectedGraph;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;

public class RPSTMain {

	public static void main(String[] args) {
//		ProcessModel p = createTestProcess();

		XPDLParser xpdlModel = new XPDLParser("input/gef5.xpdl");
		DirectedGraph graph = xpdlModel.getGraph();
		
		System.out.println(">>>> RPST tree \n");
		SortedRPST rpst = new SortedRPST(graph);
		rpst.traverseRPST();
	}

	public static DirectedGraph getGraph(ProcessModel p) {
		DirectedGraph graph = new DirectedGraph();

		for (ControlFlow<FlowNode> cf : p.getEdges()) {
			graph.addEdge(cf.getSource(), cf.getTarget());
		}

		return graph;
	}

	public static ProcessModel createTestProcess() {
		// Create the process graph
		ProcessModel p = new ProcessModel();

		// Create the tasks
		Activity a1 = new Activity("1");
		Activity a3 = new Activity("3");
		Activity a4 = new Activity("4");
		Activity a8 = new Activity("8");
		Activity a9 = new Activity("9");

		// Create gateways
		XorGateway s2 = new XorGateway("2");
		XorGateway s6 = new XorGateway("6");
		XorGateway j7 = new XorGateway("7");
		XorGateway j5 = new XorGateway("5");

		// Add control flow edges
		p.addControlFlow(a1, s2);
		p.addControlFlow(s2, a3);
		p.addControlFlow(s2, s6);
		p.addControlFlow(s2, j5);
		p.addControlFlow(a3, a4);
		p.addControlFlow(a4, j5);
		p.addControlFlow(j7, s6);
		p.addControlFlow(s6, a8);
		p.addControlFlow(a8, j7);
		p.addControlFlow(j7, j5);
		p.addControlFlow(j5, a9);

		return p;
	}
	
}
