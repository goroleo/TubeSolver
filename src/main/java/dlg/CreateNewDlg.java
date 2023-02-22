/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */
package dlg;

import core.Options;
import gui.Palette;
import core.ResStrings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import lib.lButtons.LPictureButton;
import lib.lButtons.LToolButton;
import lib.lTextFields.LDecTextField;

public class CreateNewDlg extends JDialog {

    private final JFrame parent;
    private final int maxLabelWidth;
    private final int startY = 30;
    private final int dimY = 40;
    private final int dimX = 40;
    private int w, h; // width and height
    public int tubesFilled = 12;
    public int tubesEmpty = 2;
    public int tubesCount = tubesFilled + tubesEmpty;
    public Boolean ok = false;

    private final jctSizeEdit tsf1;
    private final jctSizeEdit tsf2;
    private final jctSizeEdit tsf3;

    private final JPanel pan = new JPanel();

    public CreateNewDlg() {
        this(null);
    }

    public CreateNewDlg(JFrame owner) {

        super(owner, ResStrings.getString("strCreateNew"), true);
        this.parent = owner;
        setResizable(false);
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
//        setLayout(null);

        pan.setBackground(null);
        pan.setForeground(null);
        pan.setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                refuseAndClose();
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> refuseAndClose(),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> confirmAndClose(),
                KeyStroke.getKeyStroke('\n', 2), // VK_ENTER + MASK_CTRL
                2); // WHEN_IN_FOCUSED_WINDOW

        LPictureButton btnOk = new LPictureButton(this, "btnDialog");
        btnOk.setText(ResStrings.getString("strCreate"));
        btnOk.setBackground(null);
        btnOk.setForeground(null);
        btnOk.setFocusable(true);
        btnOk.addActionListener((ActionEvent e) -> confirmAndClose());
        pan.add(btnOk);

        LPictureButton btnCancel = new LPictureButton(this, "btnDialog");
        btnCancel.setText(ResStrings.getString("strCancel"));
        btnCancel.setBackground(null);
        btnCancel.setForeground(null);
        btnCancel.setFocusable(true);
        btnCancel.addActionListener((ActionEvent e) -> refuseAndClose());
        pan.add(btnCancel);

        addLabel(0, ResStrings.getString("strWantToCreate"));
        JLabel lb1 = addLabel(1, ResStrings.getString("strNumberFilled"));
        JLabel lb2 = addLabel(2, ResStrings.getString("strNumberEmpty"));
        JLabel lb3 = addLabel(3, ResStrings.getString("strTotalTubes"));

        maxLabelWidth = Math.max(Math.max(lb1.getWidth(), lb2.getWidth()), lb3.getWidth());

        tsf1 = new jctSizeEdit(1, 2, 12);
        pan.add(tsf1);
        tsf2 = new jctSizeEdit(2, 1, 2);
        pan.add(tsf2);
        tsf3 = new jctSizeEdit(3, 3, 14);
        pan.add(tsf3);

        w = maxLabelWidth + tsf1.getWidth() + dimX * 3;
        h = startY + dimY * 5 + btnOk.getHeight();
        pan.setSize(w, h);
        getContentPane().add(pan);

        calculateSize();
        calculatePos();

        btnCancel.setLocation(
                w - btnCancel.getWidth() - dimX,
                h - dimY / 2 - btnCancel.getHeight());
        btnOk.setLocation(
                btnCancel.getLocation().x - btnOk.getWidth() - 15,
                btnCancel.getLocation().y);

    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            if (Options.cndFilledTubes > 0 && Options.cndEmptyTubes > 0) {
                tsf1.setValue(Options.cndFilledTubes);
                tsf2.setValue(Options.cndEmptyTubes);
                newBoardSize(tsf2);
            }
        }
        super.setVisible(b);
    }

    private void refuseAndClose() {
        ok = false;
        EventQueue.invokeLater(this::dispose);
    }

    private void confirmAndClose() {
        ok = true;
        Options.cndFilledTubes = tsf1.getValue();
        Options.cndEmptyTubes = tsf2.getValue();
        EventQueue.invokeLater(this::dispose);
    }

    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = w;
        dim.height = h;
        pan.setSize(dim);
        setPreferredSize(dim);
        pack();

        int dx = getWidth() - getContentPane().getWidth();
        int dy = getHeight() - getContentPane().getHeight();
        dim.width += dx;
        dim.height += dy;
        setPreferredSize(dim);
        pack();
    }

    private void calculatePos() {
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            Rectangle r = getGraphicsConfiguration().getBounds();
            r.x = r.x + (r.width - getWidth()) / 2;
            r.y = r.y + (r.height - getHeight()) / 2;
            setLocation(r.x, r.y);
        }
    }

    private void newBoardSize(jctSizeEdit sender) {
        if (sender != tsf3) {
            tubesFilled = tsf1.getValue();
            tubesEmpty = tsf2.getValue();
            tubesCount = tubesFilled + tubesEmpty;
            tsf3.setValue(tubesCount);
        } else {
            tubesCount = tsf3.getValue();

            int aEmpty = tsf2.tf.getMaxValue();
            int aFilled = tubesCount - aEmpty;
            aFilled = tsf1.checkValue(aFilled);

            while ((aFilled + aEmpty) != tubesCount) {
                aEmpty--;
                aEmpty = tsf2.checkValue(aEmpty);
                aFilled = tubesCount - aEmpty;
                aFilled = tsf1.checkValue(aFilled);
            }
            tubesFilled = aFilled;
            tubesEmpty = aEmpty;
            tsf1.setValue(tubesFilled);
            tsf2.setValue(tubesEmpty);
        }
    }

    private JLabel addLabel(int number, String text) {
        JLabel lb = new JLabel(text);
        int y;
        if (number == 0) {
            lb.setFont(lb.getFont().deriveFont(1)); // Font.BOLD
            y = startY;
        } else {
            lb.setFont(lb.getFont().deriveFont(0)); //Font.PLAIN
            y = startY + dimY * (number) + 4;
        }

        FontMetrics fm = lb.getFontMetrics(lb.getFont());
        lb.setBounds(30, y, fm.stringWidth(text), fm.getHeight());
        lb.setBackground(null);
        lb.setForeground(null);
        pan.add(lb);
        return lb;
    }

    private class jctSizeEdit extends JComponent {

        LDecTextField tf;
        LToolButton btnMinus;
        LToolButton btnPlus;

        public jctSizeEdit(int number, int aMin, int aMax) {
            super();
            int x = 0;
            int y = 0;

            btnMinus = new LToolButton(this, "btnTool22", "minus");
            btnMinus.setColorEnabled(new Color(0xee, 0xee, 0xee));
            btnMinus.setColorHover(new Color(184, 207, 229));
            btnMinus.setColorPressed(new Color(0xbb, 0xbb, 0xbb));
            btnMinus.setLocation(x, y);
            add(btnMinus);
            x = x + btnMinus.getWidth();

            tf = new LDecTextField(aMin, aMax) {
                @Override
                public void valueChanged() {
                    updateValues();
                }
            };
            tf.setLocation(x, y);
            add(tf);
            x = x + tf.getWidth();

            btnPlus = new LToolButton(this, "btnTool22", "plus");
            btnPlus.setColorEnabled(new Color(0xee, 0xee, 0xee));
            btnPlus.setColorHover(new Color(184, 207, 229));
            btnPlus.setColorPressed(new Color(0xbb, 0xbb, 0xbb));
            btnPlus.setLocation(x, y);
            add(btnPlus);
            x = x + btnPlus.getWidth();
            y = y + btnMinus.getHeight();

            setSize(x, y);
            setLocation(dimX * 2 + maxLabelWidth, startY + dimY * number);

            btnMinus.addActionListener((ActionEvent e) -> {
                int oldVal = tf.getValue();
                int val = tf.checkValue(oldVal - 1);
                if (val != oldVal) {
                    tf.setValue(val);
                    tf.valueChanged();
                }
                checkButtons();
            });
            btnPlus.addActionListener((ActionEvent e) -> {
                int oldVal = tf.getValue();
                int val = tf.checkValue(oldVal + 1);
                if (val != oldVal) {
                    tf.setValue(val);
                    tf.valueChanged();
                }
                checkButtons();
            });

            checkButtons();

        }

        public void checkButtons() {
            btnMinus.setEnabled(tf.getValue() != tf.getMinValue());
            btnPlus.setEnabled(tf.getValue() != tf.getMaxValue());
        }

        public void updateValues() {
            newBoardSize(this);
            checkButtons();
        }

        public int getValue() {
            return tf.getValue();
        }

        public void setValue(int newValue) {
            tf.setValue(newValue);
            checkButtons();
        }

        public int checkValue(int newValue) {
            return tf.checkValue(newValue);
        }
    }
}
