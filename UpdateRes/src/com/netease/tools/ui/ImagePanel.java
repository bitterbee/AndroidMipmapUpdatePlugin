package com.netease.tools.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by zyl06 on 2018/7/20.
 */
public class ImagePanel extends JPanel {

    private Image image = null;

    public ImagePanel() {
    }

    public void setImgPath(String path) {
        image = new ImageIcon(path, "preview").getImage();
        setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
