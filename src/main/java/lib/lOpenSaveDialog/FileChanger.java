package lib.lOpenSaveDialog;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.DEFAULT_EXT;
import static lib.lOpenSaveDialog.OpenSavePanel.fsv;

public class FileChanger {

    private File currentFolder;
    private String currentFileName = "";

    private final List<FolderListener> folderListeners = new ArrayList<>();
    private final List<FileListener> fileListeners = new ArrayList<>();

    public FileChanger() {
        currentFolder = fsv.getDefaultDirectory();
    }

    public FileChanger(File folder) {
        setFolder(folder);
    }

    public File getFolder() {
        return currentFolder;
    }

    public String getFolderPath() {
        return currentFolder.getAbsolutePath();
    }

    public String getFileName() {
        return currentFileName;
    }

    public String getDisplayedFileName() {
        if (FileItem.getFileExt(currentFileName).equalsIgnoreCase(DEFAULT_EXT)) {
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

    public File getFile() {
        if (!"".equals(currentFileName)) {
            return fsv.getChild(currentFolder, currentFileName);
        } else {
            return null;
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

    /**
     * This routine tries to set the folder stored in the file name/path.
     *
     * @param path can be an absolute path to a folder, the relative path from the current folder, or a path with the file name
     * @return the rest part of the file name without the path
     * @throws IOException if the path is not exist
     */
    public String setFolder(String path) throws IOException {

        if (path != null && !"".equals(path)) {

            File f = new File(path);
            String fPath = f.getParent();
            String fName = f.getName();

            // checks if this is a full absolute path
            if (f.exists() && f.isDirectory()) {
                setFolder(f);
                return ""; // no file name stored in a path
            }

            // check if this is a relative path from the current folder
            f = fsv.getChild(currentFolder, path);
            if (f != null && f.exists() && f.isDirectory()) {
                setFolder(f);
                return ""; // no file name stored in a path
            }

            if (fPath != null && !"".equals(fPath)) {

                // checks if this is a full absolute path with a file name
                f = new File(fPath);
                if (f.exists() && f.isDirectory()) {
                    setFolder(f);
                    return fName;
                }

                // checks if this is a relative path with a file name
                f = fsv.getChild(currentFolder, fPath);
                if (f != null && f.exists() && f.isDirectory()) {
                    setFolder(f);
                    return fName;
                }

                throw new IOException(fPath);
            }
            return fName;
        }
        return "";
    }

    public void setFolder(File folder) {
        if (folder != null
                && folder.exists()
                && folder.isDirectory()
                && !currentFolder.getAbsolutePath().equals(folder.getAbsolutePath())) {
            currentFolder = folder;
            updateFolder(folder);
        }
    }

    public void upFolder() {
        if (currentFolder != null) {
            setFolder(fsv.getParentDirectory(currentFolder));
        }
    }

    public File findExistingFile() {
        File f = fsv.getChild(currentFolder, currentFileName);
        if (f != null && f.exists()) {
            return f;
        }

        addDefaultExt();
        f = fsv.getChild(currentFolder, currentFileName);
        if (f != null && f.exists()) {
            return f;
        }
        return null;
    }

    public void addDefaultExt() {
        if (!FileItem.getFileExt(currentFileName).equalsIgnoreCase(DEFAULT_EXT)) {
            currentFileName = currentFileName + DEFAULT_EXT;
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
