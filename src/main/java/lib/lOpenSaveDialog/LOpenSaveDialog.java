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
import gui.Palette;
import core.ResStrings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import static lib.lOpenSaveDialog.OpenSavePanel.fsv;

/**
 *
 * @author leogoro
 *
 * Color dialog Window. it creates the window and place CoorPanel into the
 * window.
 *
 *
 */
public class LOpenSaveDialog extends JDialog {

    private final JFrame owner;
    private final int defaultWidth = 480; // width
    private final int defaultHeight = 400; // height
    private File file;
    public static int dialogMode;
    public final static int SAVE_MODE = 101;
    public final static int OPEN_MODE = 202;

    public static OpenSavePanel osPan;

    public LOpenSaveDialog() {
        this(null, 202, "");
    }

    public LOpenSaveDialog(JFrame owner) {
        this(owner, 202, "");
    }

    public LOpenSaveDialog(String fName) {
        this(null, 202, fName);
    }

    public LOpenSaveDialog(int mode) {
        this(null, mode, "");
    }

    public LOpenSaveDialog(JFrame owner, int mode) {
        this(owner, mode, "");
    }

    public LOpenSaveDialog(int mode, String fName) {
        this(null, mode, fName);
    }

    public LOpenSaveDialog(JFrame owner, int mode, String fName) {
        super(owner, ResStrings.getString("strOpenFile"), true);

        dialogMode = (mode == SAVE_MODE) ? SAVE_MODE : OPEN_MODE;
        if (dialogMode == SAVE_MODE) {
            this.setTitle(ResStrings.getString("strSaveFile"));
        }

        if (fName != null && !("".equals(fName))) {
            file = new File(fName);
            if (!file.isDirectory() && fsv.getParentDirectory(file) == null) {
                if (!getStoredFolder().equals("")) {
                    file = new File(getStoredFolder() + File.separator + fName);
                }
            }
        } else if (!getStoredFolder().equals("")) {
            file = new File(getStoredFolder() + File.separator + fName);
        } else {
            file = null;
        }

        osPan = new OpenSavePanel(this, file, true);
        addListeners();
        this.owner = owner;
        initFrame();
        if (!file.isDirectory()) {
            setFileName(file);
        }
    }

    private void initFrame() {
        setDefaultCloseOperation(0); // DO_NOTHING_ON_CLOSE
        setIconImage(OpenSavePanel.lnFrameIcon);
        calculateSize();

        Rectangle r = getGraphicsConfiguration().getBounds();
        if (Options.osdSizeX >= 300 && Options.osdSizeY >= 200
                && Options.osdSizeX <= r.width
                && Options.osdSizeY <= r.height) {
            setSize(Options.osdSizeX, Options.osdSizeY);
            osPan.restoreHeader(Options.osdSizeX - 37, Options.osdSizeColS, Options.osdSizeColD);
        } else {
            setSize(defaultWidth, defaultHeight);
        }

        calculatePos();
        setResizable(true);
        setLayout(null);
        setBackground(Palette.dialogColor);
        setForeground(Color.white);
        getContentPane().add(osPan);
        setMinimumSize(new Dimension(350, 300));
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
                    if (!osPan.isFoldersVisible()) {
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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                osPan.setSize(getContentPane().getWidth(),
                        getContentPane().getHeight());
            }
        });

    }

    public int getDialogMode() {
        return dialogMode;
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

    public String getFileName() {
        File f = osPan.getCurrentFile();
        if (f != null) {
            return f.getAbsolutePath();
        } else {
            return "";
        }
    }

    public final void setFileName(File f) {
        osPan.setFileName(f);
    }

    public String chooseFile() {
        setVisible(true);
        return getFileName();
    }

    public String showOpenDialog() {
        setDialogMode(OPEN_MODE);
        setVisible(true);
        return getFileName();
    }

    public String showSaveDialog() {
        setDialogMode(SAVE_MODE);
        setVisible(true);
        return getFileName();
    }

    public void saveOptions() {
        Options.osdPositionX = getX();
        Options.osdPositionY = getY();
        Options.osdSizeX = getWidth();
        Options.osdSizeY = getHeight();
        Options.osdSizeColN = osPan.getColumnWidth(1);
        Options.osdSizeColS = osPan.getColumnWidth(2);
        Options.osdSizeColD = osPan.getColumnWidth(3);
        Options.osdCurrentDir = osPan.getCurrentFolder().getAbsolutePath();
        Options.osdSortCol = osPan.getFileSortNumber();
        Options.osdSortOrder = osPan.getFileSortAscending() ? 1: 0;
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
