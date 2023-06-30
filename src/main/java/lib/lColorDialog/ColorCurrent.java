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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static lib.lColorDialog.ColorPanel.current;
import static lib.lColorDialog.ColorPanel.previousColor;

/**
 * The panel of two squares with current and previous colors.
 */
public class ColorCurrent extends JComponent implements ColorListener {

    private final int wh = 45; // width and height;

    /**
     * Creates the panel with current and previous colors.
     */
    public ColorCurrent() {
        setSize(20 + wh * 2 + 3, 20 + wh);
        lnPrevColorMouseLayer mouseLayer = new lnPrevColorMouseLayer();
        this.add(mouseLayer);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(current.getColor());
        g.fillRect(10, 10, wh, wh);
        g.setColor(new Color(previousColor));
        g.fillRect(10 + wh + 3, 10, wh, wh);
    }

    @Override
    public void updateColor() {
        repaint();
    }

    /**
     * Handles a mouse click on the previous color box.
     */
    private class lnPrevColorMouseLayer extends JComponent {

        public lnPrevColorMouseLayer() {
            setBounds(10 + wh + 3, 10, wh, wh);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();                           // Grab active focus from any text fields.
                    EventQueue.invokeLater(() -> {                    // We have to wait until all text fields will give us their focus,
                        current.setRGB(this, previousColor);   // and then we'll update color and repaint all other components
                    });
                }
            }); // addMouseListener
        }
    } // class lnPrevColorMouseLayer

}  // class lnColorCurrent

