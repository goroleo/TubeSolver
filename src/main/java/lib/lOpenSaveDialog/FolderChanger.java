package lib.lOpenSaveDialog;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.DEFAULT_EXT;

/**
 * This is a class stored the value of the current folder and current file, navigates through
 * folders, and notifies another controls when the current folder and current file have changed.
 * All listeners must implement FolderListener and FileListener interfaces to take change
 * events from this class.
 * @see FolderListener
 * @see FileListener
 */
public class FolderChanger {

    /** The current folder displayed at dialog controls. */
    private File currentFolder;

    /** The name of the current file. */
    private String currentFileName = "";

    /** Array of controls that listen events of the current folder change. */
    private final List<FolderListener> folderListeners = new ArrayList<>();

    /** Array of controls that listen events of the current file change. */
    private final List<FileListener> fileListeners = new ArrayList<>();

    /** The constructor of the class. The User directory will be set as a default folder.  */
    public FolderChanger() {
        currentFolder = OpenSavePanel.fsv.getDefaultDirectory();
    }

    /** The constructor of the class. Used to set the specified folder */
    public FolderChanger(File folder) {
        setFolder(folder);
    }

    /** This gets the current folder as a File variable */
    public File getFolder() {
        return currentFolder;
    }

    public String getFolderPath() {
        return currentFolder.getAbsolutePath();
    }

    public File getFile() {
        if (!"".equals(currentFileName)) {
            return OpenSavePanel.fsv.getChild(currentFolder, currentFileName);
        } else {
            return null;
        }
    }

    public String getFileName() {
        return currentFileName;
    }

    public String getDisplayedFileName() {
        if (FileItem.extractFileExt(currentFileName).equalsIgnoreCase(DEFAULT_EXT)) {
            return currentFileName.substring(0, currentFileName.length() - DEFAULT_EXT.length());
        } else {
            return currentFileName;
        }
    }

    public String getFilePath() {
        if (!"".equals(currentFileName)) {
            return currentFolder.getAbsolutePath() + File.separator + currentFileName;
        } else {
            return "";
        }
    }

    /**
     * This routine tries to set the folder stored in the file name/path.
     *
     * @param path can be an absolute path to a folder, the relative path from the current folder, or a path with the file name.
     * @return the rest part of the file name without the folder path after setting the folder. It can be an empty string if the entire path is a folder path.
     * @throws IOException will rise if the path is not exist.
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

    public void setFolder(File folder) {
        if (folder != null
                && folder.exists()
                && folder.isDirectory()
                && !currentFolder.getAbsolutePath().equals(folder.getAbsolutePath())) {
            if (osPan != null)
                osPan.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            currentFolder = folder;
            updateFolder(folder);
            if (osPan != null)
                osPan.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void upFolder() {
        if (currentFolder != null) {
            setFolder(OpenSavePanel.fsv.getParentDirectory(currentFolder));
        }
    }

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
        currentFileName = fName;
        return null;
    }

    public void addDefaultExt() {
        if (!FileItem.extractFileExt(currentFileName).equalsIgnoreCase(DEFAULT_EXT)) {
            currentFileName = currentFileName + DEFAULT_EXT;
            updateFile();
        }
    }

    /////////////////////////////////////////////////////////
//  Listeners routines
//
    public void addFolderListener(FolderListener toAdd) {
        folderListeners.add(toAdd);
        toAdd.updateFolder(currentFolder);
    }

    public void addFileListener(FileListener toAdd) {
        fileListeners.add(toAdd);
        toAdd.updateFile(currentFileName);
    }

    public void removeFolderListener(FolderListener toRemove) {
        folderListeners.remove(toRemove);
    }

    public void removeFileListener(FileListener toRemove) {
        fileListeners.remove(toRemove);
    }

    public void removeAllListeners() {
        for (FolderListener fl : folderListeners) {
            folderListeners.remove(fl);
        }
        for (FileListener fl : fileListeners) {
            fileListeners.remove(fl);
        }
    }

    public void updateFolder(File folder) {
        for (FolderListener fl : folderListeners) {
            fl.updateFolder(folder);
        }
    }

    public void updateFile() {
        for (FileListener fl : fileListeners) {
            fl.updateFile(currentFileName);
        }
    }


}
