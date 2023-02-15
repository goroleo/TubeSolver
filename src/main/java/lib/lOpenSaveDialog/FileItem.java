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

public class FileItem extends JComponent {

    private static final DateTimeFormatter dateFormatter
            = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static final DateTimeFormatter timeFormatter
            = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private static final DecimalFormatSymbols symbols
            = new DecimalFormatSymbols(Locale.getDefault());

    public static boolean foldersFirst = true;

    private File fFile;
    private Icon fIcon;
    private Boolean fDir;
    private Boolean fDrive;
    private Boolean fLink;
    private Boolean fParentDir;
    private String fName;
    private String fExt;
    private long fSize;
    private String fSizeStr;
    private LocalDateTime fTime;
    private String fTimeStr;
    private int viewMode = 0; // 0 - detail (file name, size, date), 1 - list (file name only);
    public static int DETAIL_MODE = 0; // file name, size, date,
    public static int LIST_MODE = 1; //  file name only;

    private int fLevel = 0;

    private final JLabel nameLabel = new JLabel();
    private final JLabel sizeLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();

    private int nameWidth;
    private int sizeWidth = 80;
    private int dateWidth = 110;

    //    private final JLabel[] labels = new JLabel[3];
//    private final int[] widths = new int[3]; 
    private boolean selected = false;
    private boolean rollover = false;

    private final Color selectedBackground = new Color(0xb8cfe5);
    private final Color selectedForeground = Color.BLACK;
    private final Color rolloverFrame = Color.GRAY;


    public FileItem(File f) {
        this(f, true, 0);
    }

    public FileItem(File f, boolean detailsMode, int level) {
//        parent = owner;
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

        setSize(450, 25);
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

    public void setFile(File f) {
        fParentDir = false;
        fExt = getFileExt(f);

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
        fFile = f;
        fDir = fFile.isDirectory();
        if (fDir) {
            fSize = 0;
            fSizeStr = "";
        } else {
            fSize = fFile.length();
            fSizeStr = getFileSizeAsString(fSize);
        }
        fTime = getFileTime(fFile);
        fTimeStr = getFileTimeAsString(fTime);
        setLabels();
    }

    public void setLabels() {
        nameLabel.setText(getFName());
        nameLabel.setBackground(null);
        nameLabel.setForeground(null);
        nameLabel.setHorizontalAlignment(2); // left
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        nameLabel.setFont(nameLabel.getFont().deriveFont(0));
        if (getIcon() != null) {
            nameLabel.setIcon(getIcon());
        }

        if (viewMode == DETAIL_MODE) {
            sizeLabel.setText(getLengthStr());
            sizeLabel.setBackground(null);
            sizeLabel.setForeground(null);
            sizeLabel.setHorizontalAlignment(4); // right
            sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            sizeLabel.setFont(sizeLabel.getFont().deriveFont(0));

            dateLabel.setText(getTimeStr());
            dateLabel.setBackground(null);
            dateLabel.setForeground(null);
            dateLabel.setHorizontalAlignment(4); // right
            dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
            dateLabel.setFont(dateLabel.getFont().deriveFont(0));
        }
    }

    public String getFName() {
        if (viewMode == DETAIL_MODE) {
            if (fParentDir) {
                return "[..]";
            } else if (fDir) {
                return "[" + fName + "]";
            }
        }
        return fName;
    }

    public File getFile() {
        return fFile;
    }

    public String getExt() {
        return fExt;
    }

    public Icon getIcon() {
        return fIcon;
    }

    public String getTimeStr() {
        return fTimeStr;
    }

    public String getLengthStr() {
        return fSizeStr;
    }

    public long getLength() {
        return fSize;
    }

    public LocalDateTime getTime() {
        return fTime;
    }

    public boolean isFolder() {
        return fDir;
    }

    public boolean isLink() {
        return fLink;
    }

    public static String getFileExt(File f) {
        if (f.isDirectory()) {
            return "";
        }
        return getFileExt(f.getName());
    }

    public static String getFileExt(String fName) {
        if (fName == null || "".equals(fName)) return "";
        String[] ss = fName.split("\\.");
        if (ss.length > 1) {
            return "." + ss[ss.length - 1];
        } else {
            return "";
        }
    }

    public String getFileSizeAsString(File f) {
        return getFileSizeAsString(f.length());
    }

    public String getFileSizeAsString(long size) {
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

    public LocalDateTime getFileTime(File f) {
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

    public String getFileTimeAsString(File f) {
        LocalDateTime dt = getFileTime(f);
        return getFileTimeAsString(dt);
    }

    public String getFileTimeAsString(LocalDateTime dt) {
        if (dt != null) {
            return dt.format(dateFormatter) + " " + dt.format(timeFormatter);
        } else {
            return "";
        }
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        updateWidth();
    }

    public void updateWidth() {
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

    public void setFileName(String name) {
        nameLabel.setText(name);
    }

    public void setFileDate(String date) {
        if (viewMode == DETAIL_MODE) {
            dateLabel.setText(date);
        }
    }

    public void setFileSize(String date) {
        if (viewMode == DETAIL_MODE) {
            sizeLabel.setText(date);
        }
    }

    public void setWidths(int name, int size, int date) {
        sizeWidth = size;
        dateWidth = date;
        setSize(name + dateWidth + sizeWidth, getHeight());
    }

    public void setWidths(int size, int date) {
        if (viewMode == DETAIL_MODE) {
            int oldDW = dateWidth;
            int oldSW = sizeWidth;
            if (date >= 50) {
                dateWidth = date;
            }
            if (size >= 50) {
                sizeWidth = size;
            }
            if (oldDW != dateWidth || oldSW != sizeWidth) {
                updateWidth();
            }
        }
    }

    public void setNameWidth(int width) {
        if (viewMode == DETAIL_MODE) {
            setSize(width + dateWidth + sizeWidth, getHeight());
        } else {
            setSize(width, getHeight());
        }
    }

    public void setDateWidth(int width) {
        if (viewMode == DETAIL_MODE) {
            dateWidth = width;
            updateWidth();
        }
    }

    public void setSizeWidth(int width) {
        if (viewMode == DETAIL_MODE) {
            sizeWidth = width;
            updateWidth();
        }
    }

    public int getLevel() {
        return fLevel;
    }

    public void setLevel(int newLevel) {
        if (viewMode == LIST_MODE) {
            fLevel = newLevel;
            nameLabel.setSize(getWidth() - 10 * fLevel, getHeight());
            nameLabel.setLocation(10 * fLevel, 0);
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed() {
    }

    public void mouseEntered() {
    }

    public void mouseExited() {
    }

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

    public boolean isSelected() {
        return selected;
    }

    public void setRollover(boolean b) {
        if (rollover != b) {
            rollover = b;
            repaint();
        }
    }

    public boolean isRollover() {
        return rollover;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (selected) {
            g.setColor(selectedBackground);
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        if (rollover) {
            g.setColor(rolloverFrame);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    public static Comparator<FileItem> NameComparatorAsc = (o1, o2) -> {
        int res = 0;
        if (o2.fParentDir || o1.fParentDir) {
            res = (o2.fParentDir ? 1 : 0) - (o1.fParentDir ? 1 : 0);
        }
        if (res == 0 && (o2.fDrive || o1.fDrive)) {
            res = (o2.fDrive ? 1 : 0) - (o1.fDrive ? 1 : 0);
            if (res == 0) {
                String s1 = o1.fFile.getAbsolutePath();
                String s2 = o2.fFile.getAbsolutePath();
                res = (s1).compareToIgnoreCase(s2);
            }
        }
        if (res == 0 && (o2.fDir || o1.fDir)) {
            res = (o2.fDir ? 1 : 0) - (o1.fDir ? 1 : 0);
        }
        if (res == 0) {
            res = o1.fName.compareToIgnoreCase(o2.fName);
        }
        return res;
    };

    public static Comparator<FileItem> NameComparatorDesc = (o1, o2) -> {
        int res = 0;
        if (o2.fParentDir || o1.fParentDir) {
            res = (o2.fParentDir ? 1 : 0) - (o1.fParentDir ? 1 : 0);
        }
        if (res == 0 && (o2.fDrive || o1.fDrive)) {
            res = (o2.fDrive ? 1 : 0) - (o1.fDrive ? 1 : 0);
            if (res == 0) {
                String s1 = o1.fFile.getAbsolutePath();
                String s2 = o2.fFile.getAbsolutePath();
                res = (s1).compareToIgnoreCase(s2);
            }
        }
        if (res == 0 && (o2.fDir || o1.fDir)) {
            res = (o2.fDir ? 1 : 0) - (o1.fDir ? 1 : 0);
        }
        if (res == 0 && (o2.fDir && o1.fDir)) {
            res = o1.fName.compareToIgnoreCase(o2.fName);
        }
        if (res == 0) {
            res = o2.fName.compareToIgnoreCase(o1.fName);
        }
        return res;
    };

    public static Comparator<FileItem> SizeComparatorAsc = (o1, o2) -> {
        if (foldersFirst && !Objects.equals(o2.fDir, o1.fDir)) {
            return (o2.fDir ? 1 : 0) - (o1.fDir ? 1 : 0);
        } else {
            if (o1.fSize != o2.fSize) {
                return (o1.fSize > o2.fSize) ? 1 : -1;
            } else {
                return NameComparatorAsc.compare(o1, o2);
            }
        }
    };

    public static Comparator<FileItem> SizeComparatorDesc = (o1, o2) -> {
        if (foldersFirst && !Objects.equals(o2.fDir, o1.fDir)) {
            return (o2.fDir ? 1 : 0) - (o1.fDir ? 1 : 0);
        } else {
            if (o1.fSize != o2.fSize) {
                return (o2.fSize > o1.fSize) ? 1 : -1;
            } else {
                return NameComparatorAsc.compare(o1, o2);
            }
        }
    };

    public static Comparator<FileItem> TimeComparatorAsc = (o1, o2) -> {
        if (o1.fTime == null) {
            o1.fTime = LocalDateTime.MIN;
        }
        if (o2.fTime == null) {
            o2.fTime = LocalDateTime.MIN;
        }

        if (foldersFirst && !Objects.equals(o2.fDir, o1.fDir)) {
            return (o2.fDir ? 1 : 0) - (o1.fDir ? 1 : 0);
        } else {
            if (!o1.fTime.equals(o2.fTime)) {
                return o1.fTime.compareTo(o2.fTime);
            } else {
                return NameComparatorAsc.compare(o1, o2);
            }
        }
    };

    public static Comparator<FileItem> TimeComparatorDesc = (o1, o2) -> {
        if (o1.fTime == null) {
            o1.fTime = LocalDateTime.MIN;
        }
        if (o2.fTime == null) {
            o2.fTime = LocalDateTime.MIN;
        }

        if (foldersFirst && !Objects.equals(o2.fDir, o1.fDir)) {
            return (o2.fDir ? 1 : 0) - (o1.fDir ? 1 : 0);
        } else {
            if (!o1.fTime.equals(o2.fTime)) {
                return o2.fTime.compareTo(o1.fTime);
            } else {
                return NameComparatorAsc.compare(o1, o2);
            }
        }
    };

}
