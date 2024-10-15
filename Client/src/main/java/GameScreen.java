import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import model.User;
import org.json.JSONObject;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class GameScreen extends JFrame{
    class CustomPanel extends JPanel {
    private Image backgroundImage;
    public CustomPanel() {
        // Tải hình nền từ đường dẫn bạn đã cung cấp
        backgroundImage = new ImageIcon("Image/background.png").getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ hình nền lên toàn bộ panel
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

    private List<String> allColors;
    private List<String> displayedColors;
    private List<String> correctColors;
    private List<String> selectedColors;
    private JPanel colorPanel;
    private JButton submitButton;
    private User user;
    private String color_random_3;
    private int luot_chon = 1;
    private StompSession stompSession;
    private JDialog waitExit;
    private String doithu;
    private boolean exit_doi_thu = false;
    private boolean exit = false;
    
    public GameScreen(User user1, String color_random_4,StompSession session) {
        user = user1;
        color_random_3 = color_random_4;
        stompSession = session;
        
        session.subscribe("/topic/gamecolor/" + user.getUsername(), new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                JSONObject jsonObject = new JSONObject((String)payload);
                if(jsonObject.getString("ketqua").equals("true")){
                    waitExit.dispose();
                    JOptionPane.showMessageDialog(GameScreen.this, "Bạn đã thua cuộc. Đối thủ đã chọn đúng!");
                    GameScreen.this.dispose(); // Đóng màn hình trò chơi
                    new RankingScreen(stompSession).setVisible(true); // Chuyển đến màn hình xếp hạng
                }else{
                    exit_doi_thu = true;
                    if(exit == true ){
                        waitExit.dispose();
                        SaveScoreMySQL((float) (user.getScore()+ 0.5));
                        JOptionPane.showMessageDialog(GameScreen.this, "2 Bạn đã hòa.!");
                        GameScreen.this.dispose(); // Đóng màn hình trò chơi
                        new RankingScreen(stompSession).setVisible(true); // Chuyển đến màn hình xếp hạng
                    } 
                }
            }
        });
        // Sử dụng chính GameScreen làm JFrame
        setTitle("Trò Chơi Đoán Màu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Khởi tạo danh sách tất cả các màu
        allColors = List.of("Đỏ", "Xanh", "Vàng", "Hồng", "Xám", "Cam", "Xanh lá", "Tím", "Nâu", "Tím nhạt",
                            "Xanh dương", "Hồng nhạt", "Vàng nhạt", "Xanh nước", "Xanh lá nhạt", "Xanh đậm",
                            "Cam sáng", "Màu be", "Xanh ô liu", "Xanh ngọc", "Xanh lá đậm", "Xanh bạc hà");

        correctColors = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(color_random_3);
        correctColors.add(jsonObject.getString("color1").trim());
        correctColors.add(jsonObject.getString("color2").trim());
        correctColors.add(jsonObject.getString("color3").trim());
           
        if(user.getUsername().equals(jsonObject.getString("sender"))){
            doithu = jsonObject.getString("receiver");
        }else{
            doithu = jsonObject.getString("sender");
        }
        // Tạo danh sách màu hiển thị với ít nhất 20 màu, bao gồm ba màu chính xác
        displayedColors = generateRandomColorsIncludingCorrectColors(20, correctColors);
        selectedColors = new ArrayList<>();
        showColors(); // Hiển thị giao diện màu

        setVisible(true); // Hiển thị JFrame
    }

    private void showColors() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());

        colorPanel = new CustomPanel();
        colorPanel.setLayout(new GridLayout(4, 5, 15, 15));
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        for (String color : displayedColors) {
            JButton colorButton = new JButton(color);
            colorButton.setBackground(getColorFromString(color));
            colorButton.setForeground(Color.BLACK);
            colorButton.setFont(new Font("Arial", Font.BOLD, 14));
            colorButton.setFocusPainted(false);
            colorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleColorSelection(colorButton, color);
                }
            });
            colorPanel.add(colorButton);
        }

         // Tạo và hiển thị thông báo với icon
        ImageIcon originalIcon = new ImageIcon("Image/siamese-cat.png");
        // Thay đổi kích thước icon
        Image iconImage = originalIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(iconImage);
        JLabel iconLabel = new JLabel(scaledIcon);

        // Tạo icon thứ hai
        ImageIcon secondIcon = new ImageIcon("Image/color-palette.png"); // Đường dẫn icon thứ hai
        Image secondIconImage = secondIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon scaledSecondIcon = new ImageIcon(secondIconImage);
        JLabel secondIconLabel = new JLabel(scaledSecondIcon);

        JLabel infoLabel = new JLabel("Chọn ba màu đã được hiển thị trước đó!");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Thêm icon và tiêu đề vào panel
        infoPanel.add(iconLabel);
        infoPanel.add(infoLabel);
        infoPanel.add(secondIconLabel); // Thêm icon thứ hai

        submitButton = new JButton("Gửi");
        submitButton.setFont(new Font("Arial", Font.BOLD, 18));
        submitButton.setBackground(new Color(34, 139, 34)); // Màu xanh lá cây
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmission();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        buttonPanel.add(submitButton);

        add(infoPanel, BorderLayout.NORTH);
        add(colorPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void handleColorSelection(JButton button, String color) {
        // Toggle màu đã chọn
        if (selectedColors.contains(color)) {
            selectedColors.remove(color);
            button.setBorder(BorderFactory.createEmptyBorder());
        } else {
            if (selectedColors.size() < 3) {
                selectedColors.add(color);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Đánh dấu màu đã chọn
            } else {
                JOptionPane.showMessageDialog(this, "Bạn đã chọn đủ ba màu. Vui lòng gửi!");
            }
        }
    }

    private void handleSubmission() {
        if (selectedColors.size() == 3) {
            if (correctColors.containsAll(selectedColors)) {
                JSONObject message = new JSONObject();
                message.put("sender", user.getUsername());
                message.put("receiver", doithu);
                message.put("ketqua", "true");
                stompSession.send("/app/gamecolor", message.toString());
                JOptionPane.showMessageDialog(this, "Chúc mừng! Bạn đã thắng!");
                SaveScoreMySQL((float) (user.getScore()+ 1));
                GameScreen.this.dispose(); // Đóng màn hình trò chơi
                new RankingScreen(stompSession).setVisible(true); // Chuyển đến màn hình xếp hạng
            } else {
                if (luot_chon == 2) {
                    JOptionPane.showMessageDialog(this, "Sai rồi! Các màu đúng là: " + String.join(", ", correctColors));
                    JSONObject message = new JSONObject();
                    message.put("sender", user.getUsername());
                    message.put("receiver", doithu);
                    message.put("ketqua", "false");
                    stompSession.send("/app/gamecolor", message.toString());
                    exit = true;
                    if(exit_doi_thu == true){
                        JOptionPane.showMessageDialog(GameScreen.this, "2 Bạn đã hòa.!");
                        SaveScoreMySQL((float) (user.getScore()+ 0.5));
                        GameScreen.this.dispose(); // Đóng màn hình trò chơi
                        new RankingScreen(stompSession).setVisible(true); // Chuyển đến màn hình xếp hạng
                    }else{
                        waitExitDialog();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Sai rồi! - Bạn còn 1 lượt chọn lại");
                }
            }
            if (luot_chon != 2) {
                luot_chon += 1;
            }
            selectedColors = new ArrayList<>();
            for (Component comp : colorPanel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    button.setBorder(BorderFactory.createEmptyBorder()); // Xóa viền của nút
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ba màu trước khi gửi!");
        }
    }
    
    private void waitExitDialog() {
        if (waitExit != null && waitExit.isVisible()) {
            waitExit.dispose();
        }
        waitExit = new JDialog(this, "Đợi chút nhé", true);
        waitExit.setLayout(new BorderLayout());
        waitExit.setSize(300, 150);
        waitExit.setLocationRelativeTo(this);

        JLabel messageLabel = new JLabel("Đợi người chơi đồng còn lại");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waitExit.add(messageLabel, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Rời khỏi");
        cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JSONObject message = new JSONObject();
                    message.put("sender", user.getUsername());
                    message.put("receiver", doithu);
                    message.put("luotchon", luot_chon);
                    message.put("ketqua", "false");
                    stompSession.send("/app/gamecolor", message.toString());
                    waitExit.dispose();
                    GameScreen.this.dispose(); // Đóng màn hình trò chơi
                    new RankingScreen(stompSession).setVisible(true); // Chuyển đến màn hình xếp hạng
                }
            });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        waitExit.add(buttonPanel, BorderLayout.SOUTH);
        waitExit.setVisible(true);
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
    
    private void SaveScoreMySQL(float element){
        JSONObject message = new JSONObject();
        message.put("sender", user.getUsername());
        message.put("score", element);
        stompSession.send("/app/SaveScoreMySQL", message.toString());
    }
    
    private List<String> generateRandomColorsIncludingCorrectColors(int totalCount, List<String> correctColors) {
        List<String> selectedColors = new ArrayList<>(correctColors);
        Random rand = new Random();

        // Thêm màu ngẫu nhiên cho đến khi đủ số lượng yêu cầu
        while (selectedColors.size() < totalCount) {
            String color = allColors.get(rand.nextInt(allColors.size()));
            if (!selectedColors.contains(color)) {
                selectedColors.add(color);
            }
        }

        // Xáo trộn danh sách màu để đảm bảo ba màu chính xác không ở cùng một chỗ
        Collections.shuffle(selectedColors);
        return selectedColors;
    }
}