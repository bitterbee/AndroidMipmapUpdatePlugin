package com.netease.tools.ui.select;

/**
 * Created by zyl06 on 2018/7/21.
 */
public class CategoryNodeData {
    public String name;
    public boolean selected;

    public CategoryNodeData(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    @Override
    public String toString() {
        return name;
    }
}