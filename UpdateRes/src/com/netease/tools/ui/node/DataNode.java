package com.netease.tools.ui.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class DataNode<T extends Data> {

    private T data;
    protected DataNode parent;
    protected List<DataNode> children = new ArrayList<DataNode>();

    public DataNode(T data) {
        this.data = data;
    }

    public T data() {
        return data;
    }

    public List<DataNode> children() {
        return children;
    }

    public DataNode parent() {
        return parent;
    }

    public DataNode add(DataNode node) {
        children.add(node);
        node.parent = this;

        return this;
    }

    public DataNode remove(DataNode node) {
        if (children.contains(node)) {
            children.remove(node);
            node.parent = null;
        }
        return this;
    }

    public DataNode clear() {
        for (DataNode child : children) {
            child.parent = null;
        }
        children.clear();
        return this;
    }

    public boolean isSelected() {
        return data != null && data.selected;
    }

    public void setSelected(boolean selected) {
        if (data != null) {
            data.selected = selected;
            for (DataNode child : children) {
                child.setSelected(selected);
            }
        }
    }
}
