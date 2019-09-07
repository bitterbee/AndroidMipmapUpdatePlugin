package com.netease.tools.operation;

import com.intellij.openapi.project.Project;
import com.netease.tools.model.ImgStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class UpdateImgOperationFactory extends BaseImgOperationFactory {

    public UpdateImgOperationFactory(Project project, String subProjPath, String gitPath) {
        super(project, subProjPath, gitPath);
    }

    @Override
    protected List<ImgOperation> create(String mipmap) throws Exception {
        List<ImgOperation> result = new ArrayList<ImgOperation>();

        String inMipmapPath = getInputMipmapPath(gitPath, subProjPath, mipmap);
        String toMipmapPath = getOutputMipmapPath(project, subProjPath, mipmap);
        makeDirExist(toMipmapPath);

        File inMipmapFile = new File(inMipmapPath);
        if (!inMipmapFile.exists()) {
//            String msg = "input path " + inMipmapPath + " not exists";
//            throw new Exception(msg);
            return result;
        }

        if (!inMipmapFile.isDirectory()) {
//            String msg = "input path " + inMipmapPath + " is not directory";
//            throw new Exception(msg);
            return result;
        }

        File[] modules = inMipmapFile.listFiles();
        if (modules != null) {
            for (File module : modules) {
                result.addAll(getUpdateMipmapModuleOperations(toMipmapPath, module, subProjPath));
            }
        }

        return result;
    }

    private List<ImgOperation> getUpdateMipmapModuleOperations(String toMipmapPath, File module, String subProjPath) throws Exception {
        List<ImgOperation> result = new ArrayList<ImgOperation>();

        if (!module.isDirectory()) {
            return result;
        }

        File[] subImages = module.listFiles();
        if (subImages == null) {
            return result;
        }

        for (File subImage : subImages) {
            if (isPhoto(subImage.getName())) {
                String inPath = subImage.getAbsolutePath();
                String toPath = toMipmapPath + File.separator + module.getName() + "_" + subImage.getName();
                ImgOperation op = new ImgOperation(subProjPath, inPath, toPath);
                if (op.status() == ImgStatus.ADD || op.status() == ImgStatus.MODIFY) {
                    result.add(op);
                }
            }
        }

        return result;
    }

}
