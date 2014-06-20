package br.ufrj.cos.prisma.model;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.abs.IFragment;
import org.jbpt.hypergraph.abs.IGObject;
import org.jbpt.hypergraph.abs.Vertex;

import br.ufrj.cos.prisma.WorkflowType;

public class CustomIRPSTNode extends Vertex implements IRPSTNode<DirectedEdge, Vertex> {
	IRPSTNode<DirectedEdge, Vertex> node;
	WorkflowType workflowType;
	
	public CustomIRPSTNode(IRPSTNode<DirectedEdge, Vertex> node) {
		this.node = node;
	}
	
	public IRPSTNode<DirectedEdge, Vertex> getIRPSTNode() {
		return node;
	}
	
	@Override
	public int getX() {
		return this.node.getX();
	}

	@Override
	public void setX(int x) {
		this.node.setX(x);
	}

	@Override
	public int getY() {
		return this.node.getY();
	}

	@Override
	public void setY(int y) {
		this.node.setY(y);
	}

	@Override
	public int getWidth() {
		return this.node.getWidth();
	}

	@Override
	public void setWidth(int w) {
		this.node.setWidth(w);
	}

	@Override
	public int getHeight() {
		return this.node.getHeight();
	}

	@Override
	public void setHeight(int h) {
		this.node.setHeight(h);
	}

	@Override
	public void setLocation(int x, int y) {
		this.node.setLocation(x, y);
	}

	@Override
	public void setSize(int w, int h) {
		this.node.setSize(w, h);
	}

	@Override
	public void setLayout(int x, int y, int w, int h) {
		this.node.setLayout(x, y, w, h);
	}

	@Override
	public String getId() {
		return this.node.getId();
	}

	@Override
	public void setId(String id) {
		this.node.setId(id);
	}

	@Override
	public Object getTag() {
		return this.node.getTag();
	}

	@Override
	public void setTag(Object tag) {
		this.node.setTag(tag);
	}

	@Override
	public String getName() {
		return this.node.getName();
	}

	@Override
	public void setName(String name) {
		this.node.setName(name);
	}

	@Override
	public String getDescription() {
		return this.node.getDescription();
	}

	@Override
	public void setDescription(String desc) {
		this.node.setDescription(desc);
	}

	@Override
	public int compareTo(IGObject arg0) {
		return this.node.compareTo(arg0);
	}

	@Override
	public String getLabel() {
		return this.node.getLabel();
	}

	@Override
	public TCType getType() {
		return this.node.getType();
	}

	@Override
	public Vertex getEntry() {
		return this.node.getEntry();
	}

	@Override
	public Vertex getExit() {
		return this.node.getExit();
	}

	@Override
	public IFragment<DirectedEdge, Vertex> getFragment() {
		return this.node.getFragment();
	}

	public WorkflowType getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(WorkflowType workflowType) {
		this.workflowType = workflowType;
	}

	
}