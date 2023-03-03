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
import lib.lTextFields.LTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.DEFAULT_EXT;
import static lib.lOpenSaveDialog.OpenSavePanel.current;

/**
 * A File Name input field. With a "File" announcement and default file extension.
 * Based on the lTextField component.
 * Also, this field is used to show currently selected file and folder.
 * @see lib.lTextFields.LTextField
 */
public class FileEditPanel extends JComponent implements FolderListener, FileListener {

    /** A file name field */
    private final LTextField nameField;

    /** "File" label */
    private final JLabel lbFile;

    /** File extension label */
    private final JLabel lbExt;

    /** The X-coordinate stores end pixel of the file name field (depends on the FoldersDropDown width).
     * @see FoldersDropDown
     */
    public int fieldEnd = 120;

    /**
     * FileEditPanel constructor
     */
    @SuppressWarnings("MagicConstant")
    public FileEditPanel() {
        setBackground(null);
        setForeground(null);

        // creating and configuring nameField
        nameField = new LTextField() {
            @Override
            public void valueChanged() {
                osPan.scrollToFileName(getValue());
            }
        };
        nameField.setForbiddenSigns("?*");
        nameField.setSize(200, 26);
        nameField.setFont(nameField.getFont().deriveFont(13.0f));
        nameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (osPan.isFoldersPanelVisible()) {
                    osPan.showFoldersPanel(nameField, false);
                }
            }
        });
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == '\n' && !e.isControlDown()) {
                    if (!nameField.getValue().equals("")) {
                        osPan.confirmAndClose();
                    }
                }
            }
        });
        add(nameField);

        // creating and configuring File label
        lbFile = new JLabel(ResStrings.getString("strFile"));
        lbFile.setFont(lbFile.getFont().deriveFont(0));
        lbFile.setBackground(null);
        lbFile.setForeground(null);
        add(lbFile);

        // creating and configuring extension label
        lbExt = new JLabel(DEFAULT_EXT);
        lbExt.setFont(lbExt.getFont().deriveFont(1, 13.0f));
        lbExt.setBackground(null);
        lbExt.setForeground(null);
        add(lbExt);
    }

    @Override
    public void setSize(int w, int h) {
        h = nameField.getHeight();
        FontMetrics fm;

        fm = lbFile.getFontMetrics(lbFile.getFont());
        lbFile.setSize(fm.stringWidth(lbFile.getText()), fm.getHeight());
        lbFile.setLocation(0, (h - fm.getHeight()) / 2);

        fm = lbExt.getFontMetrics(lbExt.getFont());
        lbExt.setSize(fm.stringWidth(lbExt.getText()), fm.getHeight());
        lbExt.setLocation(fieldEnd + 20, (h - fm.getHeight()) / 2);

        nameField.setLocation(10 + lbFile.getWidth(), 0);
        nameField.setSize(fieldEnd - nameField.getX() - 10, h);
        super.setSize(w, h);
    }

    /**
     * Gets the current text value from the File name field
     * @return typed file name or/and folder path
     */
    public String getInputFieldValue() {
        return nameField.getValue();
    }

    /**
     * This sets the temporary text to display in the file name field. This text will be replaced
     * when the current folder is changed.
     * @param fName text to temporally display at the file name field
     */
    public void setInputFieldValue(String fName) {
        nameField.setValue(fName.trim(), true);
    }

    @Override
    public void setFocusable(boolean focusable) {
        // this panel is nof focusable anyway
        super.setFocusable(false);
        // the focus has translated to the nameField
        nameField.setFocusable(focusable);
    }

    /**
     * This catches the folder change events
     * @param folder current folder
     */
    @Override
    public void updateFolder(File folder) {
        nameField.setValue(current.getDisplayedFileName(), true);
    }

    /**
     * This catches the current file change events
     */
    @Override
    public void updateFile() {
        nameField.setValue(current.getDisplayedFileName(), true);
    }

}
