import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.simp.stomp.StompSession;

public class RankingScreen extends JFrame {
    private StompSession stompSession;
    private DefaultTableModel model; // Thêm biến để lưu trữ DefaultTableModel

    public RankingScreen(StompSession session) {
        stompSession = session;

        setTitle("Xếp Hạng");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180)); 
        JLabel rankingLabel = new JLabel("XẾP HẠNG", SwingConstants.CENTER);
        rankingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        rankingLabel.setForeground(Color.WHITE);
        headerPanel.add(rankingLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tablePanel.setBackground(new Color(240, 248, 255));

        // Create Table
        model = new DefaultTableModel(new String[]{"STT", "Name", "Score"}, 0);
        JTable rankingTable = new JTable(model);
        rankingTable.setFillsViewportHeight(true);
        rankingTable.setFont(new Font("Arial", Font.PLAIN, 18));
        rankingTable.setRowHeight(30);
        rankingTable.setBackground(new Color(255, 255, 255));
        rankingTable.setForeground(Color.BLACK);
        rankingTable.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        rankingTable.setEnabled(false); // Make the table non-editable

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255)); 

        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBackground(new Color(255, 69, 0)); 
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createRaisedBevelBorder());
        closeButton.addActionListener(e -> dispose()); 

        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

        showRankingData();
    }

    private void showRankingData() {
        try {
            //URL url = new URL("http://10.21.45.117:8080/rankk"); // URL cho GET request
            URL url = new URL("https://5684-117-5-79-91.ngrok-free.app/rankk"); // URL cho GET request
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray userListJson = jsonResponse.getJSONArray("list");
                    System.out.println("Danh sách người dùng:");
                    updateTableWithData(userListJson);
                }
            } else {
                System.out.println("Xem rank thất bại. Mã phản hồi: " + responseCode);
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    System.out.println("Lỗi từ server: " + errorResponse.toString());
                }
                JOptionPane.showMessageDialog(this, "Xem rank thất bại!");
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTableWithData(JSONArray userListJson) {
        model.setRowCount(0); // Clear existing data

        for (int i = 0; i < userListJson.length(); i++) {
            JSONArray userItem = userListJson.getJSONArray(i);
            model.addRow(new Object[]{
                i + 1, // STT (số thứ tự) bắt đầu từ 1
                userItem.getString(0), // Name
                userItem.getDouble(1) // Score
            });
        }
    }
}
