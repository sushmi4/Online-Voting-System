import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.geom.RoundRectangle2D;

/**
 * Central design system: palette, code-drawn gradient background, rounded
 * buttons, fields, cards, pills, icons, tables, scrollbars and dialogs.
 */
public final class UITheme {

    // ================= PALETTE =================
    public static final Color PRIMARY = new Color(0x1E293B);       // slate-800
    public static final Color PRIMARY_DARK = new Color(0x0F172A);   // slate-900
    public static final Color ACCENT = new Color(0x2563EB);         // blue-600
    public static final Color ACCENT_HOVER = new Color(0x1D4ED8);   // blue-700
    public static final Color ACCENT_LIGHT = new Color(0xEFF6FF);   // blue-50
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_MUTED = new Color(0xF1F5F9);  // slate-100
    public static final Color TABLE_STRIPE = new Color(0xF8FAFC);   // slate-50 (table zebra)
    public static final Color BORDER = new Color(0xE2E8F0);         // slate-200
    public static final Color GREEN = new Color(0x059669);          // emerald-600
    public static final Color GREEN_BG = new Color(0xD1FAE5);       // emerald-100
    public static final Color RED = new Color(0xDC2626);            // red-600
    public static final Color RED_BG = new Color(0xFEE2E2);         // red-100
    public static final Color WARNING = new Color(0xD97706);        // amber-600
    public static final Color WARNING_BG = new Color(0xFEF3C7);     // amber-100
    public static final Color TEXT_DARK = new Color(0x0F172A);      // slate-900
    public static final Color TEXT_MUTED = new Color(0x64748B);     // slate-500
    public static final Color WHITE = Color.WHITE;
    public static final Color SIDEBAR = new Color(0xF1F5F9);        // slate-100 (light)
    public static final Color SIDEBAR_ITEM = new Color(0x475569);    // slate-600
    public static final Color GRAD_TOP = new Color(0xC7D2FE);        // indigo-200
    public static final Color GRAD_BOTTOM = new Color(0xF8FAFC);     // slate-50

    public static final int CORNER = 14;

    private UITheme() {
    }

    // ================= FONTS =================
    public static Font font(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    public static JLabel label(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        return l;
    }

    // ================= BACKGROUND =================
    /** Panel that paints a modern light indigo->white gradient with soft accents. */
    public static class GradientBackground extends JPanel {
        public GradientBackground() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, GRAD_TOP, 0, h, GRAD_BOTTOM));
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(59, 130, 246, 30));
            g2.fillOval(-180, -140, 460, 460);
            g2.setColor(new Color(129, 140, 248, 28));
            g2.fillOval(w - 340, h - 320, 520, 520);
            g2.setColor(new Color(56, 189, 248, 24));
            g2.fillOval(w / 2 - 260, h / 2 - 220, 420, 420);
            g2.dispose();
        }
    }

    /** Gradient top bar used on dashboards. */
    public static class HeaderBar extends JPanel {
        public HeaderBar() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), 0, new Color(0x1D4ED8)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ================= BUTTONS =================
    private static Color hoverFor(Color c) {
        if (c.equals(ACCENT)) return ACCENT_HOVER;
        if (c.equals(GREEN)) return new Color(0x047857);
        if (c.equals(RED)) return new Color(0xB91C1C);
        if (c.equals(WARNING)) return new Color(0xB45309);
        if (c.equals(PRIMARY)) return new Color(0x334155);
        return c.brighter();
    }

    private static Color darkerFor(Color c) {
        if (c.equals(ACCENT)) return new Color(0x1E40AF);
        return c.darker();
    }

    public static class RoundedButton extends JButton {
        private final Color base, hover, pressed;
        private final boolean filled;

        public RoundedButton(String text, Color base, boolean filled) {
            super(text);
            this.base = base;
            this.hover = hoverFor(base);
            this.pressed = darkerFor(base);
            this.filled = filled;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(font(Font.BOLD, 14));
            setForeground(filled ? WHITE : base);
            setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int arc = Math.min(CORNER, Math.max(0, h));
            Color bg = base;
            if (getModel().isPressed()) {
                bg = pressed;
            } else if (getModel().isRollover()) {
                bg = hover;
            }
            if (filled) {
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
            } else {
                boolean hovered = getModel().isRollover() || getModel().isPressed();
                g2.setColor(hovered
                        ? new Color(base.getRed(), base.getGreen(), base.getBlue(), 34)
                        : new Color(255, 255, 255, 0));
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                g2.setColor(bg);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = Math.max(d.height, 38);
            return d;
        }
    }

    /** Flat text button with a soft translucent hover pill (navbar links). */
    public static class TextButton extends JButton {
        private final Color fg;

        public TextButton(String text) {
            this(text, PRIMARY_DARK);
        }

        public TextButton(String text, Color fg) {
            super(text);
            this.fg = fg;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(font(Font.BOLD, 13));
            setForeground(fg);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isRollover() || getModel().isPressed()) {
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JButton button(String text) {
        return new RoundedButton(text, ACCENT, true);
    }

    public static JButton button(String text, Color bg) {
        return new RoundedButton(text, bg, true);
    }

    public static JButton outlineButton(String text, Color fg) {
        return new RoundedButton(text, fg, false);
    }

    // ================= FIELDS =================
    /** Rounded border that can be applied to any component. */
    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        public RoundBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }

    public static class RoundedTextField extends JTextField {
        private boolean focused = false;

        public RoundedTextField(int cols) {
            this("", cols);
        }

        public RoundedTextField(String text) {
            this(text, 0);
        }

        public RoundedTextField(String text, int cols) {
            super(text, cols);
            setOpaque(false);
            setFont(font(Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    focused = true;
                    repaint();
                }

                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(SURFACE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER, CORNER);
            g2.setColor(focused ? ACCENT : BORDER);
            g2.setStroke(new BasicStroke(focused ? 1.8f : 1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER, CORNER);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class RoundedPasswordField extends JPasswordField {
        private boolean focused = false;
        private final boolean eyeToggle;
        private boolean eyeVisible = false;

        public RoundedPasswordField(int cols) {
            this(cols, false);
        }

        public RoundedPasswordField(int cols, boolean eyeToggle) {
            super(cols);
            this.eyeToggle = eyeToggle;
            setOpaque(false);
            setFont(font(Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(9, 12, 9, eyeToggle ? 40 : 12));
            setEchoChar('\u2022');
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    focused = true;
                    repaint();
                }

                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
            if (eyeToggle) {
                installEyeToggle();
            }
        }

        private void installEyeToggle() {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getX() >= getWidth() - 46) {
                        toggleEye();
                    }
                }
            });
            addMouseMotionListener(new MouseAdapter() {
                public void mouseMoved(MouseEvent e) {
                    setCursor(new Cursor(e.getX() >= getWidth() - 46
                            ? Cursor.HAND_CURSOR : Cursor.TEXT_CURSOR));
                }
            });
        }

        private void toggleEye() {
            eyeVisible = !eyeVisible;
            setEchoChar(eyeVisible ? (char) 0 : '\u2022');
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(SURFACE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER, CORNER);
            g2.setColor(focused ? ACCENT : BORDER);
            g2.setStroke(new BasicStroke(focused ? 1.8f : 1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER, CORNER);
            g2.dispose();
            super.paintComponent(g);
            if (eyeToggle) {
                Icon eye = eyeVisible ? Icons.eye(ACCENT) : Icons.eye(TEXT_MUTED);
                int iw = eye.getIconWidth();
                eye.paintIcon(this, g, getWidth() - iw - 12, (getHeight() - eye.getIconHeight()) / 2);
            }
        }
    }

    public static void styleTextArea(JTextArea ta) {
        ta.setFont(font(Font.PLAIN, 13));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setOpaque(false);
        ta.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    // ================= CARDS & PILLS =================
    public static class Card extends JPanel {
        public Card() {
            setOpaque(false);
        }

        public Card(java.awt.LayoutManager lm) {
            super(lm);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(15, 23, 42, 16));
            g2.fillRoundRect(0, 4, getWidth(), getHeight(), 22, 22);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.dispose();
        }
    }

    public static class Pill extends JLabel {
        private final Color bg;

        public Pill(String text, Color fg, Color bg) {
            super(text, SwingConstants.CENTER);
            this.bg = bg;
            setFont(font(Font.BOLD, 12));
            setForeground(fg);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(3, 14, 3, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = Math.max(d.height, 26);
            return d;
        }
    }

    public static Pill pillForStatus(String status) {
        if (status == null) {
            return new Pill("Pending", WARNING, WARNING_BG);
        }
        if ("Approved".equalsIgnoreCase(status)) {
            return new Pill("Approved", GREEN, GREEN_BG);
        }
        if ("Rejected".equalsIgnoreCase(status)) {
            return new Pill("Rejected", RED, RED_BG);
        }
        if ("Voted".equalsIgnoreCase(status)) {
            return new Pill("Voted", ACCENT, ACCENT_LIGHT);
        }
        return new Pill(status, WARNING, WARNING_BG);
    }

    // ================= SIDEBAR =================
    public static class SidebarItem extends JLabel {
        private boolean active;
        private boolean hovered;

        public SidebarItem(String text, Icon icon) {
            super(text, icon, SwingConstants.LEFT);
            setFont(font(Font.PLAIN, 14));
            setForeground(SIDEBAR_ITEM);
            setIconTextGap(14);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            setForeground(active ? WHITE : SIDEBAR_ITEM);
            setFont(font(active ? Font.BOLD : Font.PLAIN, 14));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else if (hovered) {
                g2.setColor(new Color(37, 99, 235, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ================= TABLES =================
    public static class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
            setIcon(null);
            if (value instanceof Icon) {
                setIcon((Icon) value);
                setText("");
            }
            setHorizontalAlignment(value instanceof Icon ? SwingConstants.CENTER : SwingConstants.LEFT);
            if (isSelected) {
                setBackground(ACCENT_LIGHT);
                setForeground(TEXT_DARK);
                int top = (row == 0 || !t.isRowSelected(row - 1)) ? 2 : 0;
                int bottom = (row == t.getRowCount() - 1 || !t.isRowSelected(row + 1)) ? 2 : 0;
                int left = column == 0 ? 2 : 0;
                int right = column == t.getColumnCount() - 1 ? 2 : 0;
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(top, left, bottom, right, ACCENT),
                        BorderFactory.createEmptyBorder(2, 8, 2, 8)));
            } else {
                setBackground(row % 2 == 0 ? SURFACE : TABLE_STRIPE);
                setForeground(TEXT_DARK);
                setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            }
            setFont(font(isSelected ? Font.BOLD : Font.PLAIN, 13));
            return this;
        }
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(54);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ACCENT_LIGHT);
        table.setSelectionForeground(TEXT_DARK);
        table.setFont(font(Font.PLAIN, 13));
        table.setFillsViewportHeight(true);
        table.setBackground(SURFACE);
        table.setDefaultRenderer(Object.class, new ZebraRenderer());
        table.setDefaultRenderer(Icon.class, new ZebraRenderer());

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0xF8FAFC));
        header.setForeground(TEXT_MUTED);
        header.setFont(font(Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }

    public static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(SURFACE);
        sp.getViewport().setOpaque(true);
        sp.setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new ThinScrollBarUI());
    }

    public static class ThinScrollBarUI extends BasicScrollBarUI {
        public ThinScrollBarUI() {
            thumbColor = new Color(0xCBD5E1);
            trackColor = new Color(0xF1F5F9);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private JButton zeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, r.width, r.width);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.dispose();
        }
    }

    // ================= PROGRESS =================
    public static class RoundedProgressBar extends JProgressBar {
        public RoundedProgressBar(int min, int max) {
            super(min, max);
            setOpaque(false);
            setBorderPainted(false);
            setStringPainted(true);
            setForeground(ACCENT);
            setBackground(SURFACE_MUTED);
            setFont(font(Font.BOLD, 11));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, w, h, h, h);
            double pct = getMaximum() > getMinimum()
                    ? (double) getValue() / (double) (getMaximum() - getMinimum())
                    : 0;
            int fillW = Math.max(0, (int) Math.round(w * pct));
            g2.setColor(getForeground());
            g2.fillRoundRect(0, 0, fillW, h, h, h);
            if (isStringPainted()) {
                FontMetrics fm = g2.getFontMetrics(getFont());
                String s = getString();
                int tw = fm.stringWidth(s);
                int tx = Math.max(0, (w - tw) / 2);
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                boolean onFill = tx >= fillW - tw || tx + tw > fillW;
                g2.setColor(onFill && fillW > 0 ? WHITE : TEXT_MUTED);
                g2.drawString(s, tx, ty);
            }
            g2.dispose();
        }
    }

    // ================= ICONS =================
    public static class LineIcon implements Icon {
        private final int width, height;
        private final Color color;
        private final Consumer<Graphics2D> painter;

        public LineIcon(int width, int height, Color color, Consumer<Graphics2D> painter) {
            this.width = width;
            this.height = height;
            this.color = color;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            painter.accept(g2);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }

    public static final class Icons {
        private Icons() {
        }

        public static Icon eye(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawOval(2, 6, 18, 9);
                g.drawOval(9, 8, 4, 4);
                g.fillOval(10, 9, 2, 2);
            });
        }

        public static Icon eyeOff(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawOval(2, 6, 18, 9);
                g.drawOval(9, 8, 4, 4);
                g.fillOval(10, 9, 2, 2);
                g.drawLine(2, 3, 20, 19);
            });
        }

        public static Icon lock(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawRoundRect(5, 11, 13, 9, 3, 3);
                g.drawArc(7, 4, 9, 12, 0, 180);
                g.fillOval(10, 14, 3, 3);
            });
        }

        public static Icon person(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawOval(9, 3, 6, 6);
                g.drawArc(5, 12, 14, 9, 0, 180);
            });
        }

        public static Icon users(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawOval(4, 5, 6, 6);
                g.drawArc(1, 13, 12, 9, 0, 180);
                g.drawOval(14, 7, 6, 6);
                g.drawArc(12, 15, 10, 8, 0, 180);
            });
        }

        public static Icon menu(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawLine(3, 6, 21, 6);
                g.drawLine(3, 12, 21, 12);
                g.drawLine(3, 18, 21, 18);
            });
        }

        public static Icon chevronDown(Color c) {
            return new LineIcon(14, 14, c, g -> {
                g.drawLine(3, 4, 7, 10);
                g.drawLine(11, 4, 7, 10);
            });
        }

        public static Icon home(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawLine(2, 12, 12, 3);
                g.drawLine(22, 12, 12, 3);
                g.drawRoundRect(4, 11, 16, 10, 3, 3);
                g.drawRect(10, 15, 4, 6);
            });
        }

        public static Icon chart(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawLine(3, 21, 21, 21);
                g.drawRoundRect(5, 12, 4, 9, 2, 2);
                g.drawRoundRect(10, 8, 4, 13, 2, 2);
                g.drawRoundRect(15, 14, 4, 7, 2, 2);
            });
        }

        public static Icon list(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawLine(4, 6, 20, 6);
                g.drawLine(4, 12, 20, 12);
                g.drawLine(4, 18, 20, 18);
            });
        }

        public static Icon logout(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawRoundRect(3, 4, 10, 16, 3, 3);
                g.drawLine(15, 4, 22, 12);
                g.drawLine(22, 12, 15, 20);
                g.drawLine(7, 12, 21, 12);
            });
        }

        public static Icon ballot(Color c) {
            return new LineIcon(22, 22, c, g -> {
                g.drawRoundRect(3, 3, 18, 18, 4, 4);
                g.drawPolyline(new int[] { 8, 11, 16 }, new int[] { 12, 15, 9 }, 3);
            });
        }

        public static Icon info(Color c) {
            return new LineIcon(26, 26, c, g -> {
                g.drawOval(3, 3, 20, 20);
                g.drawLine(13, 12, 13, 19);
                g.fillOval(12, 8, 2, 2);
            });
        }

        public static Icon error(Color c) {
            return new LineIcon(26, 26, c, g -> {
                g.drawOval(3, 3, 20, 20);
                g.drawLine(9, 9, 17, 17);
                g.drawLine(17, 9, 9, 17);
            });
        }

        public static Icon warn(Color c) {
            return new LineIcon(26, 26, c, g -> {
                g.drawPolygon(new int[] { 13, 4, 22 }, new int[] { 4, 22, 22 }, 3);
                g.drawLine(13, 11, 13, 17);
                g.fillOval(12, 19, 2, 2);
            });
        }

        public static Icon check(Color c) {
            return new LineIcon(26, 26, c, g -> {
                g.drawOval(3, 3, 20, 20);
                g.drawPolyline(new int[] { 8, 12, 19 }, new int[] { 14, 18, 9 }, 3);
            });
        }
    }

    // ================= LOGO =================
    /**
     * Code-drawn app logo: a blue gradient rounded badge with a ballot box
     * and a check mark. Use {@link #logoIcon(int)} to get an icon of any size.
     */
    public static class LogoIcon implements Icon {
        private final int size;

        public LogoIcon(int size) {
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float arc = Math.max(6f, size * 0.28f);

            // soft drop shadow
            g2.setColor(new Color(15, 23, 42, 26));
            g2.fill(new RoundRectangle2D.Float(x + 1, y + 3, size, size, arc, arc));

            // gradient badge
            Shape badge = new RoundRectangle2D.Float(x, y, size, size, arc, arc);
            g2.setClip(badge);
            g2.setPaint(new GradientPaint(x, y, ACCENT, x + size, y + size, new Color(0x1E40AF)));
            g2.fillRect(x, y, size, size);
            // subtle top highlight
            g2.setColor(new Color(255, 255, 255, 46));
            g2.fillRoundRect(x, y, size, (int) (size * 0.45f), (int) arc, (int) arc);
            g2.setClip(null);

            float pad = size * 0.22f;
            float bx = x + pad;
            float by = y + pad;
            float bw = size - pad * 2f;

            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(Math.max(1.4f, size * 0.07f)));
            g2.drawRoundRect((int) (x + pad), (int) (y + pad), (int) bw, (int) bw,
                    Math.round(size * 0.14f), Math.round(size * 0.14f));
            g2.setStroke(new BasicStroke(Math.max(2.2f, size * 0.11f),
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int[] xs = { (int) (bx + bw * 0.18f), (int) (bx + bw * 0.44f), (int) (bx + bw * 0.84f) };
            int[] ys = { (int) (by + bw * 0.50f), (int) (by + bw * 0.72f), (int) (by + bw * 0.26f) };
            g2.drawPolyline(xs, ys, 3);
            g2.dispose();
        }
    }

    public static Icon logoIcon(int size) {
        return new LogoIcon(size);
    }

    // ================= DIALOGS =================
    public static JDialog buildDialog(Window owner, String title, String message, Icon icon, Color iconColor) {
        JDialog dlg = new JDialog(owner, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setUndecorated(true);
        dlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Card card = new Card(new java.awt.BorderLayout(0, 18));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(false);
        JLabel iconLbl = new JLabel(icon);
        iconWrap.add(iconLbl);
        card.add(iconWrap, java.awt.BorderLayout.NORTH);

        JLabel msg = new JLabel(toHtml(message), SwingConstants.CENTER);
        msg.setFont(font(Font.PLAIN, 14));
        msg.setForeground(TEXT_DARK);
        msg.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        card.add(msg, java.awt.BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new GridBagLayout());
        btnRow.setOpaque(false);
        JButton ok = button("OK");
        ok.addActionListener(e -> dlg.dispose());
        btnRow.add(ok);
        card.add(btnRow, java.awt.BorderLayout.SOUTH);

        dlg.setContentPane(card);
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        return dlg;
    }

    public static void showMessage(Component parent, String title, String message, int kind) {
        Window owner = parent != null
                ? javax.swing.SwingUtilities.getWindowAncestor(parent)
                : null;
        Icon icon;
        Color color;
        if (kind == javax.swing.JOptionPane.ERROR_MESSAGE) {
            icon = Icons.error(RED);
            color = RED;
        } else if (kind == javax.swing.JOptionPane.WARNING_MESSAGE) {
            icon = Icons.warn(WARNING);
            color = WARNING;
        } else {
            icon = Icons.info(ACCENT);
            color = ACCENT;
        }
        JDialog dlg = buildDialog(owner, title, message, icon, color);
        dlg.setVisible(true);
    }

    public static boolean confirm(Component parent, String title, String message) {
        final boolean[] result = { false };
        Window owner = parent != null
                ? javax.swing.SwingUtilities.getWindowAncestor(parent)
                : null;
        JDialog dlg = new JDialog(owner, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setUndecorated(true);
        dlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Card card = new Card(new java.awt.BorderLayout(0, 18));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(false);
        iconWrap.add(new JLabel(Icons.info(ACCENT)));
        card.add(iconWrap, java.awt.BorderLayout.NORTH);

        JLabel msg = new JLabel(toHtml(message), SwingConstants.CENTER);
        msg.setFont(font(Font.PLAIN, 14));
        msg.setForeground(TEXT_DARK);
        card.add(msg, java.awt.BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new GridBagLayout());
        btnRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 6, 0, 6);
        JButton yes = button("Yes", ACCENT);
        yes.addActionListener(e -> {
            result[0] = true;
            dlg.dispose();
        });
        JButton no = outlineButton("No", TEXT_MUTED);
        no.addActionListener(e -> dlg.dispose());
        btnRow.add(yes, gbc);
        btnRow.add(no, gbc);
        card.add(btnRow, java.awt.BorderLayout.SOUTH);

        dlg.setContentPane(card);
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
        return result[0];
    }

    private static String toHtml(String text) {
        return "<html><div style='text-align:center;'>"
                + text.replace("\n", "<br>")
                + "</div></html>";
    }

    /** Keeps the native LAF default fonts for components we do not restyle. */
    public static void tuneLookAndFeel() {
        UIManager.put("Label.font", font(Font.PLAIN, 13));
        UIManager.put("Button.font", font(Font.BOLD, 13));
        UIManager.put("ComboBox.font", font(Font.PLAIN, 13));
        UIManager.put("ToolTip.font", font(Font.PLAIN, 12));
        UIManager.put("OptionPane.messageFont", font(Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", font(Font.BOLD, 13));
        UIManager.put("Table.font", font(Font.PLAIN, 13));
        UIManager.put("TableHeader.font", font(Font.BOLD, 12));
        UIManager.put("ScrollPane.background", Color.WHITE);
    }
}
