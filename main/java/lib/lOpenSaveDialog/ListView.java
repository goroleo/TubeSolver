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
@SuppressWarnings("unused")
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

    /**
     * Creates the list of FilItems.
     */
    public ListView() {
        setBackground(gui.Palette.dialogColor);
        setForeground(Color.WHITE);
    }

    /**
     * @return the list with the file items.
     */
    public ArrayList<FileItem> getList() {
        return list;
    }

    /**
     * Creates a new FileItem and adds it to the list with a default parameters: detailsMode is true, itemLevel is 0.
     * @param f file
     * @return a new created FileItem.
     */
    @SuppressWarnings("UnusedReturnValue")
    public FileItem addNewItem(File f) {
        return addNewItem(f, true, 0);
    }

    /**
     * Creates a new FileItem and adds it to the list.
     * @param f file
     * @param detailsMode true for details view, false to single name view.
     * @param level level of the item at the list (single name) view.
     * @return a new created FileItem.
     */
    public FileItem addNewItem(File f, boolean detailsMode, int level) {
        FileItem fi = createNewItem(f, detailsMode, level);
        list.add(fi);
        return fi;
    }

    /**
     * Creates a new FileItem and no adds it to the list. Used default parameters: detailsMode is true, itemLevel is 0.
     * @param f file
     * @return a new created FileItem.
     */
    public FileItem createNewItem(File f) {
        return createNewItem(f, true, 0);
    }

    /**
     * Creates a new FileItem without adding it to the list.
     * @param f file
     * @param detailsMode true for details view, false to single name view.
     * @param level level of the item at the list (single name) view.
     * @return a new created FileItem.
     */
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

    /**
     * Gets the height of the every single list item.
     * @return item height.
     */
    public int getItemHeight() {
        return itemHeight;
    }

    /**
     * Sets the height of the every single list item.
     * @param value a new height for list items.
     */
    public void setItemHeight(int value) {
        itemHeight = value;
        setSize(getWidth(), list.size() * itemHeight);
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, list.size() * itemHeight);
        updateItemsPos();
    }

    /**
     * Transfer the FileItems list to the visual component to display it.
     */
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

    /**
     * Adds the single FileItem to the visual component.
     * @param item FileItem from the list.
     */
    public void addItemToView(FileItem item) {
        add(item);
    }

    /**
     * Updates widths of List columns due to the columns resize.
     *
     * @param size width of the Size column.
     * @param date width of the Date column.
     */
    public void setColumnWidths(int size, int date) {
        for (FileItem item : list) {
            item.setLabelWidths(size, date);
        }
    }

    /**
     * Updates the visual positions of items when they have been re-sorted.
     */
    public void updateItemsPos() {
        int w = getWidth();
        for (FileItem item : list) {
            item.setSize(w, itemHeight);
            item.setLocation(0, list.indexOf(item) * itemHeight);
        }
    }

    /**
     * Gets the number of column currently sorted.
     *
     * @return column number.
     */
    public int getSortNumber() {
        return sortNumber;
    }

    /**
     * Gets the current order of fileList sort.
     *
     * @return true for ascending order, false for descending
     */
    public boolean getSortAscending() {
        return sortAscending;
    }

    /**
     * Sorts the List.
     *
     * @param number column number to sort the List
     * @param ascending  true for ascending order, false for descending.
     */
    public void sort(int number, boolean ascending) {
        sortNumber = number;
        sortAscending = ascending;
        doSort();
    }

    /**
     * Sorts the List by the column number. If the list currently sorted by this column, just changes
     * the sort order. Otherwise, it changes sort column using the current sort order.
     *
     * @param number column number to sort the List
     */
    public void sort(int number) {
        if (sortNumber == number) {
            sortAscending = !sortAscending;
        } else {
            sortNumber = number;
        }
        doSort();
    }

    /**
     * Doing sort using the current sort column and the current sort order.
     */
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

    /**
     * @return current FileItem of the List
     */
    public FileItem getCurrentItem() {
        return currentItem;
    }

    /**
     * Sets the currently selected item.
     * @param item FileItem of the List
     */
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

    /**
     * Gets index of the specified item.
     * @param item FileItem of the List
     * @return index of this item or null.
     */
    public int getItemIndex(FileItem item) {
        return list.indexOf(item);
    }

    /**
     * Gets FileItem of the list by specified index.
     * @param index index of the Item.
     * @return FileItem with the specified index or null/
     */
    public FileItem getItemOf(int index) {
        return list.get(index);
    }

    /**
     * Gets how many items can be displayed on one page (one screen).
     * @param pageSize height of the viewable area of the list, in pixels.
     * @return items number on the page.
     */
    public int getItemsAtPage(int pageSize) {
        return pageSize / itemHeight;
    }

    /**
     * Sets the first item as a selected item.
     */
    public void setFirstItem() {
        if (!list.isEmpty()) {
            setCurrentItem(list.get(0));
        }
    }

    /**
     * Sets the selected item one page before the current one.
     * @param pageSize height of the viewable area of the list, in pixels.
     */
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

    /**
     * Sets the previous item as a selected item.
     */
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

    /**
     * Sets the next item as a selected item.
     */
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

    /**
     * Sets the selected item one page after the current one.
     * @param pageSize height of the viewable area of the list, in pixels.
     */
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

    /**
     * Sets the last item as a selected item.
     */
    public void setLastItem() {
        if (!list.isEmpty()) {
            setCurrentItem(list.get(list.size() - 1));
        }
    }

    /**
     * Gets the item with the mouse cursor over it.
     * @return file list item with the mouse cursor over it.
     */
    public FileItem getMouseOverItem() {
        return mouseOverItem;
    }

    /**
     * Sets the item with the mouse cursor over it.
     * @param item file list item.
     */
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

    /**
     * Handles the mouse click on the specified item. Used to override it.
     * @param item the FileList item that has been clicked.
     * @param e mouse event
     * @see MouseEvent
     */
    public void onItemClicked(FileItem item, MouseEvent e) {
    }

    /**
     * Handles the mouse press on the specified item. Used to override it.
     * @param item the FileList item that has been clicked.
     */
    public void onItemPressed(FileItem item) {
    }

    /**
     * Handles the mouse entered on the specified item. Used to override it.
     * @param item the FileList item that has been clicked.
     */
    public void onItemEntered(FileItem item) {
    }

    /**
     * Handles the mouse exited from the specified item. Used to override it.
     * @param item the FileList item that has been clicked.
     */
    public void onItemExited(FileItem item) {
    }

    /**
     * Checks and returns the index of the first item whose file name begins with the input string.
     * @param fileName string to find
     * @return index of the founded fileList item or -1 if not found.
     */
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

    /**
     * Checks and returns the first item of the List whose file name begins with the input string.
     * @param fileName string to find
     * @return founded fileList item or null if not found.
     */
    public FileItem getNearestItem(String fileName) {
        int idx = getNearestItemIndex(fileName);
        if (idx >= 0) {
            return list.get(idx);
        }
        return null;
    }

    /**
     * Finds a FileItem that displays the specified file.
     * @param f file to find
     * @return founded fileList item or null if not found.
     */
    public FileItem getItemByFile(File f) {
        if (f != null)
            for (FileItem item : list) {
                if (item.getFile().compareTo(f) == 0) {
                    return item;
                }
            }
        return null;
    }

    /**
     * Finds a FileItem that displays the specified file name.
     * @param fileName string to find
     * @return founded fileList item or null if not found.
     */
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
