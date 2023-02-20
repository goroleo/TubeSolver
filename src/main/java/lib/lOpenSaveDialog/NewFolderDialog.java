/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */
package lib.lOpenSaveDialog;

import core.ResStrings;
import gui.Palette;
import lib.lButtons.LPictureButton;
import lib.lTextFields.LTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static lib.lOpenSaveDialog.OpenSavePanel.current;

public class NewFolderDialog extends JDialog {

    private final Window owner;
    private final JLabel lbError;
    private final LPictureButton btnOk;
    private final LPictureButton btnCancel;
    private final LTextField nameField;

    private final int w = 350;
    private final int h = 135;

    public NewFolderDialog(Window owner) {
        super(owner, ResStrings.getString("strNewFolder"), ModalityType.APPLICATION_MODAL);
        this.owner = owner;

        setDefaultCloseOperation(0); // DO_NOTHING_ON_CLOSE
        setIconImage(null);
        setLayout(null);

        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        getContentPane().setLayout(null);

        JLabel nameLabel = new JLabel(ResStrings.getString("strCreateFolder"));
        nameLabel.setBackground(null);
        nameLabel.setForeground(null);
        nameLabel.setLocation(10, 10);
        nameLabel.setSize(w - 20, 22);
        nameLabel.setFont(nameLabel.getFont().deriveFont(0));
        getContentPane().add(nameLabel);

        nameField = new LTextField() {
            @Override
            public void valueChanged() {
                lbError.setVisible(false);
            }
        };
        nameField.setForbiddenSigns("/?*&\\$");
        nameField.setLocation(10, 10 + 10 + 22);
        nameField.setSize(w - 20, 26);
        nameField.setFont(nameField.getFont().deriveFont(13.0f));
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == '\n' && !e.isControlDown()) {
                    if (!nameField.getValue().equals("")) {
                        confirmAndClose();
                    }
                }
            }
        });
        getContentPane().add(nameField);

        btnOk = new LPictureButton(this);
        btnOk.setText(ResStrings.getString("strOk"));
        btnOk.setBackground(null);
        btnOk.setForeground(null);
        btnOk.setFocusable(true);
        btnOk.setLocation(w - 215, h - 45);
        btnOk.addActionListener((ActionEvent e) -> confirmAndClose());
        getContentPane().add(btnOk);

        btnCancel = new LPictureButton(this);
        btnCancel.setText(ResStrings.getString("strCancel"));
        btnCancel.setBackground(null);
        btnCancel.setForeground(null);
        btnCancel.setFocusable(true);
        btnCancel.setLocation(w - 105, h - 45);
        btnCancel.addActionListener((ActionEvent e) -> refuseAndClose());
        getContentPane().add(btnCancel);

        lbError = new JLabel(ResStrings.getString("strError"));
        lbError.setBackground(null);
        lbError.setForeground(new Color(0xff4c6e));
        lbError.setLocation(10, 10 + 10 + 22 + 26 + 10);
        lbError.setSize(w - 235, 22);
        lbError.setVisible(false);
        getContentPane().add(lbError);

//        add(panel);
//        panel.setSize(w, h);

        calculateSize();
        calculatePos();
        addListeners();
    }

    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = w;
        dim.height = h;
        setPreferredSize(dim);
        pack();
        int realW = getContentPane().getWidth();
        int realH = getContentPane().getHeight();
        dim.width += (dim.width - realW);
        dim.height += (dim.height - realH);
        setPreferredSize(dim);
        pack();
        setResizable(false);
    }

    private void calculatePos() {
        Rectangle r = getGraphicsConfiguration().getBounds();
        if (owner != null) {
            setLocationRelativeTo(owner);
        } else {
            r.x = r.x + (r.width - getWidth()) / 2;
            r.y = r.y + (r.height - getHeight()) / 2;
            setLocation(r.x, r.y);
        }
    }

    private void addListeners() {

        // CLOSE WINDOW click
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                refuseAndClose();
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> refuseAndClose(),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> confirmAndClose(),
                KeyStroke.getKeyStroke('\n', 2), // VK_ENTER + MASK_CTRL
                2); // WHEN_IN_FOCUSED_WINDOW
    }

    public void refuseAndClose() {
        EventQueue.invokeLater(this::dispose);
    }

    public void confirmAndClose() {
        if (!nameField.getValue().equals("")) {
            String newFolderName = current.getFolder().getAbsolutePath() + File.separator + nameField.getValue();
            File f = new File(newFolderName);
            if (f.mkdir()) {
                current.setFolder(f);
                EventQueue.invokeLater(this::dispose);
            } else {
                lbError.setText(ResStrings.getString("strError"));
                lbError.setVisible(true);
            }
        } else {
            lbError.setText(ResStrings.getString("strEmptyName"));
            lbError.setVisible(true);
        }
    }

}
