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

public abstract class RPSTVisitor extends RPST<DirectedEdge, Vertex> {
	DirectedGraph graph;
	String processString;

	public RPSTVisitor(DirectedGraph graph) {
		super(graph);
		this.graph = graph;
		this.processString = "";
	}

	public void traverseRPST() {
		CustomIRPSTNode customRoot = new CustomIRPSTNode(root, 0);
		traverseFromNode(customRoot);
	}

	public String getProcessString() {
		return this.processString;
	}

	public void traverseFromNode(CustomIRPSTNode rootnode) {
		String levelTab = StringUtils.repeat("", rootnode.getTreeLevel());

		List<CustomIRPSTNode> children = getSortedChildren(rootnode);
		String rootnodeStr = printNode(rootnode);
		if (rootnodeStr.length() > 0) {
			this.processString = String.format("%s%s\n", processString, rootnodeStr);
		}

		boolean hasCondition = false;
		if (rootnode.getWorkflowType().equals(WorkflowType.LOOP)) {
			this.processString = String.format("%s%s\n", processString, levelTab + "LOOP() {");
			hasCondition = true;
		} else if (rootnode.getWorkflowType().equals(WorkflowType.SEQUENCE)
				&& rootnode.isCondition()) {

			String conditionalStatement = (rootnode.getIndex() == 0) ? "IF ()?" : "ELSE IF () {";			
			this.processString = String.format("%s%s\n", processString, levelTab + conditionalStatement);
			hasCondition = true;
		}

		
		if (rootnode.isConditional()) {
			// sort children by type inside a conditional SEQUENCE -> EDGE
			rootnode.sortChildrenForConditional();
			children = rootnode.getChildren();			
		}

		int index = 0;
		for (CustomIRPSTNode child : children) {
			boolean isTrivial = child.getType().equals(TCType.TRIVIAL);
			if (isTrivial) {
				
				child.setWorkflowType(WorkflowType.EDGE);
				String nodeStr = printNode(child);
				
				if (nodeStr.length() > 0) {
					this.processString = String.format("%s%s\n", processString,
						nodeStr);
				}
				continue;
			}
			child.setIndex(index);
			index++;
			traverseFromNode(child);
		}

		if (hasCondition) {
			this.processString = String.format("%s%s\n", processString, levelTab + "}");
		}

	}

	protected abstract String printNode(CustomIRPSTNode node);

	private List<CustomIRPSTNode> getSortedChildren(CustomIRPSTNode parentNode) {
		Set<IRPSTNode<DirectedEdge, Vertex>> childrenList = getChildren(parentNode);
		List<IRPSTNode<DirectedEdge, Vertex>> children = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>(
				childrenList);

		// find first and last nodes
		CustomIRPSTNode customFirstNode = null;
		CustomIRPSTNode customLastNode = null;

		List<IRPSTNode<DirectedEdge, Vertex>> childrenToRemove = new ArrayList<IRPSTNode<DirectedEdge, Vertex>>();

		for (IRPSTNode<DirectedEdge, Vertex> child : children) {
			if (child.getEntry().equals(parentNode.getEntry())) {
				customFirstNode = new CustomIRPSTNode(child,
						parentNode.getTreeLevel() + 1);
				childrenToRemove.add(child);
			}

			if (child.getExit().equals(parentNode.getExit())) {
				customLastNode = new CustomIRPSTNode(child,
						parentNode.getTreeLevel() + 1);
				childrenToRemove.add(child);
			}
		}
		children.removeAll(childrenToRemove);

		if (children.size() == 0) {
			// Verify if all nodes had the same entry and exit
			boolean isConditional = customFirstNode.getEntry().equals(
					customLastNode.getEntry())
					&& customFirstNode.getExit().equals(
							customLastNode.getExit());

			// Assign an workflow type to the parent node
			parentNode.setWorkflowType(WorkflowType.SEQUENCE);
			if (isConditional) {
				parentNode.setWorkflowType(WorkflowType.CONDITIONAL);
			}

			for (IRPSTNode<DirectedEdge, Vertex> node : childrenList) {
				parentNode
						.addChildAtIndex(
								new CustomIRPSTNode(node, parentNode
										.getTreeLevel() + 1), 0);
			}

		} else {
			parentNode.addChild(customFirstNode);
			parentNode.addChild(customLastNode);

			IRPSTNode<DirectedEdge, Vertex> lastChild = null;
			while (children.size() > 0) {
				IRPSTNode<DirectedEdge, Vertex> child = children.remove(0);
				boolean added = false;

				if (child == lastChild) {
					System.out.println("while: " + lastChild);
					parentNode.addChild(new CustomIRPSTNode(child, parentNode
							.getTreeLevel() + 1));
					continue;
				}

				for (int i = 0; i < parentNode.getChildren().size(); i++) {
					IRPSTNode<DirectedEdge, Vertex> node = parentNode
							.getChildren().get(i);
					int index = -2;

					if (child.getEntry().equals(node.getExit())) {
						index = i + 1;
					}

					if (child.getExit().equals(node.getEntry())) {
						index = i;
					}

					if (index != -2) {
						CustomIRPSTNode customNode = new CustomIRPSTNode(child,
								parentNode.getTreeLevel() + 1);
						parentNode.addChildAtIndex(customNode, index);
						added = true;
						i = parentNode.getChildren().size();
					}
				}

				lastChild = child;
				if (!added) {
					children.add(child);
				}
			}

			parentNode.setWorkflowType(WorkflowType.SEQUENCE);
			if (customFirstNode.getIRPSTNode().equals(
					customLastNode.getIRPSTNode())) {
				parentNode.setWorkflowType(WorkflowType.LOOP);
				parentNode.removeChild(customLastNode);
			}

		}

		return parentNode.getChildren();
	}

	public Set<IRPSTNode<DirectedEdge, Vertex>> getChildren(CustomIRPSTNode node) {
		return getChildren(node.getIRPSTNode());
	}

}
