import java.awt.*;

public class Run {
    public static void main(String[] args) {
        run();
    }

    private static void run() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Implementation m = new Implementation();
                m.setVisible(true);
            }
        });
    }
}
