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
import ani.FlowerLayer;
import ani.WaveLayer;
import ani.ScaleLayer;
import dlg.StartDlg;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class CongratsPanel extends JComponent {

    private static final BufferedImage imgBadge
            = core.Options.createBufImage("imgBadge.png");
    private static final BufferedImage imgBadgeText
            = core.Options.createBufImage("imgBadge_text.png");

    private final FlowerLayer flower;
    private final ScaleLayer badge;
    private final WaveLayer text;

    public CongratsPanel() {
        setBackground(null);
        setForeground(null);

        text = new WaveLayer(imgBadgeText);
        add(text);

        badge = new ScaleLayer(imgBadge) {
            @Override
            public void onThreadFinished() {
                text.startUnlimited();
            }
        };
        add(badge);

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
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            updateSizeAndPos();
            badge.start();
            flower.start();
        } else {
            flower.stop();
            badge.stop();
            text.stop();
        }

        super.setVisible(aFlag);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        flower.setSize(width, height);
        flower.setRotationPoint(width / 2, height / 2);
        badge.setSize(width, height);
        text.setLocation((width - text.getWidth()) / 2,
                (height - text.getHeight()) / 2);
    }

    public void updateSizeAndPos() {
        updateSize();
        updatePos();
    }

    public void updateSize() {
        setSize(Math.max(imgBadge.getWidth(), MainFrame.tubesPan.getWidth()),
                Math.max(imgBadge.getHeight(), MainFrame.tubesPan.getHeight()));
    }

    public void updatePos() {
        Rectangle r = Main.frame.getTubesArea();
        setLocation(
                r.x + (r.width - getWidth()) / 2,
                r.y + (r.height - getHeight()) / 2);
    }

    public void doClick() {

        for (int i = 0; i < MainFrame.tubesPan.getTubesCount(); i++) {
            MainFrame.tubesPan.getTube(i).setActive(false);
            MainFrame.tubesPan.getTube(i).setClosed(true);
            MainFrame.tubesPan.getTube(i).setShade(4);
            MainFrame.tubesPan.getTube(i).showShade();
        }

        setVisible(false);
        StartDlg startFrame = new StartDlg(Main.frame);
        EventQueue.invokeLater(() -> startFrame.setVisible(true));

    }

}
