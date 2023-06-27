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
import javax.swing.KeyStroke;
import lib.lButtons.LPictureButton;
import lib.lButtons.LToolButton;
import lib.lTextFields.LDecTextField;

/**
 * A dialog which asks the user how many tubes will be on the new game board.
 */
public class CreateNewDlg extends JDialog {

    private final JFrame parent;
    private final int maxLabelWidth;
    private final int startY = 30;
    private final int dimY = 40;
    private final int dimX = 40;
    private final int w, h; // width and height

    /**
     * Result of the dialog. True if user confirmed tubes count.
     */
    public Boolean ok = false;

    private final TubesCountField tcf1;
    private final TubesCountField tcf2;
    private final TubesCountField tcf3;

    /**
     * Creates the dialog.
     * @param owner frame owner
     */
    @SuppressWarnings("MagicConstant")
    public CreateNewDlg(JFrame owner) {

        super(owner, ResStrings.getString("strCreateNew"), true);
        this.parent = owner;
        setResizable(false);
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        getContentPane().setLayout(null);

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
        getContentPane().add(btnOk);

        LPictureButton btnCancel = new LPictureButton(this, "btnDialog");
        btnCancel.setText(ResStrings.getString("strCancel"));
        btnCancel.setBackground(null);
        btnCancel.setForeground(null);
        btnCancel.setFocusable(true);
        btnCancel.addActionListener((ActionEvent e) -> refuseAndClose());
        getContentPane().add(btnCancel);

        addLabel(0, ResStrings.getString("strWantToCreate"));
        JLabel lb1 = addLabel(1, ResStrings.getString("strNumberFilled"));
        JLabel lb2 = addLabel(2, ResStrings.getString("strNumberEmpty"));
        JLabel lb3 = addLabel(3, ResStrings.getString("strTotalTubes"));

        maxLabelWidth = Math.max(Math.max(lb1.getWidth(), lb2.getWidth()), lb3.getWidth());

        tcf1 = new TubesCountField(1, 2, 12);
        getContentPane().add(tcf1);
        tcf2 = new TubesCountField(2, 1, 2);
        getContentPane().add(tcf2);
        tcf3 = new TubesCountField(3, 3, 14);
        getContentPane().add(tcf3);

        w = maxLabelWidth + tcf1.getWidth() + dimX * 3;
        h = startY + dimY * 5 + btnOk.getHeight();

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
                tcf1.setValue(Options.cndFilledTubes);
                tcf2.setValue(Options.cndEmptyTubes);
            } else {
                tcf1.setValue(12);
                tcf2.setValue(2);
            }
            updateTubesCount(tcf1);
        }
        super.setVisible(b);
    }

    /**
     * Handles the click/press of the Escape/Cancel button.
     */
    private void refuseAndClose() {
        ok = false;
        EventQueue.invokeLater(this::dispose);
    }

    /**
     * Handles the click/press of the OK button. Applies the new values for tubes counts.
     */
    private void confirmAndClose() {
        ok = true;
        Options.cndFilledTubes = tcf1.getValue();
        Options.cndEmptyTubes = tcf2.getValue();
        EventQueue.invokeLater(this::dispose);
    }

    /**
     * Calculates and sets the frame size.
     */
    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = w;
        dim.height = h;
        setPreferredSize(dim);
        pack();

        int dx = getWidth() - getContentPane().getWidth();
        int dy = getHeight() - getContentPane().getHeight();
        dim.width += dx;
        dim.height += dy;
        setPreferredSize(dim);
        pack();
    }

    /**
     * Calculates and sets the frame position.
     */
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

    /**
     * Updates tubes counts when one of the field changed.
     * @param invoker the field that initiate changes. All other fields will be updated.
     */
    private void updateTubesCount(TubesCountField invoker) {
        if (invoker != tcf3) {
            int aFilled = tcf1.getValue();
            int aEmpty = tcf2.getValue();
            tcf3.setValue(aFilled + aEmpty);
        } else {

            int aCount = tcf3.getValue();
            int aEmpty = tcf2.getMaxValue();
            int aFilled = tcf1.checkValue(aCount - aEmpty);

            while ((aFilled + aEmpty) != aCount) {
                aEmpty--;
                aEmpty = tcf2.checkValue(aEmpty);
                aFilled = tcf1.checkValue(aCount - aEmpty);
            }

            tcf1.setValue(aFilled);
            tcf2.setValue(aEmpty);
        }
    }

    /**
     * Adds label to the frame.
     * @param number number of the label.
     * @param text label caption.
     * @return label created.
     */
    @SuppressWarnings("MagicConstant")
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
        getContentPane().add(lb);
        return lb;
    }

    /**
     * A decimal number text field to display and edit tubes counts, with plus and minus buttons on the sides.
     */
    private class TubesCountField extends JComponent {

        private final LDecTextField tf;
        private final LToolButton btnMinus;
        private final LToolButton btnPlus;

        /**
         * Creates the field
         */
        public TubesCountField(int number, int aMin, int aMax) {
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
                    updateTubesCount(TubesCountField.this);
                    updateButtonsState();
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
                updateButtonsState();
            });
            btnPlus.addActionListener((ActionEvent e) -> {
                int oldVal = tf.getValue();
                int val = tf.checkValue(oldVal + 1);
                if (val != oldVal) {
                    tf.setValue(val);
                    tf.valueChanged();
                }
                updateButtonsState();
            });

            updateButtonsState();
        }

        private void updateButtonsState() {
            btnMinus.setEnabled(tf.getValue() >= tf.getMinValue());
            btnPlus.setEnabled(tf.getValue() <= tf.getMaxValue());
        }
        public int getMaxValue() {
            return tf.getMaxValue();
        }

        public int getValue() {
            return tf.getValue();
        }

        public void setValue(int newValue) {
            tf.setValue(newValue);
            updateButtonsState();
        }

        public int checkValue(int newValue) {
            return tf.checkValue(newValue);
        }
    }
}
