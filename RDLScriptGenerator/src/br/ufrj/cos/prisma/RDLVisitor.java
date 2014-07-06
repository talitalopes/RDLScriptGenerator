package br.ufrj.cos.prisma;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

import br.ufrj.cos.prisma.model.CustomIRPSTNode;
import br.ufrj.cos.prisma.utils.StringUtils;

public class RDLVisitor extends RPSTVisitor {
	Set<Vertex> visitedVertexes;
	private boolean DEBUG = true;

	public RDLVisitor(DirectedGraph graph) {
		super(graph);
		visitedVertexes = new HashSet<Vertex>();
	}

	@Override
	protected String printNode(CustomIRPSTNode node) {
		String levelTab = StringUtils.repeat("\t", node.getTreeLevel());

		if (!DEBUG) {
			if (node.getWorkflowType().equals(WorkflowType.SEQUENCE)
					|| node.getWorkflowType().equals(WorkflowType.CONDITIONAL)) {
				return "";
			}

			if (node.getWorkflowType().equals(WorkflowType.LOOP)) {
				return "";
			}

			if (node.getWorkflowType().equals(WorkflowType.EDGE)) {
				String message = "";

				if (!isVisited(node.getEntry())) {
					if (!node.getEntry().getName().contains("GATEWAY")) {
						message = levelTab + node.getEntry().getName();
					}
					visitedVertexes.add(node.getEntry());
				}

				if (!isVisited(node.getExit())) {
					if (!node.getExit().getName().contains("GATEWAY")) {
						message = levelTab + node.getExit().getName();
					}
					visitedVertexes.add(node.getExit());
				}

				return message;
			}
		}
		String format = "%s [%s] (%s,%s)";

		String workflowType = node.getWorkflowType() != null ? node
				.getWorkflowType().toString() : "";

		String message = String.format(format, levelTab, workflowType,
				node.getEntry(), node.getExit());

		System.out.println(message);

		return message;
	}

	public boolean isVisited(Vertex v) {
		return visitedVertexes.contains(v);
	}
}
