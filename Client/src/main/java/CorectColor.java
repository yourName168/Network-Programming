import javax.swing.*;
import java.awt.*;
import model.User;
import org.json.JSONObject;
import org.springframework.messaging.simp.stomp.StompSession;

public class CorectColor extends JFrame {
    private User user;
    private String color_random_3;

    public CorectColor(User user1, String color_random_4,StompSession session) {
        user = user1;
        color_random_3 = color_random_4;
        
        // Sử dụng chính lớp CorectColor làm JFrame
        setTitle("Trò Chơi Đoán Màu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        show_3cr_Colors(); // Hiển thị ba màu

        // Thiết lập Timer để đóng giao diện sau 5 giây
        Timer timer = new Timer(5000, e -> {
            new GameScreen(user, color_random_4, session).setVisible(true); 
            dispose(); // Đóng giao diện hiện tại
        });
        timer.setRepeats(false); // Đảm bảo rằng Timer chỉ chạy một lần
        timer.start();
        
        setVisible(true); // Hiển thị JFrame
    }
    
    private void show_3cr_Colors() {
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(1, 3)); // Sắp xếp ba màu theo hàng ngang

        // Hiển thị ba màu từ chuỗi color_random_3
        JSONObject jsonObject = new JSONObject(color_random_3);
        
        JPanel panel1 = new JPanel();
        panel1.setBackground(getColorFromString(jsonObject.getString("color1").trim()));
        colorPanel.add(panel1);
        
        JPanel panel2 = new JPanel();
        panel2.setBackground(getColorFromString(jsonObject.getString("color2").trim()));
        colorPanel.add(panel2);
        
        JPanel panel3 = new JPanel();
        panel3.setBackground(getColorFromString(jsonObject.getString("color3").trim()));
        colorPanel.add(panel3);
        
        add(colorPanel, BorderLayout.CENTER); // Thêm panel màu vào chính JFrame này
    }
    
    private Color getColorFromString(String colorName) {
        switch (colorName.toLowerCase()) {
            case "đỏ":
                return Color.RED;
            case "xanh":
                return Color.BLUE;
            case "vàng":
                return Color.YELLOW;
            case "hồng":
                return Color.PINK;
            case "xám":
                return Color.GRAY;
            case "cam":
                return Color.ORANGE;
            case "xanh lá":
                return Color.GREEN;
            case "tím":
                return Color.MAGENTA;
            case "nâu":
                return new Color(139, 69, 19); // Màu nâu
            case "tím nhạt":
                return new Color(221, 160, 221); // Màu tím nhạt
            case "xanh dương":
                return new Color(0, 0, 139); // Màu xanh dương đậm
            case "hồng nhạt":
                return new Color(255, 182, 193); // Màu hồng nhạt
            case "vàng nhạt":
                return new Color(255, 255, 224); // Màu vàng nhạt
            case "xanh nước":
                return new Color(0, 255, 255); // Màu xanh nước
            case "xanh lá nhạt":
                return new Color(144, 238, 144); // Màu xanh lá nhạt
            case "xanh đậm":
                return new Color(0, 128, 0); // Màu xanh đậm
            case "cam sáng":
                return new Color(255, 165, 0); // Màu cam sáng
            case "màu be":
                return new Color(245, 245, 220); // Màu be
            case "xanh ô liu":
                return new Color(128, 128, 0); // Màu xanh ô liu
            case "xanh ngọc":
                return new Color(0, 255, 127); // Màu xanh ngọc
            case "xanh lá đậm":
                return new Color(34, 139, 34); // Màu xanh lá đậm
            case "xanh bạc hà":
                return new Color(152, 251, 152); // Màu xanh bạc hà
            default:
                return Color.GRAY; // Màu mặc định
        }
    }
}
