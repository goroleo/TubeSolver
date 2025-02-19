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
    public static OpenSavePanel osPanel;

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
     * @param mode the mode of the dialog. It can be OPEN_MODE or SAVE_MODE.
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

        osPanel = new OpenSavePanel(this, true);
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
            osPanel.setColumnWidths(Options.osdSizeX - 37, Options.osdSizeColS, Options.osdSizeColD);
        } else {
            // sets the default size
            setSize(480, 400);
        }

        calculatePos();
        setResizable(true);
        setLayout(null);
        setBackground(Palette.dialogColor);
        setForeground(Color.white);

        getLayeredPane().add(osPanel, 100);
    }

    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = osPanel.getWidth();
        dim.height = osPanel.getHeight();
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
                osPanel.refuseAndClose();
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> {
                    if (!osPanel.isFoldersPanelVisible()) {
                        osPanel.refuseAndClose();
                    } else {
                        osPanel.showFoldersPanel(false);
                    }
                },
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> osPanel.confirmAndClose(),
                KeyStroke.getKeyStroke('\n', InputEvent.CTRL_DOWN_MASK), // VK_ENTER + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // Frame resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                osPanel.setSize(getContentPane().getWidth(),
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
        osPanel.updateButtonCaption();
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
     * Sets the default file name. Use it before showing the dialog.
     *
     * @param fName file name
     */
    public void setFileName(String fName) {
        current.setFile(fName);
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
     * Saves current settings of the dialog to the application settings.
     * ?Needs to be relocated?
     */
    public void saveOptions() {
        // todo: relocate this routine
        Options.osdPositionX = getX();
        Options.osdPositionY = getY();
        Options.osdSizeX = getWidth();
        Options.osdSizeY = getHeight();
        Options.osdSizeColN = osPanel.getColumnWidth(1);
        Options.osdSizeColS = osPanel.getColumnWidth(2);
        Options.osdSizeColD = osPanel.getColumnWidth(3);
        Options.osdCurrentDir = current.getFolder().getAbsolutePath();
        Options.osdSortCol = osPanel.getFileSortNumber();
        Options.osdSortOrder = osPanel.getFileSortAscending() ? 1 : 0;
    }

    /**
     * Gets the stored folder from the application settings.
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
