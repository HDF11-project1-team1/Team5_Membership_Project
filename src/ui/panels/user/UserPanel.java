package ui.panels.user;

import ui.UIConstants;
import ui.MainFrame;
import ui.components.RoundedButton;
import user.dto.UserDto;
import user.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

import ui.components.RoundedTextField;
import user.dto.UserTotalInfoDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserPanel extends JPanel {
    private MainFrame mainFrame;
    private UserService userService;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private List<UserDto> currentUserList;

    // 상세 정보 및 폼 관련 컴포넌트
    private JPanel detailContainer;
    private RoundedTextField nameField, phoneField, genderField, birthField, cardNumField, cardPeriodField, membershipField;
    private JCheckBox employeeCheck;
    private JLabel detailTitle;
    private int selectedUserId = -1;

    // 상세 실적 레이블
    private JLabel gradeLabel, vipAmountLabel, mileageLabel, rewardLabel, visitLabel, purchaseLabel, discountLabel;
    
    private JComboBox<String> membershipFilter;

    public UserPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userService = new UserService();
        this.currentUserList = new ArrayList<>();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createTableArea(), BorderLayout.CENTER);
        add(createDetailArea(), BorderLayout.EAST);

        loadUserData();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("회원 관리");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        
        // 멤버십 필터 추가
        JLabel filterLabel = new JLabel("멤버십 필터:");
        filterLabel.setFont(UIConstants.BODY_BOLD_FONT);
        actionPanel.add(filterLabel);
        
        String[] filters = {
            "전체 보기", 
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
        membershipFilter.addActionListener(e -> loadUserData());
        actionPanel.add(membershipFilter);

        RoundedButton newUserBtn = new RoundedButton("신규 등록", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        newUserBtn.addActionListener(e -> prepareNewUser());
        
        RoundedButton refreshBtn = new RoundedButton("새로고침", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        refreshBtn.addActionListener(e -> {
            membershipFilter.setSelectedIndex(0);
            loadUserData();
        });
        
        actionPanel.add(newUserBtn);
        actionPanel.add(refreshBtn);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createTableArea() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(UIConstants.LINE_COLOR, 1));

        String[] columnNames = {"ID", "이름", "성별", "전화번호", "멤버십ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(UIConstants.BODY_FONT);
        userTable.setRowHeight(55); // 행 높이 상향
        userTable.setShowVerticalLines(false);
        userTable.setGridColor(UIConstants.LINE_COLOR);
        userTable.setSelectionBackground(new Color(0xF2F4F6));
        userTable.setSelectionForeground(UIConstants.TEXT_MAIN);
        
        // 셀 내부 패딩(여백) 설정을 위한 커스텀 렌더러
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // 좌우 패딩 20px
                return label;
            }
        };
        userTable.setDefaultRenderer(Object.class, cellRenderer);

        userTable.getTableHeader().setFont(UIConstants.BODY_BOLD_FONT);
        userTable.getTableHeader().setBackground(Color.WHITE);
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 50)); // 헤더 높이도 상향
        
        // 헤더 패딩 설정
        userTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
                label.setBackground(Color.WHITE);
                label.setFont(UIConstants.BODY_BOLD_FONT);
                return label;
            }
        });
        
        // 선택 이벤트 리스너
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = userTable.getSelectedRow();
                if (row >= 0) {
                    showUserDetail(currentUserList.get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 컬럼 너비 설정 (텍스트 잘림 방지)
        userTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(120); // 이름
        userTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // 성별
        userTable.getColumnModel().getColumn(3).setPreferredWidth(180); // 전화번호
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 멤버십ID

        return tablePanel;
    }

    private JPanel createDetailArea() {
        detailContainer = new JPanel();
        detailContainer.setPreferredSize(new Dimension(350, 0));
        detailContainer.setBackground(Color.WHITE);
        detailContainer.setLayout(new BorderLayout());
        detailContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.LINE_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        detailTitle = new JLabel("회원 정보");
        detailTitle.setFont(UIConstants.SUBHEADER_FONT);
        detailTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        detailContainer.add(detailTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // 1. 상세 실적 정보 영역 (조회 전용)
        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        statsPanel.setBackground(new Color(0xF8F9FA));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.LINE_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        gradeLabel = createStatLabel("현재 등급", "-");
        vipAmountLabel = createStatLabel("VIP 선정 적립금", "0원");
        mileageLabel = createStatLabel("적립된 마일리지", "0p");
        rewardLabel = createStatLabel("누적 리워드", "0원");
        visitLabel = createStatLabel("내점일수", "0일");
        purchaseLabel = createStatLabel("구매횟수", "0회");
        discountLabel = createStatLabel("특별할인 잔액", "0원");
        
        statsPanel.add(gradeLabel);
        statsPanel.add(vipAmountLabel);
        statsPanel.add(mileageLabel);
        statsPanel.add(rewardLabel);
        statsPanel.add(visitLabel);
        statsPanel.add(purchaseLabel);
        statsPanel.add(discountLabel);
        statsPanel.add(new JLabel("")); // 그리드 정렬용 빈 라벨
        
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2. 기본 정보 입력 폼
        nameField = addField(contentPanel, "이름");
        genderField = addField(contentPanel, "성별 (M/F)");
        phoneField = addField(contentPanel, "전화번호");
        birthField = addField(contentPanel, "생년월일 (yyyy-MM-dd)");
        membershipField = addField(contentPanel, "멤버십 ID");
        cardNumField = addField(contentPanel, "카드번호");
        cardPeriodField = addField(contentPanel, "카드 유효기간 (yyyy-MM-dd)");
        
        employeeCheck = new JCheckBox("임직원 여부");
        employeeCheck.setBackground(Color.WHITE);
        employeeCheck.setFont(UIConstants.BODY_FONT);
        contentPanel.add(employeeCheck);
        contentPanel.add(Box.createVerticalStrut(20));

        JScrollPane scrollForm = new JScrollPane(contentPanel);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        detailContainer.add(scrollForm, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 1, 10, 0));
        btnPanel.setOpaque(false);
        RoundedButton saveBtn = new RoundedButton("정보 저장", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        saveBtn.addActionListener(e -> saveUser());
        btnPanel.add(saveBtn);
        
        detailContainer.add(btnPanel, BorderLayout.SOUTH);

        return detailContainer;
    }

    private JLabel createStatLabel(String title, String initialValue) {
        JLabel label = new JLabel("<html><font color='#8B95A1' size='3'>" + title + "</font><br><font color='#191F28' size='4'><b>" + initialValue + "</b></font></html>");
        label.setFont(UIConstants.CAPTION_FONT);
        return label;
    }

    private void updateStatLabels(UserTotalInfoDto info) {
        gradeLabel.setText("<html><font color='#8B95A1' size='3'>현재 등급</font><br><font color='#191F28' size='4'><b>" + getGradeName(info.getMembershipId()) + "</b></font></html>");
        vipAmountLabel.setText("<html><font color='#8B95A1' size='3'>VIP 선정 적립금</font><br><font color='#191F28' size='4'><b>" + String.format("%,d", info.getVipAmount()) + "원</b></font></html>");
        mileageLabel.setText("<html><font color='#8B95A1' size='3'>적립된 마일리지</font><br><font color='#191F28' size='4'><b>" + info.getMileageAmount() + " p</b></font></html>");
        rewardLabel.setText("<html><font color='#8B95A1' size='3'>누적 리워드</font><br><font color='#191F28' size='4'><b>" + String.format("%,d", info.getTotalRewardAmount()) + "원</b></font></html>");
        visitLabel.setText("<html><font color='#8B95A1' size='3'>내점일수</font><br><font color='#191F28' size='4'><b>" + info.getVisitDateCount() + "일</b></font></html>");
        purchaseLabel.setText("<html><font color='#8B95A1' size='3'>구매횟수</font><br><font color='#191F28' size='4'><b>" + info.getPurchaseDateCount() + "회</b></font></html>");
        discountLabel.setText("<html><font color='#8B95A1' size='3'>특별할인 잔액</font><br><font color='#191F28' size='4'><b>" + String.format("%,d", info.getRemainSpecialDiscountAmount()) + "원</b></font></html>");
    }

    private String getGradeName(int id) {
        switch (id) {
            case 1: return "JASMIN SIGNATURE";
            case 2: return "JASMIN BLACK";
            case 3: return "JASMIN BLUE";
            case 4: return "JASMIN";
            case 5: return "SAGE";
            case 6: return "CLUB YP";
            case 7: return "GREEN 1";
            case 8: return "GREEN 2";
            case 9: return "EARLY GREEN";
            case 10: return "BASIC";
            default: return "알 수 없음";
        }
    }

    private RoundedTextField addField(JPanel parent, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.CAPTION_FONT);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        parent.add(label);
        parent.add(Box.createVerticalStrut(5));
        
        RoundedTextField field = new RoundedTextField(20);
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));
        return field;
    }

    private void loadUserData() {
        tableModel.setRowCount(0);
        try {
            int filterIndex = membershipFilter.getSelectedIndex();
            if (filterIndex == 0) {
                currentUserList = userService.getAllUsers();
            } else {
                currentUserList = userService.getUsersByMembershipId(filterIndex);
            }
            
            for (UserDto user : currentUserList) {
                tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getName(),
                    user.getGender(),
                    user.getPhoneNumber(),
                    user.getMembershipId()
                });
            }
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "데이터 로드 실패: " + e.getMessage());
        }
    }

    private void showUserDetail(UserDto user) {
        selectedUserId = user.getUserId();
        detailTitle.setText("회원 정보 수정");
        nameField.setText(user.getName());
        genderField.setText(user.getGender());
        phoneField.setText(user.getPhoneNumber());
        birthField.setText(user.getBirth().toLocalDate().toString());
        membershipField.setText(String.valueOf(user.getMembershipId()));
        cardNumField.setText(user.getCardNumber());
        cardPeriodField.setText(user.getCardPeriod().toLocalDate().toString());
        employeeCheck.setSelected(user.isEmployeeYn());
        
        // 상세 실적 정보 추가 조회
        try {
            UserTotalInfoDto detail = userService.getUserDetailByNameAndBirth(user.getName(), user.getBirth().toLocalDate());
            if (detail != null) {
                updateStatLabels(detail);
            }
        } catch (Exception e) {
            System.err.println("상세 정보 조회 실패: " + e.getMessage());
        }
    }

    private void prepareNewUser() {
        userTable.clearSelection();
        selectedUserId = -1;
        detailTitle.setText("신규 회원 등록");
        clearForm();
    }

    private void clearForm() {
        nameField.setText("");
        genderField.setText("");
        phoneField.setText("");
        birthField.setText("");
        membershipField.setText("");
        cardNumField.setText("");
        cardPeriodField.setText("");
        employeeCheck.setSelected(false);
        
        gradeLabel.setText("<html><font color='#8B95A1' size='3'>현재 등급</font><br><font color='#191F28' size='4'><b>-</b></font></html>");
        vipAmountLabel.setText("<html><font color='#8B95A1' size='3'>VIP 선정 적립금</font><br><font color='#191F28' size='4'><b>0원</b></font></html>");
        mileageLabel.setText("<html><font color='#8B95A1' size='3'>적립된 마일리지</font><br><font color='#191F28' size='4'><b>0p</b></font></html>");
        rewardLabel.setText("<html><font color='#8B95A1' size='3'>누적 리워드</font><br><font color='#191F28' size='4'><b>0원</b></font></html>");
        visitLabel.setText("<html><font color='#8B95A1' size='3'>내점일수</font><br><font color='#191F28' size='4'><b>0일</b></font></html>");
        purchaseLabel.setText("<html><font color='#8B95A1' size='3'>구매횟수</font><br><font color='#191F28' size='4'><b>0회</b></font></html>");
        discountLabel.setText("<html><font color='#8B95A1' size='3'>특별할인 잔액</font><br><font color='#191F28' size='4'><b>0원</b></font></html>");
    }

    private void saveUser() {
        try {
            UserDto user = new UserDto();
            user.setUserId(selectedUserId);
            user.setName(nameField.getText());
            user.setGender(genderField.getText());
            user.setPhoneNumber(phoneField.getText());
            user.setBirth(LocalDate.parse(birthField.getText()).atStartOfDay());
            user.setMembershipId(Integer.parseInt(membershipField.getText()));
            user.setCardNumber(cardNumField.getText());
            user.setCardPeriod(LocalDate.parse(cardPeriodField.getText()).atStartOfDay());
            user.setEmployeeYn(employeeCheck.isSelected());

            if (selectedUserId == -1) {
                userService.registerUser(user);
                JOptionPane.showMessageDialog(this, "회원이 등록되었습니다.");
            } else {
                userService.updateUser(user);
                JOptionPane.showMessageDialog(this, "회원 정보가 수정되었습니다.");
            }
            loadUserData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "저장 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

