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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;

public class FilesPanel extends JComponent {

    private final FileList filelist;
    private final FileListHeader header;
    private final ScrollBar sbVert;

    private final JComponent viewport;

    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));
    private final Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb8cfe5)),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));

    public FilesPanel(File folder) {

        setBackground(null);
        setForeground(null);

        setFocusable(true);
        setBorder(border);

        filelist = new FileList(this, folder);

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
                if (osPan.isFoldersVisible()) {
                    osPan.showFoldersPanel(false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                FilesPanel.this.requestFocus();
            }
        });

        viewport.setBackground(null);

        viewport.add(filelist);
        add(viewport);

        sbVert = new ScrollBar(ScrollBar.VERTICAL) {
            @Override
            public void onChangePosition() {
                filelist.setLocation(0, -sbVert.getPosition());
            }
        };
        sbVert.setSize(11, 200);
        add(sbVert);

        header = new FileListHeader(this);
        add(header);

        setSize(200, 200);

        addMouseWheelListener((MouseWheelEvent e) -> {
            if (isEnabled() && sbVert.isVisible()) {
                if (!e.isAltDown()) {
                    if (e.getWheelRotation() > 0) {
                        sbVert.setPosition(sbVert.getPosition() + filelist.getItemHeight(), true);
                    } else {
                        sbVert.setPosition(sbVert.getPosition() - filelist.getItemHeight(), true);
                    }
                } else {
                    if (e.getWheelRotation() > 0) {
                        sbVert.setPosition(sbVert.getPosition() + filelist.getItemHeight() * 5, true);
                    } else {
                        sbVert.setPosition(sbVert.getPosition() - filelist.getItemHeight() * 5, true);
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
                        filelist.setPreviousItem();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        filelist.setNextItem();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        if (e.isControlDown()) {
                            filelist.setFirstItem();
                        } else {
                            filelist.setPreviousPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        if (e.isControlDown()) {
                            filelist.setLastItem();
                        } else {
                            filelist.setNextPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (getCurrentItem() != null) {
                            filelist.itemClicked(getCurrentItem(), 1, 2);
                        }
                        break;
                    case KeyEvent.VK_F5:
                        osPan.folderRefresh();
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        osPan.folderUp();
                        break;
                }
                sbVert.scrollToComponent(filelist.getCurrentItem());
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

    public void setFolder(File folder) {
        filelist.setFolder(folder);
        updateComponents();
        sbVert.setPosition(0, true);
        filelist.setLocation(0, 0);
    }

    public void updateComponents() {
        int w = getWidth() - 4;
        int h = getHeight() - 4;

        sbVert.setValues(0, filelist.getHeight(), filelist.getItemHeight(), h - header.getHeight() - 1);

        int sbV_width = (sbVert.isVisible()) ? 11 : 0;

        header.setLocation(2, 2);
        sbVert.setLocation(w - sbV_width + 2, 2);
        viewport.setLocation(2, 2 + header.getHeight());

        header.setSize(w - 1 - sbV_width, header.getHeight());
        sbVert.setSize(sbV_width, h);
        viewport.setSize(w - sbV_width, h - header.getHeight() - 1);
        filelist.setSize(viewport.getWidth(), filelist.getHeight());
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
        if (filelist != null) {
            filelist.setColumnWidths(name, size, date);
        }
    }

    public void folderRefresh() {
        filelist.setFolder(osPan.getCurrentFolder());
        updateComponents();
    }

    public void sortFileList(int sortNumber) {
        filelist.sort(sortNumber);
        filelist.repaint();
    }

    public void sortFileList(int sortNumber, boolean ascending) {
        filelist.sort(sortNumber, ascending);
        filelist.repaint();
    }

    public void setSorting(int number, boolean ascending) {
        filelist.setSorting(number, ascending);
    }

    public int getSortNumber() {
        return filelist.getSortNumber();
    }

    public boolean getSortAscending() {
        return filelist.getSortAscending();
    }

    public FileItem getCurrentItem() {
        return filelist.getCurrentItem();
    }

    public void fileChanged() {
        if (filelist != null) {
            if (filelist.getCurrentItem() != null) {
                osPan.setFileName(filelist.getCurrentItem().getFile());
            } else {
                osPan.setFileName(null);
            }
        } else {
            if (osPan != null) {
                osPan.setFileName(null);
            }
        }
    }

    public void chooseFolder(FileItem item) {
        chooseFolder(item.getFile());
    }

    public void chooseFolder(File folder) {
        if (osPan != null) {
            filelist.setCurrentItem(null);
            osPan.setCurrentFolder(folder);
        }
    }

    public void restoreHeader(int name, int size, int date) {
        header.setWidths(name, size, date);
    }

    public void scrollToFile(String s) {
        FileItem item = filelist.getNearestItem(s);
        if (item != null) {
            sbVert.scrollToComponent(item);
        } 
    }

}
