/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package ani;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ShadeLayer extends JComponent implements Runnable {

    private final Double alphaIncUp = 0.06d;    // Alpha-channel change value when the picture appear
    private final Double alphaIncDown = -0.1d;  // Alpha-channel change value when the picture disappear
    private final int delayStd = 30;         // delay between every frame (milliseconds)
    private final int delayAtUp = 100;       // delay when the picture has fully appeared
    private final int delayAtDown = 0;       // delay when the picture disappear

    private BufferedImage imgOrig;           // original picture
    private BufferedImage imgFrame;          // image for every single frame
    private int w;                           // width 
    private int h;                           // height

    private boolean working = false;         // if true, the cycle is working, we don't need to restart it
    private boolean limited = false;         // if true, the cycle will stop when the picture appears or disappears 
    private double alphaLimit;               // Alpha-channel value for cycle stop

    private Double alpha;                    // current alpha value
    private Double alphaInc;                 // current alpha increment

    public Boolean useAnimation = true;      // 

    public ShadeLayer(BufferedImage bi) {
        super();
        restoreAlpha();
        setImage(bi);
    }

    public void setImage(BufferedImage bi) {
        imgOrig = bi;
        if (imgFrame == null || w != bi.getWidth() || h != bi.getHeight()) {
            w = bi.getWidth();
            h = bi.getHeight();
            imgFrame = new BufferedImage(w, h, 2);
            setBounds(0, 0, w, h);
        }
        calculateCurrentFrame();
    }

    public void start() {
        if (!working) {
            working = true;
            Thread t = new Thread(this);
            t.start();
        }
    }

    public void startUnlimited() {
        if (useAnimation) {
            limited = false;
            start();
        }
    }

    public void doShow() {
        limited = true;
        alphaLimit = 1.0d;
        alphaInc = alphaIncUp;

        if (useAnimation) {
            start();
        } else {
            alpha = alphaLimit;
            calculateCurrentFrame();
            repaint();
        }
    }

    public void doHide() {
        limited = true;
        alphaLimit = 0.0d;
        alphaInc = alphaIncDown;

        if (useAnimation) {
            start();
        } else {
            alpha = alphaLimit;
            calculateCurrentFrame();
            repaint();
        }
    }

    public final void restoreAlpha() {
        alpha = 0.0d;
        alphaInc = alphaIncUp;
    }

    public void calculateCurrentFrame() {
        int pix, newAlpha;
        for (int x = 0; x < imgOrig.getWidth(); x++) {
            for (int y = 0; y < imgOrig.getHeight(); y++) {
                pix = imgOrig.getRGB(x, y);
                newAlpha = (int) Math.round(((pix >> 24) & 0xff) * alpha);
                pix = (pix & 0xffffff) | ((newAlpha & 0xff) << 24);
                imgFrame.setRGB(x, y, pix);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(imgFrame, 0, 0, null);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (!(limited && alpha == alphaLimit)) {

            int delay = delayStd;
            alpha += alphaInc;

            if (alpha > 1.0d) {
                alpha = 1.0d;
                alphaInc = alphaIncDown;
                delay = limited ? 0 : delayAtUp;
            } else if (alpha < 0.0d) {
                alpha = 0.0d;
                alphaInc = alphaIncUp;
                delay = limited ? 0 : delayAtDown;
            }

            calculateCurrentFrame();
            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
        working = false; // thread is done 
        onThreadFinished();
    }

    @SuppressWarnings("EmptyMethod")
    public void onThreadFinished() {
    }
}
