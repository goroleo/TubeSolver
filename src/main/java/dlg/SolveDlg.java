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

import ani.WheelLayer;
import core.BoardModel;
import core.ResStrings;
import core.Solver;
import gui.Palette;
import lib.lButtons.LPictureButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * The game solution search frame. While the search is in progress, the animation entertains the user.
 */
public class SolveDlg extends JDialog {

    private final JFrame parent;
    /**
     * The result of the dialog. Results can be as follows: <ul>
     *     <li> 2 - the game is solved.
     *     <li> 1 - the game is not solved (solution is not found).
     *     <li> 0 - escape / cancel pressed while solver is working.
     *     <li> -1 - window 'Close' button pressed while solver is working.
     * </ul>
     */
    public int result = 0;
    /**
     * Dialog width.
     */
    private final int w = 350;
    /**
     * Dialog height.
     */
    private int h; //width & height

    private WheelLayer wheelLayer;

    private int breakCount = 100000;

    /**
     * The start (before search) configuration of the game board.
     */
    private final BoardModel start;
    private final Solver ts;

    /**
     * Creates the Solver dialog.
     * @param owner frame owner to center this dialog
     * @param startBoard start game board position
     */
    @SuppressWarnings("MagicConstant")
    public SolveDlg(JFrame owner, BoardModel startBoard) {

        super(owner, ResStrings.getString("strSolving"), true);
        this.parent = owner;
        setResizable(false);
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        getContentPane().setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog(-1);
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> closeDialog(0),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

        start = startBoard;

        ts = new Solver(startBoard, breakCount) {
            @Override
            public void onSolved() {
                closeDialog(2);
            }

            @Override
            public void onNotSolved() {
                breakCount <<= 1;
                if (breakCount > 0) {
                    System.out.println("!!! NOT SOLVED. new break " + breakCount);
                    setStartTubes(start);
                    setBreakStop(breakCount);
                    if (!externalBreak) {
                        startSolve();
                    }
                } else {
                    closeDialog(1);
                }
            }

            @Override
            public void onExternalBreak() {
                System.err.println("External break");
            }
        };

        initContent();
        calculateSize();
        calculatePos();
    }

    private void initContent() {

        wheelLayer = new WheelLayer();

        LPictureButton btn = new LPictureButton(this, "btnDialog");
        btn.setText(ResStrings.getString("strCancel"));
        btn.setBackground(null);
        btn.setForeground(null);
        btn.setFocusable(true);
        btn.addActionListener((ActionEvent e) -> closeDialog(0));

        getContentPane().add(wheelLayer);
        getContentPane().add(btn);

        int dimY = 20; // the space between components

        h = dimY * 3 + wheelLayer.getHeight() + btn.getHeight();
        wheelLayer.setLocation((w - wheelLayer.getWidth()) / 2, dimY);
        btn.setLocation((w - btn.getWidth()) / 2, dimY * 2+ wheelLayer.getHeight());

    }

    /**
     * Calculates and sets the dialog size.
     */
    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = w;
        dim.height = h;
        setPreferredSize(dim);
        pack();

        int dx = (getWidth() - getContentPane().getWidth());
        int dy = (getHeight() - getContentPane().getHeight());
        dim.width += dx;
        dim.height += dy;
        setPreferredSize(dim);
        pack();
    }

    /**
     * Calculates and sets the dialog position.
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
     * Closes the dialog and sets the dialog result.
     * @param reason result of the dialog described at <i>result</i> field.
     * @see #result
     */
    private void closeDialog(int reason) {

        if (reason < 1) {
            // solver is still working, we have to stop it before close.
            ts.stopProcess();
        }

        result = reason;
        if (ts.workingTime > 120000   // more than 2 minutes
                && reason < 1) {      // and escape|cancel pressed
            result = 1;
        }
        EventQueue.invokeLater(this::dispose);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            wheelLayer.start();
        } else {
            wheelLayer.stop();
        }
        super.setVisible(b);
    }

    /**
     * Start solving and show color wheel.
     */
    public void solve() {
        ts.startSolve();
        setVisible(true);
    }
}
