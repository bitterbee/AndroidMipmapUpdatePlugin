package com.netease.tools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.netease.tools.model.ImgStatus;
import com.netease.tools.util.CommandUtil;
import com.netease.tools.util.ConfigUtil;
import com.netease.tools.util.Fio;
import operation.ImgOperation;
import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zyl06 on 20/02/2017.
 */
public class YanXuanResUpdate extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
        Project project = anActionEvent.getData(DataKeys.PROJECT);

        if (project != null) {
//            if (!project.getName().equals("YanXuan")) {
//                return;
//            }

            YanXuanGuiPathDialog guiPathDlg = new YanXuanGuiPathDialog(project);
            guiPathDlg.show();

            if (guiPathDlg.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
                return;
            }

            try {
                startUpdateResProcess(project, guiPathDlg.getGitPath(), guiPathDlg.getYanXuanGuiPath());
            } catch (Exception e) {
                e.printStackTrace();
                String error = e.getMessage() + "   \n  " + e.getCause();

                for (StackTraceElement stackTrace : e.getStackTrace()) {
                    error += stackTrace.toString() + "\n";
                }

                Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
            }
        }
    }

    private void startUpdateResProcess(Project project, String gitPath, String yanxuanGuiPath) {
        if (TextUtils.isEmpty(yanxuanGuiPath)) {
            Messages.showMessageDialog("wrong yanxuan_gui path", "Error", Messages.getErrorIcon());
            return;
        }
        if (TextUtils.isEmpty(gitPath)) {
            Messages.showMessageDialog("wrong git path", "Error", Messages.getErrorIcon());
            return;
        }

        ConfigUtil.setYanXuanGuiPath(yanxuanGuiPath);
        ConfigUtil.setGitPath(gitPath);

        Process process = doGitPullProcess(project, gitPath, yanxuanGuiPath);
        if (process == null) {
            return;
        }

        StringBuilder msg = new StringBuilder(128 * 1024)
                .append("===================\n")
                .append(new Date(System.currentTimeMillis())).append("\n");

        boolean isSuccess = true;
        try {
            String outputResPath = getOutputResPath(project);

            List<ImgOperation> operations = new ArrayList<ImgOperation>();
            operations.addAll(getUpdateMipmapOperations(project, yanxuanGuiPath, "mipmap-mdpi"));
            operations.addAll(getUpdateMipmapOperations(project, yanxuanGuiPath, "mipmap-hdpi"));
            operations.addAll(getUpdateMipmapOperations(project, yanxuanGuiPath, "mipmap-xhdpi"));
            operations.addAll(getUpdateMipmapOperations(project, yanxuanGuiPath, "mipmap-xxhdpi"));
            operations.addAll(getUpdateMipmapOperations(project, yanxuanGuiPath, "mipmap-xxxhdpi"));
            operations.addAll(getRemoveUnusedImagesOperations(getInputMipmapPath(yanxuanGuiPath, "mipmap-mdpi"), outputResPath));
            operations.addAll(getRemoveUnusedImagesOperations(getInputMipmapPath(yanxuanGuiPath, "mipmap-hdpi"), outputResPath));
            operations.addAll(getRemoveUnusedImagesOperations(getInputMipmapPath(yanxuanGuiPath, "mipmap-xhdpi"), outputResPath));
            operations.addAll(getRemoveUnusedImagesOperations(getInputMipmapPath(yanxuanGuiPath, "mipmap-xxhdpi"), outputResPath));
            operations.addAll(getRemoveUnusedImagesOperations(getInputMipmapPath(yanxuanGuiPath, "mipmap-xxxhdpi"), outputResPath));

            SelectImgDialog selectDlg = new SelectImgDialog(operations);
            selectDlg.setVisible(true);

//            for (ImgOperation op : operations) {
//                msg.append(op.run())
//                        .append("\n");
//            }
        } catch (Exception e) {
            Messages.showMessageDialog(e.toString(), "Error", Messages.getErrorIcon());
            isSuccess = false;
        }

        msg.append("\n");

        YXConsole.show(project, process, msg.toString());

        if (isSuccess) {
            writeToFile(project, msg.toString());
        }
    }

    private void writeToFile(Project project, String msg) {
        String filePath = project.getBaseDir().getParent().getPath() + File.separator + "res_update_record.txt";
        Fio.writeToFile(filePath, msg, true);
    }

    private Process doGitPullProcess(Project project, String gitCmd, String yanxuanGuiPath) {
        if (TextUtils.isEmpty(gitCmd)) {
            Messages.showMessageDialog("git uninstall", "Error", Messages.getErrorIcon());
            return null;
        }

        if (TextUtils.isEmpty(yanxuanGuiPath)) {
            Messages.showMessageDialog("wrong yanxuan_gui path", "Error", Messages.getErrorIcon());
            return null;
        }


        String commandLine = "cd " + yanxuanGuiPath + "\n";
        commandLine += gitCmd + " pull";

        String[] cmds = CommandUtil.getSystemCmds(commandLine);

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmds, null, new File(project.getBasePath()));
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage() + "   \n  " + e.getCause();

            for (StackTraceElement stackTrace : e.getStackTrace()) {
                error += stackTrace.toString() + "\n";
            }

            Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
            return null;
        }

        YXConsole.show(project, process, cmds.toString());
        return process;
    }

    private List<ImgOperation> getUpdateMipmapOperations(Project project, String yanxuanGuiPath, String mipmap) throws Exception {
        List<ImgOperation> result = new ArrayList<ImgOperation>();

        String inMipmapPath = getInputMipmapPath(yanxuanGuiPath, mipmap);
        String toMipmapPath = getOutputMipmapPath(project, mipmap);
        makeDirExist(toMipmapPath);

        File inMipmapFile = new File(inMipmapPath);
        if (!inMipmapFile.exists()) {
            String msg = "input path " + inMipmapPath + " not exists";
            throw new Exception(msg);
        }

        if (!inMipmapFile.isDirectory()) {
            String msg = "input path " + inMipmapPath + " is not directory";
            throw new Exception(msg);
        }

        File[] modules = inMipmapFile.listFiles();
        if (modules != null) {
            for (File module : modules) {
                result.addAll(getUpdateMipmapModuleOperations(toMipmapPath, module));
            }
        }

        return result;
    }

    private List<ImgOperation> getUpdateMipmapModuleOperations(String toMipmapPath, File module) throws Exception {
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
                ImgOperation op = new ImgOperation(inPath, toPath);
                if (op.status() == ImgStatus.ADD || op.status() == ImgStatus.MODIFY) {
                    result.add(op);
                }
            }
        }

        return result;
    }

    private List<ImgOperation> getRemoveUnusedImagesOperations(String inMipmapPath, String toResPath) throws Exception {
        List<ImgOperation> result = new ArrayList<ImgOperation>();

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
                    result.add(new ImgOperation(inPath, toFile.getAbsolutePath()));
                }
            }
        }

        return result;
    }

    private boolean isPhoto(String path) {
        if (path == null) {
            return false;
        }

        return path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg");
    }

    private String getInputMipmapPath(String yanxuanGuiPath, String mipmap) {
        return yanxuanGuiPath + File.separator + "android" + File.separator + mipmap;
    }

    private String getOutputResPath(Project project) {
        return project.getBasePath() + File.separator +
                "app" + File.separator +
                "src" + File.separator +
                "main" + File.separator +
                "res";
    }

    private String getOutputMipmapPath(Project project, String mipmap) {
        return getOutputResPath(project) + File.separator + mipmap;
    }

    private boolean makeDirExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        }

        return file.isDirectory();
    }
}
