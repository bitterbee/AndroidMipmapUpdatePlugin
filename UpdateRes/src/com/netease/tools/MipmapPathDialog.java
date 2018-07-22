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
public class MipmapPathDialog extends DialogWrapper {
    private Project project;
    private JPanel contentPane;
    private TextFieldWithBrowseButton guiTextField;
    private TextFieldWithBrowseButton gitTextField;

    public MipmapPathDialog(@Nullable Project project) {
        super(project, true);
        this.project = project;
        setTitle("mipmap_git 目录");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        guiTextField.addBrowseFolderListener("Please choose mipmap_git path", "The path where mipmap_git is cloned", null, descriptor);
        myPreferredFocusedComponent = guiTextField;

        String gitPath = CommandUtil.getGitCommandPath(project);
        if (!TextUtils.isEmpty(gitPath)) {
            gitTextField.setText(gitPath);
        }

        String mipmapGitPath = ConfigUtil.getMipmapGitPath();
        if (!TextUtils.isEmpty(mipmapGitPath)) {
            guiTextField.setText(mipmapGitPath);
        }

        return contentPane;
    }

    public String getMipmapGitPath() {
        return guiTextField.getText().trim();
    }

    public String getGitPath() {
        return gitTextField.getText().trim();
    }

    protected void doOKAction() {
        if (!PathUtil.isPathValid(getMipmapGitPath())) {

            DialogEarthquakeShaker.shake((JDialog)MipmapPathDialog.this.getPeer().getWindow());
            return;
        }
        super.doOKAction();
    }
}
