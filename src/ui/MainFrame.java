package ui;

import javax.swing.*;
import java.awt.*;
import ui.panels.HomePanel;
import ui.panels.user.UserPanel;
import ui.panels.policy.PolicyPanel;
import ui.panels.purchase.PurchasePanel;
import ui.panels.vip.VipPanel;
import ui.panels.benefit.BenefitPanel;
import ui.panels.master.MasterPanel;

public class MainFrame extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel navPanel;
    private JLabel titleLabel;
    
    // 네비게이션 활성화 관리를 위한 버튼 참조 리스트
    private java.util.List<JButton> navButtons = new java.util.ArrayList<>();
    private JButton activeNavButton;

    // 각 패널 참조
    private UserPanel userPanel;
    private PurchasePanel purchasePanel;
    private VipPanel vipPanel;
    private BenefitPanel benefitPanel;
    private PolicyPanel policyPanel;
    private ui.panels.stat.StatPanel statPanel;
    private MasterPanel masterPanel;

    public MainFrame() {
        setTitle("현대백화점 VIP 멤버십 관리 프로그램");
        setSize(1500, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 패널들 선 생성
        userPanel = new UserPanel(this);
        purchasePanel = new PurchasePanel(this);
        vipPanel = new VipPanel(this);
        benefitPanel = new BenefitPanel(this);
        policyPanel = new PolicyPanel(this);
        statPanel = new ui.panels.stat.StatPanel();
        masterPanel = new MasterPanel(this);

        // 1. 네비게이션 바 (좌측)
        navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        // 2. 컨텐츠 영역 (우측 메인, CardLayout 적용)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        cardPanel.add(new HomePanel(this), "HOME");
        cardPanel.add(userPanel, "USER");
        cardPanel.add(purchasePanel, "PURCHASE");
        cardPanel.add(vipPanel, "VIP");
        cardPanel.add(benefitPanel, "BENEFIT");
        cardPanel.add(policyPanel, "POLICY");
        cardPanel.add(statPanel, "STAT");
        cardPanel.add(masterPanel, "MASTER");

        add(cardPanel, BorderLayout.CENTER);

        // 첫 화면 HOME 네비게이션 동기화 활성화
        switchPanel("HOME");
    }

    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(220, getHeight()));
        panel.setBackground(UIConstants.SURFACE_COLOR);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.LINE_COLOR));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 상단 타이틀
        titleLabel = new JLabel("VIP 멤버십");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setMaximumSize(new Dimension(220, 100));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 25, 40, 0));
        panel.add(titleLabel);

        // 메인 메뉴 버튼들 (심플 버전)
        panel.add(createNavButton("메인 홈", "HOME", "home.png"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createNavButton("회원 관리", "USER", "user.png"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createNavButton("구매 관리", "PURCHASE", "bag.png"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createNavButton("VIP 멤버십 관리", "VIP", "crown.png"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createNavButton("혜택 이용 관리", "BENEFIT", "gift.png"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createNavButton("정책 관리", "POLICY", "setting.png"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createNavButton("통계 및 분석", "STAT", "analysis.png"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createNavButton("기준 정보 관리", "MASTER", "database.png"));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JButton createNavButton(String text, String panelName, String iconFile) {
        JButton btn = new JButton(text);
        btn.putClientProperty("panelName", panelName);

        if (iconFile != null) {
            try {
                String path = "src/ui/icon/" + iconFile;
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(15);
            } catch (Exception e) {
                System.err.println("Icon load error: " + e.getMessage());
            }
        }

        btn.setFont(UIConstants.BODY_BOLD_FONT);
        btn.setForeground(UIConstants.TEXT_MAIN);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true); // 배경색 표시를 위해 true 설정
        btn.setBackground(UIConstants.SURFACE_COLOR);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 50));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 20));

        btn.addActionListener(e -> switchPanel(panelName));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (activeNavButton != btn) {
                    btn.setForeground(UIConstants.PRIMARY_COLOR);
                    btn.setBackground(new Color(0xF2F4F6));
                    btn.setContentAreaFilled(true);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (activeNavButton != btn) {
                    btn.setForeground(UIConstants.TEXT_MAIN);
                    btn.setBackground(UIConstants.SURFACE_COLOR);
                    btn.setContentAreaFilled(false);
                }
            }
        });

        navButtons.add(btn);
        return btn;
    }

    public void switchPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);

        // 1. 네비게이션 버튼 활성화 스타일 전환
        for (JButton navBtn : navButtons) {
            String name = (String) navBtn.getClientProperty("panelName");
            if (panelName.equals(name)) {
                activeNavButton = navBtn;
                navBtn.setForeground(UIConstants.PRIMARY_COLOR);
                navBtn.setBackground(new Color(0xE8F3FF)); // 연한 블루 하이라이트배경 카드
                navBtn.setContentAreaFilled(true);
            } else {
                navBtn.setForeground(UIConstants.TEXT_MAIN);
                navBtn.setBackground(UIConstants.SURFACE_COLOR);
                navBtn.setContentAreaFilled(false);
            }
        }

        // 2. 대상 패널 자동 갱신(Auto-Refresh) 트리거 호출
        Component[] components = cardPanel.getComponents();
        for (Component comp : components) {
            if (comp.isVisible() && comp instanceof Refreshable) {
                ((Refreshable) comp).refresh();
            }
        }
    }
}
