/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */

package lib.lColorDialog;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import static lib.lColorDialog.ColorPanel.currentColor;
import static lib.lColorDialog.ColorPanel.lnPrevColor;

public class ColorCurrent extends JComponent implements ColorListener {

    private final int wh = 45; // width and height;

    public ColorCurrent() {
        setSize(20 + wh * 2 + 3, 20 + wh);
        lnPrevColorMouseLayer mouseLayer = new lnPrevColorMouseLayer();
        this.add(mouseLayer);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(currentColor.getColor());
        g.fillRect(10, 10, wh, wh);
        g.setColor(new Color(lnPrevColor));
        g.fillRect(10 + wh + 3, 10, wh, wh);
    }

    @Override
    public void updateColor(int rgb) {
        repaint();
    }

    private class lnPrevColorMouseLayer extends JComponent {

        public lnPrevColorMouseLayer() {
            setBounds(10 + wh + 3, 10, wh, wh);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();                        // Grab active focus from any text feilds.
                    EventQueue.invokeLater(() -> {                 // We have to wait until all textfields will give us their focus,   
                        currentColor.setRGB(this, lnPrevColor);    // and then we'll update color and repaint all other components
                    });
                }
            }); // addMouseListener
        } 
    } // class lnPrevColorMouseLayer

}  // class lnColorCurrent

