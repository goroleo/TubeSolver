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

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.fsv;

public class FilesList extends ListView {

    private final FilesPanel fPanel;

    public FilesList() {
        this(null);
    }

    public FilesList(FilesPanel owner) {
        super();
        fPanel = owner;
        setItemHeight(25);
    }

    public void setFolder(File folder) {
        setCurrentItem(null);
        if (!getFileList().isEmpty()) {
            getFileList().clear();
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
            updateView();
        }
    }

    public void itemClicked(FileItem item, int button, int clickCount) {
        if (osPan.isFoldersPanelVisible()) {
            osPan.showFoldersPanel(false);
            return;
        }
        if (button == 1) {
            if (clickCount == 2) {
                if (item.isFolder()) {
                    if (item.isLink()) {
                        try {
                            File f1 = fsv.getLinkLocation(item.getFile());
                            if (f1 != null && f1.isDirectory()) {
                                osPan.setFolder(f1);
                            }
                        } catch (FileNotFoundException ex) {
                            // do nothing
                        }
                    } else {
                        osPan.setFolder(item.getFile());
                    }
                } else { // not folder and clickCount = 2
                    osPan.confirmAndClose();
                }
            } else { // clickCount = 1
                setCurrentItem(item);
            }
        }
    }

    @Override
    public void itemClicked(FileItem item, MouseEvent e) {
        itemClicked(item, e.getButton(), e.getClickCount());
    }

    @Override
    public void itemPressed(FileItem item) {
        if (!fPanel.isFocusOwner()) fPanel.requestFocus();
    }

    @Override
    public void itemEntered(FileItem item) {
        if (!osPan.isFoldersPanelVisible()) {
            setMouseOverItem(item);
        }
    }

    @Override
    public void itemExited(FileItem item) {
        setMouseOverItem(null);
    }

    @Override
    public void setCurrentItem(FileItem file) {
        super.setCurrentItem(file);
        if (file != null)
            fPanel.chooseFile(file.getFile());
    }

    @Override
    public void addItemToView(FileItem item) {
        item.setLabelWidths(fPanel.getColumnWidth(2), fPanel.getColumnWidth(3));
        add(item);
    }

}
