package com.netease.tools.ui.node;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class CategoryNode extends WrapNode {

    public CategoryNode(DefaultMutableTreeNode node) {
        this.node = node;
        this.data = new DataNode<CategoryData>(null);
    }

    public CategoryNode(String name, boolean selected) {
        data = new DataNode<CategoryData>(new CategoryData(name, selected));
        node = new DefaultMutableTreeNode(data);
    }

    public DataNode<CategoryData> data() {
        return data;
    }

    public DefaultMutableTreeNode node() {
        return node;
    }
}
