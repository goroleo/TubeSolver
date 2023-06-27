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

import lib.lTextFields.LTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.current;

/**
 * A File Name input field. With a "File" announcement and default file extension.
 * Based on the lTextField component.
 * Also, this field is used to show currently selected file and folder.
 * @see lib.lTextFields.LTextField
 */
public class FileNameEdit extends LTextField implements FolderListener, FileListener {

    /**
     * FileEditPanel constructor
     */
    public FileNameEdit() {

        // configuring nameField
        this.setForbiddenSigns("?*");
        this.setSize(200, 26);
        this.setFont(getFont().deriveFont(13.0f));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (osPan.isFoldersPanelVisible()) {
                    osPan.showFoldersPanel(FileNameEdit.this, false);
                }
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == '\n' && !e.isControlDown()) {
                    if (!getValue().equals("")) {
                        osPan.confirmAndClose();
                    }
                }
            }
        });
    }

    @Override
    public void valueChanged() {
        osPan.scrollToFileName(getValue());
    }

    /**
     * This sets the temporary text to display in the file name field. This text will be replaced
     * when the current folder is changed.
     * @param fName text to temporally display at the file name field
     */
    public void setValue(String fName) {
        super.setValue(fName.trim(), true);
    }

    /**
     * This catches the folder change events
     */
    @Override
    public void updateFolder() {
        setValue(current.getDisplayedFileName());
    }

    /**
     * This catches the current file change events
     */
    @Override
    public void updateFile() {
        setValue(current.getDisplayedFileName());
    }

}
