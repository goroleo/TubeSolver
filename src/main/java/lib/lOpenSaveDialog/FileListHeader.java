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

import core.ResStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.imgBtnDown;
import static lib.lOpenSaveDialog.OpenSavePanel.imgBtnUp;

/**
 * The header of the FileList. It shows the file list columns names and current sort state.
 * It can change the column widths. It sets up a new sort for the list of files.
 */
public class FileListHeader extends JComponent {

    /**
     * The label with the Name word.
     */
    private final JLabel nameLabel;

    /**
     * The label with the Size word.
     */
    private final JLabel sizeLabel;

    /**
     * The label with the Date word.
     */
    private final JLabel dateLabel;

    /**
     * An additional layer above the header that shows the current header's state.
     * It displays the current position of the column separators and the current sort state.
     */
    private final JComponent stateLayer;

    /**
     * A width of the Name label
     */
    private int nameWidth;

    /**
     * A width of the Size label
     */
    private int sizeWidth = 75;

    /**
     * A width of the Date label
     */
    private int dateWidth = 110;

    /**
     * A width of the header. Its width is the sum of nameWidth + sizeWidth + dateWidth.
     */
    private int headerWidth;

    /**
     * True if the header is in the columns sizes dragging mode.
     */
    private boolean dragging = false;

    /**
     * An active separator number while columns widths are dragging.
     * The number returns from the routine.
     *
     * @see #getLabelAtMousePos
     */
    private int sepNumber = 0;

    /**
     * A starting drag position. The mouse position when the dragging starts.
     */
    private int dragPos = 0;

    /**
     * An image to display the Ascending sort direction.
     *
     * @see #getImageShape()
     */
    private BufferedImage imgUp;

    /**
     * An image to display the Descending sort direction.
     *
     * @see #getImageShape()
     */
    private BufferedImage imgDown;

    /**
     * The parent panel for access all the dialog's possibilities.
     */
    private final FilesPanel fPanel;

    /**
     * This creates the Header without a parent.
     */
    public FileListHeader() {
        this(null);
    }

    /**
     * This creates the Header.
     *
     * @param owner a FilesPanel object that owns this header.
     */
    public FileListHeader(FilesPanel owner) {

        fPanel = owner;

        // no need to draws anything
        setBackground(null);
        setForeground(null);

        // preparing images with the specified color.
        prepareImages(new Color(0xb8cfe5));

        // creating a state layer. It only draws the header state.
        stateLayer = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(Color.GRAY);
                g.drawLine(nameWidth, 3, nameWidth, getHeight() - 4);
                g.drawLine(nameWidth + sizeWidth, 3, nameWidth + sizeWidth, getHeight() - 4);
                g.drawImage(getImageShape(), getImageX(), 1, null);
            }
        };
        add(stateLayer);

        // creating header labels
        nameLabel = new JLabel(ResStrings.getString("strFileName"));
        nameLabel.setBackground(null);
        nameLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setHorizontalAlignment(0); // center
        nameLabel.setFont(nameLabel.getFont().deriveFont(0));

        sizeLabel = new JLabel(ResStrings.getString("strFileSize"));
        sizeLabel.setBackground(null);
        sizeLabel.setForeground(Color.LIGHT_GRAY);
        sizeLabel.setHorizontalAlignment(0); // center
        sizeLabel.setFont(sizeLabel.getFont().deriveFont(0));

        dateLabel = new JLabel(ResStrings.getString("strFileDate"));
        dateLabel.setBackground(null);
        dateLabel.setForeground(Color.LIGHT_GRAY);
        dateLabel.setHorizontalAlignment(0); // center
        dateLabel.setFont(dateLabel.getFont().deriveFont(0));

        add(nameLabel);
        add(sizeLabel);
        add(dateLabel);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (osPan.isFoldersPanelVisible()) {
                    // nothing to do
                    return;
                }
                if (getLabelAtMousePos(e.getX()) < 10) {
                    setCursor(Cursor.getDefaultCursor());
                } else {
                    // if the cursor is over one of the separators
                    setCursor(OpenSavePanel.cursorResize);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    if (sepNumber == 11) { // separator between Name and Size
                        // column size to the left of separator (name), it must be 50 pix or more.
                        nameWidth = Math.max(e.getX() - dragPos, 50);
                        // column size to the right of separator (size), it also must be 50 or more
                        sizeWidth = Math.max(headerWidth - dateWidth - nameWidth, 50);
                        // dateWidth is not changes
                    } else if (sepNumber == 12) { // separator between Size and Date
                        // column size to the left of separator (size), it must be 50 or more.
                        sizeWidth = Math.max(e.getX() - dragPos - nameWidth, 50);
                        // column size to the right of separator (date)
                        dateWidth = headerWidth - nameWidth - sizeWidth;
                        //  date label size is also must be 50 or more
                        if (dateWidth < 50) {
                            dateWidth = 50;
                            sizeWidth = headerWidth - nameWidth - dateWidth;
                        }
                    }
                    updateColumnWidths();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (osPan.isFoldersPanelVisible()) {
                    osPan.showFoldersPanel(false);
                    return;
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int labelNum = getLabelAtMousePos(e.getX());

                    // double click
                    if (e.getClickCount() == 2) {
                        switch (labelNum) {
                            case 1: // name label
                                sizeWidth = 75;
                                dateWidth = 110;
                                updateColumnWidths();
                                break;
                            case 2: // size label
                                sizeWidth = 75;
                                updateColumnWidths();
                                break;
                            case 3: // date label
                                dateWidth = 110;
                                updateColumnWidths();
                                break;
                            default:
                                break;
                        }
                    }
                    // single click
                    else if (e.getClickCount() == 1) {
                        if (fPanel != null && labelNum < 10) {
                            fPanel.sortFileList(labelNum);
                            stateLayer.repaint();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (fPanel != null) {
                    fPanel.requestFocus();
                }

                if (e.getButton() == MouseEvent.BUTTON1) {
                    int mousePos = e.getX();
                    sepNumber = getLabelAtMousePos(mousePos);

                    if (sepNumber == 11) {
                        dragging = true;
                        dragPos = mousePos - nameWidth;
                    } else if (sepNumber == 12) {
                        dragging = true;
                        dragPos = mousePos - (nameWidth + sizeWidth);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });
    }

    @Override
    public void setSize(int width, int height) {
        height = 24;
        super.setSize(width, height);
        stateLayer.setSize(width, height);
        updateColumnWidths();
    }

    /**
     * Updates column widths. The most often resized column is the Name column.
     * Date & Size columns will save them widths if it is possible.
     * Finally, it makes FileList to resize its components as well.
     */
    public void updateColumnWidths() {
        headerWidth = getWidth();
        nameWidth = headerWidth - sizeWidth - dateWidth;

        if (nameWidth < 50) {
            nameWidth = 50;
            int theRest = headerWidth - nameWidth;
            double sizePart = (double) sizeWidth / (double) (sizeWidth + dateWidth);
            if (sizePart < 0.5d) {
                sizeWidth = Math.max((int) (theRest * sizePart), 50);
                dateWidth = theRest - sizeWidth;
            } else {
                dateWidth = Math.max((int) (theRest * (1 - sizePart)), 50);
                sizeWidth = theRest - dateWidth;
            }
        }

        int h = getHeight();

        nameLabel.setSize(nameWidth, h);
        nameLabel.setLocation(0, 0);

        sizeLabel.setSize(sizeWidth, h);
        sizeLabel.setLocation(nameWidth, 0);

        dateLabel.setSize(dateWidth, h);
        dateLabel.setLocation(nameWidth + sizeWidth, 0);
        stateLayer.repaint();

        if (fPanel != null) {
            fPanel.updateColumnWidths(nameWidth, sizeWidth, dateWidth);
        }
    }

    /**
     * This sets the initial columns widths.
     *
     * @param name width of the Name column
     * @param size width of the Size column
     * @param date width of the Date column
     */
    public void setColumnWidths(int name, int size, int date) {
        dateWidth = date;
        sizeWidth = size;
        setSize(name + dateWidth + sizeWidth, getHeight());
    }

    /**
     * @return a width of the Name label
     */
    public int getNameWidth() {
        return nameWidth;
    }

    /**
     * @return a width of the Size label
     */
    public int getSizeWidth() {
        return sizeWidth;
    }

    /**
     * @return a width of the Date label
     */
    public int getDateWidth() {
        return dateWidth;
    }

    /**
     * Determines where the mouse cursor is situated above the header. It returns number
     * of the column or number of the separator between columns: <ul>
     * <li>1 - cursor is over the Name column
     * <li>2 - the Size column
     * <li>3 - the Date column
     * <br>--------------
     * <li>11 - the separator between Name & Size columns
     * <li>12 - the separator between Size & Date columns
     * </ul>
     *
     * @param pos mouse position (X-coordinate)
     * @return label number as specified.
     */
    private int getLabelAtMousePos(int pos) {
        if (pos < nameWidth - 3) {
            // name label
            return 1;
        } else if (pos < nameWidth + 3) {
            // name - size separator
            return 11; // separator # 1
        } else if (pos < nameWidth + sizeWidth - 3) {
            // size label
            return 2;
        } else if (pos < nameWidth + sizeWidth + 3) {
            // size - date separator
            return 12; // separator # 2
        } else {
            // date label
            return 3;
        }
    }

    /**
     * Fills images of sorting direction in the desired color.
     *
     * @param clr the desired color
     */
    private void prepareImages(Color clr) {
        imgDown = new BufferedImage(imgBtnDown.getWidth(), imgBtnDown.getHeight(), 2);
        for (int x = 0; x < imgBtnDown.getWidth(); x++) {
            for (int y = 0; y < imgBtnDown.getHeight(); y++) {
                int rgb = (imgBtnDown.getRGB(x, y) & 0xff000000) | (clr.getRGB() & 0xffffff);
                imgDown.setRGB(x, y, rgb);
            }
        }
        imgUp = new BufferedImage(imgBtnUp.getWidth(), imgBtnUp.getHeight(), 2);
        for (int x = 0; x < imgBtnUp.getWidth(); x++) {
            for (int y = 0; y < imgBtnUp.getHeight(); y++) {
                int rgb = (imgBtnUp.getRGB(x, y) & 0xff000000) | (clr.getRGB() & 0xffffff);
                imgUp.setRGB(x, y, rgb);
            }
        }
    }

    /**
     * Returns the position of the sorting direction image depending on the sorted column.
     *
     * @return image X position
     */
    private int getImageX() {
        if (fPanel != null) {
            switch (fPanel.getSortNumber()) {
                case 1: // sort by name
                    return nameWidth - 18;
                case 2: // sort by size
                    return nameWidth + sizeWidth - 18;
                case 3: // sort by date
                    return getWidth() - 18;
            }
        }
        return -30;
    }

    /**
     * Returns the desired image (up or down) depending on the sorting direction .
     *
     * @return image Up or image Down
     */
    private BufferedImage getImageShape() {
        if (fPanel != null && fPanel.getSortAscending()) {
            return imgUp;
        } else {
            return imgDown;
        }
    }
}
