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

import static lib.lOpenSaveDialog.OpenSavePanel.current;
import static lib.lOpenSaveDialog.OpenSavePanel.imgFrameIcon;

/**
 * The OpenSave file chooser dialog.
 */
@SuppressWarnings("unused")
public class LOpenSaveDialog extends JDialog {

    /**
     * The owner of this dialog to place it in relative to the owner.
     */
    private final JFrame owner;

    /**
     * The mode of the dialog. It can be SAVE_MODE or OPEN_MODE.
     */
    public static int dialogMode;

    /**
     * A constant for the SAVE_MODE of the dialog.
     */
    public final static int SAVE_MODE = 101;

    /**
     * A constant for the OPEN_MODE of the dialog.
     */
    public final static int OPEN_MODE = 202;

    /**
     * The OpenSavePanel instance to access of all its routines from anywhere of the dialog.
     */
    public static OpenSavePanel osPan;

    /**
     * Creates the Open file dialog.
     * @param owner the owner frame.
     */
    public LOpenSaveDialog(JFrame owner) {
        this(owner, 202, "");
    }

    /**
     * Creates the OpenSave file dialog.
     * @param owner the owner frame.
     * @param mode the mode of the dialog. It can be OPEN_MODE of SAVE_MODE.
     */
    public LOpenSaveDialog(JFrame owner, int mode) {
        this(owner, mode, "");
    }

    /**
     * Creates the OpenSave file dialog.
     * @param owner the owner frame.
     * @param mode the mode of the dialog. It can be OPEN_MODE of SAVE_MODE.
     * @param fName the expected name of the file.
     */
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
        setIconImage(imgFrameIcon);
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

        getLayeredPane().add(osPan, 100);
//        getContentPane().add(osPan);


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

    /**
     * Sets or resets the mode of the dialog.
     * @param mode a new mode. It can be OPEN_MODE of SAVE_MODE.
     */
    public void setDialogMode(int mode) {
        dialogMode = (mode == SAVE_MODE) ? SAVE_MODE : OPEN_MODE;
        setTitle((dialogMode == SAVE_MODE)
                ? ResStrings.getString("strSaveFile") : ResStrings.getString("strOpenFile"));
        osPan.updateButtonCaption();
    }

    @Override
    public void setBackground(Color bg) {
        getContentPane().setBackground(bg);
        getLayeredPane().setBackground(bg);
    }

    @Override
    public void setForeground(Color fg) {
        getContentPane().setForeground(fg);
        getLayeredPane().setForeground(fg);
    }

    /**
     * Shows the OpenFile dialog.
     * @return an absolute path of the chosen file.
     */
    public String showOpenDialog() {
        setDialogMode(OPEN_MODE);
        setVisible(true);
        return current.getFilePath();
    }

    /**
     * Shows the SaveFile dialog.
     * @return an absolute path of the chosen file.
     */
    public String showSaveDialog() {
        setDialogMode(SAVE_MODE);
        setVisible(true);
        return current.getFilePath();
    }

    /**
     * Saves current settings of the dialog to the tubesolver settings.
     * Needs to be relocated.
     */
    public void saveOptions() {
        // todo: relocate this routine
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

    /**
     * Gets the stored folder from the tubesolver settings.
     * Needs to be relocated.
     * @return stored folder or the empty string.
     */
    public final String getStoredFolder() {
        // todo: relocate this routine
        if (Options.osdCurrentDir != null && !"".equals(Options.osdCurrentDir)) {
            File f = new File(Options.osdCurrentDir);
            if (f.exists() && f.isDirectory()) {
                return Options.osdCurrentDir;
            }
        }
        return "";
    }

}
