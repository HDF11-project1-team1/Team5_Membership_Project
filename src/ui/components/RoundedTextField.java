package ui.components;

import ui.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private int radius = 10;
    private Color borderColor = UIConstants.LINE_COLOR;

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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 배경색
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        
        super.paintComponent(g);
        g2.dispose();
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
