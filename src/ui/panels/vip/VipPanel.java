package ui.panels.vip;

import membership.dto.MembershipCurrentGradeDto;
import membership.dto.MembershipHistoryDto;
import membership.service.MembershipService;
import ui.UIConstants;
import ui.MainFrame;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VipPanel extends JPanel implements ui.Refreshable {
    private MainFrame mainFrame;
    private MembershipService membershipService;
    private JTable vipTable;
    private DefaultTableModel tableModel;
    private RoundedTextField nameField, birthField;

    public VipPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.membershipService = new MembershipService();

        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainArea(), BorderLayout.CENTER);

        // 진입 시 바로 전체 조회
        loadCurrentGrades();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("VIP 멤버십 관리");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        RoundedButton currentBtn = new RoundedButton("전체 등급 현황", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER,
                Color.WHITE);
        currentBtn.addActionListener(e -> loadCurrentGrades());

        RoundedButton earlyBtn = new RoundedButton("EARLY GREEN", UIConstants.SECONDARY_BTN_COLOR,
                UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        earlyBtn.addActionListener(e -> loadEarlyGreen());

        actionPanel.add(currentBtn);
        actionPanel.add(earlyBtn);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout(0, 20));
        mainArea.setOpaque(false);

        // 상단 이력 검색 영역
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(UIConstants.LINE_COLOR));

        searchPanel.add(new JLabel("이력 조회:"));
        nameField = new RoundedTextField(10);
        nameField.setPlaceholder("이름");
        searchPanel.add(nameField);

        birthField = new RoundedTextField(10);
        birthField.setPlaceholder("yyyy-MM-dd");
        searchPanel.add(birthField);

        RoundedButton historyBtn = new RoundedButton("이력 검색", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER,
                Color.WHITE);
        historyBtn.addActionListener(e -> loadHistory());
        searchPanel.add(historyBtn);

        mainArea.add(searchPanel, BorderLayout.NORTH);

        // 테이블 영역
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(UIConstants.LINE_COLOR, 1));

        tableModel = new DefaultTableModel(new String[] { "ID", "이름", "전화번호", "등급" }, 0);
        vipTable = new ui.components.StyledTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(vipTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainArea.add(tablePanel, BorderLayout.CENTER);
        return mainArea;
    }

    public void loadCurrentGrades() {
        tableModel.setColumnIdentifiers(new String[] { "회원ID", "이름", "전화번호", "현재 등급" });
        tableModel.setRowCount(0);
        List<MembershipCurrentGradeDto> list = membershipService.getCurrentMembershipGrades();
        for (MembershipCurrentGradeDto d : list) {
            tableModel.addRow(new Object[] { d.getUserId(), d.getName(), d.getPhoneNumber(), d.getMembershipGrade() });
        }
    }

    public void loadEarlyGreen() {
        tableModel.setColumnIdentifiers(new String[] { "회원ID", "이름", "전화번호", "현재 등급(EARLY)" });
        tableModel.setRowCount(0);
        List<MembershipCurrentGradeDto> list = membershipService.getEarlyGreenMembers();
        for (MembershipCurrentGradeDto d : list) {
            tableModel.addRow(new Object[] { d.getUserId(), d.getName(), d.getPhoneNumber(), d.getMembershipGrade() });
        }
    }

    public void loadHistory() {
        try {
            String name = nameField.getText().trim();
            LocalDate birth = LocalDate.parse(birthField.getText().trim());

            tableModel.setColumnIdentifiers(new String[] { "이름", "등급", "시작일", "종료일", "산정금액" });
            tableModel.setRowCount(0);
            List<MembershipHistoryDto> list = membershipService.getMembershipHistories(name, birth);
            for (MembershipHistoryDto d : list) {
                tableModel.addRow(new Object[] {
                        d.getName(),
                        d.getMembershipGrade(),
                        d.getStartDate().toLocalDate(),
                        d.getEndDate() != null ? d.getEndDate().toLocalDate() : "-",
                        String.format("%,d원", d.getCalculateAmount())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "조회 실패: " + e.getMessage());
        }
    }

    @Override
    public void refresh() {
        if (nameField != null) nameField.setText("");
        if (birthField != null) birthField.setText("");
        loadCurrentGrades();
    }
}
