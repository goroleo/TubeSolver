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
import core.Solver;
import gui.Palette;
import java.awt.Color;
import core.ResStrings;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import lib.lButtons.LPictureButton;

public class SolveDlg extends JDialog {

    private final JFrame parent;
    public int modalResult = 0;
    int dimY = 20;
    int w = 350; //width & height 

    private JPanel contPan;
    private WheelLayer wl;
    private LPictureButton btn; 

    int breakCount = 100000;

    BoardModel start;
    Solver ts;
    boolean solved = false;

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
                btnClick(-1);
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> btnClick(0),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

        start = startBoard;

        ts = new Solver(startBoard, breakCount) {
            @Override
            public void onSolved() {
                solved = true;
                btnClick(2);
            }

            @Override
            public void onNotSolved() {
                solved = false;
                breakCount <<= 1;
                if (breakCount > 0) {
                    System.out.println("!!! NOT SOLVED. new break " + breakCount);
                    setStartTubes(start);
                    setBreakStop(breakCount);
                    if (!externalBreak) {
                        startSolve();
                    }
                } else {
                    btnClick(1);
                }
            }

            @Override
            public void onExternalBreak() {
                solved = false;
                System.err.println("External break");
            }
        };

        initContent();
        calculateSize();
        calculatePos();
    }

    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = contPan.getWidth();
        dim.height = contPan.getHeight();
        getContentPane().setSize(dim);
        setPreferredSize(dim);
        pack();

        int dx = (getWidth() - getContentPane().getWidth());
        int dy = (getHeight() - getContentPane().getHeight());
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

    private void btnClick(int number) {

        solved = (number == 2);
        modalResult = number;
        if (ts.workingTime > 120000 // more than 2 minutes
                && number < 1) {      // and escape|cancel pressed
            modalResult = 1;
        }

        switch (number) {
            case -1: // frame close pressed
            case 0:  // escape | cancel pressed
                ts.stopProcess();
                break;
            case 1: // not solved
            case 2: // solved
                // solver has terminated by itself
                // do nothing
                break;
        }

        EventQueue.invokeLater(this::dispose);
    }

    private void initContent() {
        contPan = new JPanel();
        contPan.setBackground(null);
        contPan.setForeground(null);
        contPan.setLayout(null);

        wl = new WheelLayer();
        btn = addButton(ResStrings.getString("strCancel"));
        
        contPan.add(wl);
        contPan.add(btn);
        
        contPan.setSize(w, dimY * 3 + wl.getHeight() + btn.getHeight());
        wl.setLocation((w - wl.getWidth()) / 2, dimY);
        btn.setLocation((w - btn.getWidth()) / 2, dimY*2+wl.getHeight());
        
        getContentPane().add(contPan);
    }

    private LPictureButton addButton(String aCaption) {
        LPictureButton pb = new LPictureButton(this, "btnDialog");
        pb.setText(aCaption);
        pb.setBackground(null);
        pb.setForeground(null);
        pb.setFocusable(true);
        pb.addActionListener((ActionEvent e) -> btnClick(0));
        return pb;
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            wl.start();
            //          fl.start();
        } else {
            wl.stop();
        }
        super.setVisible(b);
    }

    public boolean solve() {
        solved = false;
        ts.startSolve();
        setVisible(true);
        return solved;
    }
}
