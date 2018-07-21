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
        if (path != null) {
            image = new ImageIcon(path, "preview").getImage();
//        setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
            repaint();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int iw = image.getWidth(this);
            int ih = image.getHeight(this);
            int w = getWidth();
            int h = getHeight();

            int x = 0;
            int y = 0;
            int dw = w;
            int dh = h;
            if (iw < w && ih < h) {
                x = (w - iw) / 2;
                y = (h - ih) / 2;
                dw = iw;
                dh = ih;
            } else {
                float ir = 1.0f * iw / ih;
                float r = 1.0f * w / h;
                if (ir > r) {
                    dw = w;
                    dh = (int) (w / ir);
                    x = 0;
                    y = (h - dh) / 2;
                } else {
                    dh = h;
                    dw = (int) (h * ir);
                    x = (w - dw) / 2;
                    y = 0;
                }
            }

            g.drawImage(image, x, y, dw, dh, this);
        }
    }
}
