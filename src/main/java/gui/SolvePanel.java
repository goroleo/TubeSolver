/*
 * Copyright (c) 2023 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package gui;

import ani.*;
import core.BoardModel;
import core.ResStrings;
import core.Solver;
import lib.lButtons.LPictureButton;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * The panel that is displayed when a solution is being searched.
 */
public class SolvePanel extends JComponent {

    /**
     * The solver of the game.
     */
    private static Solver ts;

    /**
     * The layer that blurs the MainFrame's content .
     */
    private final BlurLayer blur;

    /**
     * A spinning circle showing the application busyness.
     */
    private static final WheelLayer wheel = new WheelLayer();

    /**
     * The button to cancel calculation solve.
     */
    private static LPictureButton btn;

    /**
     * How much tries before we'll break the solving and start it again with the new color
     */
    private int breakCount = 100000;

    /**
     * The result of the solve process. Results can be as follows: <ul>
     * <li> 3 - the game is solved.
     * <li> 2 - the game is not solved (solution is not found).
     * <li> 1 - escape / cancel pressed while solver is working.
     * <li> 0 - still working.
     * </ul>
     */
    public int solveResult = 0;

    /**
     * Creates the SolvePanel.
     */
    public SolvePanel() {

        setVisible(false);
        add(wheel);

        btn = new LPictureButton(this, "btnDialog");
        btn.setText(ResStrings.getString("strCancel"));
        btn.setFocusable(true);
        btn.addActionListener((ActionEvent e) -> stopSolver(1));
        btn.setVisible(false);
        add(btn);

        blur = new BlurLayer() {
            public void onThreadFinished(boolean appearing) {
                onBlurThreadFinished(appearing);
            }
        };
        add(blur);

        // ESCAPE pressed
        registerKeyboardAction(
                (ActionEvent e) -> stopSolver(1),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

    }

    /**
     * Starts the solve process.
     *
     * @param startBoard - the current position at the game board that applies as the start combination.
     */
    public void startSolve(BoardModel startBoard) {

        setBounds(Main.frame.getContentPane().getBounds());

        BufferedImage img = new BufferedImage(
                Main.frame.getContentPane().getWidth(), Main.frame.getContentPane().getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Main.frame.getContentPane().paint(img.getGraphics());
        blur.setImage(img);

        int h = wheel.getHeight() + btn.getHeight() + 30;
        Rectangle r = Main.frame.getTubesArea();

        wheel.setLocation(r.x + (r.width - wheel.getWidth()) / 2,
                r.y + (r.height - h) / 2);
        btn.setLocation(r.x + (r.width - btn.getWidth()) / 2,
                r.y + (r.height + h) / 2 - btn.getHeight());

        blur.setVisible(true);
        setVisible(true);
        blur.startBlur();

        breakCount = 100000;

        ts = new Solver(startBoard, breakCount) {
            @Override
            public void onSolved() {
                stopSolver(3);
            }

            @Override
            public void onNotSolved() {
                breakCount <<= 1;
                if (breakCount > 0) {
                    setStartTubes(startBoard);
                    setBreakStop(breakCount);
                    if (!externalBreak) {
                        startSolve();
                    }
                } else {
                    stopSolver(2);
                }
            }
        };
        ts.startSolve();
    }

    /**
     * Stop solving!
     *
     * @param result result of the solve process
     * @see #solveResult
     */
    public void stopSolver(int result) {

        if (result < 2 && ts != null) {
            // solver is still working, we have to stop it before close.
            ts.stopProcess();
        }

        solveResult = result;
        wheel.stop();
        wheel.setVisible(false);
        btn.setVisible(false);
        blur.startHide();
        Main.frame.endSolve(solveResult);
    }

    /**
     * This routine calls when the blur animation has done.
     *
     * @param b the direction of the animation performed: appearing (blurring) if <i>true</i>, or hiding if <i>false</i>.
     */
    public void onBlurThreadFinished(boolean b) {
        if (b) {
            // after blurring shows the button and the wheel circle
            btn.setVisible(true);
            btn.requestFocus();
            wheel.setVisible(true);
            wheel.start();
        } else {
            // hides the panel
            setVisible(false);
        }
    }

    /**
     * Updates a button caption when the language of the application is changed.
     */
    public void updateLanguage() {
        btn.setText(ResStrings.getString("strCancel"));
    }

}
