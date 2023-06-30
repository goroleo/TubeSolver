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

import ani.FlowerLayer;
import ani.ScaleLayer;
import ani.WaveLayer;
import dlg.StartDlg;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * The top panel that shows congratulations: "you win!".
 * It consists of three layers: a rotating flower, an award ribbon and a congratulatory text.
 */
public class CongratsPanel extends JComponent {

    /**
     * The image of the award ribbon.
     */
    private static final BufferedImage imgRibbon
            = core.Options.createBufImage("imgBadge_ribbon.png");

    /**
     * The image of the congratulatory text.
     */
    private static final BufferedImage imgText
            = core.Options.createBufImage("imgBadge_text.png");

    /**
     * The rotating flower layer.
     */
    private final FlowerLayer flower;

    /**
     * The award ribbon layer.
     */
    private final ScaleLayer ribbon;

    /**
     * The congratulatory text layer.
     */
    private final WaveLayer text;

    /**
     * Constructor. It creates the panel and adds layers.
     */
    public CongratsPanel() {
        setBackground(null);
        setForeground(null);

        text = new WaveLayer(imgText);
        add(text);

        ribbon = new ScaleLayer(imgRibbon) {
            @Override
            public void onThreadFinished() {
                text.startUnlimited();
            }
        };
        add(ribbon);

        flower = new FlowerLayer();
        add(flower);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doClick();
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            updateSizeAndPos();
            ribbon.start();
            flower.start();
        } else {
            flower.stop();
            ribbon.stop();
            text.stop();
        }
        super.setVisible(b);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        flower.setSize(width, height);
        flower.setRotationPoint(width / 2, height / 2);
        ribbon.setSize(width, height);
        text.setLocation((width - text.getWidth()) / 2,
                (height - text.getHeight()) / 2);
    }

    /**
     * Sets new size and position in depends on the main frame size.
     */
    public void updateSizeAndPos() {

        Rectangle r = Main.frame.getTubesArea();
        int w = Math.max(imgRibbon.getWidth(), MainFrame.tubesPan.getWidth());
        int h = Math.max(imgRibbon.getHeight(), MainFrame.tubesPan.getHeight());

        setBounds(r.x + (r.width - w) / 2, r.y + (r.height - h) / 2, w, h);
    }

    /**
     * Handles the mouse click above the panel.
     */
    public void doClick() {

        for (int i = 0; i < MainFrame.tubesPan.getTubesCount(); i++) {
            MainFrame.tubesPan.getTube(i).setClosed(true);
            MainFrame.tubesPan.getTube(i).setFrame(4);
            MainFrame.tubesPan.getTube(i).showFrame();
        }

        setVisible(false);
        StartDlg startFrame = new StartDlg(Main.frame);
        EventQueue.invokeLater(() -> startFrame.setVisible(true));
    }
}
