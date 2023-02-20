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
    private final ScrollBar sbVert;

    private final JComponent viewport;

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

        viewport = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };

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

        sbVert = new ScrollBar(ScrollBar.VERTICAL) {
            @Override
            public void onChangePosition() {
                fileList.setLocation(0, -sbVert.getPosition());
            }

            @Override
            public boolean isActive() {
                return !osPan.isFoldersPanelVisible();
            }
        };
//        sbVert.setSize(11, 200);
        add(sbVert);

        header = new FileListHeader(this);
        add(header);

//        setSize(200, 200);

        addMouseWheelListener((MouseWheelEvent e) -> {
            if (isEnabled() && sbVert.isVisible() && sbVert.isActive()) {
                if (!e.isAltDown()) {
                    if (e.getWheelRotation() > 0) {
                        sbVert.setPosition(sbVert.getPosition() + fileList.getItemHeight());
                    } else {
                        sbVert.setPosition(sbVert.getPosition() - fileList.getItemHeight());
                    }
                } else {
                    if (e.getWheelRotation() > 0) {
                        sbVert.setPosition(sbVert.getPosition() + fileList.getItemHeight() * 5);
                    } else {
                        sbVert.setPosition(sbVert.getPosition() - fileList.getItemHeight() * 5);
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
                            fileList.itemClicked(getCurrentItem(), 1, 2);
                        }
                        break;
                    case KeyEvent.VK_F5:
                        refreshFolder();
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        osPan.upFolder();
                        break;
                }
                sbVert.scrollToComponent(fileList.getCurrentItem());
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

    public void updateComponents() {
        int w = getWidth() - 4;
        int h = getHeight() - 4;

        sbVert.setValues(0, fileList.getHeight(), fileList.getItemHeight(), h - header.getHeight() - 1);

        int sbV_width = (sbVert.isVisible()) ? 11 : 0;

        header.setLocation(2, 2);
        sbVert.setLocation(w - sbV_width + 2, 2);
        viewport.setLocation(2, 2 + header.getHeight());

        header.setSize(w - sbV_width, header.getHeight());

        sbVert.setSize(sbV_width, h);
        viewport.setSize(w - sbV_width, h - header.getHeight() - 1);
        fileList.setSize(viewport.getWidth(), fileList.getHeight());
        viewport.repaint();
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        if (header != null) {
            updateComponents();
        }
    }

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

    public void refreshFolder() {
        updateFolder(current.getFolder());
    }

    public void sortFileList(int sortNumber) {
        fileList.sort(sortNumber);
        sbVert.scrollToComponent(fileList.getCurrentItem());
    }

    public void sortFileList(int sortNumber, boolean ascending) {
        fileList.sort(sortNumber, ascending);
        sbVert.scrollToComponent(fileList.getCurrentItem());
    }

    public void setSorting(int number, boolean ascending) {
        fileList.setSorting(number, ascending);
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

    public void restoreHeader(int name, int size, int date) {
        header.setWidths(name, size, date);
    }

    public void scrollToFile(String s) {
        FileItem item = fileList.getNearestItem(s);
        if (item != null) {
            sbVert.scrollToComponent(item);
        }
    }

    @Override
    public void updateFolder(File folder) {
        boolean b = sbVert.isVisible();
        fileList.setFolder(folder);
        sbVert.setValues(0, fileList.getHeight(), fileList.getItemHeight(), getHeight() - header.getHeight() - 5);
        sbVert.setPosition(0);
        fileList.setLocation(0, 0);
        if (sbVert.isVisible() != b)
            updateComponents();
    }
}
