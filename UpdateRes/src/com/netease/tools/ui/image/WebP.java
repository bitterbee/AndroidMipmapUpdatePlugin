package com.netease.tools.ui.image;

import com.netease.tools.util.Fio;
import com.netease.tools.util.ImageSuffex;
import io.github.biezhi.webp.WebpIO;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by zyl06 on 2019/9/7.
 */
public class WebP implements IImageGenerator {
    private WebpIO webpIO;

    public WebP() {
        webpIO = WebpIO.create();
    }

    @Override
    public Image getImage(String path) {
        byte[] bytes = toPng(path);
        if (bytes != null) {
            return new ImageIcon(bytes, "preview").getImage();
        }
        return null;
    }

    private byte[] toPng(String webpPath) {
        if (TextUtils.isEmpty(webpPath) || !webpPath.toLowerCase().endsWith(ImageSuffex.WEBP)) {
            return null;
        }

        File png = null;
        try {
            File webp = new File(webpPath);
            png = File.createTempFile(webpPath, ImageSuffex.PNG);
            png.deleteOnExit();
            webpIO.toNormalImage(webp, png);
            return Fio.read(png);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (png != null && png.exists()) {
                png.delete();
            }
        }
        return null;
    }
}
