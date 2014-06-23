package br.ufrj.cos.prisma;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

import br.ufrj.cos.prisma.model.CustomIRPSTNode;
import br.ufrj.cos.prisma.utils.StringUtils;

public class RDLVisitor extends RPSTVisitor {
	Set<Vertex> visitedVertexes;

	public RDLVisitor(DirectedGraph graph) {
		super(graph);
		visitedVertexes = new HashSet<Vertex>();
	}

	@Override
	protected void printNode(CustomIRPSTNode node) {
		String levelTab = StringUtils.repeat("\t", node.getTreeLevel());

		if (node.getWorkflowType().equals(WorkflowType.SEQUENCE)
				|| node.getWorkflowType().equals(WorkflowType.CONDITIONAL)) {
			return;
		}

		if (node.getWorkflowType().equals(WorkflowType.LOOP)) {
			return;
		}

		if (node.getWorkflowType().equals(WorkflowType.EDGE)) {

			if (!isVisited(node.getEntry())) {
				if (!node.getEntry().getName().contains("GATEWAY")) {
					System.out.println(levelTab + node.getEntry().getName());
				}
				visitedVertexes.add(node.getEntry());
			}

			if (!isVisited(node.getExit())) {
				if (!node.getExit().getName().contains("GATEWAY")) {
					System.out.println(levelTab + node.getExit().getName());
				}
				visitedVertexes.add(node.getExit());
			}
			return;
		}

		String format = "%s [%b %s] %s: (Entry,Exit) -> (%s,%s) - F %s";

		String workflowType = node.getWorkflowType() != null ? node
				.getWorkflowType().toString() : "";

		System.out.println(String.format(format, levelTab, node.isCondition(),
				workflowType, node.getName(), node.getEntry(), node.getExit(),
				node.getFragment()));
	}

	public boolean isVisited(Vertex v) {
		return visitedVertexes.contains(v);
	}
}
