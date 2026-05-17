package ui.panels.stat;

import statistics.dto.StatDto;
import statistics.service.StatisticsService;
import ui.UIConstants;
import ui.components.RoundedButton;
import ui.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public class StatPanel extends JPanel implements ui.Refreshable {
    private StatisticsService statService;
    private JComboBox<Integer> yearCombo;
    private JComboBox<Integer> monthCombo;
    private JTabbedPane mainTabs;
    private DecimalFormat df = new DecimalFormat("#,###");

    public StatPanel() {
        this.statService = new StatisticsService();
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);

        // 1. 헤더 (타이틀 및 필터)
        add(createHeader(), BorderLayout.NORTH);

        // 2. 메인 탭 구성
        mainTabs = new JTabbedPane();
        mainTabs.setFont(UIConstants.BODY_BOLD_FONT);
        mainTabs.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        add(mainTabs, BorderLayout.CENTER);

        loadAllData();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.LINE_COLOR),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        JLabel title = new JLabel("통계 및 분석 Dashboard");
        title.setFont(UIConstants.HEADER_FONT);
        title.setForeground(UIConstants.TEXT_MAIN);
        panel.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        int currentYear = LocalDate.now().getYear();
        yearCombo = new JComboBox<>();
        for (int y = currentYear; y >= 2020; y--)
            yearCombo.addItem(y);
        monthCombo = new JComboBox<>();
        for (int m = 1; m <= 12; m++)
            monthCombo.addItem(m);
        monthCombo.setSelectedItem(LocalDate.now().getMonthValue());

        filterPanel.add(new JLabel("조회 기간:"));
        filterPanel.add(yearCombo);
        filterPanel.add(new JLabel("년"));
        filterPanel.add(monthCombo);
        filterPanel.add(new JLabel("월"));

        RoundedButton refreshBtn = new RoundedButton("데이터 조회", UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_HOVER,
                Color.WHITE);
        refreshBtn.addActionListener(e -> loadAllData());
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(refreshBtn);

        panel.add(filterPanel, BorderLayout.EAST);
        return panel;
    }

    private void loadAllData() {
        int y = (int) yearCombo.getSelectedItem();
        int m = (int) monthCombo.getSelectedItem();

        mainTabs.removeAll();
        mainTabs.addTab("종합 구매 통계", createGeneralTab(y, m));
        mainTabs.addTab("연령/성별 분석", createAgeGenderTab(y, m));
        mainTabs.addTab("구매 상위권 회원", createTopBuyersTab(y, m));
        mainTabs.addTab("혜택 이용률", createBenefitUsageTab(y, m));
        mainTabs.addTab("월별 실적 추이", createMonthlyTrendTab(y));

        mainTabs.revalidate();
        mainTabs.repaint();
    }

    @Override
    public void refresh() {
        loadAllData();
    }

    // 1. 종합 구매 통계 탭
    private Component createGeneralTab(int y, int m) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIConstants.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // 1행: 멤버십 등급별 비중 (단독 배치로 강조)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(new ChartContainer("멤버십 등급별 매출 비중", new SimplePieChart(statService.getStatByMembership(y, m))), gbc);

        // 2행: 지점 및 브랜드 실적 (가로 배치)
        gbc.gridy = 1; gbc.gridwidth = 1;
        List<StatDto> branchData = statService.getStatByBranch(y, m);
        ChartContainer branchChart = new ChartContainer("지점별 실적", new HorizontalBarChart(branchData));
        branchChart.setPreferredSize(new Dimension(500, Math.max(350, branchData.size() * 40 + 60)));
        gbc.gridx = 0; p.add(branchChart, gbc);

        List<StatDto> brandData = statService.getStatByBrand(y, m);
        ChartContainer brandChart = new ChartContainer("브랜드별 실적", new HorizontalBarChart(brandData));
        brandChart.setPreferredSize(new Dimension(500, Math.max(350, brandData.size() * 40 + 60)));
        gbc.gridx = 1; p.add(brandChart, gbc);

        // 3행: 결제수단 및 카테고리 비중 (가로 배치)
        gbc.gridy = 2; gbc.gridx = 0;
        p.add(new ChartContainer("결제수단별 비중", new SimplePieChart(statService.getStatByPayment(y, m))), gbc);
        
        gbc.gridx = 1;
        p.add(new ChartContainer("카테고리별 매출 비중", new SimplePieChart(statService.getStatByCategory(y, m))), gbc);

        // 4행: 카테고리별 상세 실적 (하단 전체 너비, 스크롤 가능)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        JPanel detailTable = createDetailTablePanel("카테고리별 상세 실적 목록", statService.getStatByCategory(y, m));
        detailTable.setPreferredSize(new Dimension(1000, 400));
        p.add(detailTable, gbc);

        JScrollPane scrollPane = new JScrollPane(p);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        return scrollPane;
    }

    private JPanel createDetailTablePanel(String title, List<StatDto> data) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.LINE_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(UIConstants.SUBHEADER_FONT);
        panel.add(lbl, BorderLayout.NORTH);

        String[] cols = { "항목", "구매 건수", "총 매출액", "평균 객단가" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (StatDto d : data) {
            long avg = d.getCount() > 0 ? d.getAmount() / d.getCount() : 0;
            model.addRow(new Object[] {
                    d.getLabel(),
                    df.format(d.getCount()),
                    df.format(d.getAmount()),
                    df.format(avg)
            });
        }
        panel.add(new JScrollPane(new StyledTable(model)), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(800, 300));
        return panel;
    }

    // 2. 구매 상위권 회원 탭
    private Component createTopBuyersTab(int y, int m) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("월간 구매 상위 10위 회원 목록");
        title.setFont(UIConstants.SUBHEADER_FONT);
        p.add(title, BorderLayout.NORTH);

        String[] cols = { "순위", "회원 정보 (이름/ID)", "구매 건수", "총 결제 금액", "평균 결제액" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        List<StatDto> top = statService.getTopBuyers(y, m);
        for (int i = 0; i < top.size(); i++) {
            StatDto d = top.get(i);
            long avg = d.getCount() > 0 ? d.getAmount() / d.getCount() : 0;
            model.addRow(new Object[] {
                    (i + 1) + "위",
                    d.getLabel(),
                    df.format(d.getCount()),
                    df.format(d.getAmount()),
                    df.format(avg)
            });
        }

        p.add(new JScrollPane(new StyledTable(model)), BorderLayout.CENTER);
        return p;
    }

    // 3. 혜택 이용률 탭
    private Component createBenefitUsageTab(int y, int m) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIConstants.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.weightx = 1.0;

        List<StatDto> data = statService.getBenefitUsageRate(y, m);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        ChartContainer chart = new ChartContainer("주요 혜택별 적용 비중 (구매 건수 기준)", new SimplePieChart(data));
        chart.setPreferredSize(new Dimension(800, 400));
        p.add(chart, gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        JPanel detail = createDetailTablePanel("혜택 적용 상세 내역", data);
        detail.setPreferredSize(new Dimension(800, 380));
        p.add(detail, gbc);

        JScrollPane scrollPane = new JScrollPane(p);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.setBorder(null);
        return scrollPane;
    }

    // 4. 연령/성별 분석 탭
    private Component createAgeGenderTab(int y, int m) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIConstants.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.weightx = 1.0;

        List<StatDto> data = translateLabels(statService.getVipChangeByAgeGender(y, m));

        // 1. 시각화 (위로 이동 및 크기 최적화)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        ChartContainer chart = new ChartContainer("연령/성별 구매 비중 시각화", new HorizontalBarChart(data));
        chart.setPreferredSize(new Dimension(800, Math.max(350, data.size() * 40 + 60)));
        p.add(chart, gbc);

        // 2. 매트릭스 (아래로 이동 및 크기 최적화)
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        JPanel matrix = createAgeGenderMatrixPanel("연령/성별 구매 분포 매트릭스", data);
        matrix.setPreferredSize(new Dimension(800, 420));
        p.add(matrix, gbc);

        JScrollPane scrollPane = new JScrollPane(p);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.setBorder(null);
        return scrollPane;
    }

    private List<StatDto> translateLabels(List<StatDto> data) {
        if (data == null)
            return null;
        for (StatDto d : data) {
            String label = d.getLabel();
            if (label == null)
                continue;
            if (label.equals("F"))
                d.setLabel("여성");
            else if (label.equals("M"))
                d.setLabel("남성");
            else {
                d.setLabel(label.replace(" F", " 여성").replace(" M", " 남성"));
            }
        }
        return data;
    }

    private JPanel createAgeGenderMatrixPanel(String title, List<StatDto> data) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.LINE_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(UIConstants.SUBHEADER_FONT);
        panel.add(lbl, BorderLayout.NORTH);

        // Matrix 구성: Key=연령대, Value=[여성건수, 여성금액, 남성건수, 남성금액]
        java.util.Map<String, long[]> matrix = new java.util.TreeMap<>();
        for (StatDto d : data) {
            String label = d.getLabel();
            int idx = label.indexOf("대");
            if (idx == -1)
                continue;
            String age = label.substring(0, idx + 1);
            boolean isFemale = label.contains("여성");

            long[] vals = matrix.computeIfAbsent(age, k -> new long[4]);
            if (isFemale) {
                vals[0] += d.getCount();
                vals[1] += d.getAmount();
            } else {
                vals[2] += d.getCount();
                vals[3] += d.getAmount();
            }
        }

        String[] cols = { "연령대", "여성 (건수)", "여성 (금액)", "남성 (건수)", "남성 (금액)", "합계 (건수)", "합계 (금액)" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        long totalFemaleCount = 0, totalFemaleAmount = 0;
        long totalMaleCount = 0, totalMaleAmount = 0;
        long grandTotalCount = 0, grandTotalAmount = 0;

        for (String age : matrix.keySet()) {
            long[] v = matrix.get(age);
            totalFemaleCount += v[0];
            totalFemaleAmount += v[1];
            totalMaleCount += v[2];
            totalMaleAmount += v[3];

            long rowTotalCount = v[0] + v[2];
            long rowTotalAmount = v[1] + v[3];
            grandTotalCount += rowTotalCount;
            grandTotalAmount += rowTotalAmount;

            model.addRow(new Object[] {
                    age,
                    df.format(v[0]),
                    df.format(v[1]),
                    df.format(v[2]),
                    df.format(v[3]),
                    df.format(rowTotalCount),
                    df.format(rowTotalAmount)
            });
        }

        // 총계 행 추가 (남성/여성 개별 총계 포함)
        model.addRow(new Object[] {
                "<html><b>전체 총계</b></html>",
                "<html><b>" + df.format(totalFemaleCount) + "</b></html>",
                "<html><b>" + df.format(totalFemaleAmount) + "</b></html>",
                "<html><b>" + df.format(totalMaleCount) + "</b></html>",
                "<html><b>" + df.format(totalMaleAmount) + "</b></html>",
                "<html><b>" + df.format(grandTotalCount) + "</b></html>",
                "<html><b>" + df.format(grandTotalAmount) + "</b></html>"
        });

        JTable table = new StyledTable(model);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        panel.add(tableScroll, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(800, 400));
        return panel;
    }

    // 5. 월별 실적 추이 탭
    private Component createMonthlyTrendTab(int year) {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(UIConstants.BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.LINE_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel(year + "년도 월별 통합 매출 추이");
        title.setFont(UIConstants.SUBHEADER_FONT);
        tablePanel.add(title, BorderLayout.NORTH);

        String[] cols = { "월", "총 구매 건수", "총 매출액", "성장률(전월대비)" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        List<statistics.dto.MonthlyStatDto> monthlyData = statService.getMonthlyBenefitTrend(year);
        long prevAmount = -1;

        for (int month = 1; month <= 12; month++) {
            final int m = month;
            statistics.dto.MonthlyStatDto d = monthlyData.stream()
                    .filter(x -> x.getMonth() == m)
                    .findFirst()
                    .orElse(new statistics.dto.MonthlyStatDto("전체", m, 0, 0));

            String growth = "-";
            if (prevAmount > 0) {
                double rate = ((double) (d.getAmount() - prevAmount) / prevAmount) * 100;
                growth = String.format("%.1f%%", rate);
                if (rate > 0)
                    growth = "+" + growth;
            }

            model.addRow(new Object[] {
                    month + "월",
                    df.format(d.getCount()),
                    df.format(d.getAmount()),
                    growth
            });
            prevAmount = d.getAmount();
        }

        tablePanel.add(new JScrollPane(new StyledTable(model)), BorderLayout.CENTER);
        p.add(tablePanel, BorderLayout.CENTER);

        return p;
    }

    private static class ChartContainer extends JPanel {
        public ChartContainer(String title, JComponent chart) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.LINE_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)));
            JLabel t = new JLabel(title);
            t.setFont(UIConstants.BODY_BOLD_FONT);
            t.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            add(t, BorderLayout.NORTH);
            add(chart, BorderLayout.CENTER);
            setPreferredSize(new Dimension(500, 400));
        }
    }

    private static class SimplePieChart extends JComponent {
        private List<StatDto> data;
        private DecimalFormat df = new DecimalFormat("#,###");
        private Color[] colors = { new Color(0x3182F6), new Color(0x00D084), new Color(0xFCB900), new Color(0xEB144C),
                new Color(0x9900EF), new Color(0x0693E3), new Color(0xABB8C3) };
        private int hoverIndex = -1;
        private Point mousePos = null;

        public SimplePieChart(List<StatDto> data) {
            this.data = data;
            addMouseMotionListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    mousePos = e.getPoint();
                    int newHover = calculateHoverIndex(e.getPoint());
                    if (newHover != hoverIndex) {
                        hoverIndex = newHover;
                        setCursor(hoverIndex != -1 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                        repaint();
                    }
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hoverIndex = -1;
                    repaint();
                }
            });
        }

        private int calculateHoverIndex(Point p) {
            if (data == null || data.isEmpty()) return -1;
            int w = getWidth(), h = getHeight(), size = Math.min(w - 250, h - 60);
            int x = (w - 250 - size) / 2 + 20, y = (h - size) / 2;
            int cx = x + size / 2, cy = y + size / 2;
            
            double dx = p.x - cx;
            double dy = p.y - cy;
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            if (dist < size / 4.0 || dist > size / 2.0) return -1;
            
            double angle = Math.toDegrees(Math.atan2(-dy, dx));
            double adjustedAngle = (90 - angle + 360) % 360;
            
            long total = data.stream().mapToLong(StatDto::getAmount).sum();
            if (total == 0) return -1;
            
            double curAngle = 0;
            for (int i = 0; i < data.size(); i++) {
                double sliceAngle = (data.get(i).getAmount() * 360.0) / total;
                if (adjustedAngle >= curAngle && adjustedAngle < curAngle + sliceAngle) {
                    return i;
                }
                curAngle += sliceAngle;
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            long total = data.stream().mapToLong(StatDto::getAmount).sum();
            if (total == 0) return;

            int w = getWidth(), h = getHeight(), size = Math.min(w - 250, h - 60);
            int x = (w - 250 - size) / 2 + 20, y = (h - size) / 2;

            double curAngle = 90;
            for (int i = 0; i < data.size(); i++) {
                StatDto d = data.get(i);
                double angle = (d.getAmount() * 360.0) / total;
                
                Color c = colors[i % colors.length];
                if (hoverIndex == i) {
                    g2.setColor(c.brighter());
                    g2.fill(new Arc2D.Double(x - 5, y - 5, size + 10, size + 10, curAngle, -angle, Arc2D.PIE));
                } else {
                    g2.setColor(c);
                    g2.fill(new Arc2D.Double(x, y, size, size, curAngle, -angle, Arc2D.PIE));
                }
                
                // 범례
                int legendY = 30 + i * 22;
                g2.setColor(c);
                g2.fillRect(w - 220, legendY, 10, 10);
                g2.setColor(UIConstants.TEXT_MAIN);
                g2.setFont(UIConstants.CAPTION_FONT);
                String valStr = df.format(d.getAmount());
                g2.drawString(d.getLabel() + " (" + valStr + ")", w - 205, legendY + 10);

                curAngle -= angle;
                if (i >= 10) break;
            }
            // 도넛 구멍
            g2.setColor(Color.WHITE);
            g2.fillOval(x + size / 4, y + size / 4, size / 2, size / 2);
            
            // 툴팁 (추가)
            if (hoverIndex != -1 && mousePos != null && hoverIndex < data.size()) {
                drawTooltip(g2, data.get(hoverIndex));
            }
        }

        private void drawTooltip(Graphics2D g2, StatDto d) {
            String text = d.getLabel() + ": " + df.format(d.getCount()) + "건 / " + df.format(d.getAmount()) + "원";
            g2.setFont(UIConstants.BODY_BOLD_FONT);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text) + 20;
            int th = 30;
            int tx = mousePos.x + 15;
            int ty = mousePos.y - 15;
            
            if (tx + tw > getWidth()) tx = mousePos.x - tw - 5;
            if (ty - th < 0) ty = mousePos.y + 35;
            
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(tx, ty - th, tw, th, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx + 10, ty - 10);
        }
    }

    private static class SimpleBarChart extends JComponent {
        private List<StatDto> data;
        private DecimalFormat df = new DecimalFormat("#,###"); // 전용 포맷터 추가

        public SimpleBarChart(List<StatDto> data) {
            this.data = data;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int pad = 40, ch = getHeight() - pad * 2, cw = getWidth() - pad * 2;
            long max = data.stream().mapToLong(StatDto::getCount).max().orElse(1);
            int bw = cw / Math.max(1, data.size()) - 10, x = pad + 5;
            for (StatDto d : data) {
                int bh = (int) ((d.getCount() / (double) max) * ch);
                g2.setColor(UIConstants.PRIMARY_COLOR);
                g2.fillRoundRect(x, getHeight() - pad - bh, bw, bh, 5, 5);

                // 수치 표시 (막대 위)
                g2.setColor(UIConstants.TEXT_MAIN);
                g2.setFont(new Font("Inter", Font.BOLD, 10));
                g2.drawString(df.format(d.getCount()), x + (bw / 2) - 10, getHeight() - pad - bh - 5);

                // 라벨 표시 (45도 기울이기)
                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.setFont(new Font("Inter", Font.PLAIN, 10));

                java.awt.geom.AffineTransform old = g2.getTransform();
                g2.translate(x, getHeight() - pad + 15);
                g2.rotate(Math.toRadians(35)); // 35도 정도 기울임
                g2.drawString(d.getLabel(), 0, 0);
                g2.setTransform(old);

                x += bw + 15;
            }
            g2.setColor(UIConstants.LINE_COLOR);
            g2.drawLine(pad, getHeight() - pad, getWidth() - pad, getHeight() - pad);
        }
    }

    private static class HorizontalBarChart extends JComponent {
        private List<StatDto> data;
        private DecimalFormat df = new DecimalFormat("#,###");

        public HorizontalBarChart(List<StatDto> data) {
            this.data = data;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pl = 200;
            int pr = 100;
            int cw = getWidth() - pl - pr;
            int bh = 15;
            int gap = 20;
            int y = 20;

            if (data == null || data.isEmpty()) {
                g2.drawString("데이터 없음", getWidth() / 2 - 30, getHeight() / 2);
                return;
            }

            long max = data.stream().mapToLong(StatDto::getAmount).max().orElse(1);

            for (StatDto d : data) {
                int bw = (int) ((d.getAmount() / (double) max) * cw);

                // 라벨
                g2.setColor(UIConstants.TEXT_MAIN);
                g2.setFont(new Font("Inter", Font.PLAIN, 11));
                String label = d.getLabel();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, pl - fm.stringWidth(label) - 10, y + 12);

                // 배경 바
                g2.setColor(new Color(0xF2F4F6));
                g2.fillRoundRect(pl, y, cw, bh, 5, 5);

                // 데이터 바
                g2.setColor(new Color(0x3182F6));
                g2.fillRoundRect(pl, y, Math.max(bw, 5), bh, 5, 5);

                // 수치
                g2.setColor(UIConstants.TEXT_SECONDARY);
                String valStr = df.format(d.getAmount());
                g2.drawString(valStr, pl + cw + 10, y + 12);

                y += bh + gap;
                if (y > getHeight() - 20)
                    break;
            }
        }
    }
}
