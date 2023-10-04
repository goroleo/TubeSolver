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

    /**
     * Forbidden characters for this field
     */
    private String forbidden = "";

    /**
     * True if the current value is not being changed by this field, but by any external
     * controls/routines. External edit is when the focus is not in this field.
     */
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
                        temp.append(str.charAt(i));
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

    /**
     * @return the set of characters that are forbidden to this field.
     */
    @SuppressWarnings("unused")
    public String getForbiddenSigns() {
        return forbidden;
    }

    /**
     * Sets the set of characters that would be prohibited to this field.
     *
     * @param s set of forbidden characters
     */
    public void setForbiddenSigns(String s) {
        String temp = getValue();
        forbidden = s;
        setText(temp);
    }

    /**
     * Sets the new value to this field.
     *
     * @param newValue new string value
     * @param doAnyway if true perform it in any case regardless of external or internal editing.
     */
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

    /**
     * @return the current value
     */
    public String getValue() {
        return Value;
    }

    /**
     * Handles the change of the focus. An abstract routine to override it
     */
    @SuppressWarnings("EmptyMethod")
    public void focusChanged() {
        // abstract routine to override it
    }

    /**
     * Handles the change of current value. An abstract routine to override it
     */
    public void valueChanged() {
        // abstract routine to override it
    }

} // lnTextField class

