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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import static lib.lOpenSaveDialog.OpenSavePanel.fsv;

public class FolderList extends ListView {

    private static final ArrayList<FileItem> rootList = new ArrayList<>();
    private final ArrayList<FileItem> folderList = new ArrayList<>();
    private int insertFoldersAfter;
    private final FoldersPanel fPanel;

    public FolderList() {
        this(null, null);
    }

    public FolderList(FoldersPanel owner, File folder) {
        super(owner);
        fPanel = owner;
        setItemHeight(22);

        fillRoots();
        if (folder != null) {
            setFolder(folder);
        }
    }

    private void fillRoots() {
        FileItem fi;
        if (!rootList.isEmpty()) {
            rootList.clear();
        }
        File[] roots = fsv.getChooserComboBoxFiles();
        File parentFolder;
        int level;

        for (File root : roots) {
            level = 0;
            parentFolder = fsv.getParentDirectory(root);
            if (parentFolder != null) {
                for (FileItem item : rootList) {
                    if (parentFolder.compareTo(item.getFile()) == 0) {
                        level = item.getLevel() + 1;
                    }
                }
            }
            fi = this.createNewItem(root, true, level);
            rootList.add(fi);
        }
    }

    public void setFolder(File folder) {
        FileItem fi;
        setCurrentItem(null);

        int foldersLevel = 0;
        insertFoldersAfter = rootList.size() - 1;
        boolean done = false;
        folderList.clear();
        while (!done && folder != null) {
            for (int i = 0; i < rootList.size(); i++) {
                if (folder.compareTo(rootList.get(i).getFile()) == 0) {
                    foldersLevel = rootList.get(i).getLevel() + 1;
                    insertFoldersAfter = i;
                    done = true;
                }
            }
            if (!done) {
                fi = createNewItem(folder, true, 0);
                folderList.add(0, fi);
                folder = fsv.getParentDirectory(folder);
            }
        }

        if (!folderList.isEmpty()) {
            fillFoldersLevel(foldersLevel);
        }
        fillFileList();
        setCurrentItem(findFolder(folder));
    }

    private void fillFoldersLevel(int startLevel) {
        for (FileItem folder : folderList) {
            folder.setLevel(startLevel);
            startLevel++;
        }
    }

    private void fillFileList() {
        getFileList().clear();
        if (!rootList.isEmpty()) {
            for (int i = 0; i < rootList.size(); i++) {
                getFileList().add(rootList.get(i));
                if (i == insertFoldersAfter && !folderList.isEmpty()) {
                    for (FileItem fileItem : folderList) {
                        getFileList().add(fileItem);
                    }
                }
            }
        } else {
            for (FileItem fileItem : folderList) {
                getFileList().add(fileItem);
            }
        }
        updateView();
    }

    public FileItem findFolder(File folder) {
        if (folder != null) {
            Component c;
            for (int i = 0; i < getComponentCount(); i++) {
                c = getComponent(i);
                if (c instanceof FileItem) {
                    if (((FileItem) c).getFile().compareTo(folder) == 0) {
                        return (FileItem) c;
                    }
                }
            }
        }
        return null;
    }

    public void chooseFolder(FileItem folder) {
        setCurrentItem(folder);
        if (fPanel != null) {
            fPanel.chooseFolder(folder);
        }
    }

    @Override
    public void itemClicked(FileItem item, MouseEvent e) {
        chooseFolder(item);
    }

    @Override
    public void itemEntered(FileItem item) {
        setCurrentItem(item);
    }

    @Override
    public void itemExited(FileItem item) {
//        setCurrentItem(null);
    }

    @Override
    public void addItemToView(FileItem item) {
        item.setNameWidth(fPanel.getWidth());
        add(item);
    }

}
