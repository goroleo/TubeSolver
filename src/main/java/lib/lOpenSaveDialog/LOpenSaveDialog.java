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

import core.Options;
import core.ResStrings;
import gui.Palette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static lib.lOpenSaveDialog.OpenSavePanel.*;

/**
 * OpenSave file chooser dialog.
 */
public class LOpenSaveDialog extends JDialog {

    private final JFrame owner;
    public static int dialogMode;
    public final static int SAVE_MODE = 101;
    public final static int OPEN_MODE = 202;

    public static OpenSavePanel osPan;

    @SuppressWarnings("unused")
    public LOpenSaveDialog() {
        this(null, 202, "");
    }

    public LOpenSaveDialog(JFrame owner) {
        this(owner, 202, "");
    }

    @SuppressWarnings("unused")
    public LOpenSaveDialog(String fName) {
        this(null, 202, fName);
    }

    @SuppressWarnings("unused")
    public LOpenSaveDialog(int mode) {
        this(null, mode, "");
    }

    public LOpenSaveDialog(JFrame owner, int mode) {
        this(owner, mode, "");
    }

    @SuppressWarnings("unused")
    public LOpenSaveDialog(int mode, String fName) {
        this(null, mode, fName);
    }

    public LOpenSaveDialog(JFrame owner, int mode, String fName) {
        super(owner, ResStrings.getString("strOpenFile"), true);

        dialogMode = (mode == SAVE_MODE) ? SAVE_MODE : OPEN_MODE;
        if (dialogMode == SAVE_MODE) {
            this.setTitle(ResStrings.getString("strSaveFile"));
        }

        if (!"".equals(getStoredFolder()))
            current.setFolder(new File(getStoredFolder()));

        osPan = new OpenSavePanel(this, true);
        addListeners();
        this.owner = owner;
        initFrame();

        current.setFile(fName);
    }

    private void initFrame() {
        setDefaultCloseOperation(0); // DO_NOTHING_ON_CLOSE
        setIconImage(lnFrameIcon);
        calculateSize();
        setMinimumSize(new Dimension(350, 300));

        Rectangle r = getGraphicsConfiguration().getBounds();
        if (Options.osdSizeX >= 300 && Options.osdSizeY >= 200
                && Options.osdSizeX <= r.width
                && Options.osdSizeY <= r.height) {
            setSize(Options.osdSizeX, Options.osdSizeY);
            osPan.setColumnWidths(Options.osdSizeX - 37, Options.osdSizeColS, Options.osdSizeColD);
        } else {
            // sets the default size
            setSize(480, 400);
        }

        calculatePos();
        setResizable(true);
        setLayout(null);
        setBackground(Palette.dialogColor);
        setForeground(Color.white);
        getContentPane().add(osPan);

//        osPan.setFolder(new File(getStoredFolder()));

    }

    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = osPan.getWidth();
        dim.height = osPan.getHeight();
        setPreferredSize(dim);
        pack();
        int realW = getContentPane().getWidth();
        int realH = getContentPane().getHeight();
        dim.width += (dim.width - realW);
        dim.height += (dim.height - realH);
        setPreferredSize(dim);
        pack();
    }

    private void calculatePos() {
        Rectangle r = getGraphicsConfiguration().getBounds();
        if (Options.osdPositionX >= 0 && Options.osdPositionY >= 0
                && Options.osdPositionX + getWidth() <= r.width
                && Options.osdPositionY + getHeight() <= r.height) {
            setLocation(Options.osdPositionX, Options.osdPositionY);
        } else if (owner != null) {
            setLocationRelativeTo(owner);
        } else {
            r.x = r.x + (r.width - getWidth()) / 2;
            r.y = r.y + (r.height - getHeight()) / 2;
            setLocation(r.x, r.y);
        }
    }

    @SuppressWarnings("MagicConstant")
    private void addListeners() {

        // CLOSE WINDOW click
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                osPan.refuseAndClose();
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> {
                    if (!osPan.isFoldersPanelVisible()) {
                        osPan.refuseAndClose();
                    } else {
                        osPan.showFoldersPanel(false);
                    }
                },
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> osPan.confirmAndClose(),
                KeyStroke.getKeyStroke('\n', 2), // VK_ENTER + MASK_CTRL
                2); // WHEN_IN_FOCUSED_WINDOW

        // Frame resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                osPan.setSize(getContentPane().getWidth(),
                        getContentPane().getHeight());
            }
        });

    }

    public void setDialogMode(int mode) {
        dialogMode = (mode == SAVE_MODE) ? SAVE_MODE : OPEN_MODE;
        setTitle((dialogMode == SAVE_MODE)
                ? ResStrings.getString("strSaveFile") : ResStrings.getString("strOpenFile"));
        osPan.updateButtonCaption();
    }

    @Override
    public void setBackground(Color bg) {
        getContentPane().setBackground(bg);
    }

    @Override
    public void setForeground(Color fg) {
        getContentPane().setForeground(fg);
    }

    public String showOpenDialog() {
        setDialogMode(OPEN_MODE);
        setVisible(true);
        return current.getFilePath();
    }

    public String showSaveDialog() {
        setDialogMode(SAVE_MODE);
        setVisible(true);
        return current.getFilePath();
    }

    public void saveOptions() {
        Options.osdPositionX = getX();
        Options.osdPositionY = getY();
        Options.osdSizeX = getWidth();
        Options.osdSizeY = getHeight();
        Options.osdSizeColN = osPan.getColumnWidth(1);
        Options.osdSizeColS = osPan.getColumnWidth(2);
        Options.osdSizeColD = osPan.getColumnWidth(3);
        Options.osdCurrentDir = current.getFolder().getAbsolutePath();
        Options.osdSortCol = osPan.getFileSortNumber();
        Options.osdSortOrder = osPan.getFileSortAscending() ? 1 : 0;
    }

    public final String getStoredFolder() {
        if (Options.osdCurrentDir != null && !"".equals(Options.osdCurrentDir)) {
            File f = new File(Options.osdCurrentDir);
            if (f.exists() && f.isDirectory()) {
                return Options.osdCurrentDir;
            }
        }
        return "";
    }

}
