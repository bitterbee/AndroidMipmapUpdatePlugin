package com.netease.tools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 30/10/2017.
 */
public class CleanBuild extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
//        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        Project project = anActionEvent.getData(DataKeys.PROJECT);
        String basePath = project.getBasePath();

        CleanBuildDialog dialog = new CleanBuildDialog(project);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
            return;
        }

        try {
            List<File> buildFiles = new ArrayList<File>();

            tryAddBuildFile(basePath, buildFiles);
            tryAddBuildFile(basePath + File.separator + "app", buildFiles);

            if (dialog.isIncludeModuleProjects()) {
                String parentPath = basePath + File.separator + "module";
                File parent = new File(parentPath);
                if (parent.exists() && parent.isDirectory()) {
                    File[] moduleFiles = parent.listFiles();
                    for (File moduleFile : moduleFiles) {
                        tryAddBuildFile(moduleFile.getAbsolutePath(), buildFiles);
                    }
                }
            }

            for (File build : buildFiles) {
                deleteRecursive(build);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage() + "   \n  " + e.getCause();

            for (StackTraceElement stackTrace : e.getStackTrace()) {
                error += stackTrace.toString() + "\n";
            }

            Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
        }
    }

    private void tryAddBuildFile(String parentPath, List<File> buildFiles) {
        if (parentPath == null || parentPath.isEmpty()) {
            return;
        }

        String buildPath = parentPath + File.separator + "build";
        File file = new File(buildPath);

        if (file.exists() && file.isDirectory()) {
            buildFiles.add(file);
        }
    }

    private void deleteRecursive(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File sub : subFiles) {
                deleteRecursive(sub);
            }
        }

        file.delete();
    }
}
