/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */
package lib.lOpenSaveDialog;

import gui.Palette;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import core.ResStrings;
import lib.lButtons.LToolButton;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;

public class ToolButtonsPanel extends JComponent {

    LToolButton btnUp;
    LToolButton btnRefresh;
    LToolButton btnFolder;
    int sep = 7;

    public ToolButtonsPanel() {
        btnUp = addButton(0, "up", ResStrings.getString("strFolderUp"));
        btnUp.addActionListener((ActionEvent e) -> buttonClick(1));

        btnRefresh = addButton(1, "refresh", ResStrings.getString("strRefresh"));
        btnRefresh.addActionListener((ActionEvent e) -> buttonClick(2));

        btnFolder = addButton(2, "newfolder", ResStrings.getString("strNewFolder"));
        btnFolder.addActionListener((ActionEvent e) -> buttonClick(3));

        setSize(btnUp.getWidth() * 3 + sep * 4, btnUp.getHeight());
    }

    public LToolButton addButton(int number, String imgFName, String hintText) {
        LToolButton btn = new LToolButton(this, "btnTool22", imgFName);
        btn.setColorEnabled(new Color(184, 207, 229));
        btn.setColorHover(Color.WHITE);
        btn.setColorPressed(new Color(0xaaaaaa));
        btn.setFocusable(true);
        btn.setLocation(number * (sep + btn.getWidth()) + sep, 0);

        btn.setToolTipBackground(Palette.dialogColor);
        btn.setToolTipForeground(Color.WHITE);
        btn.setToolTipBorder(new Color(0xb8cfe5));

        btn.setToolTipText(hintText);

        add(btn);
        return btn;
    }

    @Override
    public void setFocusable(boolean b) {
        btnUp.setFocusable(b);
        btnRefresh.setFocusable(b);
        btnFolder.setFocusable(b);
    }

    public void buttonClick(int btnNumber) {
        if (osPan.isFoldersVisible()) {
            osPan.showFoldersPanel(false);
        } else {
            switch (btnNumber) {
                case 1:
                    osPan.upFolder();
                    break;
                case 2:
                    osPan.folderRefresh();
                    break;
                case 3:
                    osPan.createNewFolder();
            }
        }
    }

}
