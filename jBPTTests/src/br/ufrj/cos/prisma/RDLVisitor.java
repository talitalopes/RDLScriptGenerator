package br.ufrj.cos.prisma;

import org.jbpt.graph.DirectedGraph;

import br.ufrj.cos.prisma.model.CustomIRPSTNode;
import br.ufrj.cos.prisma.utils.StringUtils;

public class RDLVisitor extends RPSTVisitor {

	public RDLVisitor(DirectedGraph graph) {
		super(graph);
	}

	@Override
	protected void printNode(CustomIRPSTNode node) {
		String levelTab = StringUtils.repeat("\t", node.getTreeLevel());
		String format = "%s [%d %s] %s: (Entry,Exit) -> (%s,%s) - F %s";

		String workflowType = node.getWorkflowType() != null ? node
				.getWorkflowType().toString() : "";

		System.out.println(String.format(format, levelTab, node.getIndex(),
				workflowType, node.getName(), node.getEntry(), node.getExit(),
				node.getFragment()));
	}

}
