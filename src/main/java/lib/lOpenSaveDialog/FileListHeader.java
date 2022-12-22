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
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JLabel;
import core.ResStrings;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.imgBtnDown;
import static lib.lOpenSaveDialog.OpenSavePanel.imgBtnUp;

public class FileListHeader extends JComponent {

    private final JLabel nameLabel;
    private final JLabel sizeLabel;
    private final JLabel dateLabel;
    private final JComponent linesLayer;

    private int nameWidth;
    private int sizeWidth = 75;
    private int dateWidth = 110;
    private int headerWidth = 200;

    private boolean dragged = false;
    private int sepNumber = 0;
    private int draggedPos = 0;

    private BufferedImage upImg;
    private BufferedImage downImg;

    private final FilesPanel fPanel;

    /**
     *
     */
    public FileListHeader() {
        this(null);
    }

    public FileListHeader(FilesPanel owner) {

        fPanel = owner;

        setBackground(null);
        setForeground(null);
        prepareImages(new Color(0xb8cfe5));

        linesLayer = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(Color.GRAY);
                g.drawLine(nameWidth, 3, nameWidth, getHeight() - 4);
                g.drawLine(nameWidth + sizeWidth, 3, nameWidth + sizeWidth, getHeight() - 4);
                g.drawImage(getImageShape(), getImageX(), 1, null);
            }
        };

        add(linesLayer);

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

        setSize(300, 24);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isEnabled()) {
                    if (getLabelAtMousePos(e.getX()) < 10) {
                        setCursor(Cursor.getDefaultCursor());
                    } else {
                        setCursor(osPan.cursorResize);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isEnabled()) {
                    if (dragged) {
                        
                        if (sepNumber == 11) { // separator between Name and Size
                            int w1 = e.getX() - draggedPos;
                            if (w1 < 50) {
                                w1 = 50;
                            }
                            int w2 = headerWidth - dateWidth - w1;
                            if (w2 < 50) {
                                w2 = 50;
                            }
                            setWidths(w2, dateWidth);
                            
                        } else if (sepNumber == 12) { // separator between Size and Date
                            int w1 = e.getX() - draggedPos - nameWidth;
                            if (w1 < 50) {
                                w1 = 50;
                            }
                            int w2 = headerWidth - nameWidth - w1;
                            if (w2 < 50) {
                                w2 = 50;
                                w1 = headerWidth - nameWidth - w2;
                            }
                            setWidths(w1, w2);
                        }
                    }
                }
            }

        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (getParent() != null) {
                    getParent().requestFocus();
                }

                if (e.getButton() == MouseEvent.BUTTON1) {
                    int labelNum = getLabelAtMousePos(e.getX());

                    if (e.getClickCount() == 2) {
                        switch (getLabelAtMousePos(e.getX())) {
                            case 1:
                                setWidths(75, 110);
                                break;
                            case 2:
                                setSizeWidth(75);
                                break;
                            case 3:
                                setDateWidth(110);
                                break;
                            default:
                                break;
                        }
                    } else if (e.getClickCount() == 1) {
                        if (fPanel != null && labelNum < 10) {
                            fPanel.sortFileList(labelNum);
                            linesLayer.repaint();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    if (getParent() != null) {
                        getParent().requestFocus();
                    }

                    if (e.getButton() == MouseEvent.BUTTON1) {
                        int mousePos = e.getX();
                        sepNumber = getLabelAtMousePos(mousePos);

                        if (sepNumber == 11) {
                            dragged = true;
                            draggedPos = mousePos - nameWidth;
                        } else if (sepNumber == 12) {
                            dragged = true;
                            draggedPos = mousePos - (nameWidth + sizeWidth);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragged = false;
            }
        });
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        linesLayer.setSize(w, h);
        updateWidth();
    }

    public void updateWidth() {
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
        linesLayer.repaint();
        if (fPanel != null) {
            fPanel.updateColumnWidths(nameWidth, sizeWidth, dateWidth);
        }
    }

    public void setWidths(int name, int size, int date) {
        dateWidth = date;
        sizeWidth = size;
        setSize(name + dateWidth + sizeWidth, getHeight());
    }

    public void setWidths(int size, int date) {
        dateWidth = date;
        sizeWidth = size;
        updateWidth();
    }

    public void setNameWidth(int width) {
        setSize(width + dateWidth + sizeWidth, getHeight());
        headerWidth = nameWidth + dateWidth + sizeWidth;
    }

    public int getNameWidth() {
        return nameWidth;
    }

    public void setDateWidth(int width) {
        dateWidth = width;
        updateWidth();
    }

    public int getDateWidth() {
        return dateWidth;
    }

    public void setSizeWidth(int width) {
        sizeWidth = width;
        updateWidth();
    }

    public int getSizeWidth() {
        return sizeWidth;
    }

    public void updateCursor(int newCursor) {
        setCursor(Cursor.getPredefinedCursor(newCursor));
    }

    public int getLabelAtMousePos(int pos) {
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

    private void prepareImages(Color clr) {
        downImg = new BufferedImage(imgBtnDown.getWidth(), imgBtnDown.getHeight(), 2);
        for (int x = 0; x < imgBtnDown.getWidth(); x++) {
            for (int y = 0; y < imgBtnDown.getHeight(); y++) {
                int rgb = (imgBtnDown.getRGB(x, y) & 0xFF000000) | (clr.getRGB() & 0xFFFFFF);
                downImg.setRGB(x, y, rgb);
            }
        }

        upImg = new BufferedImage(imgBtnUp.getWidth(), imgBtnUp.getHeight(), 2);
        for (int x = 0; x < imgBtnUp.getWidth(); x++) {
            for (int y = 0; y < imgBtnUp.getHeight(); y++) {
                int rgb = (imgBtnUp.getRGB(x, y) & 0xFF000000) | (clr.getRGB() & 0xFFFFFF);
                upImg.setRGB(x, y, rgb);
            }
        }
    }

    private int getImageX() {
        if (fPanel != null) {
            switch (fPanel.getSortNumber()) {
                case 1: // name 
                    return nameWidth - 18;
                case 2: // name 
                    return nameWidth + sizeWidth - 18;
                case 3: // name 
                    return getWidth() - 18;
            }
        }
        return -30;
    }

    private BufferedImage getImageShape() {
        if (fPanel != null && fPanel.getSortAscending()) {
            return upImg;
        } else {
            return downImg;
        }
    }
}
