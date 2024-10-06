import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import model.InvitationMessage;
import model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.WebSocketHttpHeaders;

public class WaittingScreen extends JFrame implements ActionListener {
    private StompSession stompSession;
    private WebSocketStompClient stompClient;
    private JTable tblTable;
    private ArrayList<User> arrTable;
    private JButton btnStart;
    private User selectedPlayer;
    private User user;
    private JDialog invitationDialog;
    private JDialog responseDialog;
    private String receivedInvitation;
    private String receivedResponse;


    public WaittingScreen(JSONArray arrTable1, JSONObject user1) throws InterruptedException, ExecutionException {
        super("Danh sách người chơi");
        this.selectedPlayer = null;
        user = new User(user1.getLong("id"), user1.getString("username"), user1.getString("name"), (float) user1.getDouble("score"), user1.getString("status"));

        arrTable = new ArrayList<>();
        for (int i = 0; i < arrTable1.length(); i++) {
            JSONArray userItem = arrTable1.getJSONArray(i);
            long id = userItem.getLong(0);
            String username = userItem.getString(1);
            String name = userItem.getString(2);
            double score = userItem.getDouble(3);
            String status = userItem.getString(4);
            arrTable.add(new User(id,username, name, (float) score, status));
        }
        initComponents();
        connectWebSocket(); // Kết nối WebSocket khi khởi tạo
    }

    private void connectWebSocket() throws InterruptedException, ExecutionException {
        // Thiết lập WebSocket STOMP client
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());
           
        // Thêm header chứa thông tin username khi kết nối WebSocket
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("username", user.getUsername());
    
        //String url = "ws://10.21.45.117:8080/ws";
        String url = "wss://5684-117-5-79-91.ngrok-free.app/ws";

        //StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();
        StompSession session = stompClient.connect(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {}).get();
        stompSession = session;
        // Subscribe đến /topic/invitations
        session.subscribe("/topic/invitations/" + user.getUsername(), new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Tin nhắn từ server: " + payload);
                receivedInvitation = (String)payload;
                showInvitationResponseDialog();
            }
        });
        // Subscribe đến /topic/responses
        session.subscribe("/topic/responses/" + user.getUsername(), new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Người chơi phản hồi: " + payload);
                receivedResponse = (String)payload;
                JSONObject jsonObject = new JSONObject(receivedResponse);
                if(jsonObject.getString("response").equals("accept")){
                    invitationDialog.dispose();
                }else{
                    invitationDialog.dispose();
                }
            }
        });
        // Subscribe đến /topic/randomColors
        session.subscribe("/topic/randomColors/" + user.getUsername(), new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("3 màu random: " + payload);
                new CorectColor(user, (String) payload, stompSession).setVisible(true); 
            }
        });
        // Subscribe đến /topic/playeronline
        session.subscribe("/topic/playeronline", new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // Chuyển payload thành chuỗi JSON
                String jsonString = (String) payload;
                // Parse chuỗi JSON thành đối tượng JSON
                JSONArray jsonArray = new JSONArray(jsonString);
                // Cập nhật lại danh sách arrTable
                arrTable.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject user = jsonArray.getJSONObject(i);
                    long id = user.getLong("id");
                    String username = user.getString("username");
                    String name = user.getString("name");
                    double score = user.getDouble("score");
                    String status = user.getString("status");
                    arrTable.add(new User(id, username, name, (float) score, status));
                }
                // Cập nhật lại bảng tblTable
                updateTableData();
            }
        });
    }
    private void updateTableData() {
        String[] columnNames = {"ID", "Name", "Score", "Status"};
        Object[][] data = new Object[arrTable.size()][columnNames.length];

        for (int i = 0; i < arrTable.size(); i++) {
            User user = arrTable.get(i);
            data[i][0] =  i + 1;
            data[i][1] = user.getName();
            data[i][2] = String.format("%.1f", user.getScore());
            data[i][3] = user.getStatus();
        }

        // Cập nhật lại TableModel của tblTable
        tblTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
    private void showInvitationResponseDialog() {
        JSONObject jsonObject = new JSONObject(receivedInvitation);

        responseDialog = new JDialog(this, "Nhận lời mời", true);
        responseDialog.setLayout(new BorderLayout());
        responseDialog.setSize(300, 150);
        responseDialog.setLocationRelativeTo(this);

        JLabel messageLabel = new JLabel("Bạn đã nhận lời mời từ " + jsonObject.getString("sender"));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        responseDialog.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton acceptButton = new JButton("Chấp nhận");
        JButton rejectButton = new JButton("Từ chối");

        acceptButton.addActionListener((ActionEvent e) -> {
            sendInvitationResponse("accept");
            responseDialog.dispose();
        });
        rejectButton.addActionListener((ActionEvent e) -> {
            sendInvitationResponse("reject");
            responseDialog.dispose();
        });

        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        responseDialog.add(buttonPanel, BorderLayout.SOUTH);

        responseDialog.setVisible(true);
    }

    private void sendInvitationResponse(String response) {
        JSONObject jsonObject = new JSONObject(receivedInvitation);
        if (stompSession != null && stompSession.isConnected()) {
            JSONObject responseMessage = new JSONObject();
            responseMessage.put("sender", user.getUsername());
            responseMessage.put("receiver", jsonObject.getString("sender"));
            responseMessage.put("response", response);
            stompSession.send("/app/response", responseMessage.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Kết nối WebSocket chưa được thiết lập.");
        }
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"ID", "Name","Score", "Status"};
        Object[][] data = new Object[arrTable.size()][columnNames.length];

        for (int i = 0; i < arrTable.size(); i++) {
            User user = arrTable.get(i);
            data[i][0] = i + 1;
            data[i][1] = user.getName();
            data[i][2] = String.format("%.1f", user.getScore());
            data[i][3] = user.getStatus();
        }
        tblTable = new JTable(data, columnNames);
        tblTable.setBackground(new java.awt.Color(102, 204, 255));
        tblTable.setForeground(new java.awt.Color(0, 0, 0));
        JScrollPane scrollPane = new JScrollPane(tblTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        tblTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < arrTable.size()) {
                    selectedPlayer = arrTable.get(row);
                    System.out.println(selectedPlayer.getUsername() + " " +selectedPlayer.getName());
                }
            }
        });

        btnStart = new JButton("Mời");
        btnStart.addActionListener(this);
        panel.add(btnStart, BorderLayout.SOUTH);

        this.add(panel);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnStart) {
            if (selectedPlayer != null) {
                if(selectedPlayer.getUsername().equals(user.getUsername())){
                    JOptionPane.showMessageDialog(this, "Đây là bạn mà mời miếc gì");
                }else if (stompSession != null && stompSession.isConnected()) {
                    JSONObject message = new JSONObject();
                    message.put("sender", user.getUsername());
                    message.put("receiver", selectedPlayer.getUsername());
                    stompSession.send("/app/invite", message.toString());
                    showInvitationDialog();
                } else {
                    JOptionPane.showMessageDialog(this, "Kết nối WebSocket chưa được thiết lập.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn người chơi trước!");
            }
        }
    }

    private void showInvitationDialog() {
        if (invitationDialog != null && invitationDialog.isVisible()) {
            invitationDialog.dispose();
        }
        invitationDialog = new JDialog(this, "Mời người chơi", true);
        invitationDialog.setLayout(new BorderLayout());
        invitationDialog.setSize(300, 150);
        invitationDialog.setLocationRelativeTo(this);

        JLabel messageLabel = new JLabel("Đợi người chơi đồng ý...");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        invitationDialog.add(messageLabel, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Hủy mời");
        cancelButton.addActionListener((ActionEvent e) -> invitationDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        invitationDialog.add(buttonPanel, BorderLayout.SOUTH);

        invitationDialog.setVisible(true);
    }
}
