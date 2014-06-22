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

public abstract class RPSTVisitor extends RPST<DirectedEdge, Vertex> {
	DirectedGraph graph;

	public RPSTVisitor(DirectedGraph graph) {
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

		// sort children by type inside a conditional SEQUENCE -> EDGE
		if (rootnode.isConditional()) {
			List<CustomIRPSTNode> sortedChildren = new ArrayList<CustomIRPSTNode>();
			for (CustomIRPSTNode child : children) {
				boolean isTrivial = child.getType().equals(TCType.TRIVIAL);
				if (isTrivial) {
					sortedChildren.add(child);
				} else {
					sortedChildren.add(0, child);
				}
			}
			children = sortedChildren;
		}

		for (CustomIRPSTNode child : children) {
			// if (rootnode.getWorkflowType().equals(WorkflowType.COMPLEX)) {
			// System.out.println("complex: " + getChildren(rootnode).size());
			// printNode(child);
			// continue;
			// }

			boolean isTrivial = child.getType().equals(TCType.TRIVIAL);
			if (isTrivial) {
				child.setWorkflowType(WorkflowType.EDGE);
				printNode(child);
				continue;
			}
			traverseFromNode(child);
		}
	}

	protected abstract void printNode(CustomIRPSTNode node);

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

			while (children.size() > 0) {
				IRPSTNode<DirectedEdge, Vertex> child = children.remove(0);
				boolean added = false;
				
				for (int i = 0; i < sortedChildren.size(); i++) {
					IRPSTNode<DirectedEdge, Vertex> node = sortedChildren.get(i);
					int index = -2;
					
					if (child.getEntry().equals(node.getExit())) {
						index = i + 1;
					}
					
					if (child.getExit().equals(node.getEntry())) {
						index = i;
					}
										
					if (index != -2) {
						sortedChildren.add(index, child);
						added = true;
						i = sortedChildren.size();
					}
					
				}
				
				if (!added) {
					children.add(child);
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
