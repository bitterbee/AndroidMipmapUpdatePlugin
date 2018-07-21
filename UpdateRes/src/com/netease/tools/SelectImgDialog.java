package com.netease.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.netease.tools.model.ImgStatus;
import com.netease.tools.ui.ImagePanel;
import operation.ImgOperation;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectImgDialog extends DialogWrapper implements TreeSelectionListener {

    private Project project;
    private JPanel contentPane;
    private JTree imgTree;
    private JPanel imgShow;

    public SelectImgDialog(Project project, List<ImgOperation> ops) {
        super(project, true);
        setModal(true);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("mipmap-top");
        createNodes(top, ops);
        imgTree.setModel(new DefaultTreeModel(top, false));

        imgTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        imgTree.addTreeSelectionListener(this);

        init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);
        return contentPane;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        //Returns the last path element of the selection.
        //This method is useful only when the selection model allows a single selection.
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) imgTree.getLastSelectedPathComponent();

        if (node == null)
            //Nothing is selected.
            return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf() && nodeInfo instanceof ImgOperation) {
            ImgOperation img = (ImgOperation) nodeInfo;
            showImage(img);
        } else {
            showImage(null);
        }
    }

    private void showImage(ImgOperation op) {
        if (op == null) {
            ((ImagePanel) imgShow).setImgPath(null);
            return;
        }
        if (op.status() == ImgStatus.DELETE) {
            ((ImagePanel) imgShow).setImgPath(op.toPath());
        } else {
            ((ImagePanel) imgShow).setImgPath(op.inPath());
        }
    }

    private void createNodes(DefaultMutableTreeNode top, List<ImgOperation> ops) {
        Map<String, DefaultMutableTreeNode> categoryMap = new HashMap<String, DefaultMutableTreeNode>();
        for (ImgOperation op : ops) {
            if (op.toPath() == null) {
                continue;
            }
            File file = new File(op.toPath());

            String categoryName = file.getParentFile().getName();
            String imgName = file.getName();

            DefaultMutableTreeNode categoryNode = categoryMap.get(categoryName);
            if (categoryNode == null) {
                categoryNode = new DefaultMutableTreeNode(categoryName);
                categoryMap.put(categoryName, categoryNode);
                top.add(categoryNode);
            }

            DefaultMutableTreeNode imgNode = new DefaultMutableTreeNode(op);
            categoryNode.add(imgNode);
        }
    }

    private void createUIComponents() {
        imgShow = new ImagePanel();
        imgShow.setBackground(Color.LIGHT_GRAY);
        imgShow.setPreferredSize(new Dimension(300, 300));
    }
}
