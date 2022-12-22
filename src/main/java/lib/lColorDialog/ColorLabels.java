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
import static lib.lColorDialog.ColorPanel.currentColor;
import lib.lTextFields.LDecTextField;
import lib.lTextFields.LHexTextField;

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
     * The field are listen the change of current color and indicates its
     * component's value. Also the field allows to change component's value
     * directly. In this case the field initiates color change.
     */
    private final lnTextField lnTextHue, lnTextSat, lnTextBri;

    /**
     * Radio buttons for color components: Red, Green, Blue. Click on these
     * radioButtons will change mode of dialog and redraw colorBox and
     * colorLine.
     */
    private final JRadioButton lnRadioR, lnRadioG, lnRadioB;

    /**
     * Text fields for color components: Red, Green, Blue.<br>
     * The field are listen the change of current color and indicates its
     * component's value. Also the field allows to change component's value
     * directly. In this case the field initiates color change.
     */
    private final lnTextField lnTextR, lnTextG, lnTextB;

    /**
     * Text fields for hex color's value (0xRRGGBB) <br>
     * The field are listen the change of current color and allows to change
     * color value directly.
     */
    private final lnHexField lnHex;

    /**
     * <b>colorScheme</b> is an integer value. 0 means HSB/HSV, 1 means HSL:
     */
    private int colorScheme = 0;

    
    public ColorLabels() {

        // Initialize HSB color components
        // Hue: radio, text, dimension
        lnRadioHue = addRadioButton("H", 0);   // params: Text and Y-position
        lnTextHue = addTextField(360, 0);      // params: MaxValue and Y-position
        addLabel("°", 103, 0, 2);              // params: Text, X-pos, Y-pos, Alignment mode constant
        // Saturation: radio, text, dimension
        lnRadioSat = addRadioButton("S", 26);  // params: Text and Y-position
        lnTextSat = addTextField(100, 26);     // params: MaxValue and Y-position
        addLabel("%", 103, 26, 2);             // params: Text, X-pos, Y-pos, Alignment mode constant
        // Brightness: radio, text, dimension
        lnRadioBri = addRadioButton("B", 52);  // ... and so on
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
        addLabel("#", 5, 170, 4);              // params: Text, X-pos, Y-pos, Alignment mode constant
        lnHex = addHexField(30, 170);          // params: X-position, Y-position

        lnRadioHue.setSelected(true);
        setForeground(null);
        setBounds(0, 0, 118, 192);
    }

    public void updateColorScheme(int scheme) {
        if (colorScheme != scheme) {
            colorScheme = scheme;
            if (colorScheme == 0) {
                lnRadioBri.setText("B");
            } else {
                lnRadioBri.setText("L");
            }
            lnTextHue.updateColor(0);
            lnTextSat.updateColor(0);
            lnTextBri.updateColor(0);
        }
    }

    public void updateDialogMode(int newMode) {
        switch (newMode) {
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
            ColorPanel.setDialogMode(newMode);
        });

        bg.add(rb);
        this.add(rb);
        return rb;
    }

    private lnTextField addTextField(int maxValue, int y) {
        lnTextField tf = new lnTextField(y, 0, maxValue);
        currentColor.addListener(tf);
        this.add(tf);
        return tf;
    }

    private lnHexField addHexField(int x, int y) {
        lnHexField hf = new lnHexField(x, y);
        currentColor.addListener(hf);
        this.add(hf);
        return hf;
    }

    private JLabel addLabel(String txt, int x, int y, int aligment) {
        JLabel l = new JLabel(txt);
        l.setBounds(x, y, 15, 22);
        l.setBackground(null);
        l.setForeground(null);
        l.setHorizontalAlignment(aligment);
        this.add(l);
        return l;
    }

    // lnTextField class is extension of JTextFeild
    // which prints and edits components of current color
    // i.e. Hue, Saturation, Brghtness, Red, Green, Blue.
    
    private class lnTextField extends LDecTextField implements ColorListener {
    
        public lnTextField(int y, int min, int max) {
            super(min, max);
            setBounds(50, y, 50, 22);
        }
        
        // Listener of external changes of the color
        @Override
        public void updateColor(int rgb) {

            int newValue = 0;

            if (this == lnTextHue) {
                if (colorScheme == 0) {
                    newValue = Math.round(currentColor.getHSBhue() * getMaxValue());
                } else {
                    newValue = Math.round(currentColor.getHSLhue() * getMaxValue());
                }
            } else if (this == lnTextSat) {
                if (colorScheme == 0) {
                    newValue = Math.round(currentColor.getHSBsat() * getMaxValue());
                } else {
                    newValue = Math.round(currentColor.getHSLsat() * getMaxValue());
                }
            } else if (this == lnTextBri) {
                if (colorScheme == 0) {
                    newValue = Math.round(currentColor.getHSBbri() * getMaxValue());
                } else {
                    newValue = Math.round(currentColor.getHSLlight() * getMaxValue());
                }
            } else if (this == lnTextR) {
                newValue = currentColor.getRed();
            } else if (this == lnTextG) {
                newValue = currentColor.getGreen();
            } else if (this == lnTextB) {
                newValue = currentColor.getBlue();
            }

            if (newValue != getValue()) {
                setValue(newValue);
            }
        } // updateColor

        // Changes the color due to in-field edit
        @Override
        public void valueChanged() {
            if (this == lnTextHue) {
                if (colorScheme == 0) {
                    currentColor.setHSBhue(this, (float) getValue() / (float) getMaxValue());
                } else {
                    currentColor.setHSLhue(this, (float) getValue() / (float) getMaxValue());
                }
            } else if (this == lnTextBri) {
                if (colorScheme == 0) {
                    currentColor.setHSBbri(this, (float) getValue() / (float) getMaxValue());
                } else {
                    currentColor.setHSLlight(this, (float) getValue() / (float) getMaxValue());
                }
            } else if (this == lnTextSat) {
                if (colorScheme == 0) {
                    currentColor.setHSBsat(this, (float) getValue() / (float) getMaxValue());
                } else {
                    currentColor.setHSLsat(this, (float) getValue() / (float) getMaxValue());
                }
            } else if (this == lnTextR) {
                currentColor.setRed(this, getValue());
            } else if (this == lnTextG) {
                currentColor.setGreen(this, getValue());
            } else if (this == lnTextB) {
                currentColor.setBlue(this, getValue());
            }
        } // setColor
    }
    
    private class lnHexField extends LHexTextField implements ColorListener {

        public lnHexField(int x, int y) {
            super(6);
            setBounds(x, y, 70, 22);
            setText(currentColor.getHexColor());
        }

        @Override
        public void updateColor(int rgb) {
            setText(currentColor.getHexColor());
        }

        @Override
        public void valueChanged() {
            currentColor.setRGB(this, getValue());
        }

    } // lnHexField
}
