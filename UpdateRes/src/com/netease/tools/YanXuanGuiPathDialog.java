package com.netease.tools;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogEarthquakeShaker;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.netease.tools.util.CommandUtil;
import com.netease.tools.util.ConfigUtil;
import com.netease.tools.util.PathUtil;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by zyl06 on 7/25/16.
 */
public class YanXuanGuiPathDialog extends DialogWrapper {
    private Project project;
    private JPanel contentPane;
    private TextFieldWithBrowseButton yxguiTextField;
    private TextFieldWithBrowseButton gitTextField;

    public YanXuanGuiPathDialog(@Nullable Project project) {
        super(project, true);
        this.project = project;
        setTitle("yanxuan_gui 目录");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        yxguiTextField.addBrowseFolderListener("Please choose yanxuan_gui path", "The path where yanxuan_gui is cloned", null, descriptor);
        myPreferredFocusedComponent = yxguiTextField;

        String gitPath = CommandUtil.getGitCommandPath(project);
        if (!TextUtils.isEmpty(gitPath)) {
            gitTextField.setText(gitPath);
        }

        String yxguiPath = ConfigUtil.getYanXuanGuiPath();
        if (!TextUtils.isEmpty(yxguiPath)) {
            yxguiTextField.setText(yxguiPath);
        }

        return contentPane;
    }

    public String getYanXuanGuiPath() {
        return yxguiTextField.getText().trim();
    }

    public String getGitPath() {
        return gitTextField.getText().trim();
    }

    protected void doOKAction() {
        if (!PathUtil.isPathValid(getYanXuanGuiPath())) {

            DialogEarthquakeShaker.shake((JDialog)YanXuanGuiPathDialog.this.getPeer().getWindow());
            return;
        }
        super.doOKAction();
    }
}
