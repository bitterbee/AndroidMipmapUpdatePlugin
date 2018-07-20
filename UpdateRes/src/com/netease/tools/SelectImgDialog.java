package com.netease.tools;

import com.netease.tools.model.ImgData;
import com.netease.tools.ui.ImagePanel;
import operation.ImgOperation;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectImgDialog extends JDialog implements TreeSelectionListener {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTree imgTree;
    private JPanel imgShow;

    public SelectImgDialog(List<ImgOperation> ops) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        DefaultMutableTreeNode top = new DefaultMutableTreeNode("The Java Series");
        createNodes(top, ops);
        imgTree.setModel(new DefaultTreeModel(top, false));

        imgTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        imgTree.addTreeSelectionListener(this);
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
        if (node.isLeaf()) {
            ImgData img = (ImgData) nodeInfo;
            showImage(img);
        } else {
            showImage(null);
        }
    }

    private void showImage(ImgData imgData) {
        ((ImagePanel) imgShow).setImgPath(imgData.path);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        SelectImgDialog dialog = new SelectImgDialog(new ArrayList<ImgOperation>());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createNodes(DefaultMutableTreeNode top, List<ImgOperation> ops) {
        Map<String, DefaultMutableTreeNode> categoryMap = new HashMap<String, DefaultMutableTreeNode>();
        for (ImgOperation op : ops) {
            if (op.toPath() == null) {
                continue;
            }
            File file = new File(op.toPath());
            if (!file.exists()) {
                continue;
            }

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
        // TODO: place custom component creation code here
        imgShow = new ImagePanel();
    }
}
