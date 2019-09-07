package com.netease.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CleanBuildDialog extends DialogWrapper {
    private Project project;
    private JPanel contentPane;

    private JCheckBox includeModuleProjectsCheckBox;

    public CleanBuildDialog(@Nullable Project project) {
        super(project, true);
        this.project = project;
        setTitle("删除 build 文件夹");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);
        return contentPane;
    }

    public boolean isIncludeModuleProjects() {
        return includeModuleProjectsCheckBox.isSelected();
    }
}
