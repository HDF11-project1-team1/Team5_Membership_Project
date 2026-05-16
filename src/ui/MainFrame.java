package ui;

import javax.swing.*;
import java.awt.*;
import ui.panels.HomePanel;
import ui.panels.user.UserPanel;
import ui.panels.policy.PolicyPanel;

public class MainFrame extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("현대백화점 VIP 멤버십 관리 프로그램");
        setSize(1200, 800); // 패널 크기를 고려하여 창 크기 상향 조정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. 네비게이션 바 (좌측)
        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. 컨텐츠 영역 (우측 메인, CardLayout 적용)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // 패널들 생성 및 추가
        cardPanel.add(new HomePanel(this), "HOME");
        cardPanel.add(new UserPanel(this), "USER");
        cardPanel.add(new PolicyPanel(this), "POLICY");

        add(cardPanel, BorderLayout.CENTER);

        // 처음 켰을 때 보여줄 화면 설정
        cardLayout.show(cardPanel, "HOME");
    }

    // 좌측 네비게이션 바 생성 메서드
    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setPreferredSize(new Dimension(200, getHeight()));
        navPanel.setBackground(UIConstants.SURFACE_COLOR);
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.LINE_COLOR)); // 우측 연한 구분선
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        // 상단 로고/타이틀 영역
        JLabel titleLabel = new JLabel("VIP 멤버십");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        navPanel.add(titleLabel);

        // 홈으로 가기 메뉴 버튼
        JButton homeBtn = createNavButton("🏠 메인 메뉴");
        homeBtn.addActionListener(e -> switchPanel("HOME"));
        navPanel.add(homeBtn);

        // 하단 빈 공간 채우기
        navPanel.add(Box.createVerticalGlue());

        return navPanel;
    }

    // 네비게이션 바용 투명 버튼 생성기
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.BODY_BOLD_FONT);
        btn.setForeground(UIConstants.TEXT_MAIN);
        btn.setBackground(UIConstants.SURFACE_COLOR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // 좌우 패딩 넉넉하게
        
        // 마우스 호버 시 약간 회색빛으로 변하는 효과
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setOpaque(true);
                btn.setBackground(UIConstants.BACKGROUND_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setOpaque(false);
                btn.setBackground(UIConstants.SURFACE_COLOR);
            }
        });
        
        return btn;
    }

    // 다른 패널에서 화면 전환을 요청할 때 사용하는 메서드
    public void switchPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }
}
