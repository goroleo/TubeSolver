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

import java.io.File;

/**
 * A listener of the current folder change events.
 */
public interface FolderListener {
    /**
     * The listener that catches a current folder change event.
     * @param folder current folder
     */
    void updateFolder(File folder);
}
