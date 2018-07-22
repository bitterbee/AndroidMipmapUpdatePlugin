package com.netease.tools.ui.select;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.netease.tools.model.ImgStatus;
import com.netease.tools.ui.ImagePanel;
import operation.ImgOperation;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class SelectImgDialog extends DialogWrapper implements TreeSelectionListener {

    private Project project;
    private JPanel contentPane;
    private JTree imgTree;
    private JPanel imgShow;

    private Map<String, CategoryNodeData> categoryNodeDatas = new HashMap<String, CategoryNodeData>();
    private Map<CategoryNodeData, List<ImgOperationNodeData>> nodeDatas = new HashMap<CategoryNodeData, List<ImgOperationNodeData>>();

    public SelectImgDialog(Project project, List<ImgOperation> ops) {
        super(project, true);
        setModal(true);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("mipmap-top");
        createNodes(top, ops);
        imgTree.setModel(new DefaultTreeModel(top, false));

        imgTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        imgTree.addTreeSelectionListener(this);
        imgTree.setCellRenderer(new CheckBoxNodeRenderer());
        imgTree.setCellEditor(new CheckBoxNodeEditor(nodeDatas, imgTree));
        expandTree(imgTree);

        init();
    }

    public static void expandTree(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), true);
    }

    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
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
        if (node.isLeaf() && nodeInfo instanceof ImgOperationNodeData) {
            ImgOperation img = ((ImgOperationNodeData) nodeInfo).operation;
            showImage(img);
        }
    }

    private void showImage(ImgOperation op) {
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
            CategoryNodeData cnd = categoryNodeDatas.get(categoryName);
            if (categoryNode == null) {
                boolean selected = true;

                cnd = new CategoryNodeData(categoryName, selected);
                categoryNode = new DefaultMutableTreeNode(cnd);
                categoryMap.put(categoryName, categoryNode);
                top.add(categoryNode);

                categoryNodeDatas.put(categoryName, cnd);
                nodeDatas.put(cnd, new ArrayList<ImgOperationNodeData>());
            }

            boolean selected = true;
            ImgOperationNodeData iond = new ImgOperationNodeData(op, selected);
            DefaultMutableTreeNode imgNode = new DefaultMutableTreeNode(iond);
            categoryNode.add(imgNode);

            List<ImgOperationNodeData> ionds = nodeDatas.get(cnd);
            ionds.add(iond);
        }
    }

    private void createUIComponents() {
        imgShow = new ImagePanel();
        imgShow.setBackground(Color.LIGHT_GRAY);
        imgShow.setPreferredSize(new Dimension(300, 300));
    }

    public List<ImgOperation> getSelectedOps() {
        List<ImgOperation> result = new ArrayList<ImgOperation>();
        for (List<ImgOperationNodeData> v : nodeDatas.values()) {
            for (ImgOperationNodeData data : v) {
                if (data.selected) {
                    result.add(data.operation);
                }
            }
        }
        return result;
    }
}
