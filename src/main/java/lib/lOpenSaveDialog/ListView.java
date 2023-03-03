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

/**
 * This is the parent class of the list of files/folders. It provides all common methods and routines.
 */
public class ListView extends JComponent {

    /**
     *
     */
    private final ArrayList<FileItem> list = new ArrayList<>();
    private int itemHeight = 25;
    private int sortNumber = 1;
    private boolean sortAscending = true;
    private FileItem currentItem;
    private FileItem mouseOverItem;

    public ListView() {
        setBackground(gui.Palette.dialogColor);
        setForeground(Color.WHITE);
    }

    public ArrayList<FileItem> getList() {
        return list;
    }

    @SuppressWarnings("UnusedReturnValue")
    public FileItem addNewItem(File f) {
        return addNewItem(f, true, 0);
    }

    public FileItem addNewItem(File f, boolean detailsMode, int level) {
        FileItem fi = createNewItem(f, detailsMode, level);
        list.add(fi);
        return fi;
    }

    @SuppressWarnings("unused")
    public FileItem createNewItem(File f) {
        return createNewItem(f, true, 0);
    }

    public FileItem createNewItem(File f, boolean detailsMode, int level) {
        return new FileItem(f, detailsMode, level) {
            @Override
            public void mouseClicked(MouseEvent e) {
                ListView.this.onItemClicked(this, e);
            }

            @Override
            public void mousePressed() {
                ListView.this.onItemPressed(this);
            }

            @Override
            public void mouseEntered() {
                ListView.this.onItemEntered(this);
            }

            @Override
            public void mouseExited() {
                ListView.this.onItemExited(this);
            }
        };
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int value) {
        itemHeight = value;
        setSize(getWidth(), list.size() * itemHeight);
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, list.size() * itemHeight);
        updateItemsPos();
    }

    public void updateView() {
        this.removeAll();
        if (!list.isEmpty()) {
            for (FileItem item : list) {
                addItemToView(item);
            }
        }
        setSize(getWidth(), list.size() * itemHeight);
        repaint();
    }

    public void addItemToView(FileItem item) {
        add(item);
    }

    public void updateItemsPos() {
        int w = getWidth();
        for (FileItem item : list) {
            item.setSize(w, itemHeight);
            item.setLocation(0, list.indexOf(item) * itemHeight);
        }
    }

    public void setColumnWidths(int size, int date) {
        for (FileItem item : list) {
            item.setLabelWidths(size, date);
        }
    }

    public int getSortNumber() {
        return sortNumber;
    }

    public boolean getSortAscending() {
        return sortAscending;
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
                    list.sort(FileItem.NameComparatorAsc);
                } else {
                    list.sort(FileItem.NameComparatorDesc);
                }
                break;
            case 2: // sort by size
                if (sortAscending) {
                    list.sort(FileItem.SizeComparatorAsc);
                } else {
                    list.sort(FileItem.SizeComparatorDesc);
                }
                break;
            case 3: // sort by date-time
                if (sortAscending) {
                    list.sort(FileItem.TimeComparatorAsc);
                } else {
                    list.sort(FileItem.TimeComparatorDesc);
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
        return list.indexOf(item);
    }

    @SuppressWarnings("unused")
    public FileItem getItemOf(int index) {
        return list.get(index);
    }

    public int getItemsAtPage(int pageSize) {
        return pageSize / itemHeight;
    }

    public void setFirstItem() {
        if (!list.isEmpty()) {
            setCurrentItem(list.get(0));
        }
    }

    public void setPreviousPageItem(int pageSize) {
        if (currentItem != null) {
            int idx = getItemIndex(currentItem);
            idx -= getItemsAtPage(pageSize);
            if (idx < 0) {
                idx = 0;
            }
            if (list.size() > idx) {
                setCurrentItem(list.get(idx));
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
            if (list.size() > idx) {
                setCurrentItem(list.get(idx));
            }
        } else {
            setFirstItem();
        }
    }

    public void setNextItem() {
        if (currentItem != null) {
            int idx = getItemIndex(currentItem);
            idx++;
            if (idx >= list.size()) {
                idx = list.size() - 1;
            }
            setCurrentItem(list.get(idx));
        } else {
            setFirstItem();
        }
    }

    public void setNextPageItem(int pageSize) {
        if (currentItem != null) {
            int idx = getItemIndex(currentItem);
            idx += getItemsAtPage(pageSize);
            if (idx >= list.size()) {
                idx = list.size() - 1;
            }
            setCurrentItem(list.get(idx));
        } else {
            setFirstItem();
        }
    }

    public void setLastItem() {
        if (!list.isEmpty()) {
            setCurrentItem(list.get(list.size() - 1));
        }
    }

    @SuppressWarnings("unused")
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

    public void onItemClicked(FileItem item, MouseEvent e) {
    }

    public void onItemPressed(@SuppressWarnings("unused") FileItem item) {
    }

    public void onItemEntered(FileItem item) {
    }

    public void onItemExited(@SuppressWarnings("unused") FileItem item) {
    }

    private int getNearestItemIndex(String fileName) {
        int len = fileName.length();
        String temp;
        if (len > 0) {
            for (int i = 0; i < list.size(); i++) {
                temp = list.get(i).getFile().getName();
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
            return list.get(idx);
        }
        return null;
    }

    public FileItem getItemByFile(File f) {
        if (f != null)
            for (FileItem item : list) {
                if (item.getFile().compareTo(f) == 0) {
                    return item;
                }
            }
        return null;
    }

    @SuppressWarnings("unused")
    public FileItem getItemByFileName(String fileName) {
        if (fileName != null && !"".equals(fileName))
            for (FileItem item : list) {
                if (item.getFile().getName().compareToIgnoreCase(fileName) == 0) {
                    return item;
                }
            }
        return null;
    }

}
