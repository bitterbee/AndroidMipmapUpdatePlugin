package com.netease.tools.util;

import com.intellij.openapi.project.Project;
import org.apache.http.ExceptionLogger;
import org.apache.http.util.TextUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created by icy on 16/7/18.
 */
public class Fio {
    /*
     * read file to buffer
     */
    private static void readToBuffer(StringBuffer buffer, String filePath) {
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                buffer.append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            buffer.delete(0, buffer.length());
            e.printStackTrace();
        } finally {
            safeClose(reader);
            safeClose(is);
        }
    }

    /*
     * read string from file
     */
    public static String readStringFile(Project aProject, String aFilename) {
        String input = aProject.getBasePath();
        System.out.println("read file output = " + input);

        String filepath = input + File.separator + aFilename;
        return readStringFile(filepath);
    }

    public static String readStringFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        System.out.println("read file filepath = " + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("file not exists!");
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return null;
        }

        StringBuffer sb = new StringBuffer();
        readToBuffer(sb, filePath);

        return sb.toString();
    }

    public static void writeToFile(String filePath, String content, boolean isAppend) {
        if (TextUtils.isEmpty(filePath) || content == null)
            return;

        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, isAppend);
            try {
                if (isAppend) {
                    fw.append(content);
                } else {
                    fw.write(content);
                }
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                safeClose(fw);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String calcMd5(File file) {
        InputStream fis = null;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(file);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return toHexString(md5.digest(), true, "");
        } catch (Exception e) {
            return null;
        } finally {
            safeClose(fis);
        }
    }

    public static boolean copy(String source, String target) throws IOException {
        //如果原文件不存在
        File oldFile = new File(source);
        if(!oldFile.exists()) {
            return false;
        }

        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(inStream);
            safeClose(in);
            safeClose(outStream);
            safeClose(out);
        }

        return true;
    }

    public static void safeClose(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String toHexString(byte[] bytes, boolean lowerCase, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(byteHEX(b, lowerCase ? DIGITS_LOWER : DIGITS_UPPER)).append(separator);
        }
        return hexString.toString();
    }

    private static String byteHEX(byte ib, char[] digit) {
        char[] ob = new char[2];
        ob[0] = digit[(ib >>> 4) & 0X0F];
        ob[1] = digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }
}
