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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class ScaleLayer extends JComponent implements Runnable {

    private Thread t;

    private BufferedImage imgOrig;
    private BufferedImage imgStretch;

    private int centerX;
    private int centerY;

    private BufferedImage imgFrame;

    private final long delay = 20;

    private double masterAlpha;
    private final double deltaAlpha = 0.06d;
    public boolean working = false;

    public ScaleLayer(BufferedImage bi) {
        setImage(bi);
    }

    public final void setImage(BufferedImage bi) {
        imgOrig = bi;
        if (bi != null) {
            setSize(bi.getWidth(), bi.getHeight());
        }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        centerX = width / 2;
        centerY = height / 2;

        if (width > imgOrig.getWidth()) {
            imgStretch = new BufferedImage(width, imgOrig.getHeight(), 2);
            stretchImage();
        } else {
            imgStretch = imgOrig;
        }
        if (imgFrame == null
                || (imgFrame.getWidth() != width)
                || (imgFrame.getHeight() != height)) {
            imgFrame = new BufferedImage(width, height, 2);
        }
    }

    public void clearFrame() {
        if (imgFrame != null) {
            Graphics2D g = (Graphics2D) imgFrame.getGraphics();
            g.setBackground(new Color(0, 0, 0, 0));
            g.clearRect(0, 0, imgFrame.getWidth(), imgFrame.getHeight());
        }
        repaint();
    }

    private void stretchImage() {
        Graphics2D g = (Graphics2D) imgStretch.getGraphics();
        int dw = getWidth() - imgOrig.getWidth();
        int cx = imgOrig.getWidth() / 2;

        g.setBackground(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, imgStretch.getWidth(), imgStretch.getHeight());

        g.drawImage(imgOrig, 0, 0, cx, imgStretch.getHeight(),
                0, 0, cx, imgOrig.getHeight(), null);
        g.drawImage(imgOrig, cx + dw, 0, imgStretch.getWidth(), imgStretch.getHeight(),
                cx, 0, imgOrig.getWidth(), imgOrig.getHeight(), null);

        for (int x = 0; x < dw; x++) {
            for (int y = 0; y < imgStretch.getHeight(); y++) {
                imgStretch.setRGB(cx + x, y, imgOrig.getRGB(cx, y));
            }
        }
    }

    public void start() {
        masterAlpha = 0.0d;
        clearFrame();
        working = true;
        t = new Thread(this);
        t.start();
    }

    public void stop() {
        working = false;
    }

    public void drawCurrentFrame() {
        int w = imgStretch.getWidth();
        int h = imgStretch.getHeight();
        int dw = (int) (w * masterAlpha);
        int dh = (int) (h * masterAlpha);

        int alpha, pix;

        int startX = centerX - dw / 2;
        if (startX < 0) {
            startX = 0;
        }
        int startY = centerY - dh / 2;
        if (startY < 0) {
            startY = 0;
        }

        for (int x = 0; x < dw; x++) {
            for (int y = 0; y < dh; y++) {
                double dx = (double) x * (w - 1) / (dw - 1);
                double dy = (double) y * (h - 1) / (dh - 1);
                pix = imgStretch.getRGB((int) dx, (int) dy);
                alpha = (int) (((pix >> 24) & 0xff) * masterAlpha);
                imgFrame.setRGB(x + startX, y + startY,
                        ((alpha & 0xff) << 24) | (pix & 0xffffff));
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(imgFrame, 0, 0, null);
    }

    @Override
    public void run() {
        while (working) {

            if (masterAlpha < 1) {
                masterAlpha += deltaAlpha;
                if (masterAlpha > 1) {
                    masterAlpha = 1.0d;
                    working = false;
                }
            }

            drawCurrentFrame();
            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
        onThreadFinished();
    }

    public void onThreadFinished() {

    }

}
