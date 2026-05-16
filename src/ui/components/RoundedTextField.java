package ui.components;

import ui.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private int radius = 10;
    private Color borderColor = UIConstants.LINE_COLOR;
    private String placeholder;

    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        setFont(UIConstants.BODY_FONT);
        setForeground(UIConstants.TEXT_MAIN);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // 포커스 시 테두리 색상 변경
        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                borderColor = UIConstants.PRIMARY_COLOR;
                repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                borderColor = UIConstants.LINE_COLOR;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isOpaque()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 배경색 그리기
            g2.setColor(getBackground() != null ? getBackground() : Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
        
        super.paintComponent(g);

        // Placeholder 그리기
        if (placeholder != null && !placeholder.isEmpty() && getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UIConstants.TEXT_SECONDARY);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, getInsets().left, y);
            g2.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 테두리
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, radius, radius));
        
        g2.dispose();
    }
}
