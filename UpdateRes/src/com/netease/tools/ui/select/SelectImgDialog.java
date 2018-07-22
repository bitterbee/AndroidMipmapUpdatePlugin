package com.netease.tools.ui.select;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.netease.tools.model.ImgStatus;
import com.netease.tools.ui.ImagePanel;
import operation.ImgOperation;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SelectImgDialog extends DialogWrapper implements SelectImgTree.ImgOperationSelectListener {

    private Project project;
    private JPanel contentPane;
    private JTree imgTree;
    private JPanel imgShow;
    private List<ImgOperation> imgOperations;

    public SelectImgDialog(Project project, List<ImgOperation> ops) {
        super(project, true);

        imgOperations = ops;
        setModal(true);

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);
        return contentPane;
    }

    @Override
    public void onImgOperationSelected(ImgOperation op) {
        if (op == null) {
            ((ImagePanel) imgShow).setImgPath(null, ImgStatus.NO_MODIFY);
            return;
        }
        if (op.status() == ImgStatus.DELETE) {
            ((ImagePanel) imgShow).setImgPath(op.toPath(), op.status());
        } else {
            ((ImagePanel) imgShow).setImgPath(op.inPath(), op.status());
        }
    }

    private void createUIComponents() {
        imgShow = new ImagePanel();

        imgTree = new SelectImgTree(imgOperations);
        ((SelectImgTree) imgTree).setImgOperationSelectListener(this);
    }

    public List<ImgOperation> getSelectedOps() {
        return ((SelectImgTree) imgTree).getSelectedOps();
    }
}
