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

import gui.Palette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

import core.ResStrings;
import core.Options;
import lib.lButtons.LPictureButton;

/**
 * Modal dialog with some message and set of buttons. When hides it returns pressed button number.
 */
public class MessageDlg extends JDialog {

    private final JFrame parent;

    /**
     * The result of the dialog (pressed button number)
     */
    public int result = 0;

    @SuppressWarnings({"FieldCanBeLocal", "RedundantSuppression"})
    private final int dimX = 20, dimY = 20, w = 450;

    /**
     * Presets of buttons at message dialogs: Cancel button only.
     */
    public final static int BTN_CANCEL = 0;

    /**
     * Presets of buttons at message dialogs: OK button only.
     */
    public final static int BTN_OK = 1;

    /**
     * Presets of buttons at message dialogs: Start button only.
     */
    public final static int BTN_START = 2;

    /**
     * Presets of buttons at message dialogs: OK & Cancel buttons.
     */
    public final static int BTN_OK_CANCEL = 10;

    /**
     * Presets of buttons at message dialogs: Start & Cancel buttons.
     */
    public final static int BTN_START_CANCEL = 11;

    /**
     * Presets of buttons at message dialogs: Yes & No buttons.
     */
    public final static int BTN_YES_NO = 20;

    /**
     * Presets of buttons at message dialogs: Yes, No and Cancel buttons.
     */
    public final static int BTN_YES_NO_CANCEL = 30;

    /**
     * Buttons count
     */
    private int btnCount = 1;

    /**
     * Presets of buttons layout: buttons are at left side of the dialog.
     */
    public final static int BTN_LAYOUT_LEFT = 1;

    /**
     * Presets of buttons layout: buttons are at center of the dialog.
     */
    public final static int BTN_LAYOUT_CENTER = 2;

    /**
     * Presets of buttons layout: buttons are at right side of the dialog.
     */
    public final static int BTN_LAYOUT_RIGHT = 3;
    private int btnLayout = 2;

    private JPanel contentPanel;
    private JPanel buttonsPanel;

    private LPictureButton[] buttons;

    /**
     * Creates the message dialog
     * @param owner owner frame to center the dialog with/
     * @param msg message string to output
     * @param btnSet set of buttons
     */
    public MessageDlg(JFrame owner, String msg, int btnSet) {

        super(owner, "Message", true);
        this.parent = owner;
        if (owner != null) {
            this.setTitle(owner.getTitle());
        }
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        getContentPane().setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnClick(-1);
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> btnClick(0),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> {
                    if (btnCount < 3) {
                        btnClick(btnCount - 1);
                    }
                },
                KeyStroke.getKeyStroke('\n', InputEvent.CTRL_DOWN_MASK), // VK_ENTER + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        addButtonPanel(btnSet);
        addContentPanel(msg);
        calculateSize();
        calculatePos();
        setButtonsLayout(btnLayout);
    }

    private void calculateSize() {
        setResizable(true);
        Dimension dim = new Dimension();
        dim.width = Math.max(contentPanel.getWidth(), buttonsPanel.getWidth());
        dim.height = contentPanel.getHeight() + buttonsPanel.getHeight();
        setPreferredSize(dim);
        pack();

        dim.width += (getWidth() - getContentPane().getWidth());
        dim.height += (getHeight() - getContentPane().getHeight());
        setPreferredSize(dim);
        pack();
        setResizable(false);
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

    private void btnClick(int number) {
        result = number;
        EventQueue.invokeLater(this::dispose);
    }

    private void addContentPanel(String sText) {
        BufferedImage bi = Options.createBufImage("imgMessageDlg_icon.png");

        contentPanel = new JPanel();

        contentPanel.setBackground(null); // content panel
        contentPanel.setForeground(null); // content panel
        contentPanel.setLayout(null); // content panel

        JLabel icon = new JLabel(new ImageIcon(bi));
        contentPanel.add(icon);
        icon.setBounds(dimX, dimY, bi.getWidth(), bi.getHeight());

        JTextArea textArea = new JTextArea(sText);
        textArea.setBounds(
                dimX * 2 + icon.getWidth(), dimY,
                Math.max(w, buttonsPanel.getWidth()) - dimX * 3 - bi.getWidth(),
                icon.getHeight());
        textArea.setBackground(null);
        textArea.setForeground(null);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);

        Dimension dim = textArea.getPreferredSize();
        textArea.setSize(dim);

        contentPanel.setSize(dimX * 3 + icon.getWidth() + textArea.getWidth(),
                dimY * 2 + Math.max(icon.getHeight(), textArea.getHeight()));

        contentPanel.add(textArea);

        getContentPane().add(contentPanel);
    }

    private void addButton(int number, String aCaption) {
        LPictureButton btn = new LPictureButton(this, "btnDialog");
        btn.setText(aCaption);
        btn.setBackground(null);
        btn.setForeground(null);
        btn.setFocusable(true);
        btn.addActionListener((ActionEvent e) -> btnClick(number));
        buttonsPanel.add(btn);
        buttons[number] = btn;
    }

    private void addButtonPanel(int btnSet) {
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(null);
        buttonsPanel.setForeground(null);
        buttonsPanel.setLayout(null);

        changeButtons(btnSet);

        getContentPane().add(buttonsPanel);
    }

    /**
     * Sets the new buttons set
     * @param btnSet new set of buttons
     */
    public void changeButtons(int btnSet) {

        if (buttons != null) {
            for (LPictureButton button : buttons) {
                buttonsPanel.remove(button);
                btnCount--;
            }
        }

        switch (btnSet) {
            case BTN_CANCEL:
                btnCount = 1;
                buttons = new LPictureButton[btnCount];
                addButton(0, ResStrings.getString("strCancel"));
                break;
            case BTN_OK:
                btnCount = 1;
                buttons = new LPictureButton[btnCount];
                addButton(0, ResStrings.getString("strOk"));
                break;
            case BTN_START:
                btnCount = 1;
                buttons = new LPictureButton[btnCount];
                addButton(0, ResStrings.getString("strStart"));
                break;
            case BTN_OK_CANCEL:
                btnCount = 2;
                buttons = new LPictureButton[btnCount];
                addButton(1, ResStrings.getString("strOk"));
                addButton(0, ResStrings.getString("strCancel"));
                break;
            case BTN_START_CANCEL:
                btnCount = 2;
                buttons = new LPictureButton[btnCount];
                addButton(1, ResStrings.getString("strStart"));
                addButton(0, ResStrings.getString("strCancel"));
                break;
            case BTN_YES_NO:
                btnCount = 2;
                buttons = new LPictureButton[btnCount];
                addButton(1, ResStrings.getString("strYes"));
                addButton(0, ResStrings.getString("strNo"));
                break;
            case BTN_YES_NO_CANCEL:
                btnCount = 3;
                buttons = new LPictureButton[btnCount];
                addButton(2, ResStrings.getString("strYes"));
                addButton(1, ResStrings.getString("strNo"));
                addButton(0, ResStrings.getString("strCancel"));
                break;
            default:
                btnCount = 1;
                buttons = new LPictureButton[btnCount];
                addButton(0, "Cancel");
                break;
        }

        buttonsPanel.setSize(dimX * 2 + btnCount * buttons[0].getWidth() + (btnCount - 1) * 15,
                dimY * 3 / 2 + buttons[0].getHeight());

        for (int i = 0; i < btnCount; i++) {
            buttons[i].setLocation(
                    buttonsPanel.getWidth() - dimX - i * 15 - (i + 1) * buttons[i].getWidth(),
                    dimY / 2
            );
        }
    }

    /**
     * Sets new buttons' layout
     * @param buttonLayout new button's layout (left-center-right)
     */
    public final void setButtonsLayout(int buttonLayout) {
        switch (buttonLayout) {
            case BTN_LAYOUT_LEFT:
                buttonsPanel.setLocation(0, contentPanel.getHeight());
                break;
            case BTN_LAYOUT_RIGHT:
                buttonsPanel.setLocation(
                        getContentPane().getWidth() - buttonsPanel.getWidth(),
                        contentPanel.getHeight());
                break;
            default:
                buttonsPanel.setLocation(
                        (getContentPane().getWidth() - buttonsPanel.getWidth()) / 2,
                        contentPanel.getHeight());
        }
        btnLayout = buttonLayout;
    }
}
