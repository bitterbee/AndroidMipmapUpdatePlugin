package com.netease.tools.ui.node;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class WrapNode<T extends Data> {
    protected DefaultMutableTreeNode node;
    protected DataNode<T> data;

    protected WrapNode parent;
    protected List<WrapNode> children = new ArrayList<WrapNode>();

    public List<WrapNode> children() {
        return children;
    }

    public DataNode<T> data() {
        return data;
    }

    public WrapNode parent() {
        return parent;
    }

    public WrapNode add(WrapNode dataNode) {
        children.add(dataNode);
        dataNode.parent = this;

        this.node.add(dataNode.node);
        this.data.add(dataNode.data);

        return this;
    }

    public WrapNode remove(WrapNode dataNode) {
        if (children.contains(dataNode)) {
            children.remove(dataNode);
            data.remove(dataNode.data);

            dataNode.parent = null;
            dataNode.node.removeFromParent();
        }
        return this;
    }

    public WrapNode clear() {
        for (WrapNode child : children) {
            child.parent = null;
            data.remove(child.data);
            child.node.removeFromParent();
        }
        children.clear();
        return this;
    }
}
