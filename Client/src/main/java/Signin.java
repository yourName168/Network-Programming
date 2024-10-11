
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


public class Signin extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JTextField txtName;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSign;
    
    public Signin() {
        super("Signin");        
        txtUsername = new JTextField(15);
        txtName = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        btnSign = new JButton("Signin");
        btnSign.setBackground(new java.awt.Color(102, 204, 255));
        btnSign.setForeground(new java.awt.Color(0, 0, 0));
        
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width - 5, this.getSize().height - 20);        
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblHome = new JLabel("Signin");
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
        
        JPanel pnName = new JPanel();
        pnName.setLayout(new FlowLayout());
        pnName.add(new JLabel("Name:"));
        pnName.add(txtName);
        pnMain.add(pnName);
        
        pnMain.add(btnSign);    
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        btnSign.addActionListener(this);    
            
        JPanel pnLogin = new JPanel();
        pnLogin.add(new JLabel("Nếu bạn đã có acount :"));
        btnLogin = new JButton("Đăng nhập");
        pnLogin.add(btnLogin);
        btnLogin.addActionListener(this); 
        pnMain.add(pnLogin);
        
        this.setSize(400, 300);                
        this.setLocation(200, 10);
        this.setContentPane(pnMain);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() instanceof JButton) && (((JButton) e.getSource()).equals(btnSign))) {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            String name =  txtName.getText();

            try {
                // URL đích
                @SuppressWarnings("deprecation")
                URL url = new URL("https://695a-117-1-251-126.ngrok-free.app/signin");
                //ngrok http 8080
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                
                // Cấu hình yêu cầu HTTP
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Dữ liệu cần gửi
                String data = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\", \"name\":\"" + name + "\"}";

                // Gửi dữ liệu
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = data.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Kiểm tra mã phản hồi từ server
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Đăng ký thành công!");
                    // Đọc phản hồi từ server
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        // Phân tích dữ liệu JSON
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        // Lấy đối tượng user từ phản hồi JSON
                        JSONObject userJson = jsonResponse.getJSONObject("user");
                        // Lấy mảng từ phản hồi JSON
                        JSONArray userListJson = jsonResponse.getJSONArray("list");

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
        }else if((e.getSource() instanceof JButton) && (((JButton) e.getSource()).equals(btnLogin))){
            new Loginn().setVisible(true);
            this.dispose(); // Đóng màn hình đăng nhập
        }
    }
}