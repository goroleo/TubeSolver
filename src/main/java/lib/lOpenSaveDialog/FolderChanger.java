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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.DEFAULT_EXT;

/**
 * This class stores the value of the current folder and current file, navigates through
 * folders, and notifies another controls when the current folder and current file have changed.
 * All listeners must implement FolderListener and FileListener interfaces to take change
 * events from this class.
 *
 * @see FolderListener
 * @see FileListener
 */
public class FolderChanger {

    /**
     * The current folder displayed at dialog controls.
     */
    private File currentFolder;

    /**
     * The name of the current file.
     */
    private String currentFileName = "";

    /**
     * Array of controls that listen events of the current folder change.
     */
    private final List<FolderListener> folderListeners = new ArrayList<>();

    /**
     * Array of controls that listen events of the current file change.
     */
    private final List<FileListener> fileListeners = new ArrayList<>();

    /**
     * The constructor of the class. The User directory will be set as a default folder.
     */
    public FolderChanger() {
        currentFolder = OpenSavePanel.fsv.getDefaultDirectory();
    }

    /**
     * This gets the current folder as a File variable
     *
     * @return the current folder
     */
    public File getFolder() {
        return currentFolder;
    }

    /**
     * This gets the full path of the current folder as a string.
     *
     * @return absolute path of the current folder.
     */
    @SuppressWarnings("unused")
    public String getFolderPath() {
        return currentFolder.getAbsolutePath();
    }

    /**
     * @return the current file if it exists or null otherwise.
     */
    @SuppressWarnings("unused")
    public File getFile() {
        if (!"".equals(currentFileName)) {
            return OpenSavePanel.fsv.getChild(currentFolder, currentFileName);
        } else {
            return null;
        }
    }

    /**
     * @return the current file name as string.
     */
    public String getFileName() {
        return currentFileName;
    }

    /**
     * @return the current file name without the default extension.
     */
    public String getDisplayedFileName() {
        if (FileItem.extractFileExt(currentFileName).equalsIgnoreCase(DEFAULT_EXT)) {
            return currentFileName.substring(0, currentFileName.length() - DEFAULT_EXT.length());
        } else {
            return currentFileName;
        }
    }

    /**
     * @return full absolute path of the current file, consists of the current folder and current file name.
     * if file name is not set, this returns an empty string.
     */
    public String getFilePath() {
        if (!"".equals(currentFileName)) {
            return currentFolder.getAbsolutePath() + File.separator + currentFileName;
        } else {
            return "";
        }
    }

    /**
     * This routine parses the path string and tries to set the folder stored in the file
     * name/path as the current folder.
     *
     * @param path can be an absolute path to a folder, the relative path from the current
     *             folder, or a path with the file name.
     * @return the rest part of the file name without the folder path after setting the folder.
     *         It can be an empty string if the entire path is a folder path.
     * @throws IOException will rise if the parsed folder is not exist.
     */
    public String setFolder(String path) throws IOException {

        if (path == null || "".equals(path)) {
            return ""; // nothing to do
        }

        // checks if this is a full absolute path
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
            setFolder(f);
            return ""; // no file name stored in a path
        }

        // check if this is a relative path started from the current folder
        f = OpenSavePanel.fsv.getChild(currentFolder, path);
        if (f != null) {
            if (f.exists() && f.isDirectory()) {
                setFolder(f);
                return ""; // no file name stored in a path
            }
        } else {
            // f is null so this is not a path, maybe the file name
            return path;
        }

        // trying to extract the filename from the path
        String fPath = f.getParent();
        String fName = f.getName();

        if (fPath == null || "".equals(fPath)) {
            // If fPath is empty then the file name only is in the path.
            // So it is not necessary to change the current folder.
            return fName;
        }

        // checks if this is a full absolute path after file name extracted
        f = new File(fPath);
        if (f.exists() && f.isDirectory()) {
            setFolder(f);
            return fName;
        }

        // checks if this is a relative path started from the current folder.
        f = OpenSavePanel.fsv.getChild(currentFolder, fPath);
        if (f != null && f.exists() && f.isDirectory()) {
            setFolder(f);
            return fName;
        }

        // nothing has help us. So there's no as path as specified.
        throw new IOException(fPath);
    }

    /**
     * Sets the current folder and notices all listeners of the change.
     *
     * @param folder new folder must be existed.
     */
    public void setFolder(File folder) {
        if (folder != null
                && folder.exists()
                && folder.isDirectory()
                && !currentFolder.getAbsolutePath().equals(folder.getAbsolutePath())) {
            if (osPan != null)
                osPan.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            currentFolder = folder;
            updateFolder();
            if (osPan != null)
                osPan.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Sets the parent folder.
     */
    public void upFolder() {
        if (currentFolder != null) {
            setFolder(OpenSavePanel.fsv.getParentDirectory(currentFolder));
        }
    }

    /**
     * Sets the current file. But first it tries to separate file path and file name
     * using <i>setFolder()</i> routine.
     *
     * @param path a file name which may can include folders.
     * @see #setFolder(String)
     */
    public void setFile(String path) {
        String fName = null;
        try {
            fName = setFolder(path);
        } catch (IOException e) {
            // ignoring
        }
        if (fName != null && !currentFileName.equals(fName)) {
            currentFileName = fName;
            updateFile();
        }
    }

    /**
     * Tries to find an existing file with the current file name. Used in the OPEN_MODE dialog.
     * First it checks the stored file name, second - the stored file name with the default extension.
     * If one of such files exists it returns the File value. Otherwise, it returns <i>null</i>.
     *
     * @return the existing file or <i>null</i> if not found.
     */
    public File findExistingFile() {
        String fName = currentFileName;
        File f = OpenSavePanel.fsv.getChild(currentFolder, currentFileName);
        if (f != null && f.exists()) {
            return f;
        }
        addDefaultExt();
        if (!fName.equals(currentFileName)) {
            f = OpenSavePanel.fsv.getChild(currentFolder, currentFileName);
            if (f != null && f.exists()) {
                return f;
            }
        }
        // restore the previous file name
        currentFileName = fName;
        return null;
    }

    /**
     * Adds the default file extension to the stored file name.
     */
    public void addDefaultExt() {
        if (!FileItem.extractFileExt(currentFileName).equalsIgnoreCase(DEFAULT_EXT)) {
            currentFileName = currentFileName + DEFAULT_EXT;
            updateFile();
        }
    }

// -----------------------------------------------------
//     Listeners  routines
//

    /**
     * Adds a new listener of the current folder changes.
     * @param toAdd an object that implements FolderListener interface
     */
    public void addFolderListener(FolderListener toAdd) {
        folderListeners.add(toAdd);
        toAdd.updateFolder(currentFolder);
    }

    /**
     * Adds a new listener of the current file changes.
     * @param toAdd an object that implements FileListener interface
     */
    public void addFileListener(FileListener toAdd) {
        fileListeners.add(toAdd);
        toAdd.updateFile();
    }

    /**
     * Removes the folder listener from the listeners list.
     * @param toRemove an object that implements FolderListener interface
     */
    @SuppressWarnings("unused")
    public void removeFolderListener(FolderListener toRemove) {
        folderListeners.remove(toRemove);
    }

    /**
     * Removes the file listener from the listeners list.
     * @param toRemove an object that implements FileListener interface
     */
    @SuppressWarnings("unused")
    public void removeFileListener(FileListener toRemove) {
        fileListeners.remove(toRemove);
    }

    /**
     * Removes all the listeners.
     */
    @SuppressWarnings("unused")
    public void removeAllListeners() {
        for (FolderListener fl : folderListeners) {
            folderListeners.remove(fl);
        }
        for (FileListener fl : fileListeners) {
            fileListeners.remove(fl);
        }
    }

    /**
     * Notifies all folder listeners that the current folder has changed.
     */
    public void updateFolder() {
        for (FolderListener fl : folderListeners) {
            fl.updateFolder(currentFolder);
        }
    }

    /**
     * Notifies all file listeners that the current file has changed.
     */
    public void updateFile() {
        for (FileListener fl : fileListeners) {
            fl.updateFile();
        }
    }


}
