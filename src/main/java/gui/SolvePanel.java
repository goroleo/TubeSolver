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

import ani.BlurLayer;
import ani.WheelLayer;
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
    private static Solver tubeSolver;

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
    private static LPictureButton button;

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

        button = new LPictureButton(this, "btnDialog");
        button.setText(ResStrings.getString("strCancel"));
        button.setFocusable(true);
        button.addActionListener((ActionEvent e) -> stopSolver(1));
        button.setVisible(false);
        add(button);

        blur = new BlurLayer() {

            @Override
            public void onThreadFinished(boolean appeared) {
                if (appeared) {
                    // after blurring shows the button and the wheel circle
                    button.setVisible(true);
                    button.requestFocus();
                    wheel.setVisible(true);
                    wheel.start();
                } else {
                    // hides the panel
                    SolvePanel.this.setVisible(false);
                }
            }
        };
        add(blur);

        // ESCAPE pressed
        registerKeyboardAction(
                (ActionEvent e) -> stopSolver(1),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW
    }

    /**
     * Starts the solve process.
     *
     * @param startBoard - the current position at the game board that applies as the start combination.
     */
    public void startSolve(BoardModel startBoard) {

        BufferedImage img = new BufferedImage(
                Main.frame.getContentPane().getWidth(),
                Main.frame.getContentPane().getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Main.frame.getLayeredPane().paint(img.getGraphics());
        blur.setImage(img);

        updateSizeAndPos();

        setVisible(true);
        blur.startBlur();

        breakCount = 100000;

        tubeSolver = new Solver(startBoard, breakCount) {
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
        tubeSolver.startSolve();
    }

    /**
     * Stop solving!
     *
     * @param result result of the solve process
     * @see #solveResult
     */
    public void stopSolver(int result) {

        if (result < 2 && tubeSolver != null) {
            // solver is still working, we have to stop it before close.
            tubeSolver.stopProcess();
        }

        solveResult = result;
        wheel.stop();
        wheel.setVisible(false);
        button.setVisible(false);
        blur.startHide();
        Main.frame.endSolve(solveResult);
    }

    /**
     * Updates a button caption when the language of the application is changed.
     */
    public void updateLanguage() {
        button.setText(ResStrings.getString("strCancel"));
    }

    /**
     * Updates components size and location when MainFrame is being resized.
     */
    public void updateSizeAndPos() {
        setBounds(Main.frame.getContentPane().getBounds());

        int h = wheel.getHeight() + button.getHeight() + 30;
        Rectangle r = Main.frame.getTubesArea();

        wheel.setLocation(r.x + (r.width - wheel.getWidth()) / 2,
                r.y + (r.height - h) / 2);
        button.setLocation(r.x + (r.width - button.getWidth()) / 2,
                r.y + (r.height + h) / 2 - button.getHeight());

        blur.setSize(getSize());
    }
}
