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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.current;

public class FoldersPanel extends JComponent implements FolderListener {

    private final FoldersList foldersList;
    private final ScrollBar scrollbar;

    private final JComponent viewport = new JComponent() { };

    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));
    private final Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb8cfe5)),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));

    public FoldersPanel() {

        setForeground(null);
        setFocusable(true);
        setBorder(border);

        foldersList = new FoldersList(this);
        viewport.add(foldersList);
        add(viewport);

        scrollbar = new ScrollBar(ScrollBar.VERTICAL) {
            @Override
            public void onChangePosition() {
                foldersList.setLocation(0, -scrollbar.getPosition());
            }
        };
        add(scrollbar);

        addMouseWheelListener((MouseWheelEvent e) -> {
            if (scrollbar.isVisible() && scrollbar.isActive()) {
                if (e.getWheelRotation() > 0) {
                    scrollbar.setPosition(scrollbar.getPosition() + foldersList.getItemHeight());
                } else {
                    scrollbar.setPosition(scrollbar.getPosition() - foldersList.getItemHeight());
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_KP_UP:
                        foldersList.setPreviousItem();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        foldersList.setNextItem();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        if (e.isControlDown()) {
                            foldersList.setFirstItem();
                        } else {
                            foldersList.setPreviousPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        if (e.isControlDown()) {
                            foldersList.setLastItem();
                        } else {
                            foldersList.setNextPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        chooseFolder(foldersList.getCurrentItem());
                        break;
                }
                scrollbar.scrollToComponent(foldersList.getCurrentItem());
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(focusedBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(border);
            }
        }); // addFocusListener
        repaint();
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        updateComponents(w, h);
    }

    private void updateComponents(int w, int h) {
        w = w - 4;
        h = h - 4;

        scrollbar.setValues(0, foldersList.getHeight(), foldersList.getItemHeight(), h);

        int sbV_width = (scrollbar.isVisible()) ? 11 : 0;

        scrollbar.setLocation(w - sbV_width + 2, 2);
        viewport.setLocation(2, 2);

        scrollbar.setSize(sbV_width, h);
        viewport.setSize(w - sbV_width, h);
        foldersList.setSize(viewport.getWidth(), h);

        repaint();
    }

    private void updateComponents() {
        updateComponents(getWidth(), getHeight());
    }

    public int getItemHeight() {
        return foldersList.getItemHeight();
    }

    public void chooseFolder(FileItem item) {
        osPan.showFoldersPanel(false);
        current.setFolder(item.getFile());
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            foldersList.setCurrentItem(foldersList.findFolder(current.getFolder()));
            if (scrollbar.isVisible()) {
                scrollbar.setPosition(foldersList.getCurrentItem().getY());
            }
        }
        super.setVisible(b);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    @Override
    public void updateFolder(File folder) {
        boolean b = scrollbar.isVisible();
        foldersList.setFolder(folder);
        scrollbar.setValues(0, foldersList.getHeight(), foldersList.getItemHeight(), viewport.getHeight());
        scrollbar.onChangePosition();
        if (scrollbar.isVisible() != b)
            updateComponents();

    }
}
