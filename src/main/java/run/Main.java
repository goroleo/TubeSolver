/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package run;

import core.Options;
import core.ResStrings;
import gui.MainFrame;

import static java.awt.EventQueue.invokeLater;

/**
 * Runnable class of the application
 */
public class Main {

    /**
     * Application's main frame
     */
    public static MainFrame frame;

    /**
     * Just create and run main frame
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options.loadOptions();
        ResStrings.setBundle(Options.langCode);

        frame = new MainFrame();
        invokeLater(frame::showFrame);
    }
}
