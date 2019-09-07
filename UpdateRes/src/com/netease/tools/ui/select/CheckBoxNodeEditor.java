package com.netease.tools.ui.select;

import com.netease.tools.ui.node.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Created by zyl06 on 2018/7/21.
 */
public class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor, ActionListener {

    private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    private final JCheckBox check = new JCheckBox();
    private final JPanel panel = new JPanel(new BorderLayout());
    private Object userObj;

    private JTree imgTree;

    public CheckBoxNodeEditor(JTree imgTree) {
        super();
        check.addActionListener(this);
        panel.setFocusable(false);
        panel.setRequestFocusEnabled(false);
        panel.setOpaque(false);
        panel.add(check, BorderLayout.WEST);
        check.setOpaque(false);

        this.imgTree = imgTree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//                    stopCellEditing();
        boolean selected = check.isSelected();
        if (!(userObj instanceof DataNode)) {
            return;
        }

        DataNode node = (DataNode) userObj;
        node.setSelected(selected);

        DataNode parent = node.parent();
        if (parent != null) {
            java.util.List<DataNode> children = parent.children();
            for (DataNode child : children) {
                if (selected != child.isSelected()) {
                    imgTree.updateUI();
                    return;
                }
            }
            parent.setSelected(selected);
        }

        imgTree.updateUI();
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
                                                boolean isSelected, boolean expanded,
                                                boolean leaf, int row) {

        JLabel label = (JLabel) renderer.getTreeCellRendererComponent(
                tree, value, true, expanded, leaf, row, true);

        if (value instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            this.userObj = userObject;
            if (userObject instanceof DataNode) {
                Data data = ((DataNode) userObject).data();
                label.setText(data.toString());
                check.setSelected(data.selected);
            }

            panel.add(label);
            return panel;
        }
        return label;
    }

    @Override
    public Object getCellEditorValue() {
        return userObj != null ? userObj : "";
    }

    ////1.6.0_41`: work OK
    ////1.7.0_51`: not work to first click
    ////1.8.0`: work OK
    //@Override public boolean isCellEditable(EventObject e) {
    //  if (e instanceof MouseEvent && e.getSource() instanceof JTree) {
    //    return true;
    //  }
    //  return false;
    //}

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent && e.getSource() instanceof JTree) {
            MouseEvent me = (MouseEvent) e;
            JTree tree = (JTree) e.getSource();
            TreePath path = tree.getPathForLocation(me.getX(), me.getY());
            Rectangle r = tree.getPathBounds(path);
            if (r == null) {
                return false;
            }
            Dimension d = check.getPreferredSize();
            r.setSize(new Dimension(d.width, r.height));
            if (r.contains(me.getX(), me.getY())) {
                if (userObj == null && System.getProperty("java.version").startsWith("1.7.0")) {
                    System.out.println("XXX: Java 7, only on first run\n" + panel.getBounds());
                    check.setBounds(new Rectangle(0, 0, d.width, r.height));
                }
                return true;
            }
        }
        return false;
    }
}