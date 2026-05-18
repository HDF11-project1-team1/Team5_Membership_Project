package ui.panels.purchase;

import purchase.dto.PurchaseHistoryDto;
import purchase.service.PurchaseService;
import ui.UIConstants;
import ui.MainFrame;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class PurchasePanel extends JPanel {
    private MainFrame mainFrame;
    private PurchaseService purchaseService;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private RoundedTextField searchField;
    private JComboBox<String> membershipFilter;

    public PurchasePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.purchaseService = new PurchaseService();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainArea(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("구매 관리");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        // 검색 및 필터
        searchField = new RoundedTextField(15);
        searchField.setPlaceholder("회원 ID 입력");
        actionPanel.add(searchField);

        RoundedButton searchBtn = new RoundedButton("ID 검색", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        searchBtn.addActionListener(e -> searchByUserId());
        actionPanel.add(searchBtn);

        String[] filters = {
            "멤버십 필터", 
            "1. JASMIN SIGNATURE", 
            "2. JASMIN BLACK", 
            "3. JASMIN BLUE", 
            "4. JASMIN", 
            "5. SAGE", 
            "6. CLUB YP", 
            "7. GREEN 1", 
            "8. GREEN 2", 
            "9. EARLY GREEN", 
            "10. BASIC"
        };
        membershipFilter = new JComboBox<>(filters);
        membershipFilter.setFont(UIConstants.BODY_FONT);
        membershipFilter.addActionListener(e -> filterByMembership());
        actionPanel.add(membershipFilter);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createMainArea() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(UIConstants.LINE_COLOR, 1));

        String[] columnNames = {"주문번호", "회원ID", "금액", "할인액", "최종결제액", "상태", "날짜"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        ui.components.StyledTable styledTable = new ui.components.StyledTable(tableModel);
        styledTable.setColumnAlignment(2, SwingConstants.RIGHT); // 금액
        styledTable.setColumnAlignment(3, SwingConstants.RIGHT); // 할인액
        styledTable.setColumnAlignment(4, SwingConstants.RIGHT); // 최종결제액
        historyTable = styledTable;

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void searchByUserId() {
        try {
            int userId = Integer.parseInt(searchField.getText().trim());
            List<PurchaseHistoryDto> history = purchaseService.getPurchaseHistoryByUserId(userId);
            updateTable(history);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "올바른 회원 ID를 입력해주세요.");
        }
    }

    private void filterByMembership() {
        int index = membershipFilter.getSelectedIndex();
        if (index > 0) {
            List<PurchaseHistoryDto> history = purchaseService.getPurchaseHistoryByMembershipId(index);
            updateTable(history);
        }
    }

    private void updateTable(List<PurchaseHistoryDto> history) {
        tableModel.setRowCount(0);
        for (PurchaseHistoryDto h : history) {
            tableModel.addRow(new Object[]{
                h.getPurchaseHistoryId(),
                h.getUserId(),
                String.format("%,d", h.getPrice()),
                String.format("%,d", h.getDiscountPrice()),
                String.format("%,d", h.getFinalPrice()),
                h.getPurchaseStatus(),
                h.getGeneratedDate().toLocalDate()
            });
        }
    }
}
