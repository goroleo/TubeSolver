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

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.current;

public class FilesPanel extends JComponent implements FolderListener {

    private final FilesList fileList;
    private final FileListHeader header;
    private final ScrollBar scrollbar;

    private final JComponent viewport = new JComponent() {
    };


    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));
    private final Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb8cfe5)),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));

    public FilesPanel() {

        setBackground(null);
        setForeground(null);

        setFocusable(true);
        setBorder(border);

        fileList = new FilesList(this);

        viewport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (osPan.isFoldersPanelVisible()) {
                    osPan.showFoldersPanel(false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                FilesPanel.this.requestFocus();
            }
        });

        viewport.setBackground(null);
        viewport.add(fileList);
        add(viewport);

        scrollbar = new ScrollBar() {
            @Override
            public void onChangePosition() {
                fileList.setLocation(0, -scrollbar.getPosition());
            }

            @Override
            public boolean isActive() {
                return !osPan.isFoldersPanelVisible();
            }

            @Override
            public void mouseClicked(int mousePos) {
                if (!osPan.isFoldersPanelVisible()) {
                    super.mouseClicked(mousePos);
                } else {
                    osPan.showFoldersPanel(false);
                }
            }
        };
        add(scrollbar);

        header = new FileListHeader(this);
        add(header);

        addMouseWheelListener((MouseWheelEvent e) -> {
            if (isEnabled() && scrollbar.isVisible() && scrollbar.isActive()) {
                if (!e.isAltDown()) {
                    if (e.getWheelRotation() > 0) {
                        scrollbar.setPosition(scrollbar.getPosition() + fileList.getItemHeight());
                    } else {
                        scrollbar.setPosition(scrollbar.getPosition() - fileList.getItemHeight());
                    }
                } else {
                    if (e.getWheelRotation() > 0) {
                        scrollbar.setPosition(scrollbar.getPosition() + fileList.getItemHeight() * 5);
                    } else {
                        scrollbar.setPosition(scrollbar.getPosition() - fileList.getItemHeight() * 5);
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_KP_UP:
                        fileList.setPreviousItem();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        fileList.setNextItem();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        if (e.isControlDown()) {
                            fileList.setFirstItem();
                        } else {
                            fileList.setPreviousPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        if (e.isControlDown()) {
                            fileList.setLastItem();
                        } else {
                            fileList.setNextPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (getCurrentItem() != null) {
                            fileList.clickItem(getCurrentItem(), 1, 2);
                        }
                        break;
                    case KeyEvent.VK_F5:
                        refreshFolder();
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        current.upFolder();
                        break;
                }
                scrollbar.scrollToComponent(fileList.getCurrentItem());
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(focusedBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(border);
            }
        }); // addFocusListener

    }

    /**
     * Updates components size and location due to panel resized.
     */
    public void updateComponents() {
        // client area width and height
        int w = getWidth() - 4;
        int h = getHeight() - 4;

        // First we need to determine if the scroll bar will be visible.
        // This depends on the height of the FileList and height of the visible area.
        // Just set range values to the scroll bar and check its visibility.
        scrollbar.setValues(0, fileList.getHeight(), h - header.getHeight() - 1);

        // sbWidth is the width of the scroll bar
        int sbWidth = (scrollbar.isVisible()) ? 11 : 0;

        // And now visible/client area width is:
        w -= sbWidth;

        // set locations of components
        header.setLocation(2, 2);
        viewport.setLocation(2, 2 + header.getHeight());
        scrollbar.setLocation(w + 2, 2);

        // set sizes of components
        header.setSize(w, header.getHeight()); // set new width only
        scrollbar.setSize(sbWidth, h);
        viewport.setSize(w, h - header.getHeight() - 1);
        fileList.setSize(w, fileList.getHeight()); // set new width only
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        updateComponents();
    }

    /**
     * Gets widths of file list columns.
     *
     * @param colNumber column number
     * @return width of this column in pixels
     */
    public int getColumnWidth(int colNumber) {
        if (header != null) {
            switch (colNumber) {
                case 1: // name
                    return header.getNameWidth();
                case 2: // date
                    return header.getSizeWidth();
                case 3: // size
                    return header.getDateWidth();
            }
        }
        return -1;
    }

    public void updateColumnWidths(int name, int size, int date) {
        if (fileList != null) {
            fileList.setColumnWidths(size, date);
        }
    }

    public void setColumnWidths(int name, int size, int date) {
        header.setColumnWidths(name, size, date);
    }


    public void refreshFolder() {
        updateFolder(current.getFolder());
    }

    public void sortFileList(int sortNumber) {
        fileList.sort(sortNumber);
        scrollbar.scrollToComponent(fileList.getCurrentItem());
    }

    public void sortFileList(int sortNumber, boolean ascending) {
        fileList.sort(sortNumber, ascending);
        scrollbar.scrollToComponent(fileList.getCurrentItem());
    }

    public int getSortNumber() {
        return fileList.getSortNumber();
    }

    public boolean getSortAscending() {
        return fileList.getSortAscending();
    }

    public FileItem getCurrentItem() {
        return fileList.getCurrentItem();
    }

    public void chooseFile(File file) {
        osPan.setFileName(file);
    }

    public void scrollToFile(String s) {
        FileItem item = fileList.getNearestItem(s);
        if (item != null) {
            scrollbar.scrollToComponent(item);
        }
    }

    @Override
    public void updateFolder(File folder) {
        boolean b = scrollbar.isVisible();
        fileList.setFolder(folder);
        scrollbar.setValues(0, fileList.getHeight(), getHeight() - header.getHeight() - 5);
        scrollbar.setPosition(0);
        fileList.setLocation(0, 0);
        if (scrollbar.isVisible() != b)
            updateComponents();
    }
}
