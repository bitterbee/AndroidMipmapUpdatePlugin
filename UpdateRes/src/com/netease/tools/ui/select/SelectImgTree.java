package com.netease.tools.ui.select;

import com.netease.tools.operation.ImgOperation;
import com.netease.tools.ui.node.*;

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
    private Map<String, CategoryNode> mProjectNodes = new HashMap<String, CategoryNode>();
    private Map<String, CategoryNode> mMipmapNodes = new HashMap<String, CategoryNode>();

    private ImgOperationSelectListener mListener;

    public SelectImgTree(List<ImgOperation> ops) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("root");
        createNodes(top, ops);
        setModel(new DefaultTreeModel(top, false));

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        addTreeSelectionListener(this);
        setCellRenderer(new CheckBoxNodeRenderer());
        setCellEditor(new CheckBoxNodeEditor(this));
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
        if (node.isLeaf() && nodeInfo instanceof DataNode) {
            Data data = ((DataNode) nodeInfo).data();
            if (data instanceof ImgOperationData) {
                ImgOperation img = ((ImgOperationData) data).operation;
                if (mListener != null) {
                    mListener.onImgOperationSelected(img);
                }
            }
        }
    }

    public List<ImgOperation> getSelectedOps() {
        List<WrapNode> dataNodes = new ArrayList<WrapNode>();
        dataNodes.addAll(mMipmapNodes.values());
        return getSelectedOps(dataNodes);
    }

    private List<ImgOperation> getSelectedOps(List<WrapNode> nodes) {
        List<ImgOperation> result = new ArrayList<ImgOperation>();
        if (nodes == null || nodes.isEmpty()) {
            return result;
        }


        for (WrapNode node : nodes) {
            if (node instanceof ImgOperationNode) {
                DataNode<ImgOperationData> dataNode = node.data();
                if (dataNode != null && dataNode.data() != null && dataNode.data().selected) {
                    result.add(dataNode.data().operation);
                }
            } else if (node instanceof CategoryNode) {
                result.addAll(getSelectedOps(node.children()));
            }
        }
        return result;
    }

    private void createNodes(DefaultMutableTreeNode top, List<ImgOperation> ops) {

        CategoryNode topNode = new CategoryNode(top);

        for (ImgOperation op : ops) {
            if (op.toPath() == null) {
                continue;
            }
            String proj = op.projPath();

            CategoryNode projNode = mProjectNodes.get(proj);
            if (projNode == null) {
                projNode = new CategoryNode(proj, false);
                topNode.add(projNode);

                mProjectNodes.put(proj, projNode);
            }


            File file = new File(op.toPath());

            String mipmapName = file.getParentFile().getName();
            String imgName = file.getName();

            String mipmapKey = mipmapKey(proj, mipmapName);
            CategoryNode mipmapNode = mMipmapNodes.get(mipmapKey);
            if (mipmapNode == null) {
                mipmapNode = new CategoryNode(mipmapName, false);
                mMipmapNodes.put(mipmapKey, mipmapNode);

                projNode.add(mipmapNode);
            }

            ImgOperationNode ion = new ImgOperationNode(op, false);
            mipmapNode.add(ion);
        }
    }

    private String mipmapKey(String proj, String mipmapName) {
        return proj + "-" + mipmapName;
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
