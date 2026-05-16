package ui.panels.policy;

import ui.UIConstants;
import ui.MainFrame;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import policy.service.PolicyUpdateService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PolicyPanel extends JPanel {
    private MainFrame mainFrame;
    private PolicyUpdateService policyService;
    private JTabbedPane tabbedPane;

    public PolicyPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.policyService = new PolicyUpdateService();

        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 상단 타이틀
        JLabel titleLabel = new JLabel("정책 관리");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        add(titleLabel, BorderLayout.NORTH);

        // 중앙 탭 패널
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

    public void setTab(int index) {
        if (tabbedPane != null && index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    private JPanel createMileageTab() {
        JPanel panel = createTabBase();
        panel.add(createDescription("마일리지 적립률 정책을 지점, 브랜드, 결제수단별로 업데이트합니다."));
        
        RoundedTextField branchIdsField = addLabeledField(panel, "지점 ID (쉼표 구분)");
        RoundedTextField brandIdsField = addLabeledField(panel, "브랜드 ID (쉼표 구분)");
        RoundedTextField paymentIdsField = addLabeledField(panel, "결제수단 ID (쉼표 구분)");
        RoundedTextField rateField = addLabeledField(panel, "마일리지 적립률 (0.01 ~ 1.0)");

        RoundedButton updateBtn = new RoundedButton("마일리지 정책 반영", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        updateBtn.addActionListener(e -> {
            try {
                List<Integer> bIds = parseIds(branchIdsField.getText());
                List<Integer> brIds = parseIds(brandIdsField.getText());
                List<Integer> pIds = parseIds(paymentIdsField.getText());
                double rate = Double.parseDouble(rateField.getText());

                int result = policyService.updateMileageRate(bIds, brIds, pIds, rate);
                showResult(result);
            } catch (Exception ex) {
                showError(ex);
            }
        });
        panel.add(updateBtn);
        return panel;
    }

    private JPanel createVipTab() {
        JPanel panel = createTabBase();
        panel.add(createDescription("VIP 산정률 정책을 지점 및 결제수단별로 업데이트합니다."));

        RoundedTextField branchIdsField = addLabeledField(panel, "지점 ID (쉼표 구분)");
        RoundedTextField paymentIdsField = addLabeledField(panel, "결제수단 ID (쉼표 구분)");
        RoundedTextField rateField = addLabeledField(panel, "VIP 산정률 (0.01 ~ 1.0)");

        RoundedButton updateBtn = new RoundedButton("VIP 산정 정책 반영", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        updateBtn.addActionListener(e -> {
            try {
                List<Integer> bIds = parseIds(branchIdsField.getText());
                List<Integer> pIds = parseIds(paymentIdsField.getText());
                double rate = Double.parseDouble(rateField.getText());

                int result = policyService.updateVipRate(bIds, pIds, rate);
                showResult(result);
            } catch (Exception ex) {
                showError(ex);
            }
        });
        panel.add(updateBtn);
        return panel;
    }

    private JPanel createValetTab() {
        JPanel panel = createTabBase();
        panel.add(createDescription("발레파킹 정책을 지점 및 멤버십 등급별로 업데이트합니다."));

        RoundedTextField branchIdsField = addLabeledField(panel, "지점 ID (쉼표 구분)");
        RoundedTextField membershipIdsField = addLabeledField(panel, "멤버십 등급 ID (쉼표 구분)");
        RoundedTextField minField = addLabeledField(panel, "최소 구매 기준액");
        RoundedTextField maxField = addLabeledField(panel, "최대 구매 기준액");
        
        JCheckBox availableCheck = new JCheckBox("발레파킹 이용 가능 여부");
        availableCheck.setBackground(Color.WHITE);
        availableCheck.setSelected(true);
        panel.add(availableCheck);
        panel.add(Box.createVerticalStrut(20));

        RoundedButton updateBtn = new RoundedButton("발레 정책 반영", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        updateBtn.addActionListener(e -> {
            try {
                List<Integer> bIds = parseIds(branchIdsField.getText());
                List<Integer> mIds = parseIds(membershipIdsField.getText());
                int min = Integer.parseInt(minField.getText());
                int max = Integer.parseInt(maxField.getText());
                boolean available = availableCheck.isSelected();

                int result = policyService.updateValetPolicy(bIds, mIds, min, max, available);
                showResult(result);
            } catch (Exception ex) {
                showError(ex);
            }
        });
        panel.add(updateBtn);
        return panel;
    }

    private JPanel createParkingTab() {
        JPanel panel = createTabBase();
        panel.add(createDescription("무료주차 정책을 지점 및 멤버십 등급별로 업데이트합니다."));

        RoundedTextField branchIdsField = addLabeledField(panel, "지점 ID (쉼표 구분)");
        RoundedTextField membershipIdsField = addLabeledField(panel, "멤버십 등급 ID (쉼표 구분)");
        
        JCheckBox availableCheck = new JCheckBox("무료주차 이용 가능 여부");
        availableCheck.setBackground(Color.WHITE);
        availableCheck.setSelected(true);
        panel.add(availableCheck);
        panel.add(Box.createVerticalStrut(20));

        RoundedButton updateBtn = new RoundedButton("무료주차 정책 반영", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        updateBtn.addActionListener(e -> {
            try {
                List<Integer> bIds = parseIds(branchIdsField.getText());
                List<Integer> mIds = parseIds(membershipIdsField.getText());
                boolean available = availableCheck.isSelected();

                int result = policyService.updateFreeParkingAvailable(bIds, mIds, available);
                showResult(result);
            } catch (Exception ex) {
                showError(ex);
            }
        });
        panel.add(updateBtn);
        return panel;
    }

    private JPanel createLoungeTab() {
        JPanel panel = createTabBase();
        panel.add(createDescription("라운지 이용 정책을 지점, 라운지, 멤버십 등급별로 업데이트합니다."));

        RoundedTextField branchIdsField = addLabeledField(panel, "지점 ID (쉼표 구분)");
        RoundedTextField loungeIdsField = addLabeledField(panel, "라운지 ID (쉼표 구분)");
        RoundedTextField membershipIdsField = addLabeledField(panel, "멤버십 등급 ID (쉼표 구분)");
        
        JCheckBox availableCheck = new JCheckBox("라운지 이용 가능 여부");
        availableCheck.setBackground(Color.WHITE);
        availableCheck.setSelected(true);
        panel.add(availableCheck);
        panel.add(Box.createVerticalStrut(20));

        RoundedButton updateBtn = new RoundedButton("라운지 정책 반영", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        updateBtn.addActionListener(e -> {
            try {
                List<Integer> bIds = parseIds(branchIdsField.getText());
                List<Integer> lIds = parseIds(loungeIdsField.getText());
                List<Integer> mIds = parseIds(membershipIdsField.getText());
                boolean available = availableCheck.isSelected();

                int result = policyService.updateLoungeAvailable(bIds, lIds, mIds, available);
                showResult(result);
            } catch (Exception ex) {
                showError(ex);
            }
        });
        panel.add(updateBtn);
        return panel;
    }

    private JPanel createTabBase() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JLabel createDescription(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.CAPTION_FONT);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return label;
    }

    private RoundedTextField addLabeledField(JPanel parent, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.BODY_BOLD_FONT);
        parent.add(label);
        parent.add(Box.createVerticalStrut(5));
        
        RoundedTextField field = new RoundedTextField(20);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));
        return field;
    }

    private List<Integer> parseIds(String text) {
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private void showResult(int result) {
        if (result > 0) {
            JOptionPane.showMessageDialog(this, result + "건의 정책이 업데이트되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "업데이트된 정책이 없습니다. ID를 확인해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "입력값이 올바르지 않습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
    }
}
