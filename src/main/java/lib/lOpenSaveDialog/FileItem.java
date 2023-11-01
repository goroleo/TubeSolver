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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

import static lib.lOpenSaveDialog.OpenSavePanel.DEFAULT_EXT;

/**
 * This class is all about file/folder item for any controls of the dialog.
 */
public class FileItem extends JComponent {

// -----------------------------------------------------
//     Locale formatters for date, time and numbers
//
    /**
     * Formatting date using the user locale.
     */
    private static final DateTimeFormatter dateFormatter
            = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    /**
     * Formatting time using the user locale.
     */
    private static final DateTimeFormatter timeFormatter
            = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    /**
     * Formatting numbers using the user locale.
     */
    private static final DecimalFormatSymbols symbols
            = new DecimalFormatSymbols(Locale.getDefault());

// -----------------------------------------------------
//     Fields of the FileItem
//

    /**
     * File field
     */
    private File fFile;

    /**
     * Icon of this file
     */
    private Icon fIcon;

    /**
     * True if this file item is a folder/directory
     */
    private Boolean fFolder;

    /**
     * True if this file item is a drive
     */
    private Boolean fDrive;

    /**
     * True if this file item is a link to another file/folder
     */
    private Boolean fLink;

    /**
     * True if this item is a parent folder for the current file list.<br><i>Not used in this implementation</i>
     */
    private Boolean fParentDir;

    /**
     * File / folder name without a path.
     */
    private String fName;

    /**
     * File extension.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private String fExt;

    /**
     * File size in bytes
     */
    private long fLength;

    /**
     * The date/time stamp of the file
     */
    private LocalDateTime fTime;

// -----------------------------------------------------
//     Components for visualisation a File item
//

    /**
     * Label to display the file name.
     */
    private final JLabel nameLabel = new JLabel();

    /**
     * Label to display the file size. Not visible in the list mode.
     */
    private final JLabel sizeLabel = new JLabel();

    /**
     * Label to display the file date-time. Not visible in the list mode.
     */
    private final JLabel dateLabel = new JLabel();

    /**
     * Width of the Name Label
     */
    private int nameWidth;

    /**
     * Width of the Size Label
     */
    private int sizeWidth = 80;

    /**
     * Width of the Date Label
     */
    private int dateWidth = 110;

    /**
     * Level of the folder from the root folders. Used in the LIST_MODE, ignored in the DETAIL_MODE.
     * This is an indentation level for designating child folders relative to the root.
     */
    private int fLevel;

    /**
     * True if this item is a selected item.
     */
    private boolean selected = false;

    /**
     * True if the mouse cursor is over this item.
     */
    private boolean mouseOver = false;

// -----------------------------------------------------
//     Settings for visualisation
//

    /**
     * True if all folders must be placed before files while sorting the list.
     */
    @SuppressWarnings("CanBeFinal")
    public static boolean foldersFirst = true;

    /**
     * Visualisation mode of the file item: <ul>
     * <li><b>0 - details view</b>. File name, File Size and File Date are displayed.
     * <li><b>1 - list view</b> (compact view). The File name only is displayed.</ul>
     * Folders list is always displayed in the compact mode.
     */
    private final int viewMode; // 0 - detail (file name, size, date), 1 - list (file name only);

    /**
     * Visualisation mode of the file item. <i>DETAIL_MODE</i> is a details vew: File
     * name, File Size and File Date are displayed.
     */
    public static final int DETAIL_MODE = 0; // file name, size, date,

    /**
     * Visualisation mode of the file item. <i>LIST_MODE</i> is a compact vew: the file
     * name only is displayed.
     */
    public static final int LIST_MODE = 1; //  file name only;

    /**
     * Background color when item is selected
     */
    private final Color selectedBackground = new Color(0xb8cfe5);

    /**
     * Foreground color when item is selected
     */
    private final Color selectedForeground = Color.BLACK;

    /**
     * Color for the item frame when the mouse cursor is over this item.
     */
    private final Color overFrameColor = Color.GRAY;

// -----------------------------------------------------
//     Main routines
//

    /* unused
    /**
     * FilItem class constructor with default parameters.
     *
     * @param f file to be precessed and displayed.
    public FileItem(File f) {
        this(f, true, 0);
    }
     */

    /**
     * An extended FilItem class constructor.
     *
     * @param f           file to be precessed and displayed.
     * @param detailsMode True if this file has to be displayed in the details' mode.
     *                    False if it has to be in the compact mode.
     * @param level       The level of this folder from the root folder. Used in the List/compact mode.
     *                    It will be ignored in the details' mode.
     * @see #viewMode
     */
    public FileItem(File f, boolean detailsMode, int level) {

        viewMode = (detailsMode) ? DETAIL_MODE : LIST_MODE;
        fLevel = level;
        if (f != null) setFile(f);
        setBackground(null);
        setForeground(null);

        add(nameLabel);
        if (viewMode == DETAIL_MODE) {
            add(sizeLabel);
            add(dateLabel);
        }

        setSelected(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FileItem.this.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                FileItem.this.mousePressed();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                FileItem.this.mouseEntered();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                FileItem.this.mouseExited();
            }
        });
    }

    /**
     * This routine fills all FileItem class' fields.
     *
     * @param f file
     */
    public void setFile(File f) {
        fParentDir = false;
        fFile = f;
        fFolder = fFile.isDirectory();

        if (fFolder) {
            fLength = 0;
            fExt = "";
        } else {
            fLength = fFile.length();
            fExt = extractFileExt(f.getName());
        }

        if (OpenSavePanel.fsv != null) {
            fName = OpenSavePanel.fsv.getSystemDisplayName(f);
            if (viewMode == DETAIL_MODE && fExt.compareToIgnoreCase(DEFAULT_EXT) == 0) {
                fIcon = OpenSavePanel.jctlIcon;
            } else {
                fIcon = OpenSavePanel.fsv.getSystemIcon(f);
            }
            fDrive = OpenSavePanel.fsv.isDrive(f);
            fLink = OpenSavePanel.fsv.isLink(f);
        } else {
            fName = f.getName();
            fDrive = false;
        }
        fTime = getFileTime(fFile);

        setLabels();
    }

    /**
     * This routine sets all labels options and fills labels from the class' fields.
     */
    private void setLabels() {
        nameLabel.setText(getFileName());
        nameLabel.setBackground(null);
        nameLabel.setForeground(null);
        nameLabel.setHorizontalAlignment(2); // left
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
        if (getIcon() != null) {
            nameLabel.setIcon(getIcon());
        }

        if (viewMode == DETAIL_MODE) {
            sizeLabel.setText(getLengthStr());
            sizeLabel.setBackground(null);
            sizeLabel.setForeground(null);
            sizeLabel.setHorizontalAlignment(4); // right
            sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            sizeLabel.setFont(sizeLabel.getFont().deriveFont(Font.PLAIN));

            dateLabel.setText(getTimeStr());
            dateLabel.setBackground(null);
            dateLabel.setForeground(null);
            dateLabel.setHorizontalAlignment(4); // right
            dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
            dateLabel.setFont(dateLabel.getFont().deriveFont(Font.PLAIN));
        }
    }

    /**
     * This is a getter for the File field.
     * @return a File of this item
     */
    public File getFile() {
        return fFile;
    }

    /**
     * This is a getter for the FileName field. Please note this routine returns a displayed
     * file name. You can use <i>getFile().getName()</i> to get a real file name.
     * @return a displayed file name.
     */
    public String getFileName() {
        if (viewMode == DETAIL_MODE) {
            if (fParentDir) {
                return "[..]";
            } else if (fFolder) {
                return "[" + fName + "]";
            }
        }
        return fName;
    }

    /* unused
    /**
     * @return file extension.
    public String getExt() {
        return fExt;
    }
     */

    /**
     * @return file icon.
     */
    public Icon getIcon() {
        return fIcon;
    }

    /* unused
    /**
     * @return file length (size) of the file.
     *
    public long getLength() {
        return fLength;
    }
     */

    /**
     * @return current user locale formatted string with the length (size) of the file.
     */
    public String getLengthStr() {
        if (!fFolder)
            return getFileLengthAsString(fLength);
        else return "";
    }

    /* unused
    /**
     * @return file date/time stamp.
    public LocalDateTime getTime() {
        return fTime;
    }
     */

    /**
     * @return current user locale formatted string with the date-time stamp of the file.
     */
    public String getTimeStr() {
        if (!fFolder)
            return getFileTimeAsString(fTime);
        else return "";
    }

    /**
     * @return true if this file is a folder
     */
    public boolean isFolder() {
        return fFolder;
    }

    /**
     * @return true if this file is a link to another file/folder.
     */
    public boolean isLink() {
        return fLink;
    }

    /**
     * Gets formatted string to display the file size at the list.
     *
     * @param size size of the file in bytes.
     * @return Length of the file as the string.
     */
    private String getFileLengthAsString(long size) {
        double fs = (double) size;
        int level = 0;
        while (fs > 1024 && level < 4) {
            fs /= 1024.d;
            level++;
        }

        String res = new DecimalFormat("###,###.#", symbols).format(fs);
        switch (level) {
            case 0:
                res = res + " B";
                break;
            case 1:
                res = res + " KiB";
                break;
            case 2:
                res = res + " MiB";
                break;
            case 3:
                res = res + " GiB";
                break;
            default:
                res = res + " TiB";
        }
        return res;
    }

    /**
     * Gets date-time stamp of the file.
     *
     * @param f file
     * @return DateTime stamp
     */
    private LocalDateTime getFileTime(File f) {
        if (f.isDirectory()) {
            return null;
        }
        Path file = Paths.get(f.getAbsolutePath());
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(file, BasicFileAttributes.class);
            FileTime fileTime = attr.lastModifiedTime();
            return fileTime
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Gets formatted string to display the file's date-time stamp  at the list.
     *
     * @param dt date-time stamp
     * @return DateTime of the file as the string.
     */
    private String getFileTimeAsString(LocalDateTime dt) {
        if (dt != null) {
            return dt.format(dateFormatter) + " " + dt.format(timeFormatter);
        } else {
            return "";
        }
    }

    /**
     * A service routine to extract file extension.
     *
     * @param f file to extract an extension.
     * @return extension or an empty string.
     */
    public static String extractFileExt(File f) {
        if (f.isDirectory()) {
            return "";
        }
        return extractFileExt(f.getName());
    }

    /**
     * A service routine to extract file extension.
     *
     * @param fName file name to extract an extension.
     * @return extension or an empty string.
     */
    public static String extractFileExt(String fName) {
        if (fName == null || "".equals(fName))
            return "";
        int idx = fName.lastIndexOf(".");
        if (idx > 0) {
            return fName.substring(idx);
        } else {
            return "";
        }
    }

// -----------------------------------------------------
//     Visualisation routines
//

    /**
     * @return Level of the folder from the root folders.
     */
    public int getLevel() {
        return fLevel;
    }

    /**
     * Sets an indentation level for designating child folders relative to the root.
     *
     * @param newLevel as is
     */
    public void setLevel(int newLevel) {
        if (viewMode == LIST_MODE) {
            fLevel = newLevel;
            nameLabel.setSize(getWidth() - 10 * fLevel, getHeight());
            nameLabel.setLocation(10 * fLevel, 0);
        } else fLevel = 0;
    }

    /* unused
    /**
     * @return true if this item is a selected item.
    public boolean isSelected() {
        return selected;
    }
     */

    /**
     * Sets this file item as selected or not selected.
     *
     * @param b true if selected false otherwise.
     */
    public void setSelected(boolean b) {
        if (selected != b) {
            if (b) {
                setForeground(selectedForeground);
            } else {
                setForeground(null);
            }
            selected = b;
            repaint();
        }
    }

    /* unused
    /**
     * @return true if the mouse cursor is above this item.
    public boolean isMouseOver() {
        return mouseOver;
    }
     */

    /**
     * A setter for the mouseOver field.
     *
     * @param b true or false
     * @see #mouseOver
     */
    public void setMouseOver(boolean b) {
        if (mouseOver != b) {
            mouseOver = b;
            repaint();
        }
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        updateLabelWidths();
    }

    /**
     * Sets widths of Size and Date labels for the DETAIL_MODE
     *
     * @param size width of the size label
     * @param date width of the date label
     */
    public void setLabelWidths(int size, int date) {
        if (viewMode == DETAIL_MODE) {
            int oldDW = dateWidth;
            dateWidth = date;
            int oldSW = sizeWidth;
            sizeWidth = size;
            if (oldDW != dateWidth || oldSW != sizeWidth
                    || nameWidth != getWidth() - (sizeWidth + dateWidth)) {
                updateLabelWidths();
            }
        }
    }

    /**
     * Update labels widths
     */
    public void updateLabelWidths() {
        int h = getHeight();
        if (viewMode == DETAIL_MODE) {
            nameWidth = getWidth() - sizeWidth - dateWidth;

            nameLabel.setSize(nameWidth, h);
            nameLabel.setLocation(0, 0);

            sizeLabel.setSize(sizeWidth, h);
            sizeLabel.setLocation(nameWidth, 0);

            dateLabel.setSize(dateWidth, h);
            dateLabel.setLocation(nameWidth + sizeWidth, 0);
        } else {
            nameLabel.setSize(getWidth() - 10 * fLevel, h);
            nameLabel.setLocation(10 * fLevel, 0);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (selected) {
            g.setColor(selectedBackground);
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        if (mouseOver) {
            g.setColor(overFrameColor);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * The routine to override it.
     *
     * @param e the event to be processed.
     * @see MouseEvent
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component. The routine to override it.
     */
    public void mousePressed() {
    }

    /**
     * Invoked when the mouse enters a component. The routine to override it.
     */
    public void mouseEntered() {
    }

    /**
     * Invoked when the mouse exits a component. The routine to override it.
     */
    public void mouseExited() {
    }


// -----------------------------------------------------
//     Sorting comparators
//

    /**
     * Comparator by file name ascending.
     */
    public static final Comparator<FileItem> NameComparatorAsc = (o1, o2) -> {
        int res = 0;
        if (o2.fParentDir || o1.fParentDir) {
            res = (o2.fParentDir ? 1 : 0) - (o1.fParentDir ? 1 : 0);
        }
        if (res == 0 && (o2.fDrive || o1.fDrive)) {
            res = (o2.fDrive ? 1 : 0) - (o1.fDrive ? 1 : 0);
            if (res == 0) {
                res = o1.fFile.getAbsolutePath()
                        .compareToIgnoreCase(
                                o2.fFile.getAbsolutePath());
            }
        }
        if (res == 0 && (o2.fFolder || o1.fFolder)) {
            res = (o2.fFolder ? 1 : 0) - (o1.fFolder ? 1 : 0);
        }
        if (res == 0) {
            res = o1.fName.compareToIgnoreCase(o2.fName);
        }
        return res;
    };

    /**
     * Comparator by file name descending.
     */
    public static final Comparator<FileItem> NameComparatorDesc = (o1, o2) -> {
        int res = 0;
        if (o2.fParentDir || o1.fParentDir) {
            res = (o2.fParentDir ? 1 : 0) - (o1.fParentDir ? 1 : 0);
        }
        if (res == 0 && (o2.fDrive || o1.fDrive)) {
            res = (o2.fDrive ? 1 : 0) - (o1.fDrive ? 1 : 0);
            if (res == 0) {
                res = o1.fFile.getAbsolutePath()
                        .compareToIgnoreCase(
                                o2.fFile.getAbsolutePath());
            }
        }
        if (res == 0 && (o2.fFolder || o1.fFolder)) {
            res = (o2.fFolder ? 1 : 0) - (o1.fFolder ? 1 : 0);
        }
        if (res == 0) {
            res = o2.fName.compareToIgnoreCase(o1.fName);
        }
        return res;
    };

    /**
     * Comparator by file size ascending.
     */
    public static final Comparator<FileItem> SizeComparatorAsc = (o1, o2) -> {
        if (foldersFirst && !Objects.equals(o2.fFolder, o1.fFolder)) {
            return (o2.fFolder ? 1 : 0) - (o1.fFolder ? 1 : 0);
        } else {
            if (o1.fLength != o2.fLength) {
                return (o1.fLength > o2.fLength) ? 1 : -1;
            } else {
                return NameComparatorAsc.compare(o1, o2);
            }
        }
    };

    /**
     * Comparator by file size descending.
     */
    public static final Comparator<FileItem> SizeComparatorDesc = (o1, o2) -> {
        if (foldersFirst && !Objects.equals(o2.fFolder, o1.fFolder)) {
            return (o2.fFolder ? 1 : 0) - (o1.fFolder ? 1 : 0);
        } else {
            if (o1.fLength != o2.fLength) {
                return (o2.fLength > o1.fLength) ? 1 : -1;
            } else {
                return NameComparatorAsc.compare(o1, o2);
            }
        }
    };

    /**
     * Comparator by file time ascending.
     */
    public static final Comparator<FileItem> TimeComparatorAsc = (o1, o2) -> {
        if (o1.fTime == null) {
            o1.fTime = LocalDateTime.MIN;
        }
        if (o2.fTime == null) {
            o2.fTime = LocalDateTime.MIN;
        }

        if (foldersFirst && !Objects.equals(o2.fFolder, o1.fFolder)) {
            return (o2.fFolder ? 1 : 0) - (o1.fFolder ? 1 : 0);
        } else {
            if (!o1.fTime.equals(o2.fTime)) {
                return o1.fTime.compareTo(o2.fTime);
            } else {
                return NameComparatorAsc.compare(o1, o2);
            }
        }
    };

    /**
     * Comparator by file time descending.
     */
    public static final Comparator<FileItem> TimeComparatorDesc = (o1, o2) -> {
        if (o1.fTime == null) {
            o1.fTime = LocalDateTime.MIN;
        }
        if (o2.fTime == null) {
            o2.fTime = LocalDateTime.MIN;
        }

        if (foldersFirst && !Objects.equals(o2.fFolder, o1.fFolder)) {
            return (o2.fFolder ? 1 : 0) - (o1.fFolder ? 1 : 0);
        } else {
            if (!o1.fTime.equals(o2.fTime)) {
                return o2.fTime.compareTo(o1.fTime);
            } else {
                return NameComparatorAsc.compare(o1, o2);
            }
        }
    };

}
