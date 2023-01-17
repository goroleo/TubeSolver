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

import run.Main;
import ani.ShadeLayer;
import ani.ShapeLayer;
import ani.ImageLayer;
import ani.SlideLayer;
import core.Options;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import lib.lColorDialog.LColorDialog;
import javax.swing.event.ChangeEvent;

public class ColorButton extends JButton {

///////////////////////////////////////////////////////////////////////////
//
//               * Fields / variables *
//
///////////////////////////////////////////////////////////////////////////

// Size values:

    /** Width of the component  */
    private int w;

    /** Height  of the component  */
    private int h;

// Layers images:

    /** An image for the Shade/shadow layer  */
    private static BufferedImage imgShade = null;

    /** An image for the color shape layer  */
    private static BufferedImage imgColorShape = null;

    /** An image for the bevel layer: enabled button state */
    private static BufferedImage imgBevelUp = null;

    /** An image for the bevel layer: Down/clicked button state */
    private static BufferedImage imgBevelDown = null;

    /** An image for the bevel layer: disabled button state */
    private static BufferedImage imgBevelDisabled = null;

    /** An image for the button frame: enabled */
    private static BufferedImage imgFrameEnabled = null;

    /** An image for the button frame: focused */
    private static BufferedImage imgFrameFocused = null;

    /** An image for the counter layer: 1 */
    private static BufferedImage imgCount1 = null;

    /** An image for the counter layer: 2 */
    private static BufferedImage imgCount2 = null;

    /** An image for the counter layer: 3 */
    private static BufferedImage imgCount3 = null;

    /** An image for the counter layer: 4 */
    private static BufferedImage imgCount4 = null;

// Buttons layers:

    /** An image for the counter layer: 4 */
    private final ShadeLayer shadeLayer;
    private final ShapeLayer colorLayer;
    private final ImageLayer bevelLayer;
    private final ImageLayer frameLayer;
    private final SlideLayer countLayer;

// Automation:
    private final int colorNumber;
    private int count;

    public ColorButton(int number) {

        colorNumber = number+1;

        setLayout(null);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFocusable(true);

        loadImages();

        countLayer = new SlideLayer(imgCount4);
        countLayer.setExpDegrees(1.0d, 0.5d);
        countLayer.setStartPosOfSecondImg(imgCount1.getWidth() / 3, 0);
        countLayer.setEndPosOfFirstImg(0, imgCount1.getHeight() / 2);
        this.add(countLayer);

        frameLayer = new ImageLayer(imgFrameEnabled);
        this.add(frameLayer);

        bevelLayer = new ImageLayer(imgBevelUp);
        this.add(bevelLayer);

        colorLayer = new ShapeLayer(imgColorShape);
        colorLayer.setColor(MainFrame.pal.getColor(colorNumber));
        this.add(colorLayer);

        shadeLayer = new ShadeLayer(imgShade);
        this.add(shadeLayer);

        setCount(0);
        setSize(w, h);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fireStateChanged();
            }
            @Override
            public void focusLost(FocusEvent e) {
                fireStateChanged();
            }
        }); // addFocusListener

        addChangeListener((ChangeEvent e) -> {
            if (isEnabled()) {
                if (getModel().isPressed()) {
                    bevelLayer.setImage(imgBevelDown);
                } else {
                    bevelLayer.setImage(imgBevelUp);
                }
                if (getModel().isRollover()) {
                    shadeLayer.doShow();
                } else {
                    shadeLayer.doHide();
                }
                if (hasFocus()) {
                    frameLayer.setImage(imgFrameFocused);
                } else {
                    frameLayer.setImage(imgFrameEnabled);
                }
            } else {
                frameLayer.setImage(imgFrameEnabled);
                bevelLayer.setImage(imgBevelDisabled);
                shadeLayer.doHide();
            }
        }); // addChangeListener
    }

    public void colorChange() {
        Color oldColor = MainFrame.pal.getColor(colorNumber);

        LColorDialog lcd = new LColorDialog(Main.frame, oldColor);
        lcd.setBackground(Palette.dialogColor);
        lcd.addColorListener(colorLayer::setColor);
        
        if (Options.ccdPositionX != -1 && Options.ccdPositionY != -1) {
            lcd.setLocation(Options.ccdPositionX, Options.ccdPositionY);
        }
        Color newColor = lcd.chooseColor();
        lcd.saveOptions();

        if (newColor != oldColor) {
            MainFrame.pal.set(colorNumber, newColor);
            MainFrame.pal.savePalette();
        }
        repaintColor();
    }

    public void repaintColor() {
        colorLayer.setColor(MainFrame.pal.getColor(colorNumber));
    }
    
    public void decCount() {
        setCount(count - 1);
    }

    public void incCount() {
        setCount(count + 1);
    }

    public int getCount() {
        return count;
    }

    public final void setCount(int newCount) {
        if ((count == newCount) || (newCount > 4)) {
            return;
        }
        if (newCount < 0) {
            countLayer.setSecondImage(null);
            setEnabled(false);
        } else {
            setEnabled(true);
            count = newCount;
            countLayer.setSecondImageToFirst();
            switch (count) {
                case 1:
                    countLayer.setSecondImage(imgCount1);
                    break;
                case 2:
                    countLayer.setSecondImage(imgCount2);
                    break;
                case 3:
                    countLayer.setSecondImage(imgCount3);
                    break;
                case 4:
                    countLayer.setSecondImage(imgCount4);
                    break;
                default:
                    countLayer.setSecondImage(null);
            }
        }
        countLayer.start();
    }

    public Color getColor() {
        return MainFrame.pal.get(colorNumber);
    }

    public int getColorNumber() {
        return colorNumber;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        countLayer.setBounds(0, 0, width, height);
        frameLayer.setBounds(0, 0, width, height);
        bevelLayer.setBounds(0, 0, width, height);
        colorLayer.setBounds(0, 0, width, height);
        shadeLayer.setBounds(0, 0, width, height);
        super.setBounds(x, y, width, height);
    }

    private void loadImages() {
        if (imgShade == null) {
            imgShade = core.Options.createBufImage("imgColor_shade_gray.png");
        }
        if (imgColorShape == null) {
            imgColorShape = core.Options.createBufImage("imgColor_color_shape.png");
        }
        if (imgBevelUp == null) {
            imgBevelUp = core.Options.createBufImage("imgColor_bevel_up.png");
        }
        if (imgBevelDown == null) {
            imgBevelDown = core.Options.createBufImage("imgColor_bevel_down.png");
        }
        if (imgBevelDisabled == null) {
            imgBevelDisabled = core.Options.createBufImage("imgColor_bevel_disabled.png");
        }
        if (imgFrameEnabled == null) {
            imgFrameEnabled = core.Options.createBufImage("imgColor_frame.png");
        }
        if (imgFrameFocused == null) {
            imgFrameFocused = core.Options.createBufImage("imgColor_frame_focused.png");
        }
        if (imgCount1 == null) {
            imgCount1 = core.Options.createBufImage("imgColor_count_1.png");
        }
        if (imgCount2 == null) {
            imgCount2 = core.Options.createBufImage("imgColor_count_2.png");
        }
        if (imgCount3 == null) {
            imgCount3 = core.Options.createBufImage("imgColor_count_3.png");
        }
        if (imgCount4 == null) {
            imgCount4 = core.Options.createBufImage("imgColor_count_4.png");
        }
        w = imgFrameEnabled.getWidth();
        h = imgFrameEnabled.getHeight();
    }

}
