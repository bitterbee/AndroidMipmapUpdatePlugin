package com.netease.tools.ui.select;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zyl06 on 2018/7/21.
 */
public class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor, ActionListener {

    private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    private final JCheckBox check = new JCheckBox();
    private final JPanel panel = new JPanel(new BorderLayout());
    private Object userObj;

    private Map<CategoryNodeData, List<ImgOperationNodeData>> nodeDatas = new HashMap<CategoryNodeData, List<ImgOperationNodeData>>();
    private JTree imgTree;

    public CheckBoxNodeEditor(Map<CategoryNodeData, List<ImgOperationNodeData>> data,
                              JTree imgTree) {
        super();
        this.nodeDatas = data;
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
        if (userObj instanceof ImgOperationNodeData) {
            ((ImgOperationNodeData) userObj).selected = selected;

            CategoryNodeData parentCnd = null;
            List<ImgOperationNodeData> ionds = null;
            for (CategoryNodeData cnd : CheckBoxNodeEditor.this.nodeDatas.keySet()) {
                ionds = CheckBoxNodeEditor.this.nodeDatas.get(cnd);
                if (ionds.contains(userObj)) {
                    parentCnd = cnd;
                    break;
                }
            }

            if (parentCnd != null) {
                for (ImgOperationNodeData iond : ionds) {
                    if (selected != iond.selected) {
                        return;
                    }
                }
                parentCnd.selected = selected;
                imgTree.updateUI();
            }

        } else if (userObj instanceof CategoryNodeData) {
            ((CategoryNodeData) userObj).selected = selected;

            java.util.List<ImgOperationNodeData> ionds = CheckBoxNodeEditor.this.nodeDatas.get(userObj);
            if (ionds != null) {
                for (ImgOperationNodeData iond : ionds) {
                    iond.selected = selected;
                }
            }

            imgTree.updateUI();
        }
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
            if (userObject instanceof ImgOperationNodeData) {
                ImgOperationNodeData node = (ImgOperationNodeData) userObject;
                label.setText(node.toString());
                check.setSelected(node.selected);
            } else if (userObject instanceof CategoryNodeData) {
                CategoryNodeData node = (CategoryNodeData) userObject;
                label.setText(node.name);
                check.setSelected(node.selected);
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