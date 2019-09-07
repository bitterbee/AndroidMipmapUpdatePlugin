package com.netease.tools.util;

/**
 * Created by zyl06 on 2019/9/7.
 */
public class ImageUtil {

    public static boolean isImage(String path) {
        if (path == null) {
            return false;
        }

        String lowerCase = path.toLowerCase();
        return lowerCase.endsWith(ImageSuffex.PNG)
                || lowerCase.endsWith(ImageSuffex.JPG)
                || lowerCase.endsWith(ImageSuffex.JPEG)
                || lowerCase.endsWith(ImageSuffex.WEBP)
                || lowerCase.endsWith(ImageSuffex.SVG);
    }
}
