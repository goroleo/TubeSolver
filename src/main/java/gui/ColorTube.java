/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package gui;

import ani.*;
import core.TubeModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Color tube is a GUI component.
 */
public class ColorTube extends JComponent {

    // Size:
    private int w;
    private int h;
    // Images:
    private static BufferedImage imgBottle = null;
    private static BufferedImage imgCork = null;
    private static BufferedImage imgShadeGreen = null;
    private static BufferedImage imgShadeRed = null;
    private static BufferedImage imgShadeBlue = null;
    private static BufferedImage imgShadeYellow = null;
    private static BufferedImage imgShadeGray = null;
    private static BufferedImage imgArrowYellow = null;
    private static BufferedImage imgArrowGreen = null;
    // Layers:
    private final ColorLayer ColorLayer;
    private final ShadeLayer hover;
    private final ShadeLayer shade;
    private final ImageLayer bottle;
    private final SlideLayer cork;
    private final WaveLayer arrow;
    // shades:
    private int shadeNum = 0;
    public final static int SHADE_NO_COLOR = 0;
    public final static int SHADE_RED = 1;
    public final static int SHADE_GREEN = 2;
    public final static int SHADE_YELLOW = 3;
    public final static int SHADE_BLUE = 4;

    // arrows:
    private int arrowNum = 0;
    public final static int ARROW_NO_COLOR = 0;
    public final static int ARROW_GREEN = 1;
    public final static int ARROW_YELLOW = 2;

    // TUBE MODEL
    private final TubeModel model = new TubeModel();

    private boolean active = true;
    private boolean closed = false;

    public ColorTube() {
        setLayout(null);
        loadImages();

        arrow = new WaveLayer(imgArrowGreen);
        this.add(arrow);

        cork = new SlideLayer(null);
        cork.setExpDegrees(-0.5d, 0.5d);
        this.add(cork);

        shade = new ShadeLayer(imgShadeGray);
        this.add(shade);

        bottle = new ImageLayer(imgBottle);
        this.add(bottle);

        hover = new ShadeLayer(imgShadeGray);
        this.add(hover);

        ColorLayer = new ColorLayer(w, h - 20);
        this.add(ColorLayer);
        ColorLayer.useAnimation = true;

        setSize(w, h);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (active && e.getButton() == 1) {
                    doClick();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (active) {
                    hover.doShow();
                    if (canShowArrow()) {
                        arrow.startUnlimited();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover.doHide();
                if (canHideArrow()) {
                    arrow.stop();
                }
            }

        });
    }

    public TubeModel getModel() {
        return model;
    }

    public int getColorsCount() {
        return model.count;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        if (!b) {
            arrow.stop();
            hover.doHide();
        }
        active = b;
    }

    public boolean isClosed() {
        return model.isClosed();
    }

    public void setClosed(boolean b) {
        if (b != closed) {
            closed = b;
            cork.setSecondImageToFirst();
            if (closed) {
                cork.setSecondImage(imgCork);
                cork.setStartPosOfSecondImg(0, -imgCork.getHeight() * 2);
                cork.setEndPosOfFirstImg(0, 0);
                if (MainFrame.gameMode == MainFrame.FILL_MODE) {
                    this.setShade(1);
                } else {
                    this.setShade(4);
                }
                showShade();
            } else {
                cork.setSecondImage(null);
                cork.setStartPosOfSecondImg(0, 0);
                cork.setEndPosOfFirstImg(0, -imgCork.getHeight() / 2);
                setShade(0);
            }
            cork.start();
        }
    }


    public boolean isEmpty() {
        return model.isEmpty();
    }

    public void updateState() {
        model.updateState();
        setClosed(model.isClosed());
    }

    public int getShade() {
        return shadeNum;
    }

    public void hideShade() {
        shade.doHide();
    }

    public void showShade() {
        shade.useAnimation = true;
        shade.doShow();
    }

    public void pulseShade() {
        shade.useAnimation = true;
        shade.startUnlimited();
    }

    public void setShade(int newShadeNum) {
        if (shadeNum != newShadeNum) {
            switch (newShadeNum) {
                case SHADE_RED:
                    shade.setImage(imgShadeRed);
                    break;
                case SHADE_GREEN:
                    shade.setImage(imgShadeGreen);
                    break;
                case SHADE_YELLOW:
                    shade.setImage(imgShadeYellow);
                    break;
                case SHADE_BLUE:
                    shade.setImage(imgShadeBlue);
                    break;
                default: // SHADE_NO_COLOR
                    shade.doHide();
                    break;
            }
            shadeNum = newShadeNum;
        }
    }

    public int getArrowNum() {
        return arrowNum;
    }

    public void hideArrow() {
        arrow.stop();
    }

    public void showArrow() {
        if (arrowNum != 0) {
            arrow.startUnlimited();
        }
    }

    public void setArrow(int newArrowNum) {
        switch (newArrowNum) {
            case ARROW_GREEN:
                arrow.setImage(imgArrowGreen);
                break;
            case ARROW_YELLOW:
                arrow.setImage(imgArrowYellow);
                break;
            default: // SHADE_NO_COLOR
                break;
        }
        arrowNum = newArrowNum;
    }

    public void setArrowWhenHide(int newArrowNum) {
        if (arrowNum != newArrowNum) {
            arrow.stop();
            switch (newArrowNum) {
                case ARROW_GREEN:
                    arrow.setImageWhenHide(imgArrowGreen);
                    break;
                case ARROW_YELLOW:
                    arrow.setImageWhenHide(imgArrowYellow);
                    break;
                default: // SHADE_NO_COLOR
                    break;
            }
            arrowNum = newArrowNum;
        }
    }

    public boolean canPutColor(int colorNum) {
        switch (MainFrame.gameMode) {
            case 100: // FILL_MODE
                return active && getColorsCount() < 4;
            case 200: // GAME MODE
                return model.canPutColor((byte) colorNum);
            default:
                return false;
        }
    }

    public int getColor(int number) {
        if (number >= 0 && number < 4) {
            return model.colors[number];
        } else {
            return 0;
        }
    }

    public byte getCurrentColor() {
        return model.currentColor;
    }

    public void putColor(int colorNum) {
        if (model.putColor((byte) colorNum)) {
            ColorLayer.addColor(colorNum);
        }
    }

    public void extractColor() {
        if (model.extractColor() != 0) {
            ColorLayer.removeColor();
        }
    }

    public void extractColors() {
        for (int i = 0; i < model.colorsToGet(); i++) {
            extractColor();
        }
    }

    public void repaintColors() {
        for (int i = 0; i < 4; i++) {
            if (i < model.count) {
                ColorLayer.repaintColor(i + 1, getColor(i), false);
            } else {
                ColorLayer.repaintColor(i + 1, 0, false);
            }
        }
        ColorLayer.repaint();
    }

    public void restoreColors(int StoredColors) {
        for (int i = 0; i < 4; i++) {
            putColor((byte) StoredColors);
            StoredColors = StoredColors >> 8;
        }
        updateState();
    }

    public boolean hasColor(int colorNum) {
        return model.hasColor((byte) colorNum);
    }

    public void setColorsAnimation(boolean b) {
        ColorLayer.useAnimation = b;
    }

    public boolean getColorsAnimation() {
        return ColorLayer.useAnimation;
    }

    public void clear() {
        boolean oldUseAnimation = ColorLayer.useAnimation;
        ColorLayer.useAnimation = false;
        model.clear();
        ColorLayer.clearColors();
        ColorLayer.useAnimation = oldUseAnimation;
        updateState();
    }

    public void doClick() {
        // method to override
    }

    public boolean canShowArrow() {
        // method to override    
        return true;
    }

    public boolean canHideArrow() {
        // method to override    
        return true;
    }

    private void loadImages() {
        if (imgBottle == null) {
            imgBottle = core.Options.createBufImage("imgTube_bottle.png");
        }
        if (imgCork == null) {
            imgCork = core.Options.createBufImage("imgTube_cork.png");
        }
        if (imgShadeGreen == null) {
            imgShadeGreen = core.Options.createBufImage("imgTube_shade_green.png");
        }
        if (imgShadeYellow == null) {
            imgShadeYellow = core.Options.createBufImage("imgTube_shade_yellow.png");
        }
        if (imgShadeBlue == null) {
            imgShadeBlue = core.Options.createBufImage("imgTube_shade_blue.png");
        }
        if (imgShadeRed == null) {
            imgShadeRed = core.Options.createBufImage("imgTube_shade_red.png");
        }
        if (imgShadeGray == null) {
            imgShadeGray = core.Options.createBufImage("imgTube_shade_gray.png");
        }
        if (imgArrowYellow == null) {
            imgArrowYellow = core.Options.createBufImage("imgTube_arrow_in_yellow.png");
        }
        if (imgArrowGreen == null) {
            imgArrowGreen = core.Options.createBufImage("imgTube_arrow_out_green.png");
        }

        w = imgBottle.getWidth();
        h = imgBottle.getHeight() + 20;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        ColorLayer.setBounds(0, 20, width, height - 20);
        hover.setBounds(0, 20, width, height - 20);
        shade.setBounds(0, 20, width, height - 20);
        bottle.setBounds(0, 20, width, height - 20);
        cork.setBounds(0, 10, imgCork.getWidth(), imgCork.getHeight());
        arrow.setBounds(0, 0, width, height - 20);
        super.setBounds(x, y, width, height);
    }

}
