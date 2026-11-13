package com.piet.busmanagement.config;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Colour palette, fonts and global look-and-feel setup. */
public final class UITheme {

    // Deep navy + electric blue + cyan on light cards
    public static final Color NAVY        = new Color(0x0B1F3A);
    public static final Color NAVY_MID    = new Color(0x12305A);
    public static final Color NAVY_LIGHT  = new Color(0x1B4278);
    public static final Color PRIMARY     = new Color(0x2563EB);
    public static final Color CYAN        = new Color(0x06B6D4);
    public static final Color BG          = new Color(0xF1F5F9);
    public static final Color CARD        = Color.WHITE;
    public static final Color TEXT        = new Color(0x0F172A);
    public static final Color MUTED       = new Color(0x64748B);
    public static final Color BORDER      = new Color(0xE2E8F0);
    public static final Color SUCCESS     = new Color(0x16A34A);
    public static final Color DANGER      = new Color(0xDC2626);
    public static final Color WARNING     = new Color(0xF59E0B);
    public static final Color SIDEBAR_TEXT = new Color(0xB8C7DE);

    private static final String FAMILY = pickFamily();

    private UITheme() {
    }

    private static String pickFamily() {
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String name : new String[]{"Segoe UI", "Helvetica Neue", "Inter", "Roboto", "Ubuntu"}) {
            if (available.contains(name)) {
                return name;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font font(int style, float size) {
        return new Font(FAMILY, style, 1).deriveFont(style, size);
    }

    public static Color alpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    /** Call once at start-up, before any window is created. */
    public static void install() {
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#2563EB"));
        FlatLightLaf.setup();
        UIManager.put("defaultFont", font(Font.PLAIN, 14f));
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("TextField.margin", new Insets(6, 8, 6, 8));
        UIManager.put("PasswordField.margin", new Insets(6, 8, 6, 8));
        UIManager.put("TextArea.margin", new Insets(6, 8, 6, 8));
        UIManager.put("ComboBox.padding", new Insets(5, 8, 5, 8));
        UIManager.put("OptionPane.messageFont", font(Font.PLAIN, 14f));
        UIManager.put("OptionPane.buttonFont", font(Font.BOLD, 13f));
    }
}
