package com.netease.tools.ui.image;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/9/7.
 */
public class ComposedImg implements IImageGenerator {

    private List<IImageGenerator> mGenerators = new ArrayList<IImageGenerator>();

    @Override
    public Image getImage(String path) {
        for (IImageGenerator generator : mGenerators) {
            Image img = generator.getImage(path);
            if (img != null) {
                return img;
            }
        }
        return null;
    }

    public ComposedImg add(IImageGenerator next) {
        mGenerators.add(next);
        return this;
    }
}
