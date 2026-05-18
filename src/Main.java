import ui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Swing UI는 이벤트 디스패치 스레드(EDT)에서 실행하는 것이 안전합니다.
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
