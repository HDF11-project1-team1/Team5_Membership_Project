package ui.panels.benefit;

import benefit.dto.RewardHistoryDto;
import benefit.service.BenefitService;
import ui.UIConstants;
import ui.MainFrame;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BenefitPanel extends JPanel {
    private BenefitService benefitService;
    private JTabbedPane tabs;

    public BenefitPanel(MainFrame mainFrame) {
        this.benefitService = new BenefitService();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 20));
        panel.setOpaque(false);
        JLabel title = new JLabel("혜택 이용 관리");
        title.setFont(UIConstants.HEADER_FONT);
        title.setForeground(UIConstants.TEXT_MAIN);
        panel.add(title);
        return panel;
    }

    public void setTab(int index) {
        if (tabs != null && index >= 0 && index < tabs.getTabCount()) {
            tabs.setSelectedIndex(index);
        }
    }

    private JTabbedPane createMainContent() {
        tabs = new JTabbedPane();
        tabs.setFont(UIConstants.BODY_BOLD_FONT);
        tabs.setBackground(Color.WHITE);
        tabs.setOpaque(true);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        tabs.addTab("라운지 이용", createLoungeTab());
        tabs.addTab("Cafe-H 조회", createCafeHTab());
        tabs.addTab("차량 등록/변경", createVehicleTab());
        tabs.addTab("그린 차량 지점", createGreenBranchTab());
        tabs.addTab("주차 확인", createParkingOnlyTab());
        tabs.addTab("발레파킹 확인", createValetOnlyTab());
        tabs.addTab("특별할인 조회", createDiscountOnlyTab());
        tabs.addTab("리워드 이력", createRewardOnlyTab());

        return tabs;
    }

    private Component createLoungeTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("라운지 이용 가능 여부 확인"));
        RoundedTextField name = addLabeledField(p, "회원 이름");
        RoundedTextField branch = addLabeledField(p, "지점명 (예: 압구정본점)");
        RoundedTextField lounge = addLabeledField(p, "라운지명 (예: 쟈스민 블랙 라운지)");
        
        RoundedButton btn = new RoundedButton("입장 가능 확인", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn.addActionListener(e -> {
            boolean ok = benefitService.getLoungePolicyAvailability(name.getText(), branch.getText(), lounge.getText());
            showResult(ok ? "✅ 입장 가능합니다." : "❌ 이용 대상이 아니거나 이미 이용하셨습니다.");
        });
        p.add(createBtnWrapper(btn));
        return createScroll(p);
    }

    private Component createCafeHTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("Cafe-H 커피 지급수 조회"));
        RoundedTextField grade = addLabeledField(p, "멤버십 등급 (예: GREEN 1)");

        RoundedButton btn = new RoundedButton("지급수 조회", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn.addActionListener(e -> {
            int count = benefitService.getCafeHPolicyCount(grade.getText());
            showResult("☕ " + grade.getText() + " 등급의 월간 커피 지급수: " + count + "개");
        });
        p.add(createBtnWrapper(btn));
        return createScroll(p);
    }

    private Component createVehicleTab() {
        JPanel p = createBaseTab();
        
        p.add(createSubTitle("차량 신규 등록"));
        RoundedTextField name1 = addLabeledField(p, "회원 이름");
        RoundedTextField car1 = addLabeledField(p, "신규 차량 번호");
        RoundedButton btn1 = new RoundedButton("신규 등록 실행", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn1.addActionListener(e -> {
            String res = benefitService.registerVehicle(name1.getText(), car1.getText());
            showResult(res);
        });
        p.add(createBtnWrapper(btn1));
        
        p.add(Box.createVerticalStrut(30));
        p.add(new JSeparator());
        p.add(Box.createVerticalStrut(20));

        p.add(createSubTitle("차량 정보 변경"));
        RoundedTextField name2 = addLabeledField(p, "회원 이름");
        RoundedTextField oldCar = addLabeledField(p, "기존 차량 번호");
        RoundedTextField newCar = addLabeledField(p, "새로운 차량 번호");
        
        RoundedButton btn2 = new RoundedButton("정보 변경 실행", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        btn2.addActionListener(e -> {
            String res = benefitService.updateVehicle(name2.getText(), oldCar.getText(), newCar.getText());
            showResult(res);
        });
        p.add(createBtnWrapper(btn2));
        
        return createScroll(p);
    }

    private Component createGreenBranchTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("그린 차량 주이용 지점 등록"));
        RoundedTextField name = addLabeledField(p, "회원 이름");
        RoundedTextField branch = addLabeledField(p, "등록할 지점명");
        RoundedButton btn = new RoundedButton("지점 등록/변경", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn.addActionListener(e -> showResult(benefitService.updateGreenBranch(name.getText(), branch.getText())));
        p.add(createBtnWrapper(btn));
        return createScroll(p);
    }

    private Component createParkingOnlyTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("무료주차 가능 여부 확인"));
        RoundedTextField branch = addLabeledField(p, "방문 지점명");
        RoundedTextField name = addLabeledField(p, "회원 이름");
        RoundedTextField car = addLabeledField(p, "차량 번호");

        RoundedButton btn = new RoundedButton("무료주차 확인", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn.addActionListener(e -> showResult("🅿️ " + benefitService.getFreeParkingAvailability(branch.getText(), name.getText(), car.getText())));
        p.add(createBtnWrapper(btn));
        return createScroll(p);
    }

    private Component createValetOnlyTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("발레파킹 가능 여부 확인"));
        RoundedTextField branch = addLabeledField(p, "방문 지점명");
        RoundedTextField name = addLabeledField(p, "회원 이름");
        RoundedTextField car = addLabeledField(p, "차량 번호");

        RoundedButton btn = new RoundedButton("발레파킹 확인", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        btn.addActionListener(e -> showResult("🛎️ " + benefitService.getValetParkingAvailability(branch.getText(), name.getText(), car.getText())));
        p.add(createBtnWrapper(btn));
        return createScroll(p);
    }

    private Component createDiscountOnlyTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("특별할인 잔액 조회"));
        RoundedTextField name = addLabeledField(p, "회원 이름");
        RoundedButton btn = new RoundedButton("잔액 조회", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn.addActionListener(e -> showResult(name.getText() + "님의 잔액: " + String.format("%,d", benefitService.getSpecialDiscountBalance(name.getText())) + "원"));
        p.add(createBtnWrapper(btn));
        return createScroll(p);
    }

    private JPanel createRewardOnlyTab() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "금액", "지급일"}, 0);
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(createSubTitle("리워드 지급 이력 조회"));
        RoundedTextField name = addLabeledField(top, "회원 이름");
        RoundedButton btn = new RoundedButton("이력 조회", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        btn.addActionListener(e -> {
            model.setRowCount(0);
            List<RewardHistoryDto> list = benefitService.getRewardHistory(name.getText());
            for(RewardHistoryDto d : list) model.addRow(new Object[]{d.getRewardHistoryId(), String.format("%,d원", d.getRewardAmount()), d.getOfferDate()});
        });
        top.add(createBtnWrapper(btn));
        top.add(Box.createVerticalStrut(20));
        
        main.add(top, BorderLayout.NORTH);

        ui.components.StyledTable table = new ui.components.StyledTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    private JPanel createBaseTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        return p;
    }

    private JScrollPane createScroll(JPanel p) {
        JScrollPane s = new JScrollPane(p);
        s.setBorder(BorderFactory.createEmptyBorder());
        s.getVerticalScrollBar().setUnitIncrement(20);
        return s;
    }

    private JLabel createSubTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.SUBHEADER_FONT);
        l.setForeground(UIConstants.TEXT_MAIN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return l;
    }

    private RoundedTextField addLabeledField(JPanel p, String label) {
        JLabel l = new JLabel(label);
        l.setFont(UIConstants.BODY_BOLD_FONT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        RoundedTextField f = new RoundedTextField(20);
        f.setMaximumSize(new Dimension(500, 45));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(f);
        p.add(Box.createVerticalStrut(15));
        return f;
    }

    private JPanel createBtnWrapper(JButton btn) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(btn);
        return wrapper;
    }

    private void showResult(String msg) {
        JOptionPane.showMessageDialog(this, msg, "처리 결과", JOptionPane.INFORMATION_MESSAGE);
    }
}
