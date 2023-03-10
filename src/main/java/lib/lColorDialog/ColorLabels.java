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

import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import static lib.lColorDialog.ColorPanel.current;
import static lib.lColorDialog.LColorDialog.cPanel;

import lib.lTextFields.LDecTextField;
import lib.lTextFields.LHexTextField;

/**
 * The panel with text fields that display the current values
 * of the color components. These values can be edited.
 */
public class ColorLabels extends JComponent {

    /**
     * Uses to group all radio buttons at form
     */
    private final ButtonGroup bg = new ButtonGroup();

    /**
     * Radio buttons for color components: Hue, Saturation, Brightness. Click on
     * these radioButtons will change mode of dialog and redraw colorBox and
     * colorLine.
     */
    private final JRadioButton lnRadioHue, lnRadioSat, lnRadioBri;

    /**
     * Text fields for color components: Hue, Saturation, Brightness.<br>
     * The field is listen the change of current color and indicates its
     * component's value. Also, the field allows to change component's value
     * directly. In this case the field initiates color change.
     */
    private final LTextField lnTextHue, lnTextSat, lnTextBri;

    /**
     * Radio buttons for color components: Red, Green, Blue. Click on these
     * radioButtons will change mode of dialog and redraw colorBox and
     * colorLine.
     */
    private final JRadioButton lnRadioR, lnRadioG, lnRadioB;

    /**
     * Text fields for color components: Red, Green, Blue.<br>
     * The field is listen the change of current color and indicates its
     * component's value. Also, the field allows to change component's value
     * directly. In this case the field initiates color change.
     */
    private final LTextField lnTextR, lnTextG, lnTextB;

    /*
     * Text fields for hex color's value (0xRRGGBB) <br>
     * The field are listen the change of current color and allows to change
     * color value directly.
     */
    //  private final LHexField lnHex; // used one time only. deprecated variable


    /**
     *  Creates the panel with text fields that display the current values
     *  of the color components.
     */
    public ColorLabels() {

        // Initialize HSB color components
        // Hue: radio, text, dimension
        lnRadioHue = addRadioButton("H", 0);    // params: Text and Y-position
        lnTextHue = addTextField(360, 0);  // params: MaxValue and Y-position
        addLabel("°", 103, 0, 2);    // params: Text, X-pos, Y-pos, Alignment mode constant
        // Saturation: radio, text, dimension
        lnRadioSat = addRadioButton("S", 26);   // params: Text and Y-position
        lnTextSat = addTextField(100, 26); // params: MaxValue and Y-position
        addLabel("%", 103, 26, 2);   // params: Text, X-pos, Y-pos, Alignment mode constant
        // Brightness: radio, text, dimension
        lnRadioBri = addRadioButton("B", 52);   // ... and so on
        lnTextBri = addTextField(100, 52);
        addLabel("%", 103, 52, 2);

        // Initialize RGB color components
        // Red: radio, text
        lnRadioR = addRadioButton("R", 85);
        lnTextR = addTextField(255, 85);
        // Green: radio, text
        lnRadioG = addRadioButton("G", 111);
        lnTextG = addTextField(255, 111);
        // Blue: radio, text
        lnRadioB = addRadioButton("B", 137);
        lnTextB = addTextField(255, 137);

        // Color in HEX format: label, text
        addLabel("#", 5, 170, 4);    // params: Text, X-pos, Y-pos, Alignment mode constant
        addHexField(30, 170);                    // params: X-position, Y-position

        setForeground(null);
        setBounds(0, 0, 118, 192);
    }

    /**
     * Updates the panel in depends on the ColorScheme.
     * @see ColorPanel#getColorScheme()
     */
    public void updateColorScheme() {
            if (cPanel.getColorScheme() == 0) {
                lnRadioBri.setText("B");
            } else {
                lnRadioBri.setText("L");
            }
            lnTextHue.updateColor();
            lnTextSat.updateColor();
            lnTextBri.updateColor();
    }

    /**
     * Updates the panel in depends on the DialogMode.
     * @see ColorPanel#getDialogMode()
     */
    public void updateDialogMode() {
        switch (cPanel.getDialogMode()) {
            case 0: // hue
                lnRadioHue.setSelected(true);
                break;
            case 1:
                lnRadioSat.setSelected(true);
                break;
            case 2:
                lnRadioBri.setSelected(true);
                break;
            case 3:
                lnRadioR.setSelected(true);
                break;
            case 4:
                lnRadioG.setSelected(true);
                break;
            case 5:
                lnRadioB.setSelected(true);
        }
    }

    /**
     * Adds the RadioButton of the color components
     * @param txt button caption / text
     * @param y the Y position of the Button
     * @return radio button instance
     */
    private JRadioButton addRadioButton(String txt, int y) {
        JRadioButton rb = new JRadioButton();
        rb.setText(txt);
        rb.setBounds(0, y, 49, 22);
        rb.setFocusable(true);
        rb.setBackground(null);
        rb.setIcon(ColorPanel.rbIcon);
        rb.setSelectedIcon(ColorPanel.rbIconSelected);
        rb.setForeground(null);

        rb.addActionListener((ActionEvent e) -> {
            int newMode = 0;
            JRadioButton rBut = (JRadioButton) e.getSource();
            if (rBut == lnRadioSat) {
                newMode = 1;
            } else if (rBut == lnRadioBri) {
                newMode = 2;
            } else if (rBut == lnRadioR) {
                newMode = 3;
            } else if (rBut == lnRadioG) {
                newMode = 4;
            } else if (rBut == lnRadioB) {
                newMode = 5;
            }
            cPanel.setDialogMode(newMode);
        });

        bg.add(rb);
        this.add(rb);
        return rb;
    }

    /**
     * Adds the TextEdit field of the color components.
     * @param maxValue the maximum value of this component
     * @param y the Y position of the text field
     * @return text field instance
     */
    private LTextField addTextField(int maxValue, int y) {
        LTextField tf = new LTextField(y, 0, maxValue);
        current.addListener(tf);
        this.add(tf);
        return tf;
    }

    /**
     * Adds the HexEdit field of the hex color value (0xRRGGBB).
     * @param x the X position of the text field
     * @param y the Y position of the text field
     */
    @SuppressWarnings("SameParameterValue")
    private void addHexField(int x, int y) {
        LHexField hf = new LHexField(x, y);
        current.addListener(hf);
        this.add(hf);
    }

    /**
     * Adds the describing label to the panel.
     * @param txt text / caption to display
     * @param x the X position of the text field
     * @param y the Y position of the text field
     * @param alignment the text alignment value (left = 2, center = 0, right = 4)
     * @see javax.swing.SwingConstants
     */
    private void addLabel(String txt, int x, int y, int alignment) {
        JLabel l = new JLabel(txt);
        l.setBounds(x, y, 15, 22);
        l.setBackground(null);
        l.setForeground(null);
        l.setHorizontalAlignment(alignment);
        this.add(l);
    }


    /**
     * LTextField class is extension of JTextField which prints and edits components of
     * current color i.e. Hue, Saturation, Brightness, Red, Green, Blue.
     */
    private class LTextField extends LDecTextField implements ColorListener {
    
        public LTextField(int y, int min, int max) {
            super(min, max);
            setBounds(50, y, 50, 22);
        }

        /**
         * The listener of external changes of the color
         */
        @Override
        public void updateColor() {

            int newValue = 0;

            if (this == lnTextHue) {
                if (cPanel.getColorScheme() == 0) {
                    newValue = Math.round(current.getHSBhue() * getMaxValue());
                } else {
                    newValue = Math.round(current.getHSLhue() * getMaxValue());
                }
            } else if (this == lnTextSat) {
                if (cPanel.getColorScheme() == 0) {
                    newValue = Math.round(current.getHSBsat() * getMaxValue());
                } else {
                    newValue = Math.round(current.getHSLsat() * getMaxValue());
                }
            } else if (this == lnTextBri) {
                if (cPanel.getColorScheme() == 0) {
                    newValue = Math.round(current.getHSBbri() * getMaxValue());
                } else {
                    newValue = Math.round(current.getHSLlight() * getMaxValue());
                }
            } else if (this == lnTextR) {
                newValue = current.getRed();
            } else if (this == lnTextG) {
                newValue = current.getGreen();
            } else if (this == lnTextB) {
                newValue = current.getBlue();
            }

            if (newValue != getValue()) {
                setValue(newValue);
            }
        } // updateColor

        /**
         * Changes the color due to in-field edit
         */
        @Override
        public void valueChanged() {
            if (this == lnTextHue) {
                if (cPanel.getColorScheme() == 0) {
                    current.setHSBhue(this, (float) getValue() / (float) getMaxValue());
                } else {
                    current.setHSLhue(this, (float) getValue() / (float) getMaxValue());
                }
            } else if (this == lnTextBri) {
                if (cPanel.getColorScheme() == 0) {
                    current.setHSBbri(this, (float) getValue() / (float) getMaxValue());
                } else {
                    current.setHSLlight(this, (float) getValue() / (float) getMaxValue());
                }
            } else if (this == lnTextSat) {
                if (cPanel.getColorScheme() == 0) {
                    current.setHSBsat(this, (float) getValue() / (float) getMaxValue());
                } else {
                    current.setHSLsat(this, (float) getValue() / (float) getMaxValue());
                }
            } else if (this == lnTextR) {
                current.setRed(this, getValue());
            } else if (this == lnTextG) {
                current.setGreen(this, getValue());
            } else if (this == lnTextB) {
                current.setBlue(this, getValue());
            }
        } // setColor
    }

    /**
     * LHexField class is extension of JTextField which prints and edits the current color
     * in Hex format (0xRRGGBB)
     */
    private static class LHexField extends LHexTextField implements ColorListener {

        public LHexField(int x, int y) {
            super(6);
            setBounds(x, y, 70, 22);
            setValue(current.getColorInt());
        }

        /**
         * The listener of external changes of the color
         */
        @Override
        public void updateColor() {
            setValue(current.getColorInt());
        }

        /**
         * Changes the color due to in-field edit
         */
        @Override
        public void valueChanged() {
            current.setRGB(this, getValue());
        }

    } // lnHexField
}
