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

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPanel;
import static lib.lOpenSaveDialog.OpenSavePanel.current;
import static lib.lOpenSaveDialog.OpenSavePanel.fsv;

/**
 * The list of the files in the current folder. It is an inheritor of the ListView and provides only specific methods and routines.
 */
public class FilesList extends ListView {

    /**
     * The parent panel for access its possibilities.
     */
    private final FilesPanel filesPanel;

    /**
     * Creates the Files List.
     *
     * @param owner is a Files Panel
     */
    public FilesList(FilesPanel owner) {
        super();
        filesPanel = owner;
        setItemHeight(25);
    }

    /**
     * Sets the current folder. It scans the current folder and adds to the FileList children folders and files with the default extension.
     *
     * @param folder current folder
     */
    public void setFolder(File folder) {
        setCurrentItem(null);
        if (!getList().isEmpty()) {
            getList().clear();
        }
        if (folder != null) {
            File[] files = fsv.getFiles(folder, true);
            for (File f : files) {
                if (f.isDirectory()
                        || OpenSavePanel.DEFAULT_EXT.equalsIgnoreCase(
                        FileItem.extractFileExt(f))) {
                    addNewItem(f);
                }
            }
            doSort();
            refreshView();
        }
    }

    /**
     * Handles a mouse click on an item.
     *
     * @param item an item has clicked
     * @param button number of a mouse button
     * @param clickCount number of clicks
     */
    public void clickItem(FileItem item, int button, int clickCount) {
        if (osPanel.isFoldersPanelVisible()) {
            osPanel.showFoldersPanel(false);
            return;
        }
        if (button == 1) { // left mouse button
            if (clickCount == 2) { // two clicks
                if (item.isFolder()) { // click on a folder item
                    if (item.isLink()) { // item is a link to another folder
                        try {
                            File f = fsv.getLinkLocation(item.getFile());
                            if (f != null && f.isDirectory()) {
                                current.setFolder(f);
                            }
                        } catch (FileNotFoundException ex) {
                            // do nothing
                        }
                    } else { // item is not link but still a folder and clicks count is 2
                        current.setFolder(item.getFile());
                    }
                } else { // item is not a folder and clicks count is 2
                    osPanel.confirmAndClose();
                }
            } else { // clicks count is 1
                setCurrentItem(item);
            }
        }
    }

    @Override
    public void onItemClicked(FileItem item, MouseEvent e) {
        clickItem(item, e.getButton(), e.getClickCount());
    }

    @Override
    public void onItemPressed(FileItem item) {
        if (!filesPanel.isFocusOwner()) filesPanel.requestFocus();
    }

    @Override
    public void onItemEntered(FileItem item) {
        if (!osPanel.isFoldersPanelVisible()) {
            setMouseOverItem(item);
        }
    }

    @Override
    public void onItemExited(FileItem item) {
        setMouseOverItem(null);
    }

    @Override
    public void setCurrentItem(FileItem file) {
        super.setCurrentItem(file);
        if (file != null)
            filesPanel.chooseFile(file.getFile());
    }

    @Override
    public void addItemToView(FileItem item) {
        item.setLabelWidths(filesPanel.getColumnWidth(2), filesPanel.getColumnWidth(3));
        add(item);
    }

}
