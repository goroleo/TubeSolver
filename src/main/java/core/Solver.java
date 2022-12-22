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

public class Solver implements Runnable {

    private BoardModel tubes;
    

    // if false we'll get an unlimited 
    private boolean useBreak;
    
    // how much tries before we'll break the solving and start it again with the new color
    private int breakStop;
    
    // 
    public double workingTime;
    
    // true if user has pressed the BREAK key 
    public boolean externalBreak;

    public Solver(BoardModel startTubes, int breakStop) {
        setStartTubes(startTubes);
        setBreakStop(breakStop);
        workingTime = 0;
    }

    public final void setStartTubes(BoardModel startTubes) {
        // prepare new board model 
        if (tubes == null) {
            tubes = new BoardModel();
            tubes.root = tubes;
        } else {
            tubes.clear();
        }
        
        // copying board tubes into new model 
        for (int i = 0; i < startTubes.size(); i++) {
            tubes.addNewTube();
            tubes.get(i).assignColors(startTubes.get(i));
        }
    }

    public final void setBreakStop(int breakStop) {
        useBreak = breakStop > 0;
        this.breakStop = breakStop;
    }

    public void startSolve() {
        Thread t = new Thread(this);
        t.start();
    }

    public void stopProcess() {
        externalBreak = true;
    }

    @Override
    public void run() {

        // myCount - counts all processed moves 
        long myCount = 0;

        // breakCount - counts all processed moves after the last break
        int breakCount = 0;

        // solved or not solved - this is a question
        boolean solved = false;

        // external break is true if a user interrupts process
        externalBreak = false;

        // time when the routine starts
        long startTime = System.currentTimeMillis();

        // initial values 
        tubes.calculateMoves();
        ColorMoveItem move = tubes.currentMove;

        if (move != null) { // if this board has any moves 

            do {

                // doMove returns true if the move was successful and false otherwise. 
                if (move.doMove()) {

                    // Successful (true) means we have a continue. 
                    // So we'll put this move to the stack and go with a new combination
                    // counts
                    myCount++;
                    breakCount++;

                    // now we'll go with a new tubes configuration that we got after the move
                    tubes = move.bmAfter;

                    // is it solved already?
                    solved = tubes.isSolved();

                    if (!solved) {
                        // our next move will be the best move of a new board
                        move = tubes.currentMove;
                    } else {
                        // !!! SOLVED !!! 
                    }

                } else { // move.doMove() == false

                    // doMove wasn't successful due to any reason (no continue, repeated combination etc.)
                    // counts
                    myCount++;
                    breakCount++;

                    // First we'll check is there enough to count this starting color 
                    if (useBreak && (breakCount >= breakStop)) {
                        breakCount = 0;

                        // Return to the beginning... 
                        tubes = tubes.root;
                        byte curColor = tubes.currentMove.color;

                        // And search for the new color to start 
                        while (curColor == tubes.currentMove.color) {
                            tubes.deleteMove(tubes.currentMove);
                            if (tubes.currentMove == null) {
                                break;
                            }
                        }
                        // next move will be the best of the rest move of the root board
                        move = tubes.currentMove;

                    } else { // move.doMove() == false && breakCount < breakStop

                        // doMove wasn't successful, and we have no reasons to start with a new color.
                        // So:  
                        tubes = move.bmBefore;

                        do {
                            // delete current move from moves array 
                            tubes.deleteMove(tubes.currentMove);

                            // next move will be next of moves array 
                            move = tubes.currentMove;

                            // if tubes have not any moves...
                            if (move == null) {

                                // we'll try with parent tubes 
                                tubes = tubes.parent;

                                // and if tubes have no parent...
                                if (tubes == null) {

                                    // then we have to stop
                                    break;
                                }
                            }
                        } while (move == null);
                    }
                }

            } while (!solved && !externalBreak && move != null);

        } else {
            // TODO: out error no moves 
        }

        workingTime = (double) System.currentTimeMillis() - startTime + workingTime;

        if (solved) {

            // place solution to the gameMoves array
            do {
                gameMoves.add(movesDone, move.storeMove());
                move = move.parent;
            } while (move != null);
            
            System.out.println("Solved! " + "Count: " + myCount);
            System.out.println("Time: " + workingTime + " ms");
            System.out.println("Break: " + breakStop);

            onSolved();

        } else { // NOT SOLVED !!!! 
            if (externalBreak) {

                onExternalBreak();

            } else {
                System.out.println("NOT SOLVED. Try another parameters and/or change the break value.");
                System.out.println("Time: " + workingTime + " ms");
                System.out.println("Count: " + myCount);
                System.out.println("Break: " + breakStop);
                onNotSolved();
            }

        }
    }

    public void onSolved() {

    }

    public void onExternalBreak() {

    }

    public void onNotSolved() {

    }

}
