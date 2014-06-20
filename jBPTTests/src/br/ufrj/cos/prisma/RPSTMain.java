package br.ufrj.cos.prisma;

import java.util.Set;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;

public class RPSTMain {

	public static void main(String[] args) {
		ProcessModel p = createTestProcess();
		DirectedGraph graph = getGraph(p);

		RPST<DirectedEdge, Vertex> rpst = new RPST<DirectedEdge, Vertex>(graph);
		IRPSTNode<DirectedEdge, Vertex> root = rpst.getRoot();
		
		System.out.println(">>>> RPST tree \n");
		printRPST(rpst, root, 0);
	}

	private static DirectedGraph getGraph(ProcessModel p) {
		DirectedGraph graph = new DirectedGraph();

		for (ControlFlow<FlowNode> cf : p.getEdges()) {
			graph.addEdge(cf.getSource(), cf.getTarget());
		}

		return graph;
	}

	private static ProcessModel createTestProcess() {
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
		AndGateway s6 = new AndGateway("6");
		AndGateway j7 = new AndGateway("7");
		XorGateway j5 = new XorGateway("5");

		// Add control flow edges
		p.addControlFlow(a1, s2);
		p.addControlFlow(s2, a3);
		p.addControlFlow(s2, s6);
		p.addControlFlow(s2, j5);
		p.addControlFlow(a3, a4);
		p.addControlFlow(a4, j5);
		p.addControlFlow(s6, j7);
		p.addControlFlow(s6, a8);
		p.addControlFlow(a8, j7);
		p.addControlFlow(j7, j5);
		p.addControlFlow(j5, a9);

		return p;
	}

	private static void printRPST(RPST<DirectedEdge, Vertex> rpst,
			IRPSTNode<DirectedEdge, Vertex> rootnode, int level) {
		Set<IRPSTNode<DirectedEdge, Vertex>> children = rpst
				.getChildren(rootnode);
		printNode(level, rootnode);

		for (IRPSTNode<DirectedEdge, Vertex> child : children) {
			int childLevel = level + 1;
			boolean isTrivial = child.getType().equals(TCType.TRIVIAL);
			if (isTrivial) {
				printNode(childLevel, child);
				continue;
			}

			printRPST(rpst, child, childLevel);
		}
	}

	private static void printNode(int level,
			IRPSTNode<DirectedEdge, Vertex> node) {
		String format = "%s [%s] %s: (Entry,Exit) -> (%s,%s) - F %s";
		String levelTab = repeatString("\t", level);
		System.out.println(String.format(format, levelTab, node.getType()
				.toString(), node.getName(), node.getEntry(), node.getExit(),
				node.getFragment()));
	}
	
	private static String repeatString(String str, int count) {
		int i = 0;
		String finalStr = "";
		while (i < count) {
			finalStr = String.format("%s%s", finalStr, str);
			i++;
		}
		return finalStr;
	}
}
