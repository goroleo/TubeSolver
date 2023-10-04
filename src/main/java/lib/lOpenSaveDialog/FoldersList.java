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
import java.util.ArrayList;

import static lib.lOpenSaveDialog.OpenSavePanel.fsv;

/**
 * FolderList is the list for a FoldersPanel. Practically FolderList consists of two lists: system roots list
 * and folders path list to the nearest system root.
 * This class is an inheritor of the ListView class. All it does is fill these two list and compose them into one.
 */
public class FoldersList extends ListView {

    /**
     * The list of system roots.
     */
    private static final ArrayList<FileItem> rootsList = new ArrayList<>();

    /**
     * The list of path folders to the nearest system root.
     */
    private final ArrayList<FileItem> foldersList = new ArrayList<>();

    /**
     * The item of the roots list that is a parent for the path list items.
     */
    private FileItem currentRoot;

    /**
     * The parent panel for access its possibilities.
     */
    private final FoldersPanel foldersPanel;

    /**
     * Creates the Folder List.
     *
     * @param owner is a Folders Panel
     */
    public FoldersList(FoldersPanel owner) {
        super();
        foldersPanel = owner;
        setItemHeight(22);
        fillRoots();
    }

    /**
     * Fills the list with system root folders. Any root folder can be child of a previously listed
     * root folder, so this routine also checks them.
     */
    private void fillRoots() {
        FileItem fi;
        if (!rootsList.isEmpty()) {
            rootsList.clear();
        }
        File[] roots = fsv.getChooserComboBoxFiles();
        File parentFolder;
        int level;

        for (File root : roots) {
            level = 0;
            parentFolder = fsv.getParentDirectory(root);
            if (parentFolder != null) {
                for (FileItem item : rootsList) {
                    if (parentFolder.compareTo(item.getFile()) == 0) {
                        level = item.getLevel() + 1;
                    }
                }
            }
            fi = this.createNewItem(root, false, level);
            rootsList.add(fi);
        }
    }

    /**
     * Fills the paths list. It scans the current folder path and finds the correct
     * root for the folder.
     *
     * @param folder - current folder
     */
    private void fillPaths(File folder) {

        int startLevel = 0;
        currentRoot = null;
        boolean done = false;
        foldersList.clear();

        while (!done && folder != null) {

            // check if this folder is in the roots list
            int i = 0;
            while (!done && i < rootsList.size()) {
                if (folder.compareTo(rootsList.get(i).getFile()) == 0) {
                    currentRoot = rootsList.get(i);
                    startLevel = currentRoot.getLevel() + 1;
                    done = true;
                } else {
                    i++;
                }
            }

            if (!done) {
                // this folder is not in the root list, add this folder to the paths list
                foldersList.add(0, createNewItem(folder, false, 0));
                // and try again with the parent of this folder
                folder = fsv.getParentDirectory(folder);
            }
        }

        // update paths list items levels
        if (!foldersList.isEmpty()) {
            for (FileItem fi : foldersList) {
                fi.setLevel(startLevel);
                startLevel++;
            }
        }
    }

    /**
     * Fills the ListView's FileList as a composition of two lists (roots and paths).
     */
    private void fillFileList() {
        getList().clear();

        if (!rootsList.isEmpty()) {

            for (FileItem rootItem : rootsList) {
                getList().add(rootItem);
                if (rootItem == currentRoot && !foldersList.isEmpty()) {
                    for (FileItem pathItem : foldersList) {
                        getList().add(pathItem);
                    }
                }
            }

        } else { // root list is empty, just add items from paths list
            for (FileItem pathItem : foldersList) {
                getList().add(pathItem);
            }
        }
        updateView();
    }

    /**
     * Sets the current folder. It fills the Paths list, scans the current folder path, finds the correct
     * root for the folder, and inserts the list of paths into the right place.
     *
     * @param folder - current folder
     */
    public void setFolder(File folder) {
        setCurrentItem(null);
        fillPaths(folder);
        fillFileList();
        setCurrentItem(getItemByFile(folder));
    }

    @SuppressWarnings("unused")
    @Override
    public void onItemClicked(FileItem item, MouseEvent e) {
        foldersPanel.chooseFolder(item);
    }

    @SuppressWarnings("unused")
    @Override
    public void onItemEntered(FileItem item) {
        setCurrentItem(item);
    }

    @SuppressWarnings("unused")
    @Override
    public void onItemPressed(FileItem item) {
        if (!foldersPanel.isFocusOwner()) {
            foldersPanel.requestFocus();
        }
    }

}
