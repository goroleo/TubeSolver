/*
 * Copyright (c) 2021 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package core;

import static gui.MainFrame.gameMoves;
import static gui.MainFrame.movesDone;

/**
 * The solver is the solver. It passes through the moves' tree  and finds a solution to the game. Or does not find.
 */
public class Solver implements Runnable {

    /**
     * Current game board with some color tubes
     */
    private BoardModel board;


    /**
     * If <i>true</i> we have limited tries to solve the board and then to we have to stop and start with another color.
     * If <i>false</i> we'll get unlimited tries.
     */
    private boolean useBreak;

    /**
     * How much tries before we'll break the solving and start it again with the new color
     */
    private int breakStop;

    /**
     * the time (in milliseconds) between start and end
     */
    public double workingTime;

    /**
     * It becomes <i>true</i> when user has pressed the BREAK key.
     */
    public boolean externalBreak;

    /**
     * Constructor of the class Solver
     *
     * @param startBoard the start configuration of the tubes board
     * @param breakStop  how much tries before we'll break the solving and start it again with the new color
     */
    public Solver(BoardModel startBoard, int breakStop) {
        setStartTubes(startBoard);
        setBreakStop(breakStop);
        workingTime = 0;
    }

    /**
     * Sets the start tubes board
     *
     * @param startBoard the start configuration of the tubes board
     */
    public final void setStartTubes(BoardModel startBoard) {
        // prepare new board model 
        if (board == null) {
            board = new BoardModel();
            board.root = board;
        } else {
            board.clear();
        }

        // copying board tubes into new model 
        for (int i = 0; i < startBoard.size(); i++) {
            board.addNewTube();
            board.get(i).assignColors(startBoard.get(i));
        }
    }

    /**
     * Sets the breakStop
     *
     * @param breakStop how much tries before we'll break the solving and start it again with the new color
     * @see #breakStop
     * @see #useBreak
     */
    public final void setBreakStop(int breakStop) {
        useBreak = breakStop > 0;
        this.breakStop = breakStop;
    }

    /**
     * Runs the solve process / thread.
     */
    public void startSolve() {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Breaks the solve process
     */
    public void stopProcess() {
        externalBreak = true;
    }

    @Override
    public void run() {

        // myCount - counts all processed moves
        // used at final output only
//        long myCount = 0;

        // breakCount - counts all processed moves after the last break
        int breakCount = 0;

        // solved or not solved - this is a question
        boolean solved = false;

        // external break is true if a user interrupts process
        externalBreak = false;

        // time when the routine starts
        long startTime = System.currentTimeMillis();

        // initial values 
        board.calculateMoves();
        ColorMoveItem move = board.currentMove;

        if (move != null) { // if this board has any moves 

            do {

                // doMove returns true if the move was successful and false otherwise. 
                if (move.doMove()) {

                    // Successful (true) means we have a continuation.
                    // So we'll put this move to the stack and go with a new combination
                    // counts
                    // myCount++; // not used
                    breakCount++;

                    // now we'll go with a new tubes configuration that we got after the move
                    board = move.bmAfter;

                    // is it solved already?
                    solved = board.isSolved();

                    if (!solved) {
                        // our next move will be the best move of a new board
                        move = board.currentMove;
                    }  // else the cycle will be finished

                } else { // move.doMove() == false

                    // doMove wasn't successful due to any reason (no continue, repeated combination etc.)
                    // counts
                    // myCount++; // not used
                    breakCount++;

                    // First we'll check is there enough to count this starting color 
                    if (useBreak && (breakCount >= breakStop)) {
                        breakCount = 0;

                        // Return to the beginning... 
                        board = board.root;
                        byte curColor = board.currentMove.color;

                        // And search for the new color to start 
                        while (curColor == board.currentMove.color) {
                            board.deleteMove(board.currentMove);
                            if (board.currentMove == null) {
                                break;
                            }
                        }
                        // next move will be the best of the rest move of the root board
                        move = board.currentMove;

                    } else { // move.doMove() == false && breakCount < breakStop

                        // doMove wasn't successful, and we have no reasons to start with a new color.
                        // So:  
                        board = move.bmBefore;

                        do {
                            // delete current move from moves array 
                            board.deleteMove(board.currentMove);

                            // next move will be next of moves array 
                            move = board.currentMove;

                            // if tubes have not any moves...
                            if (move == null) {

                                // we'll try with parent tubes 
                                board = board.parent;

                                // and if tubes have no parent...
                                if (board == null) {

                                    // then we have to stop
                                    break;
                                }
                            }
                        } while (move == null);
                    }
                }

            } while (!solved && !externalBreak && move != null);

        }

        workingTime = (double) System.currentTimeMillis() - startTime + workingTime;

        if (solved) {

            // save statistics
            Options.solverTimeLast = workingTime / 1000;
            if (Options.solverTimeMax < Options.solverTimeLast)
                Options.solverTimeMax = Options.solverTimeLast;
            Options.solverTimeAvg = (Options.solverTimeAvg * Options.numSolverSuccess
                    + Options.solverTimeLast) / (Options.numSolverSuccess + 1);

            // place solution to the gameMoves array
            do {
                gameMoves.add(movesDone, move.storeMove());
                move = move.parent;
            } while (move != null);

//            System.out.println("Solved! " + "Count: " + myCount);
//            System.out.println("Time: " + workingTime + " ms");
//            System.out.println("Break: " + breakStop);

            // runs an external procedure if anyone was override it
            onSolved();

        } else { // NOT SOLVED !!!! 
            if (externalBreak) {

                // runs an external procedure if anyone was override it
                onExternalBreak();

            } else {
//                System.out.println("NOT SOLVED. Try another parameters and/or change the break value.");
//                System.out.println("Time: " + workingTime + " ms");
//                System.out.println("Count: " + myCount);
//                System.out.println("Break: " + breakStop);

                // runs an external procedure if anyone was override it
                onNotSolved();
            }

        }
    }

    /**
     * Calls when the Solver was finished successfully. The routine to override it.
     */
    public void onSolved() {
        // the routine to override
    }

    /**
     * Calls when the Solver was interrupted by the user. The routine to override it.
     */
    @SuppressWarnings("EmptyMethod")
    public void onExternalBreak() {
        // the routine to override
    }

    /**
     * Calls when the Solver was finished unsuccessfully. The routine to override it.
     */
    public void onNotSolved() {
        // the routine to override
    }

}
