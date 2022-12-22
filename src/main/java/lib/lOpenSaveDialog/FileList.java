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

public class FileList extends ListView {

    private final FilesPanel fPanel;

    public FileList() {
        this(null, null);
    }

    public FileList(FilesPanel owner, File folder) {
        super(owner);
        fPanel = owner;
        setItemHeight(25);
        setFolder(folder);
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
                        || OpenSavePanel.DEFAULT_EXT.equalsIgnoreCase(getFileExt(f))) {
                    addNewItem(f);
                }
            }
            doSort();
            updateView();
        }
    }

    public void itemClicked(FileItem item, int button, int clickCount) {
        if (osPan.isFoldersVisible()) {
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
                                osPan.setCurrentFolder(f1);
                            }
                        } catch (FileNotFoundException ex) {
                            // do nothing
                        }
                    } else {
                        osPan.setCurrentFolder(item.getFile());
                    }
                } else {
                    osPan.confirmAndClose();
                }
            } else {
                setCurrentItem(item);
            }
        }
    }
    
    @Override
    public void itemClicked(FileItem item, MouseEvent e) {
        itemClicked(item, e.getButton(), e.getClickCount());
    }

    @Override
    public void itemEntered(FileItem item) {
        if (!osPan.isFoldersVisible()) {
            setRollovedItem(item);
        }
    }

    @Override
    public void itemExited(FileItem item) {
        setRollovedItem(null);
    }

    @Override
    public void setCurrentItem(FileItem file) {
        super.setCurrentItem(file);
        fPanel.fileChanged();
    }

    @Override
    public void addItemToView(FileItem item) {
        item.setWidths(fPanel.getColumnWidth(2), fPanel.getColumnWidth(3));
        add(item);
    }

    public String getFileExt(File f) {
        if (f.isDirectory()) {
            return "";
        }
        String[] ss = f.getName().split("\\.");
        if (ss.length > 1) {
            return "." + ss[ss.length - 1];
        } else {
            return "";
        }
    }

}
