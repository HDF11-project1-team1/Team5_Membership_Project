package ui.components;

import ui.UIConstants;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;
    private Color textColor;
    private int radius = 10; // 기본 모서리 둥글기 정도

    // 기본 생성자: 메인 버튼 (토스 블루)
    public RoundedButton(String text) {
        this(text, UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
    }

    // 커스텀 버튼 생성자
    public RoundedButton(String text, Color bgColor, Color hoverColor, Color textColor) {
        super(text);
        this.backgroundColor = bgColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;

        // 기본 JButton 스타일 없애기
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(textColor);
        setFont(UIConstants.BODY_BOLD_FONT);
        setBackground(this.backgroundColor);

        // 마우스 호버(올림/내림) 애니메이션 효과 적용
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(RoundedButton.this.hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(RoundedButton.this.backgroundColor);
            }
        });
    }

    // 모서리 둥글기 반경 설정
    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    // 버튼의 외형 그리기 오버라이드
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // 안티앨리어싱 (곡선을 부드럽게 렌더링)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 배경 그리기
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        
        // 텍스트 중앙에 그리기
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), x, y);
        
        g2.dispose();
    }
}
