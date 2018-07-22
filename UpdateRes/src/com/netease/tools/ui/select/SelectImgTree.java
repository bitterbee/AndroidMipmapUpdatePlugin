package com.netease.tools.ui.select;

import operation.ImgOperation;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.io.File;
import java.util.*;

/**
 * Created by zyl06 on 2018/7/22.
 */
public class SelectImgTree extends JTree implements TreeSelectionListener {
    private Map<String, CategoryNodeData> mCategoryNodeDatas = new HashMap<String, CategoryNodeData>();
    private Map<CategoryNodeData, List<ImgOperationNodeData>> mNodeDatas = new HashMap<CategoryNodeData, List<ImgOperationNodeData>>();
    private ImgOperationSelectListener mListener;

    public SelectImgTree(List<ImgOperation> ops) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("mipmap-root");

        createNodes(top, ops);
        setModel(new DefaultTreeModel(top, false));

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        addTreeSelectionListener(this);
        setCellRenderer(new CheckBoxNodeRenderer());
        setCellEditor(new CheckBoxNodeEditor(mNodeDatas, this));
        expandTree();
    }

    public void setImgOperationSelectListener(ImgOperationSelectListener listener) {
        this.mListener = listener;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        //Returns the last path element of the selection.
        //This method is useful only when the selection model allows a single selection.
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

        if (node == null)
            //Nothing is selected.
            return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf() && nodeInfo instanceof ImgOperationNodeData) {
            ImgOperation img = ((ImgOperationNodeData) nodeInfo).operation;
            if (mListener != null) {
                mListener.onImgOperationSelected(img);
            }
        }
    }

    public List<ImgOperation> getSelectedOps() {
        List<ImgOperation> result = new ArrayList<ImgOperation>();
        for (List<ImgOperationNodeData> v : mNodeDatas.values()) {
            for (ImgOperationNodeData data : v) {
                if (data.selected) {
                    result.add(data.operation);
                }
            }
        }
        return result;
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
            CategoryNodeData cnd = mCategoryNodeDatas.get(categoryName);
            if (categoryNode == null) {
                boolean selected = true;

                cnd = new CategoryNodeData(categoryName, selected);
                categoryNode = new DefaultMutableTreeNode(cnd);
                categoryMap.put(categoryName, categoryNode);
                top.add(categoryNode);

                mCategoryNodeDatas.put(categoryName, cnd);
                mNodeDatas.put(cnd, new ArrayList<ImgOperationNodeData>());
            }

            boolean selected = true;
            ImgOperationNodeData iond = new ImgOperationNodeData(op, selected);
            DefaultMutableTreeNode imgNode = new DefaultMutableTreeNode(iond);
            categoryNode.add(imgNode);

            List<ImgOperationNodeData> ionds = mNodeDatas.get(cnd);
            ionds.add(iond);
        }
    }

    public void expandTree() {
        TreeNode root = (TreeNode) getModel().getRoot();
        expandAll(this, new TreePath(root), true);
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

    public interface ImgOperationSelectListener {
        void onImgOperationSelected(ImgOperation op);
    }
}
