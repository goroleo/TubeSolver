/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */
package lib.lTextFields;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LDecTextField extends JTextField {

    private int minValue;
    private int maxValue;
    private int Value;
    private int digits;

    private boolean externalEdit = true;
    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(2, 3, 2, 3));
    private final Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb8cfe5)),
            BorderFactory.createEmptyBorder(2, 3, 2, 3));

    public LDecTextField(int min, int max) {
        setMinValue(min);
        setMaxValue(max);
        Value = maxValue;
        setSize(40, 22);
        setBackground(null);
        setForeground(null);
        setBorder(border);
        setCaretColor(Color.white);
        setHorizontalAlignment(0);  // SwingConstants.CENTER

        setDocument(new PlainDocument() {

            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (externalEdit) {
                    super.insertString(offs, str, a);
                    return;
                }
                boolean doInsert = true;
                String chars = "0123456789";
                for (int i = 0; i < str.length(); i++) {
                    if (!chars.contains(str.substring(i, i + 1))) {
                        doInsert = false;
                        break;
                    }
                }
                if (doInsert && getLength() < digits) {
                    int newValue = 0;
                    super.insertString(offs, str, a);
                    str = getText(0, getLength()).trim();
                    try {
                        newValue = Integer.parseInt(str);
                        if (getLength() > 1 && "0".equals(str.substring(0, 1))) {
                            remove(0, 1);
                        }
                    } catch (NumberFormatException e) {
                        // do nothing
                    }
                    Value = checkValue(newValue);
                    valueChanged();
                }
            } // insertSring

            @Override
            public void replace(int offset, int length, String text,
                    AttributeSet attrs) throws BadLocationException {

                if (length > 0) {
                    super.remove(offset, length);
                }
                if (text != null && text.length() > 0) {
                    insertString(offset, text, attrs);
                }
            } // replace

            @Override
            public void remove(int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                if (!externalEdit) {
                    int newValue = 0;
                    String str = getText(0, getLength()).trim();
                    try {
                        newValue = Integer.parseInt(str);
                    } catch (NumberFormatException e) {
                        // do nothing
                    }

                    Value = checkValue(newValue);
                    if (newValue == 0 && getLength() == 0) {
                        insertString(0, "0", null);
                    }
                    valueChanged();
                }
            } // remove

        }); // setDocument

        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                setBorder(focusedBorder);
                externalEdit = false;
                focusChanged();
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(border);
                externalEdit = true;
                setText(Integer.toString(Value));
                focusChanged();
            }
        }); // addFocusListener

        setText(Integer.toString(Value));
    } // constructor lnTextField

    public int checkValue(int newValue) {
        if (newValue < minValue) {
            return minValue;
        } else return Math.min(newValue, maxValue);
    }

    // external changes of the value
    public void setValue(int newValue) {
        if (externalEdit) {
            int oldValue = Value;
            Value = checkValue(newValue);
            if (oldValue != Value) {
                setText(Integer.toString(Value));
            }
        }
    } // setValue

    public int getValue() {
        return Value;
    }

    public void setMinValue(int newMinValue) {
        minValue = newMinValue;
        int newValue = checkValue(Value);
        if (newValue != Value) {
            setValue(newValue);
        }
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMaxValue(int newMaxValue) {
        maxValue = newMaxValue;
        int newValue = checkValue(Value);
        if (newValue != Value) {
            setValue(newValue);
        }
        int tmp = newMaxValue;
        digits = 1;
        do {
            digits++;
            tmp = tmp / 10;
        } while (tmp > 9); 
    }

    public int getMaxValue() {
        return maxValue;
    }

    // Change the value due to in-field edit
    public void focusChanged() {
        
    } 
    
    // Change the value due to in-field edit
    public void valueChanged() {
        
    } 

} // lnTextField class

