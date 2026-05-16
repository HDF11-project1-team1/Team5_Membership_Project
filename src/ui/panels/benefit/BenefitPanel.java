package ui.panels.benefit;

import benefit.dto.RewardHistoryDto;
import benefit.service.BenefitService;
import user.dto.UserDto;
import user.service.UserService;
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
    private UserService userService;
    
    // 글로벌 선택 회원 정보
    private String selectedUserName = "";
    private String selectedUserGrade = "";
    private String selectedUserCar = "";
    
    // UI 컴포넌트
    private JLabel userInfoLabel;
    private RoundedTextField globalSearchField;
    
    // 각 탭의 필드들 (자동 동기화 대상)
    private RoundedTextField[] nameFields;

    public BenefitPanel(MainFrame mainFrame) {
        this.benefitService = new BenefitService();
        this.userService = new UserService();
        this.nameFields = new RoundedTextField[10];

        setLayout(new BorderLayout(0, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createTopSearchPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createTopSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel title = new JLabel("혜택 이용 관리  ");
        title.setFont(UIConstants.HEADER_FONT);
        title.setForeground(UIConstants.TEXT_MAIN);
        left.add(title);

        globalSearchField = new RoundedTextField(15);
        globalSearchField.setPlaceholder("대상 회원 성함 입력");
        left.add(globalSearchField);

        RoundedButton searchBtn = new RoundedButton("회원 조회", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        searchBtn.addActionListener(e -> searchUser());
        left.add(Box.createHorizontalStrut(10));
        left.add(searchBtn);
        panel.add(left, BorderLayout.WEST);

        userInfoLabel = new JLabel("조회된 회원 정보가 없습니다.");
        userInfoLabel.setFont(UIConstants.BODY_BOLD_FONT);
        userInfoLabel.setForeground(UIConstants.TEXT_SECONDARY);
        panel.add(userInfoLabel, BorderLayout.EAST);

        return panel;
    }

    private void searchUser() {
        String name = globalSearchField.getText().trim();
        if (name.isEmpty()) return;

        userInfoLabel.setText("조회 중...");
        userInfoLabel.setForeground(UIConstants.TEXT_SECONDARY);

        new SwingWorker<UserDto, Void>() {
            @Override
            protected UserDto doInBackground() throws Exception {
                List<UserDto> users = userService.getAllUsers();
                for (UserDto u : users) {
                    if (u.getName().equals(name)) return u;
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    UserDto found = get();
                    if (found != null) {
                        selectedUserName = found.getName();
                        String carNum = benefitService.getCarNumber(selectedUserName);
                        selectedUserCar = carNum != null ? carNum : "미등록";
                        selectedUserGrade = getGradeName(found.getMembershipId());

                        userInfoLabel.setText("<html><font color='#4E5968'>선택 회원:</font> <b>" + selectedUserName + 
                                     "</b> <font color='#4E5968'>| 등급:</font> <font color='#3182F6'><b>" + selectedUserGrade + 
                                     "</b></font> <font color='#4E5968'>| 차량:</font> <b>" + selectedUserCar + "</b></html>");
                        userInfoLabel.setForeground(UIConstants.TEXT_MAIN);
                        
                        for(RoundedTextField f : nameFields) {
                            if(f != null) f.setText(selectedUserName);
                        }
                    } else {
                        userInfoLabel.setText("회원을 찾을 수 없습니다.");
                        JOptionPane.showMessageDialog(BenefitPanel.this, "해당 이름의 회원을 찾을 수 없습니다.");
                    }
                } catch (Exception e) {
                    userInfoLabel.setText("조회 오류 발생");
                }
            }
        }.execute();
    }

    private String getGradeName(int id) {
        String[] grades = {"-", "JASMIN SIGNATURE", "JASMIN BLACK", "JASMIN BLUE", "JASMIN", "SAGE", "CLUB YP", "GREEN 1", "GREEN 2", "EARLY GREEN", "BASIC"};
        return (id >= 1 && id <= 10) ? grades[id] : "BASIC";
    }

    private JTabbedPane createMainContent() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIConstants.BODY_BOLD_FONT);
        tabs.setBackground(Color.WHITE);

        tabs.addTab("라운지", createLoungeTab());
        tabs.addTab("Cafe-H", createCafeHTab());
        tabs.addTab("차량관리", createVehicleTab());
        tabs.addTab("그린 차량 지점 등록", createGreenBranchTab());
        tabs.addTab("주차 & 발레", createParkingTab());
        tabs.addTab("할인 & 리워드", createRewardTab());

        return tabs;
    }

    private Component createLoungeTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("라운지 이용 가능 여부 확인"));
        RoundedTextField name = addLabeledField(p, "회원 이름", 0);
        RoundedTextField branch = addLabeledField(p, "지점명 (예: 압구정본점)", -1);
        RoundedTextField lounge = addLabeledField(p, "라운지명 (예: 쟈스민 블랙 라운지)", -1);
        
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
        RoundedTextField grade = addLabeledField(p, "멤버십 등급 (예: GREEN 1)", -1);
        if(!selectedUserGrade.isEmpty()) grade.setText(selectedUserGrade);

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
        RoundedTextField name1 = addLabeledField(p, "회원 이름", 1);
        RoundedTextField car1 = addLabeledField(p, "신규 차량 번호", -1);
        RoundedButton btn1 = new RoundedButton("신규 등록 실행", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn1.addActionListener(e -> {
            String n = name1.getText();
            String c = car1.getText();
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() { return benefitService.registerVehicle(n, c); }
                @Override protected void done() {
                    try {
                        String res = get();
                        showResult(res);
                        if (res.contains("성공") || res.contains("완료")) {
                            globalSearchField.setText(n);
                            searchUser();
                        }
                    } catch (Exception ex) { showResult("오류: " + ex.getMessage()); }
                }
            }.execute();
        });
        p.add(createBtnWrapper(btn1));
        
        p.add(Box.createVerticalStrut(40));
        p.add(new JSeparator());
        p.add(Box.createVerticalStrut(20));

        p.add(createSubTitle("차량 정보 변경"));
        RoundedTextField name2 = addLabeledField(p, "회원 이름", 2);
        RoundedTextField oldCar = addLabeledField(p, "기존 차량 번호", -1);
        if(!selectedUserCar.equals("미등록")) oldCar.setText(selectedUserCar);
        RoundedTextField newCar = addLabeledField(p, "새로운 차량 번호", -1);
        
        RoundedButton btn2 = new RoundedButton("정보 변경 실행", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        btn2.addActionListener(e -> {
            String n = name2.getText();
            String oc = oldCar.getText();
            String nc = newCar.getText();
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() { return benefitService.updateVehicle(n, oc, nc); }
                @Override protected void done() {
                    try {
                        String res = get();
                        showResult(res);
                        if (res.contains("성공") || res.contains("완료")) {
                            globalSearchField.setText(n);
                            searchUser();
                        }
                    } catch (Exception ex) { showResult("오류: " + ex.getMessage()); }
                }
            }.execute();
        });
        p.add(createBtnWrapper(btn2));
        
        return createScroll(p);
    }

    private Component createGreenBranchTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("그린 차량 주이용 지점 등록"));
        RoundedTextField name = addLabeledField(p, "회원 이름", 3);
        RoundedTextField branch = addLabeledField(p, "등록할 지점명", -1);
        RoundedButton btn = new RoundedButton("지점 등록/변경", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn.addActionListener(e -> showResult(benefitService.updateGreenBranch(name.getText(), branch.getText())));
        p.add(createBtnWrapper(btn));
        return createScroll(p);
    }

    private Component createParkingTab() {
        JPanel p = createBaseTab();
        p.add(createSubTitle("무료주차 및 발레파킹 가능 여부 확인"));
        RoundedTextField branch = addLabeledField(p, "방문 지점명", -1);
        RoundedTextField name = addLabeledField(p, "회원 이름", 4);
        RoundedTextField car = addLabeledField(p, "차량 번호", -1);
        if(!selectedUserCar.equals("미등록")) car.setText(selectedUserCar);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        RoundedButton btn1 = new RoundedButton("무료주차 확인", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn1.addActionListener(e -> showResult("🅿️ " + benefitService.getFreeParkingAvailability(branch.getText(), name.getText(), car.getText())));
        
        RoundedButton btn2 = new RoundedButton("발레파킹 확인", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        btn2.addActionListener(e -> showResult("🛎️ " + benefitService.getValetParkingAvailability(branch.getText(), name.getText(), car.getText())));
        
        btns.add(btn1); btns.add(btn2);
        p.add(btns);
        return createScroll(p);
    }

    private JPanel createRewardTab() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "금액", "지급일"}, 0);
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(createSubTitle("특별할인 잔액 조회"));
        RoundedTextField name1 = addLabeledField(top, "회원 이름", 5);
        RoundedButton btn1 = new RoundedButton("잔액 조회", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        btn1.addActionListener(e -> showResult(name1.getText() + "님의 잔액: " + String.format("%,d", benefitService.getSpecialDiscountBalance(name1.getText())) + "원"));
        top.add(createBtnWrapper(btn1));

        top.add(Box.createVerticalStrut(30));
        top.add(new JSeparator());
        top.add(Box.createVerticalStrut(20));

        top.add(createSubTitle("리워드 지급 이력 조회"));
        RoundedTextField name2 = addLabeledField(top, "회원 이름", 6);
        RoundedButton btn2 = new RoundedButton("이력 조회", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        btn2.addActionListener(e -> {
            model.setRowCount(0);
            List<RewardHistoryDto> list = benefitService.getRewardHistory(name2.getText());
            for(RewardHistoryDto d : list) model.addRow(new Object[]{d.getRewardHistoryId(), String.format("%,d원", d.getRewardAmount()), d.getOfferDate()});
        });
        top.add(createBtnWrapper(btn2));
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

    private RoundedTextField addLabeledField(JPanel p, String label, int nameIdx) {
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
        if(nameIdx >= 0) nameFields[nameIdx] = f;
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
