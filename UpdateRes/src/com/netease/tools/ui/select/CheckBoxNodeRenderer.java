package com.netease.tools.ui.select;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * Created by zyl06 on 2018/7/21.
 */
public class CheckBoxNodeRenderer implements TreeCellRenderer {

    private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    private final JCheckBox check = new JCheckBox();
    private final JPanel p = new JPanel(new BorderLayout());

    public CheckBoxNodeRenderer() {
        p.setFocusable(false);
        p.setRequestFocusEnabled(false);
        p.setOpaque(false);
        p.add(check, BorderLayout.WEST);
        check.setOpaque(false);
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        JLabel l = (JLabel) renderer.getTreeCellRendererComponent(
                tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode) {
            check.setEnabled(tree.isEnabled());
            check.setFont(tree.getFont());
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof ImgOperationNodeData) {
                ImgOperationNodeData node = (ImgOperationNodeData) userObject;
                l.setText(node.toString());
                check.setSelected(node.selected);
            } else if (userObject instanceof CategoryNodeData) {
                check.setSelected(((CategoryNodeData) userObject).selected);
            } else {
                return l;
            }
            p.add(l);
            return p;
        }
        return l;
    }
}