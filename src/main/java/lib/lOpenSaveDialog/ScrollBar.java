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
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JComponent;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;

public class ScrollBar extends JComponent {

    private int scrollBarType = ScrollBar.VERTICAL;
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;

    private int scrollBarXSize = 100;
    private int scrollBarYSize = 10;
    private float viewScale;
    private float trackScale;
    private float trackBarStart;
    private float trackBarLength;
    private boolean trackDragged;
    private double dragStartPos;
    private boolean hovered = false;

    private int maxValue = 100;
    private int minValue = 0;
    private int incValue = 1;
    private int viewValue = 10;
    private int position = 0;

    private final Color clrHoveredTrackBar = new Color(0xb8cfe5);
    private final Color clrHoveredScrollBar = Color.GRAY;
    private final Color clrDefaultTrackBar = Color.GRAY;
    private final Color clrDefaultScrollBar = Color.DARK_GRAY;

    public ScrollBar(int sbType) {
        this();
        setScrollBarType(sbType);
    }

    public ScrollBar() {

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isActive()) {
                    if (getParent() != null) {
                        getParent().requestFocus();
                    }
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        int mousePos = (scrollBarType == VERTICAL) ? e.getY() : e.getX();
                        if (mousePos < trackBarStart) {
                            scrollPageUp();
                        } else if (mousePos > trackBarStart + trackBarLength) {
                            scrollPageDown();
                        }
                    }
                } else {
                    osPan.showFoldersPanel(false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isActive()) {
                    if (getParent() != null) {
                        getParent().requestFocus();
                    }
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        int mousePos = (scrollBarType == VERTICAL) ? e.getY() : e.getX();
                        if (mousePos >= trackBarStart
                                && mousePos <= trackBarStart + trackBarLength) {
                            trackDragged = true;
                            dragStartPos = mousePos - trackBarStart;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                trackDragged = false;
                if (e.getX() < 0 || e.getX() >= ScrollBar.this.getWidth()
                        || e.getY() < 0 || e.getY() >= ScrollBar.this.getHeight()) {
                    hovered = false;
                    repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isActive()) {
                    hovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!trackDragged) {
                    hovered = false;
                    repaint();
                }
            }
        }); // MouseListener

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (trackDragged) {
                    int mousePos = (scrollBarType == VERTICAL) ? e.getY() : e.getX();
                    setPosition((int) ((mousePos - dragStartPos) / trackScale));
                }
            }
        }); // MouseMotionListener
    }

    public void setScrollBarType(int sbType) {
        if (scrollBarType != sbType) {
            scrollBarType = sbType;
            repaint();
        }
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        if (scrollBarType == VERTICAL) {
            scrollBarXSize = h;
            scrollBarYSize = w;
        } else {
            scrollBarXSize = w;
            scrollBarYSize = h;
        }
        updateScale();
        if (isVisible()) {
            repaint();
        }
    }

    @Override
    public int getWidth() {
        if (isVisible()) {
            return super.getWidth();
        } else {
            return 0;
        }
    }

    @Override
    public int getHeight() {
        if (isVisible()) {
            return super.getHeight();
        } else {
            return 0;
        }
    }

    public void setValues(int min, int max, int incSize, int viewSize) {

        minValue = min;
        maxValue = max;
        incValue = incSize;
        viewValue = viewSize;

        updateScale();

        if (isVisible()) {
            repaint();
        }
    }

    private void updateScale() {
        viewScale = (float) viewValue / (maxValue - minValue);
        trackScale = (float) scrollBarXSize / (maxValue - minValue);

        if (viewScale >= 1.0f) {
            viewScale = 1.0f;
            trackBarLength = scrollBarXSize;
            trackBarStart = 0;
            setVisible(false);
        } else {
            trackBarLength = scrollBarXSize * viewScale;
            setVisible(true);
            setPosition(position);
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        if (pos < minValue) {
            position = minValue;
        } else if (isVisible() && pos >= maxValue - viewValue) {
            position = maxValue - viewValue;  
        } else {                              
            position = pos;                   
        }
        trackBarStart = trackScale * (position - minValue);
        if (isVisible()) {
            repaint();
        }
        onChangePosition();
    }

    public void onChangePosition() {}

    public void scrollToFirst() {
        setPosition(minValue);
    }

    public void scrollToLast() {
        setPosition(maxValue - (int) (trackBarLength / trackScale));
    }

    public void scrollPageUp() {
        setPosition(position - viewValue);
    }

    public void scrollPageDown() {
        setPosition(position + viewValue);
    }

    public boolean isAreaVisible(int minPos, int maxPos) {
        if (minPos > maxPos) {
            int temp = maxPos;
            maxPos = minPos;
            minPos = temp;
        }
        return !(minPos < position || maxPos > position + viewValue);
    }

    public void scrollToArea(int minPos, int maxPos) {
        if (minPos > maxPos) {
            int temp = maxPos;
            maxPos = minPos;
            minPos = temp;
        }
        if (minPos < position) {
            setPosition(minPos);
        } else if (maxPos > position + viewValue) {
            setPosition(maxPos - viewValue);
        }
    }

    public void scrollToComponent(Component c) {
        if (c != null) {
            if (scrollBarType == VERTICAL) {
                scrollToArea(c.getY(), c.getY() + c.getHeight());
            } else {
                scrollToArea(c.getX(), c.getX() + c.getWidth());
            }
        }
    }

    public boolean isActive() {
        return true;
    }
     
    @Override
    public void paintComponent(Graphics g) {
        int start = Math.round(trackBarStart);
        int end = Math.round(trackBarStart + trackBarLength);
        if (end == start) end++;
        if (scrollBarType == VERTICAL) {
            if (hovered) {
                g.setColor(clrHoveredScrollBar);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(clrHoveredTrackBar);
                g.fillRect(0, start, getWidth(), end - start);
            } else {
                int size = getWidth() / 2;
                g.setColor(clrDefaultScrollBar);
                g.fillRect(getWidth() - size, 0, size, getHeight());
                g.setColor(clrDefaultTrackBar);
                g.fillRect(getWidth() - size, start, size, end - start);
            }
        } else { // scrollBarType == HORIZONTAL
            if (hovered) {
                g.setColor(clrHoveredScrollBar);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(clrHoveredTrackBar);
                g.fillRect(start, 0, end - start, getHeight());
            } else {
                int size = getHeight() / 2;
                g.setColor(clrDefaultScrollBar);
                g.fillRect(0, getHeight() - size, getWidth(), size);
                g.setColor(clrDefaultTrackBar);
                g.fillRect(start, getHeight() - size, end - start, size);
            }
        }
    }
}
