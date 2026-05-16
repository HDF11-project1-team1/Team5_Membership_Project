package ui.components;

import ui.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * 프로젝트 전체에서 공통으로 사용되는 스타일이 적용된 테이블 컴포넌트
 */
public class StyledTable extends JTable {

    public StyledTable(DefaultTableModel model) {
        super(model);
        applyDefaultStyle();
    }

    private void applyDefaultStyle() {
        // 기본 폰트 및 높이 설정
        setFont(UIConstants.BODY_FONT);
        setRowHeight(55);
        
        // 선 및 그리드 설정
        setShowVerticalLines(false);
        setGridColor(UIConstants.LINE_COLOR);
        setIntercellSpacing(new Dimension(0, 0));
        
        // 선택 색상 설정
        setSelectionBackground(new Color(0xF2F4F6));
        setSelectionForeground(UIConstants.TEXT_MAIN);
        
        // 헤더 스타일 설정
        JTableHeader header = getTableHeader();
        header.setFont(UIConstants.BODY_BOLD_FONT);
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 50));
        header.setReorderingAllowed(false); // 컬럼 순서 변경 방지
        
        // 헤더 렌더러 (패딩 및 정렬)
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
                label.setBackground(Color.WHITE);
                label.setFont(UIConstants.BODY_BOLD_FONT);
                label.setHorizontalAlignment(JLabel.LEFT);
                return label;
            }
        });

        // 기본 셀 렌더러 (패딩 적용)
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
                
                // 선택되지 않았을 때 배경색 흰색 고정
                if (!isSelected) {
                    label.setBackground(Color.WHITE);
                }
                return label;
            }
        });
    }

    /**
     * 특정 컬럼의 텍스트 정렬을 설정한다.
     * @param columnIndex 컬럼 인덱스
     * @param alignment SwingConstants.LEFT, CENTER, RIGHT
     */
    public void setColumnAlignment(int columnIndex, int alignment) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
                label.setHorizontalAlignment(alignment);
                if (!isSelected) {
                    label.setBackground(Color.WHITE);
                }
                return label;
            }
        };
        getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
    }
}
