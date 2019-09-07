package com.netease.tools.ui.node;

/**
 * Created by zyl06 on 2018/7/21.
 */
public class CategoryData extends Data {
    public String name;

    public CategoryData(String name, boolean selected) {
        super(selected);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}