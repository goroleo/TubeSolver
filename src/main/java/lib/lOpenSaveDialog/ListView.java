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
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class ListView extends JComponent {

    private final ArrayList<FileItem> fileList = new ArrayList<>();
    private int itemHeight = 25;
    private int sortNumber = 1;
    private boolean sortAscending = true;
    private FileItem currentItem;
    private FileItem mouseOverItem;

    public ListView() {
        setBackground(gui.Palette.dialogColor);
        setForeground(Color.WHITE);
    }

    public ArrayList<FileItem> getFileList() {
        return fileList;
    }

    public FileItem addNewItem(File f) {
        return addNewItem(f, true, 0);
    }

    public FileItem addNewItem(File f, boolean detailsMode, int level) {
        FileItem fi = createNewItem(f, detailsMode, level);
        fileList.add(fi);
        return fi;
    }

    public FileItem createNewItem(File f) {
        return createNewItem(f, true, 0);
    }

    public FileItem createNewItem(File f, boolean detailsMode, int level) {
        return new FileItem(f, detailsMode, level) {
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

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int value) {
        itemHeight = value;
        setSize(getWidth(), fileList.size() * itemHeight);
    }

    @Override
    public void setSize(int w, int h) {
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

    public void setColumnWidths(int size, int date) {
        for (Component item : getComponents()) {
            if (item instanceof FileItem) {
                ((FileItem) item).setLabelWidths(size, date);
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
            case 1: // sort by name
                if (sortAscending) {
                    fileList.sort(FileItem.NameComparatorAsc);
                } else {
                    fileList.sort(FileItem.NameComparatorDesc);
                }
                break;
            case 2: // sort by size
                if (sortAscending) {
                    fileList.sort(FileItem.SizeComparatorAsc);
                } else {
                    fileList.sort(FileItem.SizeComparatorDesc);
                }
                break;
            case 3: // sort by date-time
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
        } else {
            setFirstItem();
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
        } else {
            setFirstItem();
        }
    }

    public void setNextItem() {
        if (currentItem != null) {
            int idx = getItemIndex(currentItem);
            idx++;
            if (idx >= fileList.size()) {
                idx = fileList.size() - 1;
            }
            setCurrentItem(fileList.get(idx));
        } else {
            setFirstItem();
        }
    }

    public void setNextPageItem(int pageSize) {
        if (currentItem != null) {
            int idx = getItemIndex(currentItem);
            idx += getItemsAtPage(pageSize);
            if (idx >= fileList.size()) {
                idx = fileList.size() - 1;
            }
            setCurrentItem(fileList.get(idx));
        } else {
            setFirstItem();
        }
    }

    public void setLastItem() {
        if (!fileList.isEmpty()) {
            setCurrentItem(fileList.get(fileList.size() - 1));
        }
    }

    public FileItem getMouseOverItem() {
        return mouseOverItem;
    }

    public void setMouseOverItem(FileItem item) {
        if (mouseOverItem != null) {
            mouseOverItem.setMouseOver(false);
        }

        if (getItemIndex(item) >= 0) {
            item.setMouseOver(true);
            mouseOverItem = item;
        } else {
            mouseOverItem = null;
        }
    }

    public void itemClicked(FileItem item, MouseEvent e) { }

    public void itemPressed(FileItem item) {  }

    public void itemEntered(FileItem item) { }

    public void itemExited(FileItem item) { }

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
