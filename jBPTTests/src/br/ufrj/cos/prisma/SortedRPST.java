package br.ufrj.cos.prisma;

import java.util.Set;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

import br.ufrj.cos.prisma.utils.StringUtils;


public class SortedRPST extends RPST<DirectedEdge, Vertex> {

	public SortedRPST(DirectedGraph graph) {
		super(graph);
	}

	public void traverseRPST() {
		traverseFromNode(root, 0);
	}
	
	public void traverseFromNode(IRPSTNode<DirectedEdge, Vertex> rootnode, int level) {
		Set<IRPSTNode<DirectedEdge, Vertex>> children = getChildren(rootnode);
		printNode(level, rootnode);

		for (IRPSTNode<DirectedEdge, Vertex> child : children) {
			int childLevel = level + 1;
			boolean isTrivial = child.getType().equals(TCType.TRIVIAL);
			if (isTrivial) {
				printNode(childLevel, child);
				continue;
			}

			traverseFromNode(child, childLevel);
		}
	}
	
	private static void printNode(int level,
			IRPSTNode<DirectedEdge, Vertex> node) {
		String format = "%s [%s] %s: (Entry,Exit) -> (%s,%s) - F %s";
		String levelTab = StringUtils.repeat("\t", level);
		System.out.println(String.format(format, levelTab, node.getType()
				.toString(), node.getName(), node.getEntry(), node.getExit(),
				node.getFragment()));
	}
}
