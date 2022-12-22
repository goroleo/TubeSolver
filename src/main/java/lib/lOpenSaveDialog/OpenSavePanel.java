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

import dlg.MessageDlg;
import java.awt.Cursor;
import core.ResStrings;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;
import lib.lButtons.LPictureButton;
import static lib.lOpenSaveDialog.LOpenSaveDialog.dialogMode;

/**
 * This is the main panel to place all other color change controls inside. All
 * controls interact each other through this panel.
 */
public class OpenSavePanel extends JComponent {

    public static final FileSystemView fsv = FileSystemView.getFileSystemView();
    public static File currentFolder = fsv.getDefaultDirectory();
    private static File currentFile;

    private boolean showButtons = true;

    JLabel dirLabel = new JLabel(ResStrings.getString("strFolder"));

    public static FilesPanel filesPanel;
    public static ToolPanel toolPanel;
    public static FoldersPanel foldersPanel;
    public static FolderChooser folderName;
    public static FileEditPanel fileName;

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
    public static BufferedImage lnFrameIcon;    // Dialog icon )) 
    public static BufferedImage imgBtnDown;    // Dialog icon )) 
    public static BufferedImage imgBtnUp;    // Dialog icon )) 
    public static ImageIcon jctlIcon;             // Icon for JCTL files
    public Cursor cursorResize;           // cursor for FileListHeader 

    /**
     * Constructor. <br>
     *
     * @param ownerFrame the parent dialog frame used to access it from other
     * controls (i.e. from buttons).
     * @param file file to set as default
     * @param showButtons true or false
     */
    public OpenSavePanel(JDialog ownerFrame, File file, boolean showButtons) {
        dlgFrame = ownerFrame;
        setBackground(null);
        setForeground(null);
        loadResources();

        if (file != null) {
            if (file.isDirectory()) {
                currentFolder = file;
                currentFile = null;
            } else if (file.getParentFile() != null) {
                currentFile = file;
                currentFolder = file.getParentFile();
                
            }
        }

        this.showButtons = showButtons;
        dirLabel.setBackground(null);
        dirLabel.setForeground(null);
        dirLabel.setFont(dirLabel.getFont().deriveFont(0, 12.0f));
        add(dirLabel);

        foldersPanel = new FoldersPanel(currentFolder);
        add(foldersPanel);

        filesPanel = new FilesPanel(currentFolder);
        add(filesPanel);

        folderName = new FolderChooser(currentFolder);
        add(folderName);

        toolPanel = new ToolPanel();
        add(toolPanel);

        fileName = new FileEditPanel(file);
        add(fileName);

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
        }

        if (folderName != null) {
            assert dirLabel != null;
            folderName.setLocation(20 + dirLabel.getWidth(), 10);
            folderName.setSize(w - 40 - toolPanel.getWidth() - dirLabel.getWidth(), 26);
        }

        if (toolPanel != null) {
            toolPanel.setLocation(w - 10 - toolPanel.getWidth(), 12);
        }

        if (foldersPanel != null) {
            assert folderName != null;
            foldersPanel.setLocation(folderName.getX(), folderName.getY() + folderName.getHeight() + 2);
            foldersPanel.setSize(folderName.getWidth(), foldersPanel.getItemHeight() * 8 + 4);
        }

        if (fileName != null) {
            assert folderName != null;
            fileName.setLocation(10, getHeight() - ((showButtons) ? 45 : 0) - 20 - fileName.getHeight());
            fileName.setEndOftext(folderName.getX() + folderName.getWidth() - 10);
            fileName.setSize(w - 20, fileName.getHeight());
        }

        if (filesPanel != null) {
            assert folderName != null;
            assert fileName != null;
            filesPanel.setLocation(10, folderName.getY() + folderName.getHeight() + 10);
            filesPanel.setSize(w - 20, fileName.getY() - 10 - filesPanel.getY());
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

    public void setDialogMode() {

    }

    public void folderUp() {
        setCurrentFolder(fsv.getParentDirectory(currentFolder));
    }

    public void folderRefresh() {
        filesPanel.folderRefresh();
    }

    public void createNewFolder() {
        NewFolderDialog newFolderDlg = new NewFolderDialog(dlgFrame, currentFolder);
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
        if (fileName != null) {
            if (f != null) {
                if (f.isDirectory()) {
                    fileName.setDirName(fsv.getSystemDisplayName(f));
                } else {
                    fileName.setFileName(fsv.getSystemDisplayName(f));
                }
            } else {
                fileName.setFileName("");
            }
        }
    }

    public String getFile() {
        return fileName.getFileName();
    }

    public String getFileExt(String filename) {
        String[] ss = filename.split("\\.");
        if (ss.length > 1) {
            return "." + ss[ss.length - 1];
        } else {
            return "";
        }
    }

    public void fileNameChange() {
        filesPanel.scrollToFile(fileName.getFileName());
    }

    public File getCurrentFolder() {
        return currentFolder;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public int getColumnWidth(int colNumber) {
        return filesPanel.getColumnWidth(colNumber);
    }

    public void setCurrentFolder(File folder) {
        if (folder != null) {
            if (folder.isFile()) {
                folder = fsv.getParentDirectory(folder);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            currentFolder = folder;
            foldersPanel.setFolder(currentFolder);
            filesPanel.setFolder(currentFolder);
            folderName.setFolder(folder);
            fileName.restoreFileName();
            setCursor(Cursor.getDefaultCursor());
        }
    }

    public boolean isFoldersVisible() {
        return foldersPanel.isVisible();
    }

    public void setFileSorting(int number, boolean ascending) {
        filesPanel.setSorting(number, ascending);
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
        fileName.setFocusable(!b);
        toolPanel.setFocusable(!b);
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
     * The routine will close the frame. Current color is the chosen color
     * already. Call this routine when Ok button click.
     *
     * @see #dlgFrame
     */
    public void confirmAndClose() {

        File f;
        boolean doClose = false;
        String path;

        if (!isFoldersVisible()) {

            String fName = fileName.getFileName();

            // check if the full absolute path is in the file name
            f = new File(fName);
            if (f.exists() && f.isDirectory()) {
                setCurrentFolder(f);
                fileName.restoreFileName();
                return;
            }

            // check if the relative path is in the file name
            f = fsv.getChild(currentFolder, fName);
            if (f != null && f.exists() && f.isDirectory()) {
                setCurrentFolder(f);
                fileName.restoreFileName();
                return;
            }

            // Not a path is stored in the file name. Or at least not only a path ) 
            f = new File(fName);
            path = f.getParent();
            fName = f.getName();

            // check if a path (absolute or relative) is stored there
            if (path != null && !"".equals(path)) {
                f = new File(path);
                if (f.exists() && f.isDirectory()) {
                    setCurrentFolder(f);
                } else {
                    f = fsv.getChild(currentFolder, path);
                    if (f != null && f.exists() && f.isDirectory()) {
                        setCurrentFolder(f);
                    } else {
                        errorMessage(path + "\n\n" + ResStrings.getString("strPathNotFound"));
                        return;
                    }
                }
            }

            // an finally we can process the real file name
            if (getDialogMode() == LOpenSaveDialog.OPEN_MODE) {

                f = fsv.getChild(currentFolder, fName);
                if (f != null && f.exists()) {
                    doClose = true;
                    currentFile = f;
                    System.out.println("open file " + f.getAbsolutePath());
                } else {
                    fName = fName + DEFAULT_EXT;
                    f = fsv.getChild(currentFolder, fName);
                    if (f != null) {
                        if (f.exists()) {
                            doClose = true;
                            currentFile = f;
                            System.out.println("open file " + f.getAbsolutePath());
                        } else {
                            errorMessage(fName + "\n\n" + ResStrings.getString("strFileNotFound"));
                        }
                    }
                }

            } else {  // lnOpenSaveDialog.SAVE_MODE

                if (!getFileExt(fName).equalsIgnoreCase(DEFAULT_EXT)) {
                    fName = fName + DEFAULT_EXT;
                }
                f = fsv.getChild(currentFolder, fName);
                if (f != null) {
                    if (f.exists()) {
                        if (questionMessage(f.getName() + "\n\n" + ResStrings.getString("strFileExists"))) {
                            doClose = true;
                            currentFile = f;
                            System.out.println("save file " + f.getAbsolutePath());
                        }
                    } else { // file is not exists
                        doClose = true;
                        currentFile = f;
                        System.out.println("save file " + f.getAbsolutePath());
                    }
                }
            }

            if (doClose) {
                EventQueue.invokeLater(() -> dlgFrame.dispose());
                ((LOpenSaveDialog) dlgFrame).saveOptions();
            }
        }
    }

    private void errorMessage(String msg) {
        MessageDlg msgDlg = new MessageDlg(null,
                msg,
                MessageDlg.BTN_OK);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
        msgDlg.setTitle(dlgFrame.getTitle());
        msgDlg.setLocationRelativeTo(this);
        msgDlg.setVisible(true);
    }

    private boolean questionMessage(String msg) {
        MessageDlg msgDlg = new MessageDlg(null,
                msg,
                MessageDlg.BTN_YES_NO);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
        msgDlg.setTitle(dlgFrame.getTitle());
        msgDlg.setLocationRelativeTo(this);
        msgDlg.setVisible(true);

        return msgDlg.modalResult > 0;
    }

    public void refuseAndClose() {
        if (!isFoldersVisible()) {
            currentFile = null;
            EventQueue.invokeLater(() -> dlgFrame.dispose());
            ((LOpenSaveDialog) dlgFrame).saveOptions();
        }
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
