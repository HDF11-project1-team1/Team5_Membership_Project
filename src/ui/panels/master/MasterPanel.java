package ui.panels.master;

import ui.UIConstants;
import ui.MainFrame;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import ui.components.StyledTable;
import master.service.*;
import master.dto.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MasterPanel extends JPanel implements ui.Refreshable {
    private MainFrame mainFrame;
    private BrandService brandService = new BrandService();
    private BranchService branchService = new BranchService();
    private LoungeService loungeService = new LoungeService();
    private PaymentService paymentService = new PaymentService();
    private CategoryService categoryService = new CategoryService();
    private JTabbedPane tabbedPane;
    private java.util.List<Runnable> refreshActions = new java.util.ArrayList<>();

    public MasterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 상단 타이틀
        JLabel titleLabel = new JLabel("기준 정보 관리");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);
        add(titleLabel, BorderLayout.NORTH);

        // 중앙 탭 패널
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.BODY_BOLD_FONT);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("브랜드 관리", createBrandTab());
        tabbedPane.addTab("지점 관리", createBranchTab());
        tabbedPane.addTab("라운지 관리", createLoungeTab());
        tabbedPane.addTab("결제수단 관리", createPaymentTab());
        tabbedPane.addTab("카테고리 관리", createCategoryTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // 1. 브랜드 관리 탭
    private JPanel createBrandTab() {
        String[] cols = {"ID", "카테고리 ID", "브랜드명"};
        return createManagementPanel("브랜드 목록", cols, 
            () -> {
                List<BrandDto> list = brandService.getBrandList();
                Object[][] data = new Object[list.size()][3];
                for(int i=0; i<list.size(); i++) {
                    data[i][0] = list.get(i).getBrandId();
                    data[i][1] = list.get(i).getCategoryId();
                    data[i][2] = list.get(i).getBrandName();
                }
                return data;
            },
            new String[]{"브랜드명", "카테고리 ID"},
            (fields) -> brandService.registerBrand(fields[0], Integer.parseInt(fields[1]))
        );
    }

    // 2. 지점 관리 탭
    private JPanel createBranchTab() {
        String[] cols = {"ID", "지점명", "위치(주소)"};
        return createManagementPanel("지점 목록", cols, 
            () -> {
                List<BranchDto> list = branchService.getBranchList();
                Object[][] data = new Object[list.size()][3];
                for(int i=0; i<list.size(); i++) {
                    data[i][0] = list.get(i).getBranchId();
                    data[i][1] = list.get(i).getBranchName();
                    data[i][2] = list.get(i).getBranchAddress();
                }
                return data;
            },
            new String[]{"지점명", "위치(주소)"},
            (fields) -> branchService.registerBranch(fields[0], fields[1])
        );
    }

    // 3. 라운지 관리 탭
    private JPanel createLoungeTab() {
        String[] cols = {"ID", "라운지명"};
        return createManagementPanel("라운지 목록", cols, 
            () -> {
                List<LoungeDto> list = loungeService.findLoungeList();
                Object[][] data = new Object[list.size()][2];
                for(int i=0; i<list.size(); i++) {
                    data[i][0] = list.get(i).getLoungeId();
                    data[i][1] = list.get(i).getLoungeName();
                }
                return data;
            },
            new String[]{"라운지명"},
            (fields) -> loungeService.registerLounge(fields[0])
        );
    }

    // 4. 결제수단 관리 탭
    private JPanel createPaymentTab() {
        String[] cols = {"ID", "결제수단명"};
        return createManagementPanel("결제수단 목록", cols, 
            () -> {
                List<PaymentDto> list = paymentService.getPaymentList();
                Object[][] data = new Object[list.size()][2];
                for(int i=0; i<list.size(); i++) {
                    data[i][0] = list.get(i).getPaymentId();
                    data[i][1] = list.get(i).getPaymentType();
                }
                return data;
            },
            new String[]{"결제수단명"},
            (fields) -> paymentService.registerPayment(fields[0])
        );
    }

    // 5. 카테고리 관리 탭
    private JPanel createCategoryTab() {
        String[] cols = {"ID", "카테고리명"};
        return createManagementPanel("카테고리 목록", cols, 
            () -> {
                List<CategoryDto> list = categoryService.findCategoryList();
                Object[][] data = new Object[list.size()][2];
                for(int i=0; i<list.size(); i++) {
                    data[i][0] = list.get(i).getCategoryId();
                    data[i][1] = list.get(i).getCategoryName();
                }
                return data;
            },
            new String[]{"카테고리명"},
            (fields) -> categoryService.registerCategory(fields[0])
        );
    }

    private JPanel createManagementPanel(String listTitle, String[] cols, DataFetcher fetcher, String[] fieldLabels, SaveAction saver) {
        JPanel mainP = new JPanel(new BorderLayout(20, 0));
        mainP.setBackground(Color.WHITE);
        mainP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel leftP = new JPanel(new BorderLayout(0, 10));
        leftP.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(listTitle);
        lbl.setFont(UIConstants.SUBHEADER_FONT);
        headerPanel.add(lbl, BorderLayout.WEST);

        Object[][] initialData;
        try {
            initialData = fetcher.fetch();
        } catch (Exception e) {
            initialData = new Object[0][cols.length];
            System.err.println(listTitle + " 데이터 로딩 실패: " + e.getMessage());
        }

        DefaultTableModel model = new DefaultTableModel(initialData, cols);
        StyledTable table = new StyledTable(model);
        leftP.add(new JScrollPane(table), BorderLayout.CENTER);

        // 새로고침 액션 정의
        Runnable refreshAction = () -> {
            try {
                model.setDataVector(fetcher.fetch(), cols);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainP, "새로고침 중 오류 발생: " + ex.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
            }
        };
        refreshActions.add(refreshAction);

        RoundedButton refreshBtn = new RoundedButton("새로고침", UIConstants.SECONDARY_BTN_COLOR, UIConstants.SECONDARY_BTN_HOVER, UIConstants.SECONDARY_BTN_TEXT);
        refreshBtn.setFont(UIConstants.BODY_BOLD_FONT);
        refreshBtn.addActionListener(e -> {
            refreshAction.run();
            JOptionPane.showMessageDialog(mainP, "새로고침 완료!");
        });
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        leftP.add(headerPanel, BorderLayout.NORTH);

        JPanel rightP = new JPanel();
        rightP.setLayout(new BoxLayout(rightP, BoxLayout.Y_AXIS));
        rightP.setBackground(Color.WHITE);
        rightP.setPreferredSize(new Dimension(300, 0));
        rightP.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, UIConstants.LINE_COLOR),
            BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));

        JLabel formLbl = new JLabel("신규 등록");
        formLbl.setFont(UIConstants.SUBHEADER_FONT);
        formLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightP.add(formLbl);
        rightP.add(Box.createVerticalStrut(20));

        RoundedTextField[] fields = new RoundedTextField[fieldLabels.length];
        for(int i=0; i<fieldLabels.length; i++) {
            JLabel flbl = new JLabel(fieldLabels[i]);
            flbl.setFont(UIConstants.BODY_BOLD_FONT);
            flbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightP.add(flbl);
            rightP.add(Box.createVerticalStrut(5));

            fields[i] = new RoundedTextField(20);
            fields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            fields[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            rightP.add(fields[i]);
            rightP.add(Box.createVerticalStrut(15));
        }

        RoundedButton saveBtn = new RoundedButton("등록하기", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER, Color.WHITE);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            try {
                String[] values = new String[fields.length];
                for(int i=0; i<fields.length; i++) values[i] = fields[i].getText();
                if(saver.save(values)) {
                    JOptionPane.showMessageDialog(this, "등록 성공!");
                    refreshAction.run();
                    for(RoundedTextField f : fields) f.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // 콘솔에 상세 에러 출력
                String msg = ex.getMessage();
                if (ex.getCause() != null) {
                    msg += "\n원인: " + ex.getCause().getMessage();
                }
                JOptionPane.showMessageDialog(this, "오류 발생: " + msg, "에러", JOptionPane.ERROR_MESSAGE);
            }
        });
        rightP.add(saveBtn);

        mainP.add(leftP, BorderLayout.CENTER);
        mainP.add(rightP, BorderLayout.EAST);

        return mainP;
    }

    interface DataFetcher {
        Object[][] fetch() throws Exception;
    }

    interface SaveAction {
        boolean save(String[] fields) throws Exception;
    }

    @Override
    public void refresh() {
        if (tabbedPane != null && tabbedPane.getSelectedIndex() >= 0 && tabbedPane.getSelectedIndex() < refreshActions.size()) {
            refreshActions.get(tabbedPane.getSelectedIndex()).run();
        }
    }
}
