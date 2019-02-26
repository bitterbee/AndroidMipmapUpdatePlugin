package com.netease.tools.ui.node;

import com.netease.tools.operation.ImgOperation;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class ImgOperationNode extends WrapNode<ImgOperationData> {

    public ImgOperationNode(ImgOperation op, boolean selected) {
        super();
        data = new DataNode<ImgOperationData>(new ImgOperationData(op, selected));
        node = new DefaultMutableTreeNode(data);
    }

    public DataNode<ImgOperationData> data() {
        return data;
    }

    public DefaultMutableTreeNode node() {
        return node;
    }
}
