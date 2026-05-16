package ui.panels;

import ui.MainFrame;
import ui.UIConstants;
import ui.components.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private MainFrame mainFrame;

    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // 전체 화면 여백

        // 상단 타이틀
        JLabel titleLabel = new JLabel("어떤 작업을 진행하시겠어요?");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 버튼들을 담을 3x3 그리드 패널 (버튼 사이 간격 20px)
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 20, 20));
        gridPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // 메뉴 이름과 이동할 카드(패널) 이름 매핑
        String[] menuNames = {
            "1. 회원 관리",
            "2. 구매 관리",
            "3. VIP 멤버십 관리",
            "4. 혜택 이용 관리",
            "5. 정책 관리",
            "6. 통계 / 분석",
            "7. 기준 정보 관리"
        };
        
        String[] panelCodes = {
            "USER", "PURCHASE", "VIP", "BENEFIT", "POLICY", "STAT", "MASTER"
        };

        // 각 메뉴별 파스텔톤 배경색
        Color[] bgColors = {
            new Color(0xE8F0FE), // 1. Light Blue
            new Color(0xE6F4EA), // 2. Light Green
            new Color(0xF3E8FD), // 3. Light Purple
            new Color(0xFEF7E0), // 4. Light Orange
            new Color(0xFCE8E6), // 5. Light Pink
            new Color(0xE0F2F1), // 6. Light Teal
            new Color(0xF8F9FA)  // 7. Light Gray
        };
        
        // 각 메뉴별 텍스트 색상 (배경색에 어울리는 진한 톤)
        Color[] textColors = {
            new Color(0x1967D2), // Dark Blue
            new Color(0x137333), // Dark Green
            new Color(0x7627BB), // Dark Purple
            new Color(0xB06000), // Dark Orange
            new Color(0xC5221F), // Dark Red
            new Color(0x00796B), // Dark Teal
            new Color(0x3C4043)  // Dark Gray
        };
        
        // 각 메뉴별 마우스 호버(Hover) 시 조금 더 진해지는 배경색
        Color[] hoverColors = {
            new Color(0xD2E3FC),
            new Color(0xCEEAD6),
            new Color(0xE8D0FE),
            new Color(0xFEEFC3),
            new Color(0xFAD2CF),
            new Color(0xB2DFDB),
            new Color(0xE8EAED)
        };

        // 메뉴 개수만큼 버튼을 생성하여 그리드에 추가
        for (int i = 0; i < menuNames.length; i++) {
            // 다채로운 파스텔톤 색상을 적용한 카드형 버튼 생성
            RoundedButton btn = new RoundedButton(menuNames[i], bgColors[i], hoverColors[i], textColors[i]);
            btn.setFont(UIConstants.SUBHEADER_FONT);
            btn.setRadius(25); // 모서리를 더 둥글게 처리하여 귀엽고 부드러운 느낌 강조
            
            final String code = panelCodes[i];
            btn.addActionListener(e -> {
                // 정책 관리는 미구현 상태이므로 막아두기
                if (code.equals("POLICY")) {
                    JOptionPane.showMessageDialog(this, "정책 관리 로직은 아직 개발 전이므로 연동이 보류되었습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    mainFrame.switchPanel(code);
                }
            });
            gridPanel.add(btn);
        }

        // 3x3은 총 9칸이고 메뉴는 7개이므로 빈 패널 2개 추가하여 레이아웃 모양 유지
        for(int i = 0; i < 2; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false);
            gridPanel.add(emptyPanel);
        }

        add(gridPanel, BorderLayout.CENTER);
    }
}
