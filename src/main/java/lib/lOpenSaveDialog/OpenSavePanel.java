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
import dlg.MessageDlg;
import lib.lButtons.LPictureButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static lib.lOpenSaveDialog.LOpenSaveDialog.dialogMode;

/**
 * This is the main panel to place all other color change controls inside. All
 * controls interact each other through this panel.
 */
public class OpenSavePanel extends JComponent {

    public static final FileSystemView fsv = FileSystemView.getFileSystemView();
    public static FileChanger current = new FileChanger();
    //    private static File currentFile;
    private boolean showButtons = true;
    JLabel dirLabel = new JLabel(ResStrings.getString("strFolder"));
    private static FilesPanel filesPanel;
    private static ToolButtonsPanel toolsPanel;
    private static FoldersPanel foldersPanel;
    private static FolderDropDown foldersDropDown;
    private static FileEditPanel fileEditPanel;

    public static final String DEFAULT_EXT = ".jctl";

    /**
     * Dialog controls: OK button. The color panel has an option to hide
     * buttons, using <b>showButtons</b>
     * parameter on the constructor.
     */
    private static LPictureButton btnOk;

    /**
     * Dialog controls: CANCEL button. The color panel has an option to hide
     * buttons, using <b>showButtons</b>
     * parameter on the constructor.
     */
    private static LPictureButton btnCancel;

    public static JDialog dlgFrame;
    public static BufferedImage lnFrameIcon;   // Dialog icon ))
    public static BufferedImage imgBtnDown;    // ButtonDown icon for folderName
    public static BufferedImage imgBtnUp;      // ButtonUp icon for folderName  ))
    public static ImageIcon jctlIcon;          // Icon for JCTL files
    public Cursor cursorResize;                // cursor for FileListHeader

    /**
     * Constructor. <br>
     *
     * @param ownerFrame  the parent dialog frame used to access it from other
     *                    controls (i.e. from buttons).
     * @param showButtons true or false
     */
    public OpenSavePanel(JDialog ownerFrame, boolean showButtons) {
        dlgFrame = ownerFrame;
        setBackground(null);
        setForeground(null);
        loadResources();

        this.showButtons = showButtons;
        dirLabel.setBackground(null);
        dirLabel.setForeground(null);
        dirLabel.setFont(dirLabel.getFont().deriveFont(0, 12.0f));
        add(dirLabel);

        foldersPanel = new FoldersPanel();
        current.addFolderListener(foldersPanel);
        add(foldersPanel);

        filesPanel = new FilesPanel();
        current.addFolderListener(filesPanel);
        add(filesPanel);

        foldersDropDown = new FolderDropDown();
        current.addFolderListener(foldersDropDown);
        add(foldersDropDown);

        toolsPanel = new ToolButtonsPanel();
        add(toolsPanel);

        fileEditPanel = new FileEditPanel();
        current.addFolderListener(fileEditPanel);
        current.addFileListener(fileEditPanel);
        add(fileEditPanel);

        if (showButtons) {
            btnOk = addButton((dialogMode == LOpenSaveDialog.SAVE_MODE)
                    ? ResStrings.getString("strSave") : ResStrings.getString("strOpen"));
            btnOk.addActionListener((ActionEvent e) -> confirmAndClose());
            add(btnOk);

            btnCancel = addButton(ResStrings.getString("strCancel"));
            btnCancel.addActionListener((ActionEvent e) -> refuseAndClose());
            add(btnCancel);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isFoldersVisible()) {
                    showFoldersPanel(false);
                }
            }
        });

        showFoldersPanel(false);
        if (Options.osdSortCol > 0 && Options.osdSortCol < 4
                && Options.osdSortOrder >= 0 && Options.osdSortOrder <= 1) {
            setFileSorting(Options.osdSortCol, Options.osdSortOrder == 1);
        }

        filesPanel.requestFocus();
    }

    public void updateComponents() {
        int w = getWidth();
        int h = getHeight();
        FontMetrics fm;
        if (showButtons) {
            if (btnOk != null) {
                btnOk.setLocation(w - 215, h - 45);
            }
            if (btnCancel != null) {
                btnCancel.setLocation(w - 105, h - 45);
            }
        }
        if (dirLabel != null) {
            fm = dirLabel.getFontMetrics(dirLabel.getFont());
            dirLabel.setSize(fm.stringWidth(dirLabel.getText()), fm.getHeight());
            dirLabel.setLocation(10, 10 + (26 - fm.getHeight()) / 2);

            if (toolsPanel != null) {
                toolsPanel.setLocation(w - 10 - toolsPanel.getWidth(), 12);
            }

            if (foldersDropDown != null) {
                foldersDropDown.setLocation(20 + dirLabel.getWidth(), 10);
                foldersDropDown.setSize(w - 40 - toolsPanel.getWidth() - dirLabel.getWidth(), 26);

                if (foldersPanel != null) {
                    foldersPanel.setLocation(foldersDropDown.getX(), foldersDropDown.getY() + foldersDropDown.getHeight() + 2);
                    foldersPanel.setSize(foldersDropDown.getWidth(), foldersPanel.getItemHeight() * 8 + 4);
                }

                if (fileEditPanel != null) {
                    fileEditPanel.setLocation(10, getHeight() - ((showButtons) ? 45 : 0) - 20 - fileEditPanel.getHeight());
                    fileEditPanel.setInputFieldWidth(foldersDropDown.getX() + foldersDropDown.getWidth());
                    fileEditPanel.setSize(w - 20, fileEditPanel.getHeight());

                    if (filesPanel != null) {
                        filesPanel.setLocation(10, foldersDropDown.getY() + foldersDropDown.getHeight() + 10);
                        filesPanel.setSize(w - 20, fileEditPanel.getY() - 10 - filesPanel.getY());
                    }
                }
            }
        }
    }

    public void restoreHeader(int name, int size, int date) {
        if (size >= 50 && date >= 50 && name >= 50) {
            filesPanel.restoreHeader(name, size, date);
        }
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        updateComponents();
    }

    public int getDialogMode() {
        return ((LOpenSaveDialog) dlgFrame).getDialogMode();
    }

    public void upFolder() {
        current.upFolder();
    }

    public void folderRefresh() {
        filesPanel.folderRefresh();
    }

    public void createNewFolder() {
        NewFolderDialog newFolderDlg = new NewFolderDialog(dlgFrame, current.getFolder());
        newFolderDlg.setVisible(true);
        setCurrentFolder(newFolderDlg.getFolder());
    }

    public void updateButtonCaption() {
        btnOk.setText(
                (dialogMode == LOpenSaveDialog.SAVE_MODE)
                        ? ResStrings.getString("strSave")
                        : ResStrings.getString("strOpen"));

    }

    public void setFileName(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                fileEditPanel.setInputFieldValue(f.getName(), true);
            } else {
                current.setFile(f.getName());
            }
        } else {
            current.setFile("");
        }
    }

    public int getColumnWidth(int colNumber) {
        return filesPanel.getColumnWidth(colNumber);
    }

    public void setCurrentFolder(File folder) {
        if (folder != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (folder.isFile()) {
                folder = fsv.getParentDirectory(folder);
            }

            current.setFolder(folder);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    public boolean isFoldersVisible() {
        return foldersPanel.isVisible();
    }

    public void setFileSorting(int number, boolean ascending) {
        filesPanel.sortFileList(number, ascending);
    }

    public int getFileSortNumber() {
        return filesPanel.getSortNumber();
    }

    public boolean getFileSortAscending() {
        return filesPanel.getSortAscending();
    }

    public void showFoldersPanel(boolean b) {
        foldersPanel.setVisible(b);
        filesPanel.setFocusable(!b);
        btnOk.setFocusable(!b);
        btnCancel.setFocusable(!b);
        fileEditPanel.setFocusable(!b);
        toolsPanel.setFocusable(!b);
        if (b) {
            foldersPanel.requestFocus();
        } else {
            filesPanel.requestFocus();
        }
    }

    private LPictureButton addButton(String txt) {
        LPictureButton btn = new LPictureButton(this);
        btn.setText(txt);
        btn.setBackground(null);
        btn.setForeground(null);
        btn.setFocusable(true);
        return btn;
    }

    /**
     * This routine calls when Ok button clicked. <br>
     * First, this routine will check the input field for a folder or path. Second, it checks if
     * the specified file exists. And finally, if all conditions are met, the routine will set
     * the <i>currentFile</i> field and close the frame.
     */
    public void confirmAndClose() {

        File f;
        String fName;
        boolean doClose = false;

        if (isFoldersVisible()) {
            showFoldersPanel(false);
            return;
        }

        fName = fileEditPanel.getInputFieldValue().trim();
        if ("".equals(fName)) return;

        // first we'll check if the folder is in the path string
        try {
            // after this fName will be the file name without the path
            fName = current.setFolder(fName);

        } catch (IOException e) {
            // setFolder will raise an exception if the folder path has not found.
            // In this case we cannot go on
            String strError = ResStrings.getString("strPathNotFound");
            if (e.getMessage() != null && !"".equals(e.getMessage()))
                strError = e.getMessage() + "\n\n" + strError;
            outErrorMsg(strError);
            return;
        }

        // after checking and setting a specified folder we can proceed with the last fName
        if (!fName.equals("")) {

            current.setFile(fName);

            if (((LOpenSaveDialog) dlgFrame).getDialogMode()
                    == LOpenSaveDialog.OPEN_MODE) {

                f = current.findExistingFile();
                if (f != null) { // file is exist
                    doClose = true;
                } else { // file is not exist
                    outErrorMsg(current.getFileName() + "\n\n" + ResStrings.getString("strFileNotFound"));
                }

            } else {  // lnOpenSaveDialog.SAVE_MODE

                //
                current.addDefaultExt();
                f = current.findExistingFile();

                if (f != null) { // the file is already exists!
                    // we have to ask the user for permission to rewrite it.
                    if (outQuestionMsg(current.getFileName() + "\n\n" + ResStrings.getString("strFileExists"))) {
                        doClose = true;
                    }
                } else { // file is not exists,
                    // so we can write it right now
                    doClose = true;
                }
            }
        }

        if (doClose) {
            EventQueue.invokeLater(() -> dlgFrame.dispose());
            ((LOpenSaveDialog) dlgFrame).saveOptions();
        }
    }

    public void refuseAndClose() {
        if (!isFoldersVisible()) {
            current.setFile("");
            EventQueue.invokeLater(() -> dlgFrame.dispose());
            ((LOpenSaveDialog) dlgFrame).saveOptions();
        }
    }

    private void outErrorMsg(String msg) {
        MessageDlg msgDlg = new MessageDlg(null,
                msg,
                MessageDlg.BTN_OK);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
        msgDlg.setTitle(dlgFrame.getTitle());
        msgDlg.setLocationRelativeTo(this);
        msgDlg.setVisible(true);
    }

    private boolean outQuestionMsg(String msg) {
        MessageDlg msgDlg = new MessageDlg(null,
                msg,
                MessageDlg.BTN_YES_NO);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
        msgDlg.setTitle(dlgFrame.getTitle());
        msgDlg.setLocationRelativeTo(this);
        msgDlg.setVisible(true);

        return msgDlg.modalResult > 0;
    }

/////////////////////////////////////////////////////////
//         
//         Resources loader     
//         
/////////////////////////////////////////////////////////
    private void loadResources() {
        imgBtnDown = createBufImage("btnTool22_down.png");
        imgBtnUp = createBufImage("btnTool22_up.png");
        lnFrameIcon = createBufImage("lnopensavedialog_icon.png");
        cursorResize = Toolkit.getDefaultToolkit().createCustomCursor(
                createBufImage("filelist_cursor_resize.png"),
                new Point(10, 10), "");
        jctlIcon = createIconImage("appicon_16.png");
    }

    private BufferedImage createBufImage(String FName) {
        java.net.URL url = this.getClass().getResource("/img/" + FName);
        if (url == null) {
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException ex) {
            return null;
        }
    }

    private ImageIcon createIconImage(String FName) {
        java.net.URL url = this.getClass().getResource("/img/" + FName);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            return null;
        }
    }

}
