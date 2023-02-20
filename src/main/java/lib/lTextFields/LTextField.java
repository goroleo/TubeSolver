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

public class LTextField extends JTextField {

    private String Value;
    private String forbidden = "";

    private boolean externalEdit = true;
    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(2, 3, 2, 3));
    private final Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb8cfe5)),
            BorderFactory.createEmptyBorder(2, 3, 2, 3));

    public LTextField() {
        Value = "";
        setSize(40, 22);
        setBackground(null);
        setForeground(null);
        setBorder(border);
        setCaretColor(Color.white);
        setHorizontalAlignment(2);  // SwingConstants.Left

        setDocument(new PlainDocument() {

            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                StringBuilder temp = new StringBuilder();
                for (int i = 0; i < str.length(); i++) {
                    if (!forbidden.contains(str.substring(i, i + 1))) {
                        temp.append(str.substring(i, i + 1));
                    }
                }

                super.insertString(offs, temp.toString(), a);
                String newValue = getText(0, getLength()).trim();
                Value = checkValue(newValue);
                valueChanged();

            } // insertString

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
                    String newValue = getText(0, getLength()).trim();
                    Value = checkValue(newValue);
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
                setText(Value);
                focusChanged();
            }
        }); // addFocusListener

        setText(Value);
    } // constructor lnTextField

    public String checkValue(String newValue) {
        return newValue.trim();
    }

    public String getForbiddenSigns() {
        return forbidden;
    }

    public void setForbiddenSigns(String s) {
        String temp = getValue();
        forbidden = s;
        setText(temp);
    }

    // external changes of the value
    public void setValue(String newValue, boolean doAnyway) {
        if (externalEdit || doAnyway) {
            String oldValue = Value;
            Value = checkValue(newValue);
            if (!oldValue.equals(Value)) {
                Value = newValue;
                setText(newValue);
            }
        }
    } // setValue

    public String getValue() {
        return Value;
    }
    
    public void focusChanged() { }

    // Change the value due to in-field edit
    public void valueChanged() {  }

} // lnTextField class

