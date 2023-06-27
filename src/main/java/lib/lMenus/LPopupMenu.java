package lib.lMenus;

import gui.Palette;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * The Popup menu with this project's decoration.
 */
public class LPopupMenu extends JPopupMenu {
    private final Border menuBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(2, 2, 2, 2));
    private final Border itemBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 0, 0),
            BorderFactory.createEmptyBorder(3, 3, 3, 3));

    /**
     * Creates the popup menu with this project's decoration.
     */
    public LPopupMenu() {
        setBackground(Palette.dialogColor);
        setForeground(Color.white);
        setBorder(menuBorder);
    }

    /**
     * Creates the menu item that has a submenu and decorates it.
     * @param parent the parent menu item
     * @param text this menu item's caption
     * @return menu instance
     */
    @SuppressWarnings("MagicConstant")
    public JMenu addMenu(JMenu parent, String text) {
        JMenu menu = new JMenu(text);
        menu.setBackground(Palette.dialogColor);
        menu.setForeground(Color.white);
        menu.setBorder(itemBorder);
        menu.setFont(menu.getFont().deriveFont(0)); // Font.PLAIN
        menu.getPopupMenu().setBackground(Palette.dialogColor);
        menu.getPopupMenu().setForeground(Color.white);
        menu.getPopupMenu().setBorder(menuBorder);
        if (parent != null) {
            parent.add(menu);
        } else {
            this.add(menu);
        }
        return menu;
    }

    /**
     * Creates the menu item and decorates it.
     * @param parent the parent menu item
     * @param text this menu item's caption
     * @return menu item instance
     */
    @SuppressWarnings("MagicConstant")
    public JMenuItem addMenuItem(JMenu parent, String text) {
        JMenuItem menu = new JMenuItem(text);
        menu.setBackground(Palette.dialogColor);
        menu.setForeground(Color.white);
        menu.setBorder(itemBorder);
        menu.setIcon(null);
        menu.setFont(menu.getFont().deriveFont(0)); // Font.PLAIN
        if (parent != null) {
            parent.add(menu);
        } else {
            this.add(menu);
        }
        return menu;
    }

    /**
     * Creates the menu separator and decorates it.
     * @param parent the parent menu item
     * @return separator instance
     */
    public JSeparator addSeparator(JMenu parent) {
        JSeparator sep = new JSeparator();
        sep.setBackground(Palette.dialogColor);
        sep.setForeground(Color.gray);
        if (parent != null) {
            parent.add(sep);
        } else {
            this.add(sep);
        }
        return sep;
    }

}
