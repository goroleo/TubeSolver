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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import core.ResStrings;
import lib.lButtons.LPictureButton;

public class MessageDlg extends JDialog {

    private final JFrame parent;
    public int modalResult = 0;
    int dimX = 20, dimY = 20;

    int w = 450;

    public final static int BTN_CANCEL = 0;
    public final static int BTN_OK = 1;
    public final static int BTN_START = 2;
    public final static int BTN_OK_CANCEL = 10;
    public final static int BTN_START_CANCEL = 11;
    public final static int BTN_YES_NO = 20;
    public final static int BTN_YES_NO_CANCEL = 30;
    int btnCount = 1;

    public final static int BTN_LAYOUT_LEFT = 1;
    public final static int BTN_LAYOUT_CENTER = 2;
    public final static int BTN_LAYOUT_RIGHT = 3;
    int btnLayout = 2;

    private JPanel contPan;
    private JPanel btnPan;

    private LPictureButton[] buttons;

    public MessageDlg() {
        this(null, "", 0);
    }

    public MessageDlg(String msg) {
        this(null, msg, 0);
    }

    public MessageDlg(JFrame owner, String msg) {
        this(owner, msg, 0);
    }

    public MessageDlg(String msg, int btnSet) {
        this(null, msg, btnSet);
    }

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
                2); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> {
                    if (btnCount < 3) {
                        btnClick(btnCount - 1);
                    }
                },
                KeyStroke.getKeyStroke('\n', 2), // VK_ENTER + MASK_CTRL
                2); // WHEN_IN_FOCUSED_WINDOW

        addButtonPanel(btnSet);
        addContentPanel(msg);
        calculateSize();
        calculatePos();
        setButtonsLayout(btnLayout);
    }

    private void calculateSize() {
        setResizable(true);
        Dimension dim = new Dimension();
        dim.width = Math.max(contPan.getWidth(), btnPan.getWidth());
        dim.height = contPan.getHeight() + btnPan.getHeight();
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
        modalResult = number;
        EventQueue.invokeLater(this::dispose);
    }

    private void addContentPanel(String sText) {
        BufferedImage bi = core.Options.createBufImage("imgMessageDlg_icon.png");

        contPan = new JPanel();

        contPan.setBackground(null); // content panel 
        contPan.setForeground(null); // content panel 
        contPan.setLayout(null); // content panel 

        JLabel icon = new JLabel(new ImageIcon(bi));
        contPan.add(icon);
        icon.setBounds(dimX, dimY, bi.getWidth(), bi.getHeight());

        JTextArea textArea = new JTextArea(sText);
        textArea.setBounds(
                dimX * 2 + icon.getWidth(),
                dimY,
                Math.max(w, btnPan.getWidth()) - dimX * 3 - bi.getWidth(),
                icon.getHeight());
        textArea.setBackground(null);
        textArea.setForeground(null);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);

        Dimension dim = textArea.getPreferredSize();
        textArea.setSize(dim);

        contPan.setSize(dimX * 3 + icon.getWidth() + textArea.getWidth(),
                dimY * 2 + Math.max(icon.getHeight(), textArea.getHeight()));

        contPan.add(textArea);

        getContentPane().add(contPan);
    }

    private LPictureButton addButton(int number, String aCaption) {
        LPictureButton btn = new LPictureButton(this, "btnDialog");
        btn.setText(aCaption);
        btn.setBackground(null);
        btn.setForeground(null);
        btn.setFocusable(true);
        btn.addActionListener((ActionEvent e) -> btnClick(number));
        btnPan.add(btn);
        buttons[number] = btn;
        return btn;
    }

    private void addButtonPanel(int btnSet) {
        btnPan = new JPanel();
        btnPan.setBackground(null);
        btnPan.setForeground(null);
        btnPan.setLayout(null);

        changeButtons(btnSet);

        getContentPane().add(btnPan);
    }

    public void changeButtons(int btnSet) {

        if (buttons != null) {
            for (LPictureButton button : buttons) {
                btnPan.remove(button);
                btnCount--;
                button = null;
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

        btnPan.setSize(dimX * 2 + btnCount * buttons[0].getWidth() + (btnCount - 1) * 15,
                dimY * 3 / 2 + buttons[0].getHeight());

        for (int i = 0; i < btnCount; i++) {
            buttons[i].setLocation(
                    btnPan.getWidth() - dimX - i * 15 - (i + 1) * buttons[i].getWidth(),
                    dimY / 2
            );
        }
    }

    public final void setButtonsLayout(int buttonLayout) {
        switch (buttonLayout) {
            case BTN_LAYOUT_LEFT:
                btnPan.setLocation(0, contPan.getHeight());
                break;
            case BTN_LAYOUT_RIGHT:
                btnPan.setLocation(
                        getContentPane().getWidth() - btnPan.getWidth(),
                        contPan.getHeight());
                break;
            default:
                btnPan.setLocation(
                        (getContentPane().getWidth() - btnPan.getWidth()) / 2,
                        contPan.getHeight());
        }
        btnLayout = buttonLayout;
    }

}
