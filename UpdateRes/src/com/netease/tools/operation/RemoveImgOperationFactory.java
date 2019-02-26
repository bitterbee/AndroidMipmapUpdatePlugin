package com.netease.tools.operation;

import com.intellij.openapi.project.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class RemoveImgOperationFactory extends BaseImgOperationFactory {

    private String toResPath;

    public RemoveImgOperationFactory(Project project, String subProjPath, String gitPath) {
        super(project, subProjPath, gitPath);
        toResPath = getOutputResPath(project, subProjPath);
    }

    @Override
    protected List<ImgOperation> create(String mipmap) throws Exception {
        List<ImgOperation> result = new ArrayList<ImgOperation>();



        String inMipmapPath = getInputMipmapPath(gitPath, subProjPath, mipmap);
        File inMipmapFile = new File(inMipmapPath);
        if (!inMipmapFile.exists()) {
            return result;
        }

        String toMipmapPath = toResPath + File.separator + inMipmapFile.getName();
        File toMipmapFile = new File(toMipmapPath);
        if (!toMipmapFile.exists()) {
            return result;
        }

        File[] toFiles = toMipmapFile.listFiles();
        if (toFiles == null) {
            return result;
        }

        for (File toFile : toFiles) {
            String toName = toFile.getName();
            if (!isPhoto(toName)) {
                continue;
            }

            String[] parts = toName.split("[_]", 2);
            if (parts.length == 2) {
                String module = parts[0];
                String inName = parts[1];
                String inPath = inMipmapPath + File.separator + module + File.separator + inName;
                File inFile = new File(inPath);
                if (!inFile.exists()) {
                    result.add(new ImgOperation(subProjPath, inPath, toFile.getAbsolutePath()));
                }
            }
        }

        return result;
    }
}
