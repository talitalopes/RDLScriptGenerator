package br.ufrj.cos.prisma;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

import br.ufrj.cos.prisma.model.CustomIRPSTNode;
import br.ufrj.cos.prisma.utils.StringUtils;

public class SortedRPST extends RPST<DirectedEdge, Vertex> {
	DirectedGraph graph;

	public SortedRPST(DirectedGraph graph) {
		super(graph);
		this.graph = graph;
	}

	public void traverseRPST() {
		CustomIRPSTNode customRoot = new CustomIRPSTNode(root, 0);
		traverseFromNode(customRoot);
	}

	public void traverseFromNode(CustomIRPSTNode rootnode) {
		List<CustomIRPSTNode> children = getSortedChildren(rootnode);
		printNode(rootnode);

		for (CustomIRPSTNode child : children) {
			boolean isTrivial = child.getType().equals(TCType.TRIVIAL);
			if (isTrivial) {
				child.setWorkflowType(WorkflowType.EDGE);
				printNode(child);
				continue;
			}
			traverseFromNode(child);
		}
	}

	private static void printNode(CustomIRPSTNode node) {
		String levelTab = StringUtils.repeat("\t", node.getTreeLevel());
		String format = "%s [%d %s] %s: (Entry,Exit) -> (%s,%s) - F %s";

		String workflowType = node.getWorkflowType() != null ? node
				.getWorkflowType().toString() : "";

		System.out.println(String.format(format, levelTab, node.getIndex(), workflowType,
				node.getName(), node.getEntry(), node.getExit(),
				node.getFragment()));
	}

	private List<CustomIRPSTNode> getSortedChildren(CustomIRPSTNode parentNode) {
		Set<IRPSTNode<DirectedEdge, Vertex>> childrenList = getChildren(parentNode);
		List<IRPSTNode<DirectedEdge, Vertex>> children = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>(
				childrenList);

		// find first and last nodes
		IRPSTNode<DirectedEdge, Vertex> firstNode = null;
		IRPSTNode<DirectedEdge, Vertex> lastNode = null;

		List<IRPSTNode<DirectedEdge, Vertex>> childrenToRemove = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>();
		for (IRPSTNode<DirectedEdge, Vertex> child : children) {
			if (child.getEntry().equals(parentNode.getEntry())) {
				firstNode = child;
				childrenToRemove.add(child);
			}

			if (child.getExit().equals(parentNode.getExit())) {
				lastNode = child;
				childrenToRemove.add(child);
			}
		}
		children.removeAll(childrenToRemove);

		List<IRPSTNode<DirectedEdge, Vertex>> sortedChildren = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>();
		if (children.size() == 0) {
			// Verify if all nodes had the same entry and exit
			boolean isConditional = firstNode.getEntry().equals(
					lastNode.getEntry())
					&& firstNode.getExit().equals(lastNode.getExit());

			// Assign an workflow type to the parent node
			parentNode.setWorkflowType(WorkflowType.SEQUENCE);
			if (isConditional) {
				parentNode.setWorkflowType(WorkflowType.CONDITIONAL);
			}

			sortedChildren = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>(
					childrenList);

		} else {
			sortedChildren.add(firstNode);
			sortedChildren.add(lastNode);

			for (IRPSTNode<DirectedEdge, Vertex> child : children) {
				if (child.getEntry().equals(firstNode.getExit())) {
					sortedChildren.add(1, child);
					continue;
				}

				if (child.getExit().equals(lastNode.getEntry())) {
					sortedChildren.add(sortedChildren.size() - 2, child);
					continue;
				}
			}

			parentNode.setWorkflowType(WorkflowType.SEQUENCE);
			if (firstNode.equals(lastNode)) {
				parentNode.setWorkflowType(WorkflowType.LOOP);
				sortedChildren.remove(lastNode);
			}
		}

		return getCustomIRPSTNodes(parentNode, sortedChildren);
	}

	public Set<IRPSTNode<DirectedEdge, Vertex>> getChildren(CustomIRPSTNode node) {
		return getChildren(node.getIRPSTNode());
	}

	private List<CustomIRPSTNode> getCustomIRPSTNodes(
			CustomIRPSTNode parentNode,
			List<IRPSTNode<DirectedEdge, Vertex>> sortedChildren) {

		List<CustomIRPSTNode> customChildren = new ArrayList<CustomIRPSTNode>();
		for (int i = 0; i < sortedChildren.size(); i++) {
			CustomIRPSTNode cnode = new CustomIRPSTNode(sortedChildren.get(i),
					parentNode.getTreeLevel() + 1);
			cnode.setIndex(i);
			if (parentNode.isConditional()) {
				cnode.setCondition(true);
			}

			customChildren.add(cnode);			
		}

		return customChildren;
	}
}
