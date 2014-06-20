package br.ufrj.cos.prisma;

import java.util.ArrayList;
import java.util.Collection;
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
		CustomIRPSTNode customRoot = new CustomIRPSTNode(root);
		traverseFromNode(customRoot, 0);
	}

	public void traverseFromNode(CustomIRPSTNode rootnode, int level) {

		Collection<IRPSTNode<DirectedEdge, Vertex>> children = getSortedChildren(rootnode);
		WorkflowType type = WorkflowType.SEQUENCE;

		// conditional
		if (children == null) {
			children = getChildren(rootnode);
			type = WorkflowType.CONDITIONAL;

		} else {
			IRPSTNode<DirectedEdge, Vertex> first = ((List<IRPSTNode<DirectedEdge, Vertex>>) children)
					.get(0);
			IRPSTNode<DirectedEdge, Vertex> last = ((List<IRPSTNode<DirectedEdge, Vertex>>) children)
					.get(children.size() - 1);

			if (first.equals(last)) {
				type = WorkflowType.LOOP;
				children.remove(last);
			}
		}

		rootnode.setWorkflowType(type);
		printNode(level, rootnode);

		for (IRPSTNode<DirectedEdge, Vertex> child : children) {
			int childLevel = level + 1;
			boolean isTrivial = child.getType().equals(TCType.TRIVIAL);
			CustomIRPSTNode childCustomNode = new CustomIRPSTNode(child);

			if (isTrivial) {
				childCustomNode.setWorkflowType(WorkflowType.EDGE);
				printNode(childLevel, childCustomNode);
				continue;
			}

			traverseFromNode(childCustomNode, childLevel);
		}
	}

	private static void printNode(int level, CustomIRPSTNode node) {
		String format = "%s [%s] %s: (Entry,Exit) -> (%s,%s) - F %s";
		String levelTab = StringUtils.repeat("\t", level);
		String workflowType = node.getWorkflowType() != null ? node
				.getWorkflowType().toString() : "";
		System.out.println(String.format(format, levelTab, workflowType,
				node.getName(), node.getEntry(), node.getExit(),
				node.getFragment()));
	}

	private List<IRPSTNode<DirectedEdge, Vertex>> getSortedChildren(
			CustomIRPSTNode parentNode) {
		Vertex parentEntryVertex = parentNode.getEntry();
		Vertex parentExitVertex = parentNode.getExit();

		Set<IRPSTNode<DirectedEdge, Vertex>> childrenList = getChildren(parentNode);
		List<IRPSTNode<DirectedEdge, Vertex>> children = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>(
				childrenList);

		// find first and last nodes
		IRPSTNode<DirectedEdge, Vertex> firstNode = null;
		IRPSTNode<DirectedEdge, Vertex> lastNode = null;

		List<IRPSTNode<DirectedEdge, Vertex>> childrenToRemove = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>();
		for (IRPSTNode<DirectedEdge, Vertex> child : children) {
			if (child.getEntry().equals(parentEntryVertex)) {
				firstNode = child;
				childrenToRemove.add(child);
			}

			if (child.getExit().equals(parentExitVertex)) {
				lastNode = child;
				childrenToRemove.add(child);
			}
		}

		children.removeAll(childrenToRemove);
		// all nodes have same entry and exit vertexes (conditional)

		if (children.size() == 0) {

			// verify if all nodes had the same entry and exit
			boolean isConditional = firstNode.getEntry().equals(
					lastNode.getEntry())
					&& firstNode.getExit().equals(lastNode.getExit());

			if (isConditional) {
				return null;
			}

			return new ArrayList<IRPSTNode<DirectedEdge, Vertex>>(childrenList);
		}

		List<IRPSTNode<DirectedEdge, Vertex>> sortedChildren = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>();
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

		return sortedChildren;
	}

	public Set<IRPSTNode<DirectedEdge, Vertex>> getChildren(CustomIRPSTNode node) {
		return getChildren(node.getIRPSTNode());
	}
}
