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
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JComponent;

public class ListView extends JComponent {

    private final ArrayList<FileItem> fileList = new ArrayList<>();
    private int itemHeight = 25;
    private int sortNumber = 1;
    private boolean sortAscending = true;
    private FileItem currentItem;
    private FileItem rollovedItem;
    private final JComponent fPanel;
    
    public ListView() {
        this(null);
    }

    public ListView(JComponent parent) {
        fPanel = parent;
        setBackground(gui.Palette.dialogColor);
        setForeground(Color.WHITE);
        setSize(300, 100);
    }

    public ArrayList<FileItem> getFileList() {
        return fileList;
    }

    public FileItem addNewItem(File f) {
        return addNewItem(f, false, 0);
    }

    public FileItem createNewItem(File f) {
        return createNewItem(f, false, 0);
    }

    public FileItem createNewItem(File f, boolean folderMode, int level) {
        return new FileItem(f, folderMode, level) {
            @Override
            public void mouseClicked(MouseEvent e) {
                ListView.this.itemClicked(this, e);
            }

            @Override
            public void mousePressed() {
                ListView.this.itemPressed(this);
            }

            @Override
            public void mouseEntered() {
                ListView.this.itemEntered(this);
            }

            @Override
            public void mouseExited() {
                ListView.this.itemExited(this);
            }
        };
    }

    public FileItem addNewItem(File f, boolean folderMode, int level) {
        FileItem fi = createNewItem(f, folderMode, level);
        fileList.add(fi);
        return fi;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int value) {
        itemHeight = value;
        setSize(getWidth(), fileList.size() * itemHeight);
    }

    @Override
    public final void setSize(int w, int h) {
        super.setSize(w, fileList.size() * itemHeight);
        updateItemsPos();
    }

    public void updateView() {
        this.removeAll();
        if (!fileList.isEmpty()) {
            for (FileItem item : fileList) {
                addItemToView(item);
            }
        }
        setSize(getWidth(), fileList.size() * itemHeight);
        repaint();
    }
    
    public void addItemToView(FileItem item) {
        add(item); 
    }

    public void updateItemsPos() {
        int w = getWidth();
        for (FileItem item : fileList) {
            item.setSize(w, itemHeight);
            item.setLocation(0, fileList.indexOf(item) * itemHeight);
        }
    }

    public void setColumnWidths(int name, int size, int date) {
        for (Component item : getComponents()) {
            if (item instanceof FileItem) {
                ((FileItem) item).setWidths(name, size, date);
            }
        }
    }

    public int getSortNumber() {
        return sortNumber;
    }

    public boolean getSortAscending() {
        return sortAscending;
    }

    public void setSorting(int number, boolean ascending) {
        sortNumber = number;
        sortAscending = ascending;
    }

    public void sort(int number, boolean ascending) {
        sortNumber = number;
        sortAscending = ascending;
        doSort();
    }

    public void sort(int number) {
        if (sortNumber == number) {
            sortAscending = !sortAscending;
        } else {
            sortNumber = number;
        }
        doSort();
    }

    public void doSort() {
        switch (sortNumber) {
            case 1:
                if (sortAscending) {
                    fileList.sort(FileItem.NameComparatorAsc);
                } else {
                    fileList.sort(FileItem.NameComparatorDesc);
                }
                break;
            case 2:
                if (sortAscending) {
                    fileList.sort(FileItem.SizeComparatorAsc);
                } else {
                    fileList.sort(FileItem.SizeComparatorDesc);
                }
                break;
            case 3:
                if (sortAscending) {
                    fileList.sort(FileItem.TimeComparatorAsc);
                } else {
                    fileList.sort(FileItem.TimeComparatorDesc);
                }
                break;
        }
        updateItemsPos();
    }

    public FileItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(FileItem item) {
        if (currentItem != null) {
            currentItem.setSelected(false);
        }

        if (getItemIndex(item) >= 0) {
            item.setSelected(true);
            currentItem = item;
        } else {
            currentItem = null;
        }
    }

    public int getItemIndex(FileItem item) {
        return fileList.indexOf(item);
    }

    public FileItem getItemOf(int index) {
        return fileList.get(index);
    }

    public int getItemsAtPage(int pageSize) {
        return pageSize / itemHeight;
    }

    public void setFirstItem() {
        if (!fileList.isEmpty()) {
            setCurrentItem(fileList.get(0));
        }
    }

    public void setPreviousPageItem(int pageSize) {
        if (currentItem != null) {
            int idx = getItemIndex(currentItem);
            idx -= getItemsAtPage(pageSize);
            if (idx < 0) {
                idx = 0;
            }
            if (fileList.size() > idx) {
                setCurrentItem(fileList.get(idx));
            }
        }
    }

    public void setPreviousItem() {
        if (currentItem != null) {
            int idx = getItemIndex(currentItem);
            idx--;
            if (idx < 0) {
                idx = 0;
            }
            if (fileList.size() > idx) {
                setCurrentItem(fileList.get(idx));
            }
        }
    }

    public void setNextItem() {
        int idx;
        if (currentItem != null) {
            idx = getItemIndex(currentItem);
            idx++;
            if (idx >= fileList.size()) {
                idx = fileList.size() - 1;
            }
        } else {
            idx = 0;
        }
        if (idx >= 0 && idx < fileList.size()) {
            setCurrentItem(fileList.get(idx));
        }
    }

    public void setNextPageItem(int pageSize) {
        int idx;
        if (currentItem != null) {
            idx = getItemIndex(currentItem);
            idx += getItemsAtPage(pageSize);
            if (idx >= getComponentCount()) {
                idx = getComponentCount() - 1;
            }
        } else {
            idx = 0;
        }
        if (idx >= 0 && idx < fileList.size()) {
            setCurrentItem(fileList.get(idx));
        }
    }

    public void setLastItem() {
        if (!fileList.isEmpty()) {
            setCurrentItem(fileList.get(fileList.size() - 1));
        }
    }

    public FileItem getRollovedItem() {
        return rollovedItem;
    }

    public void setRollovedItem(FileItem item) {
        if (rollovedItem != null) {
            rollovedItem.setRollover(false);
        }

        if (getItemIndex(item) >= 0) {
            item.setRollover(true);
            rollovedItem = item;
        } else {
            rollovedItem = null;
        }
    }

    public void itemClicked(FileItem item, MouseEvent e) {
    }

    public void itemPressed(FileItem item) {
        fPanel.requestFocus();
    }

    public void itemEntered(FileItem item) {
    }

    public void itemExited(FileItem item) {
    }

    private int getNearestItemIndex(String fileName) {
        int len = fileName.length();
        String temp;
        if (len > 0) {
            for (int i = 0; i < fileList.size(); i++) {
                temp = fileList.get(i).getFile().getName();
                if (temp.length() > len) {
                    temp = temp.substring(0, len);
                }
                if (temp.length() == len) {
                    if (fileName.compareToIgnoreCase(temp) == 0) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public FileItem getNearestItem(String fileName) {
        int idx = getNearestItemIndex(fileName);
        if (idx >= 0) {
            return fileList.get(idx);
        }
        return null;
    }
    
}
