package com.netease.tools.ui.select;

import operation.ImgOperation;

/**
 * Created by zyl06 on 2018/7/21.
 */
public class ImgOperationNodeData {
    public ImgOperation operation;
    public boolean selected;

    public ImgOperationNodeData(ImgOperation operation, boolean selected) {
        this.operation = operation;
        this.selected = selected;
    }

    @Override
    public String toString() {
        return operation.toSimpleString();
    }
}
