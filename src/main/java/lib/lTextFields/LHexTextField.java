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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LHexTextField extends JTextField {

    private final int digits;
    private int value;

    private boolean externalEdit = true;
    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(2, 3, 2, 3));
    private final Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb8cfe5)),
            BorderFactory.createEmptyBorder(2, 3, 2, 3));

    public LHexTextField(int aDigits) {
        this.digits = aDigits;
        setSize(70, 22);
        setBackground(null);
        setForeground(null);
        setBorder(border);
        setCaretColor(Color.white);
        setMargin(new Insets(0, 4, 0, 4));
        setHorizontalAlignment(0); // SwingConstants.CENTER

        setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (externalEdit) {
                    super.insertString(offs, str, a);
                    return;
                }
                boolean doInsert = true;
                String chars = "0123456789ABCDEFabcdef";
                for (int i = 0; i < str.length(); i++) {
                    if (!chars.contains(str.substring(i, i + 1))) {
                        doInsert = false;
                        break;
                    }
                }
                if (doInsert && getLength() < digits) {
                    super.insertString(offs, str, a);

                    int newValue = 0;
                    str = getText(0, getLength()).trim();
                    try {
                        newValue = Integer.parseInt(str, 16);
                    } catch (NumberFormatException e) {
                        // do nothing
                    }
                    value = newValue;
                    valueChanged();
                }
            } // insertString

            @Override
            public void remove(int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                if (!externalEdit) {
                    int newValue = 0;
                    String str = getText(0, getLength()).trim();
                    try {
                        newValue = Integer.parseInt(str, 16);
                    } catch (NumberFormatException e) {
                        // do nothing
                    }
                    value = newValue;
                    valueChanged();
                }
            } // remove
        });

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
                setValue(value);
                focusChanged();
            }
        });

    }

    public void setValue(int newValue) {
        if (externalEdit) {
            value = newValue;
            StringBuilder s = new StringBuilder(Integer.toHexString(value));
            if (s.length() > digits) {
                s = new StringBuilder(s.substring(s.length() - digits));
            } else
                while (s.length() < digits) {
                    s.insert(0, "0");
                }
            setText(s.toString());
        }
    }

    public int getValue() {
        return value;
    }

    public void focusChanged() {
        // abstract routine for override it
    }

    public void valueChanged() {
        // abstract routine for override it
    }

}
