package ui;

import java.awt.Color;
import java.awt.Font;

public class UIConstants {
    // 배경 및 컨텐츠 영역 색상
    public static final Color BACKGROUND_COLOR = new Color(0xF2F4F6); // 밝은 회색 바탕
    public static final Color SURFACE_COLOR = new Color(0xFFFFFF); // 컨텐츠(카드) 영역 하얀색
    public static final Color LINE_COLOR = new Color(0xE5E8EB); // 연한 구분선

    // 텍스트 색상
    public static final Color TEXT_MAIN = new Color(0x191F28); // 진한 흑갈색
    public static final Color TEXT_SECONDARY = new Color(0x8B95A1); // 회색 (보조 텍스트)
    public static final Color TEXT_ERROR = new Color(0xF04452); // 빨간색

    // 포인트 및 버튼 색상
    public static final Color PRIMARY_COLOR = new Color(0x3182F6); // 토스 블루
    public static final Color PRIMARY_HOVER = new Color(0x1B64DA); // 어두운 블루
    
    public static final Color SECONDARY_BTN_COLOR = new Color(0xF2F4F6); // 보조 버튼 배경
    public static final Color SECONDARY_BTN_TEXT = new Color(0x4E5968); // 보조 버튼 텍스트
    public static final Color SECONDARY_BTN_HOVER = new Color(0xE5E8EB); // 보조 버튼 호버

    // 폰트 설정 (Pretendard 커스텀 폰트)
    public static Font HEADER_FONT;
    public static Font SUBHEADER_FONT;
    public static Font BODY_FONT;
    public static Font BODY_BOLD_FONT;
    public static Font CAPTION_FONT;

    static {
        try {
            // 프로젝트 내 resources 폴더에서 Pretendard 폰트 로드
            java.io.File regularFontFile = new java.io.File("resources/fonts/Pretendard-Regular.ttf");
            java.io.File boldFontFile = new java.io.File("resources/fonts/Pretendard-Bold.ttf");
            
            Font pretendardRegular = Font.createFont(Font.TRUETYPE_FONT, regularFontFile);
            Font pretendardBold = Font.createFont(Font.TRUETYPE_FONT, boldFontFile);

            // 폰트 크기 파생 (deriveFont는 float 값을 받으므로 f를 붙여야 함)
            HEADER_FONT = pretendardBold.deriveFont(24f);
            SUBHEADER_FONT = pretendardBold.deriveFont(18f);
            BODY_FONT = pretendardRegular.deriveFont(14f);
            BODY_BOLD_FONT = pretendardBold.deriveFont(14f);
            CAPTION_FONT = pretendardRegular.deriveFont(12f);
            
            // 시스템 폰트 환경에 등록하여 컴포넌트 내부에서 이름으로도 찾을 수 있게 함
            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pretendardRegular);
            ge.registerFont(pretendardBold);

        } catch (Exception e) {
            System.err.println("Pretendard 폰트 로드 실패. 시스템 기본 폰트를 사용합니다.");
            e.printStackTrace();
            // 기본 폰트 폴백
            HEADER_FONT = new Font("SansSerif", Font.BOLD, 24);
            SUBHEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
            BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
            BODY_BOLD_FONT = new Font("SansSerif", Font.BOLD, 14);
            CAPTION_FONT = new Font("SansSerif", Font.PLAIN, 12);
        }
    }
}
