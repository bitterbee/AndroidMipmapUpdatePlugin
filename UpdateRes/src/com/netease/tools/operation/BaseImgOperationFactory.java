package com.netease.tools.operation;

import com.intellij.openapi.project.Project;
import com.netease.tools.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/2/25.
 */
abstract class BaseImgOperationFactory {

    protected Project project;
    protected String subProjPath;
    protected String gitPath;

    protected static final String[] MIPMAPS = new String[] {
            "mipmap-mdpi",
            "mipmap-hdpi",
            "mipmap-xhdpi",
            "mipmap-xxhdpi",
            "mipmap-xxxhdpi"
    };

    public BaseImgOperationFactory(Project project, String subProjPath, String gitPath) {
        this.project = project;
        this.subProjPath = subProjPath;
        this.gitPath = gitPath;
    }

    public List<ImgOperation> create() throws Exception {
        List<ImgOperation> result = new ArrayList<ImgOperation>();
        for (String mipmap : MIPMAPS) {
            result.addAll(create(mipmap));
        }
        return result;
    }

    protected abstract List<ImgOperation> create(String mipmap) throws Exception;

    protected boolean makeDirExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        }

        return file.isDirectory();
    }

    protected String getInputMipmapPath(String mipmapGitPath, String subProjPath, String mipmap) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(mipmapGitPath).append(File.separator)
                .append("android").append(File.separator)
                .append(subProjPath).append(File.separator)
                .append(mipmap);
        return sb.toString();
    }

    protected String getOutputMipmapPath(Project project, String subProjPath, String mipmap) {
        return getOutputResPath(project, subProjPath) + File.separator + mipmap;
    }

    protected String getOutputResPath(Project project, String subProjPath) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(project.getBasePath()).append(File.separator)
                .append(subProjPath).append(File.separator)
                .append("src").append(File.separator)
                .append("main").append(File.separator)
                .append("res");
        return sb.toString();
    }

    protected boolean isPhoto(String path) {
        return ImageUtil.isImage(path);
    }
}
