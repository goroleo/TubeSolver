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
import java.util.Objects;

import static lib.lOpenSaveDialog.LOpenSaveDialog.dialogMode;

/**
 * This is the main panel to place all other controls inside. All controls interact
 * each other through this panel.
 */
public class OpenSavePanel extends JPanel {

    /**
     * Default file extension.
     */
    public static final String DEFAULT_EXT = ".jctl";

    /**
     * Access to the current file system.
     *
     * @see FileSystemView
     */
    public static final FileSystemView fsv = FileSystemView.getFileSystemView();

    /**
     * An object that stores the current folder and current file. It manages folders,
     * navigates folders, and notifies all dialog controls when the current folder
     * and current file have changed.
     */
    public static final FolderChanger current = new FolderChanger();

    /**
     * Dialog controls: A Tools Panel. Displays a small panel with tool buttons.
     */
    private final ToolsPanel toolsPanel;

    /**
     * Dialog controls: A Folder ComboBox control. Displays the current folder in a combo box.
     */
    private final FolderComboBox folderComboBox;

    /**
     * Dialog controls: A Folders Panel. Displays the drop-down list of folders below the current folders name.
     */
    private final FoldersPanel foldersPanel;

    /**
     * Dialog controls: A Files Panel. Displays the current folder's list of files.
     */
    private final FilesPanel filesPanel;

    /**
     * Dialog controls: A File Edit Panel. Displays and edits a name of the current file.
     */
    private final FileNameEdit FileNameEdit;

    /**
     * Dialog controls: OK button. The dialog panel has an option to hide
     * buttons, using <b>showButtons</b> parameter at the constructor.
     */
    private LPictureButton btnOk;

    /**
     * Dialog controls: CANCEL button. The dialog panel has an option to hide
     * buttons, using <b>showButtons</b> parameter at the constructor.
     */
    private LPictureButton btnCancel;

    /**
     * Parent dialog frame used to access it from other controls (i.e. from buttons).
     */
    public final JDialog dlgFrame;

    /**
     * An icon of this dialog frame
     */
    public static BufferedImage imgFrameIcon;

    /**
     * An ButtonDown image for foldersComboBox and FileList sort indicator
     */
    public static BufferedImage imgBtnDown;

    /**
     * An ButtonUp image for foldersDropDown and FileList sort indicator
     */
    public static BufferedImage imgBtnUp;

    /**
     * JCTL files icon image
     */
    public static ImageIcon jctlIcon;

    /**
     * A special cursor image for FileListHeader
     */
    public static Cursor cursorResize;

    /**
     * Constructor. <br>
     *
     * @param ownerFrame  the parent dialog frame used to access it from other
     *                    controls (i.e. from buttons).
     * @param showButtons true or false
     */
    @SuppressWarnings("MagicConstant")
    public OpenSavePanel(JDialog ownerFrame, boolean showButtons) {
        dlgFrame = ownerFrame;
        setBackground(null);
        setForeground(null);
        loadResources();

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(10, 10));

        // create folders panel and store it to the upper level
        foldersPanel = new FoldersPanel();
        ownerFrame.getLayeredPane().add(foldersPanel, 200);

        // create all other controls
        filesPanel = new FilesPanel();
        folderComboBox = new FolderComboBox();
        toolsPanel = new ToolsPanel();
        FileNameEdit = new FileNameEdit();

        // create labels
        JLabel folderLabel = new JLabel(ResStrings.getString("strFolder"));
        folderLabel.setBackground(null);
        folderLabel.setForeground(null);
        folderLabel.setFont(folderLabel.getFont().deriveFont(0, 12.0f));

        JLabel fileLabel = new JLabel(ResStrings.getString("strFile"));
        fileLabel.setBackground(null);
        fileLabel.setForeground(null);
        fileLabel.setFont(folderLabel.getFont().deriveFont(0, 12.0f));

        JLabel extLabel = new JLabel(DEFAULT_EXT);
        extLabel.setBackground(null);
        extLabel.setForeground(null);
        extLabel.setHorizontalAlignment(0);
        extLabel.setFont(folderLabel.getFont().deriveFont(1, 13.0f));
        extLabel.setPreferredSize(toolsPanel.getPreferredSize());


        // create buttons
        JPanel buttonsLine = new JPanel();
        if (showButtons) {
            btnOk = new LPictureButton(this);
            btnOk.setBackground(null);
            btnOk.setForeground(null);
            updateButtonCaption();
            btnOk.addActionListener((ActionEvent e) -> confirmAndClose());
            btnOk.setPreferredSize(btnOk.getSize());

            btnCancel = new LPictureButton(this);
            btnCancel.setText(ResStrings.getString("strCancel"));
            btnCancel.setBackground(null);
            btnCancel.setForeground(null);
            btnCancel.addActionListener((ActionEvent e) -> refuseAndClose());
            btnCancel.setPreferredSize(btnCancel.getSize());

            buttonsLine.setBackground(null);
            buttonsLine.setForeground(null);
            buttonsLine.setLayout(new BoxLayout(buttonsLine, BoxLayout.X_AXIS));

            buttonsLine.add(Box.createHorizontalGlue());
            buttonsLine.add(btnOk);
            buttonsLine.add(Box.createRigidArea(new Dimension(15, btnOk.getHeight() + 10)));
            buttonsLine.add(btnCancel);
        }


        //////  creating top line
        JPanel topLine = new JPanel(new BorderLayout(10, 10));
        topLine.setBackground(null);
        topLine.setForeground(null);
        topLine.add(folderLabel, BorderLayout.WEST);
        topLine.add(folderComboBox, BorderLayout.CENTER);
        topLine.add(toolsPanel, BorderLayout.EAST);

        //////  creating bottom line
        JPanel bottomLine = new JPanel(new BorderLayout(10, 10));
        bottomLine.setBackground(null);
        bottomLine.setForeground(null);
        bottomLine.add(fileLabel, BorderLayout.WEST);
        bottomLine.add(FileNameEdit, BorderLayout.CENTER);
        bottomLine.add(extLabel, BorderLayout.EAST);

        if (showButtons)
            bottomLine.add(buttonsLine, BorderLayout.SOUTH);

        // add controls to the layout manager
        this.add(topLine, BorderLayout.NORTH);
        this.add(filesPanel, BorderLayout.CENTER);
        this.add(bottomLine, BorderLayout.SOUTH);

        // add listeners after all components are created
        current.addFolderListener(foldersPanel);
        current.addFolderListener(filesPanel);
        current.addFolderListener(folderComboBox);
        current.addFolderListener(FileNameEdit);
        current.addFileListener(FileNameEdit);

        if (Options.osdSortCol > 0 && Options.osdSortCol < 4
                && Options.osdSortOrder >= 0 && Options.osdSortOrder <= 1) {
            sortFileList(Options.osdSortCol, Options.osdSortOrder == 1);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isFoldersPanelVisible()) {
                    showFoldersPanel(false);
                }
            }
        });

        showFoldersPanel(false);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);

        // Folders panel is on the upper layer, and it can't resize automatically using layout manager.
        foldersPanel.setBounds(folderComboBox.getX() + 10, // + 10 is a border
                folderComboBox.getY() + folderComboBox.getHeight() + 2 + 10, // + 10 is a border
                folderComboBox.getWidth(),
                200);
    }

    /**
     * @return a state of the FoldersPanel visibility, true or false
     */
    public boolean isFoldersPanelVisible() {
        return foldersPanel.isVisible();
    }

    /**
     * Shows or hides Folders Panel.
     *
     * @param b <i>true</i> to show the folders panel, <i>false</i> to hide it.
     */
    public void showFoldersPanel(boolean b) {
        showFoldersPanel(null, b);
    }

    /**
     * Shows and hides Folders Panel.
     *
     * @param invoker a component that will be focused after the folder panel will hide.
     *                If Invoker is null, the <i>FilesList</i> will be focused.
     *                This parameter used if FoldersPanel goes to hide only.
     * @param b       <i>true</i> to show the folders panel, <i>false</i> to hide it.
     */
    public void showFoldersPanel(JComponent invoker, boolean b) {
        if (foldersPanel.isVisible() != b) {
            foldersPanel.setVisible(b);
            foldersPanel.setFocusable(b);
            toolsPanel.setFocusable(!b);
            filesPanel.setFocusable(!b);
            FileNameEdit.setFocusable(!b);
            if (btnOk != null) {
                btnOk.setFocusable(!b);
                btnCancel.setFocusable(!b);
            }
            if (b) {
                foldersPanel.requestFocus();
            } else {
                Objects.requireNonNullElse(invoker, filesPanel).requestFocus();
            }
        }
    }

    /**
     * Gets a width of the one of FileList columns:<ul>
     * <li>1 - File Name column
     * <li>2 - File Size column
     * <li>3 - File Date column</ul>
     *
     * @param colNumber the number of the column as described
     * @return a specified column's width
     */
    public int getColumnWidth(int colNumber) {
        return filesPanel.getColumnWidth(colNumber);
    }

    /**
     * Sets the column widths of FileList.
     *
     * @param name FileName column width
     * @param size FileSize column width
     * @param date FileDate column width
     */
    public void setColumnWidths(int name, int size, int date) {
        if (size >= 50 && date >= 50 && name >= 50) {
            filesPanel.setColumnWidths(name, size, date);
        }
    }

    /**
     * Updates and refresh the current folder, reload the FilesList contents.
     */
    public void refreshFolder() {
        filesPanel.updateFolder();
    }

    /**
     * Displays the CreateNewFolder dialog.
     */
    public void createNewFolder() {
        NewFolderDialog newFolderDlg = new NewFolderDialog(dlgFrame);
        newFolderDlg.setVisible(true);
    }

    /**
     * Sets a new text at the FileEditPanel field when some FilesList item is selected.
     *
     * @param f a file selected at the FileList
     */
    public void setFileName(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                FileNameEdit.setValue(f.getName());
            } else {
                current.setFile(f.getName());
            }
        } else {
            current.setFile("");
        }
    }

    /**
     * Scrolls the FilesList to the specified file name.
     *
     * @param fn a file name
     */
    public void scrollToFileName(String fn) {
        filesPanel.scrollToFile(fn);
    }

    /**
     * Sorts the FilesList.
     *
     * @param number    column number to sort the FileList
     * @param ascending true for ascending order, false for descending.
     */
    public void sortFileList(int number, boolean ascending) {
        filesPanel.sortFileList(number, ascending);
    }

    /**
     * Returns the column number of the File List with the current sort.
     *
     * @return the column number
     */
    public int getFileSortNumber() {
        return filesPanel.getSortNumber();
    }

    /**
     * Returns the current sort order of the FileList.
     *
     * @return true for ascending order, false to descending
     */
    public boolean getFileSortAscending() {
        return filesPanel.getSortAscending();
    }

    /**
     * Updates OK button caption due to change a dialog mode
     */
    public void updateButtonCaption() {
        if (btnOk != null) {
            btnOk.setText((dialogMode == LOpenSaveDialog.SAVE_MODE)
                    ? ResStrings.getString("strSave")
                    : ResStrings.getString("strOpen"));
        }
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

        if (isFoldersPanelVisible()) {
            showFoldersPanel(false);
            return;
        }

        // reading the current file/path name value from fileEditPanel
        fName = FileNameEdit.getValue().trim();
        if ("".equals(fName)) {
            return;
        }

        try {
            // first we'll check if the folder is in the path string
            // after this fName will be the file name without any folders path
            fName = current.setFolder(fName);

        } catch (IOException e) {
            // setFolder will raise an exception if the folder path has not found.
            // In this case we cannot go on
            String strError = ResStrings.getString("strPathNotFound");
            if (e.getMessage() != null && !"".equals(e.getMessage()))
                strError = e.getMessage() + "\n\n" + strError;
            outErrorMsg(strError);
            return;
        } // that's enough about folders

        // after checking and setting a specified folder we can proceed with the last fName
        if (!"".equals(fName)) {

            // sets the rest part of fName to current file name
            current.setFile(fName);

            // And now depending on the Dialog Mode
            if (dialogMode == LOpenSaveDialog.OPEN_MODE) {

                // checks if a file exists, optionally adding a default extension
                f = current.findExistingFile();

                if (f != null) { // file is exist
                    doClose = true;
                } else { // file is not exist
                    outErrorMsg(current.getFileName() + "\n\n"
                            + ResStrings.getString("strFileNotFound"));
                }

            } else {  // lnOpenSaveDialog.SAVE_MODE

                // adding a default extension to the file name if necessary
                current.addDefaultExt();

                // checking if the file exists
                f = current.findExistingFile();

                if (f != null) { // the file is already exists!
                    // we have to ask the user for permission to overwrite it.
                    if (outQuestionMsg(current.getFileName() + "\n\n"
                            + ResStrings.getString("strFileExists"))) {
                        doClose = true;
                    }
                } else { // file is not exists,
                    // so we can write it right now
                    doClose = true;
                }
            }
        }

        if (doClose) {
            ((LOpenSaveDialog) dlgFrame).saveOptions();
            EventQueue.invokeLater(dlgFrame::dispose);
        }
    }

    /**
     * This routine calls when Cancel button clicked.
     */
    public void refuseAndClose() {
        if (isFoldersPanelVisible()) {
            showFoldersPanel(false);
        } else {
            current.setFile("");
            ((LOpenSaveDialog) dlgFrame).saveOptions();
            EventQueue.invokeLater(dlgFrame::dispose);
        }
    }

    /**
     * Shows a frame with an error message.
     *
     * @param msg message to show.
     */
    private void outErrorMsg(String msg) {
        MessageDlg msgDlg = new MessageDlg(null,
                msg,
                MessageDlg.BTN_OK);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
        msgDlg.setTitle(dlgFrame.getTitle());
        msgDlg.setLocationRelativeTo(this);
        msgDlg.setVisible(true);
    }

    /**
     * Shows a frame with a question message with YES/NO buttons.
     *
     * @param msg a question for the user.
     * @return true if user has answered YES, false otherwise.
     */
    private boolean outQuestionMsg(String msg) {
        MessageDlg msgDlg = new MessageDlg(null,
                msg,
                MessageDlg.BTN_YES_NO);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
        msgDlg.setTitle(dlgFrame.getTitle());
        msgDlg.setLocationRelativeTo(this);
        msgDlg.setVisible(true);

        return msgDlg.result > 0;
    }

// -----------------------------------------------------
//         Resources loaders
//

    @SuppressWarnings("SpellCheckingInspection")
    private void loadResources() {
        imgBtnDown = createBufImage("btnTool22_down.png");
        imgBtnUp = createBufImage("btnTool22_up.png");
        imgFrameIcon = createBufImage("lnopensavedialog_icon.png");
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

    @SuppressWarnings("SameParameterValue")
    private ImageIcon createIconImage(String FName) {
        java.net.URL url = this.getClass().getResource("/img/" + FName);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            return null;
        }
    }

}
