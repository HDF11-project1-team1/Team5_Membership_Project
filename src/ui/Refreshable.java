package ui;

/**
 * 탭 전환(Switch Panel) 발생 시 화면을 최신 데이터로 실시간 갱신하기 위한 라이프사이클 인터페이스
 */
public interface Refreshable {
    void refresh();
}
