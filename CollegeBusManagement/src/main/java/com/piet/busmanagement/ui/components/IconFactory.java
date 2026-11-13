package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/** Vector icons drawn with Java2D, so no image files or icon libraries are required. */
public final class IconFactory {

    public enum Type {
        DASHBOARD, BUS, STUDENT, DRIVER, ROUTE, ASSIGN, ALLOCATE, NOTICE, SETTINGS, LOGOUT,
        SEARCH, ADD, EDIT, DELETE, REFRESH, CLEAR, USER, LOCK, MAIL, PHONE, BADGE, CHECK, POWER
    }

    private IconFactory() {
    }

    public static Icon create(Type type, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.translate(x, y);
                g2.scale(size / 24.0, size / 24.0);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                draw(g2, type);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    /** Window / taskbar icon: a white bus on a blue rounded square. */
    public static Image appImage() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(UITheme.PRIMARY);
        g.fillRoundRect(0, 0, 64, 64, 18, 18);
        create(Type.BUS, 40, Color.WHITE).paintIcon(null, g, 12, 12);
        g.dispose();
        return img;
    }

    private static void line(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private static void draw(Graphics2D g, Type type) {
        switch (type) {
            case DASHBOARD -> {
                g.fill(new RoundRectangle2D.Double(3, 3, 8, 10, 2, 2));
                g.fill(new RoundRectangle2D.Double(13, 3, 8, 6, 2, 2));
                g.fill(new RoundRectangle2D.Double(13, 11, 8, 10, 2, 2));
                g.fill(new RoundRectangle2D.Double(3, 15, 8, 6, 2, 2));
            }
            case BUS -> {
                g.draw(new RoundRectangle2D.Double(3, 4, 18, 14, 4, 4));
                line(g, 3, 11, 21, 11);
                line(g, 12, 4, 12, 11);
                g.fill(new Ellipse2D.Double(5.5, 17.5, 4, 4));
                g.fill(new Ellipse2D.Double(14.5, 17.5, 4, 4));
            }
            case STUDENT -> {
                Path2D cap = new Path2D.Double();
                cap.moveTo(12, 4);
                cap.lineTo(22, 9);
                cap.lineTo(12, 14);
                cap.lineTo(2, 9);
                cap.closePath();
                g.fill(cap);
                Path2D gown = new Path2D.Double();
                gown.moveTo(6, 12.5);
                gown.lineTo(6, 16);
                gown.quadTo(12, 21, 18, 16);
                gown.lineTo(18, 12.5);
                g.draw(gown);
            }
            case DRIVER -> {
                g.draw(new Ellipse2D.Double(3, 3, 18, 18));
                g.draw(new Ellipse2D.Double(9.5, 9.5, 5, 5));
                line(g, 3.5, 12, 9.5, 12);
                line(g, 14.5, 12, 20.5, 12);
                line(g, 12, 14.5, 12, 21);
            }
            case ROUTE -> {
                g.draw(new Ellipse2D.Double(3.5, 15.5, 5, 5));
                g.fill(new Ellipse2D.Double(15.5, 3.5, 5, 5));
                Path2D p = new Path2D.Double();
                p.moveTo(6, 15.5);
                p.curveTo(6, 9, 18, 15, 18, 8.5);
                g.draw(p);
            }
            case ASSIGN -> {
                line(g, 4, 8, 20, 8);
                line(g, 16, 4, 20, 8);
                line(g, 16, 12, 20, 8);
                line(g, 20, 16, 4, 16);
                line(g, 8, 12, 4, 16);
                line(g, 8, 20, 4, 16);
            }
            case ALLOCATE -> {
                g.draw(new Ellipse2D.Double(5, 4, 8, 8));
                g.draw(new Arc2D.Double(2, 14, 14, 12, 0, 180, Arc2D.OPEN));
                line(g, 19, 6, 19, 12);
                line(g, 16, 9, 22, 9);
            }
            case NOTICE -> {
                Path2D bell = new Path2D.Double();
                bell.moveTo(5, 17);
                bell.curveTo(7, 15, 7, 13, 7, 10);
                bell.curveTo(7, 6.5, 9.5, 5, 12, 5);
                bell.curveTo(14.5, 5, 17, 6.5, 17, 10);
                bell.curveTo(17, 13, 17, 15, 19, 17);
                bell.closePath();
                g.draw(bell);
                line(g, 10, 20.5, 14, 20.5);
            }
            case SETTINGS -> {
                g.draw(new Ellipse2D.Double(8.5, 8.5, 7, 7));
                g.draw(new Ellipse2D.Double(5.5, 5.5, 13, 13));
                for (int i = 0; i < 8; i++) {
                    double a = Math.toRadians(i * 45);
                    line(g, 12 + 6.5 * Math.cos(a), 12 + 6.5 * Math.sin(a),
                            12 + 9.5 * Math.cos(a), 12 + 9.5 * Math.sin(a));
                }
            }
            case LOGOUT -> {
                Path2D door = new Path2D.Double();
                door.moveTo(10, 4);
                door.lineTo(5, 4);
                door.lineTo(5, 20);
                door.lineTo(10, 20);
                g.draw(door);
                line(g, 9, 12, 21, 12);
                line(g, 17, 8, 21, 12);
                line(g, 17, 16, 21, 12);
            }
            case SEARCH -> {
                g.draw(new Ellipse2D.Double(4, 4, 12, 12));
                line(g, 14.5, 14.5, 20, 20);
            }
            case ADD -> {
                line(g, 12, 5, 12, 19);
                line(g, 5, 12, 19, 12);
            }
            case EDIT -> {
                Path2D pencil = new Path2D.Double();
                pencil.moveTo(4, 20);
                pencil.lineTo(5, 15);
                pencil.lineTo(16, 4);
                pencil.lineTo(20, 8);
                pencil.lineTo(9, 19);
                pencil.closePath();
                g.draw(pencil);
                line(g, 13.5, 6.5, 17.5, 10.5);
            }
            case DELETE -> {
                line(g, 4, 6.5, 20, 6.5);
                line(g, 9, 6.5, 9, 4);
                line(g, 9, 4, 15, 4);
                line(g, 15, 4, 15, 6.5);
                g.draw(new RoundRectangle2D.Double(6, 8.5, 12, 11.5, 3, 3));
                line(g, 10, 11.5, 10, 17);
                line(g, 14, 11.5, 14, 17);
            }
            case REFRESH -> {
                g.draw(new Arc2D.Double(5, 5, 14, 14, 40, 280, Arc2D.OPEN));
                line(g, 17.5, 3.5, 17.5, 8);
                line(g, 17.5, 8, 13, 8);
            }
            case CLEAR -> {
                line(g, 6, 6, 18, 18);
                line(g, 18, 6, 6, 18);
            }
            case USER -> {
                g.draw(new Ellipse2D.Double(8, 3.5, 8, 8));
                g.draw(new Arc2D.Double(4, 14, 16, 14, 0, 180, Arc2D.OPEN));
            }
            case LOCK -> {
                g.draw(new RoundRectangle2D.Double(5, 11, 14, 9, 3, 3));
                g.draw(new Arc2D.Double(8, 3.5, 8, 10, 0, 180, Arc2D.OPEN));
                line(g, 8, 8.5, 8, 11);
                line(g, 16, 8.5, 16, 11);
            }
            case MAIL -> {
                g.draw(new RoundRectangle2D.Double(3, 5, 18, 14, 3, 3));
                line(g, 3.5, 7, 12, 13);
                line(g, 20.5, 7, 12, 13);
            }
            case PHONE -> {
                g.draw(new RoundRectangle2D.Double(7, 2.5, 10, 19, 3, 3));
                line(g, 10.5, 18.5, 13.5, 18.5);
            }
            case BADGE -> {
                g.draw(new RoundRectangle2D.Double(3, 5, 18, 14, 3, 3));
                g.draw(new Ellipse2D.Double(6, 8.5, 4, 4));
                line(g, 13, 9, 18, 9);
                line(g, 13, 13, 18, 13);
                line(g, 6, 16, 10, 16);
            }
            case CHECK -> {
                line(g, 5, 12.5, 10, 17.5);
                line(g, 10, 17.5, 19, 7);
            }
            case POWER -> {
                g.draw(new Arc2D.Double(4.5, 5, 15, 15, 120, 300, Arc2D.OPEN));
                line(g, 12, 3, 12, 11);
            }
        }
    }
}
