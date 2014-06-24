package br.ufrj.cos.prisma;

import org.jbpt.graph.DirectedGraph;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;

public class RPSTMain {

	public static void main(String[] args) {
		boolean test = false;
		ProcessModel p = testCompleteModel();

		XPDLModel xpdlModel = new XPDLModel("input/graphiti-10apps-ordered-filtered.xpdl");
		
		DirectedGraph graph = xpdlModel.getGraph();
		if (test) {
			graph = getGraph(p);
		}
		
		
		String process1 = visitProcessModel(graph);
		System.out.println(process1);
		
	}

	private static String visitProcessModel(DirectedGraph graph) {
		System.out.println(">>>> RPST tree \n");
		RDLVisitor rpst = new RDLVisitor(graph);
		rpst.traverseRPST();
		return rpst.getProcessString();
	}
	
	public static DirectedGraph getGraph(ProcessModel p) {
		DirectedGraph graph = new DirectedGraph();

		for (ControlFlow<FlowNode> cf : p.getEdges()) {
			graph.addEdge(cf.getSource(), cf.getTarget());
		}

		return graph;
	}

	public static ProcessModel testSegmentation() {
		ProcessModel p = new ProcessModel();
		
		// Create the tasks
		Activity start = new Activity("Start");
		Activity a0 = new Activity("0");
		Activity b1 = new Activity("b1");
		Activity a11 = new Activity("11");
		Activity a12 = new Activity("12");
		Activity a14 = new Activity("14");
		Activity a15 = new Activity("15");
		Activity a16 = new Activity("16");
		Activity end = new Activity("End");
		
		// Create gateways
		XorGateway s9 = new XorGateway("9");
		XorGateway s10 = new XorGateway("10");
		XorGateway s13 = new XorGateway("13");
		XorGateway s17 = new XorGateway("17");
		
		p.addControlFlow(start, a0);
		p.addControlFlow(a0, s9);
		p.addControlFlow(s9, b1);
		p.addControlFlow(s9, s17);
		p.addControlFlow(b1, s10);
		p.addControlFlow(s10, a11);
		p.addControlFlow(s10, s17);
		p.addControlFlow(a11, a12);
		p.addControlFlow(a12, s13);
		p.addControlFlow(s13, a14);
		p.addControlFlow(a14, a15);
		p.addControlFlow(a15, a16);
		p.addControlFlow(a16, s17);
		p.addControlFlow(s13, s17);
		p.addControlFlow(s17, end);
		
		return p;
	}
	
	public static ProcessModel testLoops() {
		ProcessModel p = new ProcessModel();

		// Create the tasks
		Activity la1 = new Activity("l1");
		Activity la3 = new Activity("l3");
		Activity la6 = new Activity("l6");
		Activity la8 = new Activity("l8");

		// Create gateways
		XorGateway ls2 = new XorGateway("l2");
		XorGateway ls4 = new XorGateway("l4");
		XorGateway ls5 = new XorGateway("l5");
		XorGateway ls7 = new XorGateway("l7");

		// Add control flow edges
		p.addControlFlow(la1, ls2);
		p.addControlFlow(ls2, la3);
		p.addControlFlow(la3, ls4);
		p.addControlFlow(ls4, ls2);
		p.addControlFlow(ls4, ls5);
		p.addControlFlow(ls5, la6);
		p.addControlFlow(la6, ls7);
		p.addControlFlow(ls7, ls5);
		p.addControlFlow(ls7, la8);

		return p;
	}
	
	public static ProcessModel testCompleteModel() {
		ProcessModel p = new ProcessModel();
		
		// Create the tasks
		Activity start = new Activity("Start");
		Activity a0 = new Activity("0");
		Activity a11 = new Activity("11");
		Activity a12 = new Activity("12");
		Activity a14 = new Activity("14");
		Activity a15 = new Activity("15");
		Activity a16 = new Activity("16");
		Activity end = new Activity("End");
		
		// Create gateways
		XorGateway s9 = new XorGateway("9");
		XorGateway s10 = new XorGateway("10");
		XorGateway s13 = new XorGateway("13");
		XorGateway s17 = new XorGateway("17");
		
		p.addControlFlow(start, a0);
		p.addControlFlow(a0, s9);
//		p.addControlFlow(s9, b1);
		p.addControlFlow(s9, s17);
//		p.addControlFlow(b1, s10);
		p.addControlFlow(s10, a11);
		p.addControlFlow(s10, s17);
		p.addControlFlow(a11, a12);
		p.addControlFlow(a12, s13);
		p.addControlFlow(s13, a14);
		p.addControlFlow(a14, a15);
		p.addControlFlow(a15, a16);
		p.addControlFlow(a16, s17);
		p.addControlFlow(s13, s17);
		p.addControlFlow(s17, end);
		
		// Create the tasks
		Activity la1 = new Activity("l1");
		Activity la3 = new Activity("l3");
		Activity la6 = new Activity("l6");
		Activity la8 = new Activity("l8");

		// Create gateways
		XorGateway ls2 = new XorGateway("l2");
		XorGateway ls4 = new XorGateway("l4");
		XorGateway ls5 = new XorGateway("l5");
		XorGateway ls7 = new XorGateway("l7");

		// Add control flow edges
		p.addControlFlow(la1, ls2);
		p.addControlFlow(ls2, la3);
		p.addControlFlow(la3, ls4);
		p.addControlFlow(ls4, ls2);
		p.addControlFlow(ls4, ls5);
		p.addControlFlow(ls5, la6);
		p.addControlFlow(la6, ls7);
		p.addControlFlow(ls7, ls5);
		p.addControlFlow(ls7, la8);
		
		p.addControlFlow(s9, la1);
		p.addControlFlow(la8, s10);
		
		return p;
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
