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

import java.awt.FontMetrics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JLabel;
import core.ResStrings;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.DEFAULT_EXT;
import static lib.lOpenSaveDialog.OpenSavePanel.current;

import lib.lTextFields.LTextField;

public class FileEditPanel extends JComponent implements FolderListener, FileListener {

    private final LTextField nameField;
    private final JLabel lbFile;
    private final JLabel lbExt;
    private int filedWidth = 120;

    public FileEditPanel() {
        setBackground(null);
        setForeground(null);

        nameField = new LTextField();
        nameField.setForbiddenSigns("?*");
        nameField.setSize(200, 26);
        nameField.setFont(nameField.getFont().deriveFont(13.0f));
        nameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (osPan.isFoldersVisible()) {
                    osPan.showFoldersPanel(false);
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

        lbFile = new JLabel(ResStrings.getString("strFile"));
        lbFile.setFont(lbFile.getFont().deriveFont(0));
        lbFile.setBackground(null);
        lbFile.setForeground(null);
        add(lbFile);

        lbExt = new JLabel(DEFAULT_EXT);
        lbExt.setFont(lbExt.getFont().deriveFont(1, 13.0f));
        lbExt.setBackground(null);
        lbExt.setForeground(null);
        add(lbExt);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (osPan.isFoldersVisible()) {
                    osPan.showFoldersPanel(false);
                }
            }
        }); // addFocusListener
    }

    @Override
    public void setSize(int w, int h) {
        h = nameField.getHeight();
        FontMetrics fm;

        if (lbFile != null) {
            fm = lbFile.getFontMetrics(lbFile.getFont());
            lbFile.setSize(fm.stringWidth(lbFile.getText()), fm.getHeight());
            lbFile.setLocation(0, (h - fm.getHeight()) / 2);
        }

        if (lbExt != null) {
            fm = lbExt.getFontMetrics(lbExt.getFont());
            lbExt.setSize(fm.stringWidth(lbExt.getText()), fm.getHeight());
            lbExt.setLocation(filedWidth + 20, (h - fm.getHeight()) / 2);
        }

        nameField.setLocation(10 + ((lbFile == null) ? 0 : lbFile.getWidth()), 0);
        nameField.setSize(filedWidth - nameField.getX() - 10, h);
        super.setSize(w, h);
    }

    public String getInputFieldValue() {
        return nameField.getValue();
    }
    
    public void setInputFieldValue(String fName, boolean replaceItem) {
        nameField.setValue(fName.trim(), true);
    }

    public void setInputFieldWidth(int value) {
        filedWidth = value;
    }

    @Override
    public void setFocusable(boolean b) {
        nameField.setFocusable(b);
    }

    @Override
    public void updateFolder(File folder) {
        nameField.setValue(current.getDisplayedFileName(), true);
    }

    @Override
    public void updateFile(String fileName) {
        nameField.setValue(current.getDisplayedFileName(), true);
    }
}
