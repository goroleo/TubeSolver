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
import java.awt.event.MouseMotionAdapter;

/**
 * A scroll bar component with my exterior.
 */
public class ScrollBar extends JComponent {

    /**
     * Type of the ScrollBar. Can be VERTICAL or HORIZONTAL.
     */
    private int scrollBarType;

    /**
     * A constant for the Vertical type of the ScrollBar.
     */
    public static final int VERTICAL = 1;

    /**
     * A constant for the Horizontal type of the ScrollBar.
     */
    public static final int HORIZONTAL = 2;

    /**
     * Scale of the track bar.
     */
    private float trackScale;

    /**
     * The start point of the track bar based on the current position of the ScrollBar.
     */
    private float trackBarStart;

    /**
     * The length of the track bar based on the current scale.
     */
    private float trackBarLength;

    /**
     * This will be <i>true</i> if the track bar is being dragged with the mouse.
     */
    private boolean trackDragged;

    /**
     * The starting position of the track bar when it begins to be dragged.
     */
    private double dragStartPos;

    /**
     * Indicates whether the mouse cursor is over the ScrollBar.
     */
    private boolean mouseOver = false;

    /**
     * Maximal value of the ScrollBar.
     */
    private int maxValue = 100;

    /**
     * Minimal value of the ScrollBar.
     */
    private int minValue = 0;

    /**
     * The single-screen (or single-page) value. The size of the "viewable" area.
     */
    private int viewValue = 10;

    /**
     * The current position of the ScrollBar.
     */
    private int position = 0;

    /**
     * The color of the TrackBar when the mouse is over the component.
     */
    private final Color clrMouseOverTrackBar = new Color(0xb8cfe5);

    /**
     * The color of the ScrollBar when the mouse is over the component.
     */
    private final Color clrMouseOverScrollBar = Color.GRAY;

    /**
     * Default TrackBar color.
     */
    private final Color clrDefaultTrackBar = Color.GRAY;

    /**
     * Default ScrollBar color.
     */
    private final Color clrDefaultScrollBar = Color.DARK_GRAY;

    /**
     * Creates the ScrollBar with the VERTICAL type as default.
     */
    public ScrollBar() {
        this(VERTICAL);
    }

    /**
     * Creates the ScrollBar.
     *
     * @param sbType the type of the ScrollBar: VERTICAL or HORIZONTAL.
     */
    public ScrollBar(int sbType) {
        setScrollBarType(sbType);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int mousePos = (scrollBarType == VERTICAL) ? e.getY() : e.getX();
                    ScrollBar.this.mouseClicked(mousePos);
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
                    mouseOver = false;
                    repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isActive()) {
                    mouseOver = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!trackDragged) {
                    mouseOver = false;
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

            @Override
            public void mouseMoved(MouseEvent e) {
                if (isActive()) {
                    mouseOver = true;
                    repaint();
                }
            }
        }); // MouseMotionListener
    }

    /**
     * Sets the type of the scrollbar: vertical or horizontal.
     *
     * @param sbType new type of the scrollbar.
     */
    public void setScrollBarType(int sbType) {
        sbType = (sbType == HORIZONTAL) ? HORIZONTAL : VERTICAL;
        if (scrollBarType != sbType) {
            scrollBarType = sbType;
            repaint();
        }
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
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

    /**
     * Sets three properties of the scroll bar values.
     *
     * @param min  minimal value.
     * @param max  maximal value.
     * @param view size of the "viewable" area.
     */
    public void setValues(int min, int max, int view) {

        minValue = min;
        maxValue = max;
        viewValue = view;

        updateScale();

        if (isVisible()) {
            repaint();
        }
    }

    /**
     * Updates scrollbar scales, trackbar length and other values.
     */
    private void updateScale() {
        int length = (scrollBarType == VERTICAL) ? getHeight() : getWidth();
        float viewScale = (float) viewValue / (maxValue - minValue);
        trackScale = (float) length / (maxValue - minValue);

        if (viewScale >= 1.0f) {
            trackBarLength = length;
            trackBarStart = 0;
            setVisible(false);
            setPosition(0);
        } else {
            trackBarLength = length * viewScale;
            setVisible(true);
            setPosition(position);
        }
    }

    /**
     * Gets the current scrollbar position.
     *
     * @return the position.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the current scrollbar position.
     *
     * @param pos new position of the scrollbar.
     */
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

    /**
     * Catches position change events. Used to override and to respond to position changes.
     */
    public void onChangePosition() {
    }

    /* unused
    /**
     * Scrolls to the very first position.
    public void scrollToFirst() {
        setPosition(minValue);
    }
     */

    /* unused
    /**
     * Scrolls to the very last position.
    public void scrollToLast() {
        setPosition(maxValue - (int) (trackBarLength / trackScale));
    }
     */

    /**
     * Scrolls to the previous page/view.
     */
    public void scrollPageUp() {
        setPosition(position - viewValue);
    }

    /**
     * Scrolls to the next page/view.
     */
    public void scrollPageDown() {
        setPosition(position + viewValue);
    }

    /* unused
    /**
     * Checks if the specific area is visible.
     *
     * @param minPos start position of the area.
     * @param maxPos end position of the area.
     * @return true if the area is visible, false otherwise.
    public boolean isAreaVisible(int minPos, int maxPos) {
        if (minPos > maxPos) {
            int temp = maxPos;
            maxPos = minPos;
            minPos = temp;
        }
        return !(minPos < position || maxPos > position + viewValue);
    }
     */

    /**
     * Scrolls so that a specific area is visible.
     *
     * @param minPos start position of the area.
     * @param maxPos end position of the area.
     */
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

    /**
     * Scrolls so that a specific component is visible.
     *
     * @param c a component to be visible.
     */
    public void scrollToComponent(Component c) {
        if (c != null) {
            if (scrollBarType == VERTICAL) {
                scrollToArea(c.getY(), c.getY() + c.getHeight());
            } else {
                scrollToArea(c.getX(), c.getX() + c.getWidth());
            }
        }
    }

    /**
     * Returns the current state of the ScrollBar activity. You can override it to
     * add more reasons to be inactive.
     *
     * @return true if the scrollbar is active, false otherwise.
     */
    public boolean isActive() {
        return true;
    }

    /**
     * Catches the mouse click event.
     *
     * @param mousePos mouse cursor position
     */
    public void mouseClicked(int mousePos) {
        if (getParent() != null) {
            getParent().requestFocus();
        }
        if (mousePos < trackBarStart) {
            scrollPageUp();
        } else if (mousePos > trackBarStart + trackBarLength) {
            scrollPageDown();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        int start = Math.round(trackBarStart);
        int end = Math.round(trackBarStart + trackBarLength);
        if (end == start) end++;
        if (scrollBarType == VERTICAL) {
            if (mouseOver) {
                g.setColor(clrMouseOverScrollBar);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(clrMouseOverTrackBar);
                g.fillRect(0, start, getWidth(), end - start);
            } else {
                int size = getWidth() / 2;
                g.setColor(clrDefaultScrollBar);
                g.fillRect(getWidth() - size, 0, size, getHeight());
                g.setColor(clrDefaultTrackBar);
                g.fillRect(getWidth() - size, start, size, end - start);
            }
        } else { // scrollBarType == HORIZONTAL
            if (mouseOver) {
                g.setColor(clrMouseOverScrollBar);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(clrMouseOverTrackBar);
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
