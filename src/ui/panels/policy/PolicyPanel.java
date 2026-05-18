package ui.panels.policy;

import policy.dto.PolicyOptionDto;
import policy.dto.PolicyPreviewDto;
import policy.service.PolicyUpdateService;
import ui.MainFrame;
import ui.UIConstants;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PolicyPanel extends JPanel implements ui.Refreshable {

    private final PolicyUpdateService policyService;
    private JTabbedPane tabbedPane;

    // Selection panels and tabs to reload
    private final List<SelectionPanel> branchPanels = new ArrayList<>();
    private final List<SelectionPanel> brandPanels = new ArrayList<>();
    private final List<SelectionPanel> paymentPanels = new ArrayList<>();
    private final List<SelectionPanel> membershipPanels = new ArrayList<>();
    private final List<SelectionPanel> loungePanels = new ArrayList<>();
    private final List<PolicyTab> policyTabs = new ArrayList<>();

    public PolicyPanel(MainFrame mainFrame) {
        this.policyService = new PolicyUpdateService();

        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("정책 관리");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        RoundedButton refreshButton = new RoundedButton("새로고침", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        refreshButton.setFont(UIConstants.BODY_BOLD_FONT);
        refreshButton.setPreferredSize(new Dimension(100, 36));
        refreshButton.addActionListener(e -> {
            refreshAll();
            JOptionPane.showMessageDialog(this, "지점, 브랜드, 결제수단 등의 정보가 동기화되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.add(refreshButton);
        
        headerPanel.add(btnWrapper, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.BODY_BOLD_FONT);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("마일리지 정책", createMileageTab());
        tabbedPane.addTab("VIP 산정 정책", createVipTab());
        tabbedPane.addTab("발레 정책", createValetTab());
        tabbedPane.addTab("무료주차 정책", createParkingTab());
        tabbedPane.addTab("라운지 정책", createLoungeTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        refreshAll();
    }

    public void refreshAll() {
        List<PolicyOptionDto> branches = policyService.getBranches();
        List<PolicyOptionDto> brands = policyService.getBrands();
        List<PolicyOptionDto> payments = policyService.getPayments();
        List<PolicyOptionDto> memberships = policyService.getMemberships();
        List<PolicyOptionDto> lounges = policyService.getLounges();

        for (SelectionPanel p : branchPanels) {
            p.reloadOptions(branches);
        }
        for (SelectionPanel p : brandPanels) {
            p.reloadOptions(brands);
        }
        for (SelectionPanel p : paymentPanels) {
            p.reloadOptions(payments);
        }
        for (SelectionPanel p : membershipPanels) {
            p.reloadOptions(memberships);
        }
        for (SelectionPanel p : loungePanels) {
            p.reloadOptions(lounges);
        }

        for (PolicyTab tab : policyTabs) {
            tab.refreshPreview();
        }
    }

    public void setTab(int index) {
        if (tabbedPane != null && index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    private JPanel createMileageTab() {
        SelectionPanel branches = new SelectionPanel("1. 변경 지점 선택", "전체 지점", policyService.getBranches());
        branchPanels.add(branches);
        SelectionPanel brands = new SelectionPanel("2. 변경 브랜드 선택", "전체 브랜드", policyService.getBrands());
        brandPanels.add(brands);
        SelectionPanel payments = new SelectionPanel("3. 변경 결제수단 선택", "전체 결제수단", policyService.getPayments());
        paymentPanels.add(payments);
        RoundedTextField rateField = new RoundedTextField(10);

        PolicyTab tab = new PolicyTab(
                "마일리지 적립률을 선택한 지점, 브랜드, 결제수단 조합에 일괄 적용합니다.",
                new SelectionPanel[]{branches, brands, payments},
                createRatePanel("4. 변경 기준 입력", "마일리지 적립률", rateField, "0.01 ~ 1.0 사이 값"),
                new String[]{"지점ID", "지점명", "브랜드ID", "브랜드명", "결제수단ID", "결제수단명", "현재 적립률", "변경 적립률", "상태"},
                () -> policyService.getMileagePolicyPreviews(branches.getSelectedIds(), brands.getSelectedIds(), payments.getSelectedIds()),
                () -> rateField.getText().trim(),
                () -> policyService.updateMileageRate(branches.getSelectedIds(), brands.getSelectedIds(), payments.getSelectedIds(), Double.parseDouble(rateField.getText().trim()))
        );
        tab.addAutoRefreshField(rateField);
        policyTabs.add(tab);
        return tab;
    }

    private JPanel createVipTab() {
        SelectionPanel branches = new SelectionPanel("1. 변경 지점 선택", "전체 지점", policyService.getBranches());
        branchPanels.add(branches);
        SelectionPanel payments = new SelectionPanel("2. 변경 결제수단 선택", "전체 결제수단", policyService.getPayments());
        paymentPanels.add(payments);
        RoundedTextField rateField = new RoundedTextField(10);

        PolicyTab tab = new PolicyTab(
                "선택한 지점의 선택한 결제수단 정책을 입력한 VIP 산정률로 변경합니다.",
                new SelectionPanel[]{branches, payments},
                createRatePanel("3. 변경 기준 입력", "VIP 산정률", rateField, "0.01 ~ 1.0 사이 값"),
                new String[]{"지점ID", "지점명", "결제수단ID", "결제수단명", "현재 VIP 산정률", "변경 VIP 산정률", "상태"},
                () -> policyService.getVipPolicyPreviews(branches.getSelectedIds(), payments.getSelectedIds()),
                () -> rateField.getText().trim(),
                () -> policyService.updateVipRate(branches.getSelectedIds(), payments.getSelectedIds(), Double.parseDouble(rateField.getText().trim()))
        );
        tab.addAutoRefreshField(rateField);
        policyTabs.add(tab);
        return tab;
    }

    private JPanel createValetTab() {
        SelectionPanel branches = new SelectionPanel("1. 변경 지점 선택", "전체 지점", policyService.getBranches());
        branchPanels.add(branches);
        SelectionPanel memberships = new SelectionPanel("2. 변경 멤버십 선택", "전체 멤버십", policyService.getMemberships());
        membershipPanels.add(memberships);
        RoundedTextField minField = new RoundedTextField(10);
        RoundedTextField maxField = new RoundedTextField(10);
        JCheckBox availableCheck = new JCheckBox("발레파킹 이용 가능");
        availableCheck.setSelected(true);

        PolicyTab tab = new PolicyTab(
                "선택한 지점의 선택한 멤버십 발레 정책 기준액과 이용 가능 여부를 변경합니다.",
                new SelectionPanel[]{branches, memberships},
                createValetPanel(minField, maxField, availableCheck),
                new String[]{"지점ID", "지점명", "멤버십ID", "멤버십명", "현재 기준", "변경 기준", "상태"},
                () -> policyService.getValetPolicyPreviews(branches.getSelectedIds(), memberships.getSelectedIds()),
                () -> minField.getText().trim() + " ~ " + maxField.getText().trim() + " / " + availableText(availableCheck.isSelected()),
                () -> policyService.updateValetPolicy(branches.getSelectedIds(), memberships.getSelectedIds(),
                        Integer.parseInt(minField.getText().trim()), Integer.parseInt(maxField.getText().trim()), availableCheck.isSelected())
        );
        tab.addAutoRefreshField(minField);
        tab.addAutoRefreshField(maxField);
        availableCheck.addActionListener(e -> tab.refreshPreview());
        policyTabs.add(tab);
        return tab;
    }

    private JPanel createParkingTab() {
        SelectionPanel branches = new SelectionPanel("1. 변경 지점 선택", "전체 지점", policyService.getBranches());
        branchPanels.add(branches);
        SelectionPanel memberships = new SelectionPanel("2. 변경 멤버십 선택", "전체 멤버십", policyService.getMemberships());
        membershipPanels.add(memberships);
        JCheckBox availableCheck = new JCheckBox("무료주차 이용 가능");
        availableCheck.setSelected(true);

        PolicyTab tab = new PolicyTab(
                "선택한 지점의 선택한 멤버십 무료주차 가능 여부를 변경합니다.",
                new SelectionPanel[]{branches, memberships},
                createAvailablePanel("3. 변경 기준 입력", availableCheck),
                new String[]{"지점ID", "지점명", "멤버십ID", "멤버십명", "현재 가능 여부", "변경 가능 여부", "상태"},
                () -> policyService.getFreeParkingPolicyPreviews(branches.getSelectedIds(), memberships.getSelectedIds()),
                () -> availableText(availableCheck.isSelected()),
                () -> policyService.updateFreeParkingAvailable(branches.getSelectedIds(), memberships.getSelectedIds(), availableCheck.isSelected())
        );
        availableCheck.addActionListener(e -> tab.refreshPreview());
        policyTabs.add(tab);
        return tab;
    }

    private JPanel createLoungeTab() {
        SelectionPanel branches = new SelectionPanel("1. 변경 지점 선택", "전체 지점", policyService.getBranches());
        branchPanels.add(branches);
        SelectionPanel lounges = new SelectionPanel("2. 변경 라운지 선택", "전체 라운지", policyService.getLounges());
        loungePanels.add(lounges);
        SelectionPanel memberships = new SelectionPanel("3. 변경 멤버십 선택", "전체 멤버십", policyService.getMemberships());
        membershipPanels.add(memberships);
        JCheckBox availableCheck = new JCheckBox("라운지 이용 가능");
        availableCheck.setSelected(true);

        PolicyTab tab = new PolicyTab(
                "선택한 지점, 라운지, 멤버십 조합의 라운지 이용 가능 여부를 변경합니다.",
                new SelectionPanel[]{branches, lounges, memberships},
                createAvailablePanel("4. 변경 기준 입력", availableCheck),
                new String[]{"지점ID", "지점명", "라운지ID", "라운지명", "멤버십ID", "멤버십명", "현재 가능 여부", "변경 가능 여부", "상태"},
                () -> policyService.getLoungePolicyPreviews(branches.getSelectedIds(), lounges.getSelectedIds(), memberships.getSelectedIds()),
                () -> availableText(availableCheck.isSelected()),
                () -> policyService.updateLoungeAvailable(branches.getSelectedIds(), lounges.getSelectedIds(), memberships.getSelectedIds(), availableCheck.isSelected())
        );
        availableCheck.addActionListener(e -> tab.refreshPreview());
        policyTabs.add(tab);
        return tab;
    }

    private JPanel createRatePanel(String title, String label, JTextField field, String helperText) {
        JPanel panel = createControlPanel(title);
        panel.add(createFieldLabel(label));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(field);
        panel.add(createHelperLabel(helperText));
        return panel;
    }

    private JPanel createValetPanel(JTextField minField, JTextField maxField, JCheckBox availableCheck) {
        JPanel panel = createControlPanel("3. 변경 기준 입력");
        panel.add(createFieldLabel("최소 구매 기준액"));
        panel.add(minField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldLabel("최대 구매 기준액"));
        panel.add(maxField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(availableCheck);
        return panel;
    }

    private JPanel createAvailablePanel(String title, JCheckBox availableCheck) {
        JPanel panel = createControlPanel(title);
        panel.add(availableCheck);
        panel.add(createHelperLabel("체크 시 이용 가능, 해제 시 이용 불가"));
        return panel;
    }

    private JPanel createControlPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.LINE_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.BODY_BOLD_FONT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        return panel;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.BODY_BOLD_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createHelperLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.CAPTION_FONT);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        return label;
    }

    private String availableText(boolean available) {
        return available ? "가능" : "불가";
    }

    private interface PreviewLoader {
        List<PolicyPreviewDto> load();
    }

    private interface UpdateRunner {
        int update();
    }

    private class PolicyTab extends JPanel {
        private final SelectionPanel[] selections;
        private final DefaultTableModel tableModel;
        private final PreviewLoader previewLoader;
        private final Supplier<String> changedValueSupplier;
        private final UpdateRunner updateRunner;
        private final JLabel summaryLabel = new JLabel("변경 대상: 0건");

        PolicyTab(String description, SelectionPanel[] selections, JPanel controlPanel, String[] columns,
                  PreviewLoader previewLoader, Supplier<String> changedValueSupplier, UpdateRunner updateRunner) {
            this.selections = selections;
            this.previewLoader = previewLoader;
            this.changedValueSupplier = changedValueSupplier;
            this.updateRunner = updateRunner;
            this.tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            setLayout(new BorderLayout(0, 12));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            add(createDescription(description), BorderLayout.NORTH);
            add(createTopArea(controlPanel), BorderLayout.CENTER);
            add(createResultPanel(), BorderLayout.SOUTH);

            for (SelectionPanel selection : selections) {
                selection.setChangeListener(this::refreshPreview);
            }

            refreshPreview();
        }

        void addAutoRefreshField(JTextField field) {
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    refreshPreview();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    refreshPreview();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    refreshPreview();
                }
            });
        }

        void refreshPreview() {
            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                if (!hasAllSelections()) {
                    summaryLabel.setText("변경 대상: 0건");
                    return;
                }

                List<PolicyPreviewDto> previews = previewLoader.load();
                String changedValue = changedValueSupplier.get();
                for (PolicyPreviewDto preview : previews) {
                    tableModel.addRow(toTableRow(preview, changedValue, "변경 예정"));
                }
                summaryLabel.setText("변경 대상: " + previews.size() + "건");
            });
        }

        private JPanel createTopArea(JPanel controlPanel) {
            JPanel topPanel = new JPanel(new BorderLayout(12, 0));
            topPanel.setBackground(Color.WHITE);

            JPanel selectionArea = new JPanel(new GridLayout(1, selections.length, 10, 0));
            selectionArea.setBackground(Color.WHITE);
            for (SelectionPanel selection : selections) {
                selectionArea.add(selection);
            }
            topPanel.add(selectionArea, BorderLayout.CENTER);

            JPanel rightArea = new JPanel(new BorderLayout(0, 10));
            rightArea.setBackground(Color.WHITE);
            rightArea.setPreferredSize(new Dimension(250, 230));
            rightArea.add(controlPanel, BorderLayout.CENTER);
            rightArea.add(createButtonPanel(), BorderLayout.SOUTH);
            topPanel.add(rightArea, BorderLayout.EAST);

            return topPanel;
        }

        private JPanel createButtonPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.setBackground(Color.WHITE);
            summaryLabel.setFont(UIConstants.BODY_FONT);
            summaryLabel.setForeground(UIConstants.TEXT_SECONDARY);
            panel.add(summaryLabel, BorderLayout.NORTH);

            JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
            buttons.setBackground(Color.WHITE);
            RoundedButton updateButton = new RoundedButton("변경", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
            RoundedButton clearButton = new RoundedButton("선택 초기화", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
            updateButton.addActionListener(e -> updatePolicies());
            clearButton.addActionListener(e -> {
                for (SelectionPanel selection : selections) {
                    selection.clearSelection();
                }
            });
            buttons.add(updateButton);
            buttons.add(clearButton);
            panel.add(buttons, BorderLayout.CENTER);
            return panel;
        }

        private JPanel createResultPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.setBackground(Color.WHITE);
            panel.setPreferredSize(new Dimension(100, 240));

            JLabel title = new JLabel("현재 정책 및 변경 예정/결과 내역");
            title.setFont(UIConstants.BODY_BOLD_FONT);
            panel.add(title, BorderLayout.NORTH);

            JTable table = new JTable(tableModel);
            table.setRowHeight(28);
            table.getTableHeader().setReorderingAllowed(false);
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            return panel;
        }

        private JLabel createDescription(String text) {
            JLabel label = new JLabel(text);
            label.setFont(UIConstants.CAPTION_FONT);
            label.setForeground(UIConstants.TEXT_SECONDARY);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            return label;
        }

        private boolean hasAllSelections() {
            for (SelectionPanel selection : selections) {
                if (selection.getSelectedIds().isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private void updatePolicies() {
            try {
                int result = updateRunner.update();
                refreshStatus(result > 0 ? "변경 완료" : "변경 없음");
                JOptionPane.showMessageDialog(this, result + "건의 정책이 업데이트되었습니다.", "결과", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                refreshStatus("실패");
                JOptionPane.showMessageDialog(this, "입력값이 올바르지 않습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void refreshStatus(String status) {
            int lastColumn = tableModel.getColumnCount() - 1;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(status, i, lastColumn);
            }
        }
    }

    private Object[] toTableRow(PolicyPreviewDto preview, String changedValue, String status) {
        List<Object> row = new ArrayList<>();
        row.add(preview.getBranchId());
        row.add(preview.getBranchName());
        if (preview.getBrandId() != null) {
            row.add(preview.getBrandId());
            row.add(preview.getBrandName());
        }
        if (preview.getPaymentId() != null) {
            row.add(preview.getPaymentId());
            row.add(preview.getPaymentName());
        }
        if (preview.getLoungeId() != null) {
            row.add(preview.getLoungeId());
            row.add(preview.getLoungeName());
        }
        if (preview.getMembershipId() != null) {
            row.add(preview.getMembershipId());
            row.add(preview.getMembershipName());
        }
        row.add(preview.getCurrentValue());
        row.add(changedValue);
        row.add(status);
        return row.toArray();
    }

    private static class SelectionPanel extends JPanel {
        private final JCheckBox allCheckBox;
        private final List<JCheckBox> itemCheckBoxes = new ArrayList<>();
        private final JLabel countLabel = new JLabel("선택 0개");
        private final JPanel listPanel;
        private Runnable changeListener;
        private boolean internalChange;

        SelectionPanel(String title, String allLabel, List<PolicyOptionDto> options) {
            setLayout(new BorderLayout(0, 8));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.LINE_COLOR),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(UIConstants.BODY_BOLD_FONT);
            add(titleLabel, BorderLayout.NORTH);

            listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(Color.WHITE);

            allCheckBox = new JCheckBox(allLabel);
            allCheckBox.setBackground(Color.WHITE);
            allCheckBox.setFont(UIConstants.BODY_FONT);
            allCheckBox.addActionListener(e -> {
                if (internalChange) {
                    return;
                }
                internalChange = true;
                for (JCheckBox itemCheckBox : itemCheckBoxes) {
                    itemCheckBox.setSelected(allCheckBox.isSelected());
                }
                internalChange = false;
                notifyChanged();
            });
            listPanel.add(allCheckBox);
            listPanel.add(Box.createVerticalStrut(8));

            for (PolicyOptionDto option : options) {
                JCheckBox checkBox = new JCheckBox("[" + option.getId() + "] " + option.getName());
                checkBox.putClientProperty("id", option.getId());
                checkBox.setBackground(Color.WHITE);
                checkBox.setFont(UIConstants.BODY_FONT);
                checkBox.addActionListener(e -> notifyChanged());
                itemCheckBoxes.add(checkBox);
                listPanel.add(checkBox);
            }

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setPreferredSize(new Dimension(180, 220));
            add(scrollPane, BorderLayout.CENTER);

            countLabel.setFont(UIConstants.CAPTION_FONT);
            countLabel.setForeground(UIConstants.TEXT_SECONDARY);
            add(countLabel, BorderLayout.SOUTH);
        }

        void reloadOptions(List<PolicyOptionDto> options) {
            internalChange = true;
            itemCheckBoxes.clear();
            listPanel.removeAll();
            
            allCheckBox.setSelected(false);
            listPanel.add(allCheckBox);
            listPanel.add(Box.createVerticalStrut(8));
            
            for (PolicyOptionDto option : options) {
                JCheckBox checkBox = new JCheckBox("[" + option.getId() + "] " + option.getName());
                checkBox.putClientProperty("id", option.getId());
                checkBox.setBackground(Color.WHITE);
                checkBox.setFont(UIConstants.BODY_FONT);
                checkBox.addActionListener(e -> notifyChanged());
                itemCheckBoxes.add(checkBox);
                listPanel.add(checkBox);
            }
            
            internalChange = false;
            notifyChanged();
            listPanel.revalidate();
            listPanel.repaint();
        }

        void setChangeListener(Runnable changeListener) {
            this.changeListener = changeListener;
        }

        List<Integer> getSelectedIds() {
            return itemCheckBoxes.stream()
                    .filter(JCheckBox::isSelected)
                    .map(checkBox -> (Integer) checkBox.getClientProperty("id"))
                    .collect(Collectors.toList());
        }

        void clearSelection() {
            internalChange = true;
            allCheckBox.setSelected(false);
            for (JCheckBox checkBox : itemCheckBoxes) {
                checkBox.setSelected(false);
            }
            internalChange = false;
            notifyChanged();
        }

        private void notifyChanged() {
            updateAllCheckBox();
            countLabel.setText("선택 " + getSelectedIds().size() + "개");
            if (changeListener != null) {
                changeListener.run();
            }
        }

        private void updateAllCheckBox() {
            if (internalChange) {
                return;
            }
            internalChange = true;
            allCheckBox.setSelected(!itemCheckBoxes.isEmpty() && getSelectedIds().size() == itemCheckBoxes.size());
            internalChange = false;
        }
    }
}
