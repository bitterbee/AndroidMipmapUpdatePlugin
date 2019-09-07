package com.netease.tools.ui.image;

import com.netease.tools.util.Fio;
import com.netease.tools.util.ImageSuffex;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by zyl06 on 2019/9/7.
 */
public class Svg implements IImageGenerator {

    @Override
    public Image getImage(String svgPath) {
        if (svgPath == null || !svgPath.toLowerCase().endsWith(ImageSuffex.SVG)) {
            return null;
        }

        OutputStream png_ostream = null;
        File png = null;
        try {
            //Step -1: We read the input SVG document into Transcoder Input
            //We use Java NIO for this purpose
            String svg_URI_input = new URL("file:///" + svgPath).toString();
            TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
            //Step-2: Define OutputStream to PNG Image and attach to TranscoderOutput

            png = File.createTempFile(svgPath, ImageSuffex.PNG);
            png.deleteOnExit();

            png_ostream = new FileOutputStream(png);
            TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
            // Step-3: Create PNGTranscoder and define hints if required
            PNGTranscoder my_converter = new PNGTranscoder();
            // Step-4: Convert and Write output
            my_converter.transcode(input_svg_image, output_png_image);
            // Step 5- close / flush Output Stream
            png_ostream.flush();

            byte[] pngData = Fio.read(png);
            return new ImageIcon(pngData, "preview").getImage();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Fio.safeClose(png_ostream);
            if (png != null && png.exists()) {
                png.delete();
            }
        }
        return null;
    }
}
