package com.netease.tools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.netease.tools.model.ImgStatus;
import com.netease.tools.operation.RemoveImgOperationFactory;
import com.netease.tools.operation.UpdateImgOperationFactory;
import com.netease.tools.ui.select.SelectImgDialog;
import com.netease.tools.util.CommandUtil;
import com.netease.tools.util.ConfigUtil;
import com.netease.tools.util.Fio;
import com.netease.tools.util.ProjectUtil;
import com.netease.tools.operation.ImgOperation;
import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zyl06 on 20/02/2017.
 */
public class MipmapUpdate extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
        Project project = anActionEvent.getData(DataKeys.PROJECT);

        if (project != null) {
//            if (!project.getName().equals("YanXuan")) {
//                return;
//            }

            MipmapPathDialog mipmapGitPathDlg = new MipmapPathDialog(project);
            mipmapGitPathDlg.show();

            if (mipmapGitPathDlg.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
                return;
            }

            try {
                startUpdateResProcess(project, mipmapGitPathDlg.getGitCmdPath(), mipmapGitPathDlg.getGitPath());
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

    private void startUpdateResProcess(Project project, String gitCmd, String gitPath) {
        if (TextUtils.isEmpty(gitPath)) {
            Messages.showMessageDialog("wrong mipmap_git path", "Error", Messages.getErrorIcon());
            return;
        }
        if (TextUtils.isEmpty(gitCmd)) {
            Messages.showMessageDialog("wrong git path", "Error", Messages.getErrorIcon());
            return;
        }

        ConfigUtil.setMipmapGitPath(gitPath);
        ConfigUtil.setGitCmdPath(gitCmd);

        Process process = doGitPullProcess(project, gitCmd, gitPath);
        if (process == null) {
            return;
        }

        StringBuilder msg = new StringBuilder(128 * 1024)
                .append("===================\n")
                .append(new Date(System.currentTimeMillis())).append("\n");

        boolean isSuccess = true;
        try {
            List<String> subProjPaths = ProjectUtil.findSubProjects(project);
            List<ImgOperation> operations = new ArrayList<ImgOperation>();

            for (String subProjPath : subProjPaths) {
                UpdateImgOperationFactory updateFactory = new UpdateImgOperationFactory(project, subProjPath, gitPath);
                operations.addAll(updateFactory.create());

                RemoveImgOperationFactory removeFactory = new RemoveImgOperationFactory(project, subProjPath, gitPath);
                operations.addAll(removeFactory.create());
            }

            SelectImgDialog selectDlg = new SelectImgDialog(project, operations);
            selectDlg.show();

            if (selectDlg.isOK()) {
                List<ImgOperation> selectOps = selectDlg.getSelectedOps();
                for (ImgOperation op : selectOps) {
                    msg.append(op.run())
                            .append("\n");
                }
            }
        } catch (Exception e) {
            Messages.showMessageDialog(e.toString(), "Error", Messages.getErrorIcon());
            isSuccess = false;
        }

        msg.append("\n");

        NEConsole.show(project, process, msg.toString());

        if (isSuccess) {
            writeToFile(project, msg.toString());
        }
    }

    private void writeToFile(Project project, String msg) {
        String filePath = project.getBaseDir().getParent().getPath() + File.separator + "res_update_record.txt";
        Fio.writeToFile(filePath, msg, true);
    }

    private Process doGitPullProcess(Project project, String gitCmd, String gitPath) {
        if (TextUtils.isEmpty(gitCmd)) {
            Messages.showMessageDialog("git uninstall", "Error", Messages.getErrorIcon());
            return null;
        }

        if (TextUtils.isEmpty(gitPath)) {
            Messages.showMessageDialog("wrong mipmap_git path", "Error", Messages.getErrorIcon());
            return null;
        }


        String commandLine = "cd " + gitPath + "\n";
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

        NEConsole.show(project, process, cmds.toString());
        return process;
    }
}
