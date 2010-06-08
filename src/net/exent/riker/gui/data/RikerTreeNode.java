/*
 *  The MIT License
 * 
 *  Copyright 2010 Vidar Wahlberg <canidae@exent.net>.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package net.exent.riker.gui.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Interface for a node in our tree.
 */
public abstract class RikerTreeNode implements MutableTreeNode {
	/**
	 * Parent of this node.
	 */
	private MutableTreeNode parent;
	/**
	 * Children of this node.
	 */
	private List<MutableTreeNode> children = new ArrayList<MutableTreeNode>();
	/**
	 * The user object associated with this node.
	 */
	private Object userObject;

	@Override
	public void insert(MutableTreeNode child, int index) {
		children.add(index, child);
	}

	@Override
	public void remove(int index) {
		children.remove(index);
	}

	@Override
	public void remove(MutableTreeNode child) {
		children.remove(child);
	}

	@Override
	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	@Override
	public void removeFromParent() {
		if (parent != null)
			parent.remove(this);
	}

	@Override
	public void setParent(MutableTreeNode parent) {
		this.parent = parent;
	}

	@Override
	public TreeNode getChildAt(int index) {
		return children.get(index);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public int getIndex(TreeNode child) {
		return children.indexOf(child);
	}

	@Override
	public boolean isLeaf() {
		return children.size() == 0;
	}

	@Override
	public Enumeration children() {
		return Collections.enumeration(children);
	}

	/**
	 * Get the user Object associated with this node.
	 * @return the user object associated with this node
	 */
	public Object getUserObject() {
		return userObject;
	}
}
