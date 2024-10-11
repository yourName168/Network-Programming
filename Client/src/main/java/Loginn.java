
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.json.JSONObject;

import model.User;
import model.listplayer;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;


public class Loginn extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSign;
    
    public Loginn() {
        super("Login");        
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new java.awt.Color(102, 204, 255));
        btnLogin.setForeground(new java.awt.Color(0, 0, 0));
        
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width - 5, this.getSize().height - 20);        
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblHome = new JLabel("Login");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);    
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel pnUsername = new JPanel();
        pnUsername.setLayout(new FlowLayout());
        pnUsername.add(new JLabel("Username:"));
        pnUsername.add(txtUsername);
        pnMain.add(pnUsername);
        
        JPanel pnPass = new JPanel();
        pnPass.setLayout(new FlowLayout());
        pnPass.add(new JLabel("Password:"));
        pnPass.add(txtPassword);
        pnMain.add(pnPass);
        
        pnMain.add(btnLogin);    
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        btnLogin.addActionListener(this);    

        JPanel pnSignin = new JPanel();
        pnSignin.add(new JLabel("Nếu bạn chưa có acount :"));
        btnSign = new JButton("Đăng ký");
        pnSignin.add(btnSign);
        pnMain.add(pnSignin);
        btnSign.addActionListener(this);
        
        this.setSize(400, 250);                
        this.setLocation(200, 10);
        this.setContentPane(pnMain);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() instanceof JButton) && (((JButton) e.getSource()).equals(btnLogin))) {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            
            try {
                // URL đích
                @SuppressWarnings("deprecation")
                //URL url = new URL("http://10.21.45.117:8080/login"); // Thay đổi URL này thành URL bạn muốn gửi yêu cầu tới
                URL url = new URL("https://5684-117-5-79-91.ngrok-free.app/login");
                //ngrok http 8080
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                
                // Cấu hình yêu cầu HTTP
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Dữ liệu cần gửi
                String data = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

                // Gửi dữ liệu
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = data.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Kiểm tra mã phản hồi từ server
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Đăng nhập thành công!");
                    // Đọc phản hồi từ server
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        System.out.println("Phản hồi từ server: " + response.toString());

                        // Phân tích dữ liệu JSON
                        JSONObject jsonResponse = new JSONObject(response.toString());

                        // Lấy đối tượng user từ phản hồi JSON
                        JSONObject userJson = jsonResponse.getJSONObject("user");
                        System.out.println("User:");
                        System.out.println("ID: " + userJson.getLong("id"));
                        System.out.println("Username: " + userJson.getString("username"));
                        System.out.println("Name: " + userJson.getString("name"));
                        System.out.println("Score: " + userJson.getInt("score"));
                        System.out.println("Position: " + userJson.getInt("position"));
                        System.out.println("Status: " + userJson.getString("status"));

                        // Lấy mảng từ phản hồi JSON
                        JSONArray userListJson = jsonResponse.getJSONArray("list");
                        System.out.println("Danh sách người dùng:");

                        // Duyệt qua từng phần tử trong mảng
                        for (int i = 0; i < userListJson.length(); i++) {
                            JSONArray userItem = userListJson.getJSONArray(i);
                            System.out.println("ID: " + userItem.getLong(0));
                            System.out.println("Username: " + userItem.getString(1));
                            System.out.println("Name: " + userItem.getString(2));
                            System.out.println("Score: " +  userItem.getDouble(3));
                            System.out.println("Status: " + userItem.getString(4));
                        }
                        new WaittingScreen(userListJson, userJson).setVisible(true);
                        this.dispose(); // Đóng màn hình đăng nhập
                    }
                    
                } else {
                    System.out.println("Đăng nhập thất bại. Mã phản hồi: " + responseCode);
                    JOptionPane.showMessageDialog(this, "Incorrect username and/or password!");
                }

                // Đóng kết nối
                conn.disconnect();
            } catch (Exception k) {
                k.printStackTrace();
            }
        }else if((e.getSource() instanceof JButton) && (((JButton) e.getSource()).equals(btnSign))){
            new Signin().setVisible(true);
            this.dispose(); // Đóng màn hình đăng nhập
        }
    }
    
    public static void main(String[] args) {
        Loginn myFrame = new Loginn();    
        myFrame.setVisible(true);    
    }
}
//192.168.2.9
//10.21.15.151
