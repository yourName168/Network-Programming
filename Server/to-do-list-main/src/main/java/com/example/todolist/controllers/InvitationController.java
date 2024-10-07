package com.example.todolist.controllers;

import com.example.todolist.models.ColorNames;
import com.example.todolist.repositories.UserRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class InvitationController {
    @Autowired
    private UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public InvitationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Xử lý khi người gửi gửi lời mời
    @MessageMapping("/invite")
    public void sendInvitation(String message) {
        JSONObject jsonObject = new JSONObject(message);
        System.out.println("người nhận: " + message);
        System.out.println(jsonObject.getString("sender"));
        System.out.println(jsonObject.getString("receiver"));
        messagingTemplate.convertAndSend(
                "/topic/invitations/" + jsonObject.getString("receiver"), // Điểm đến mà client đăng ký
                message // Tin nhắn bạn muốn gửi
        );
        System.out.println("Đã gửi tin nhắn: " + message);
    }

    // Xử lý khi người nhận phản hồi lại lời mời
    @MessageMapping("/response")
    public void handleResponse(String response) {

        JSONObject jsonObject = new JSONObject(response);
        System.out.println("người nhận: " + response);
        System.out.println(jsonObject.getString("sender"));
        System.out.println(jsonObject.getString("receiver"));
        // Gửi phản hồi lại cho người gửi lời mời
        messagingTemplate.convertAndSend(
                "/topic/responses/" + jsonObject.getString("receiver"), // Điểm đến mà client đăng ký
                response // Tin nhắn bạn muốn gửi
        );
        if(jsonObject.getString("response").equals("accept")){
            List<String> colors = ColorNames.getColorNames();
            Collections.shuffle(colors); // Xáo trộn danh sách màu
            List<String> randomColors = colors.subList(0, 3); // Lấy 3 màu ngẫu nhiên

            JSONObject responsecolor = new JSONObject();
            responsecolor.put("color1",randomColors.get(0) );
            responsecolor.put("color2",randomColors.get(1) );
            responsecolor.put("color3",randomColors.get(2) );
            responsecolor.put("sender",jsonObject.getString("sender") );
            responsecolor.put("receiver",jsonObject.getString("receiver"));

            messagingTemplate.convertAndSend(
                    "/topic/randomColors/" + jsonObject.getString("sender"),
                    responsecolor.toString()
            );
            messagingTemplate.convertAndSend(
                    "/topic/randomColors/" + jsonObject.getString("receiver"),
                    responsecolor.toString()
            );
            System.out.println("Đã gửi màu ngẫu nhiên: " + randomColors);
        }
    }

    @MessageMapping("/gamecolor")
    public void sendToExit(String message) {
        JSONObject jsonObject = new JSONObject(message);
        System.out.println("thông điệp: " + message);
        System.out.println(jsonObject.getString("ketqua")+ " " + jsonObject.getString("receiver"));
        messagingTemplate.convertAndSend(
                "/topic/gamecolor/" + jsonObject.getString("receiver"), // Điểm đến mà client đăng ký
                message // Tin nhắn bạn muốn gửi
        );
        System.out.println("Đã gửi tin nhắn: " + message);
    }
    // Xử lý khi người gửi lưu điểm đã chơi xong
    @MessageMapping("/SaveScoreMySQL")
    public void SaveScoreMySQL(String message) {
        JSONObject jsonObject = new JSONObject(message);
        System.out.println("người muốn lưu điểm " + jsonObject.getString("sender"));
        System.out.println("score" + jsonObject.getFloat("score"));
        userRepository.updateScoreById(jsonObject.getString("sender"),jsonObject.getFloat("score"));
    }

    @Scheduled(fixedRate = 2000)
    public void cuong() {
        List<Object[]> users = userRepository.findAllUsersWithSelectedColumns();
        JSONArray jsonArray = new JSONArray();

        for (Object[] user : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", user[0]);
            jsonObject.put("username", user[1]);
            jsonObject.put("name", user[2]);
            jsonObject.put("score", user[3]);
            jsonObject.put("status", user[4]);
            jsonArray.put(jsonObject);
        }

        messagingTemplate.convertAndSend(
                "/topic/playeronline", // Điểm đến mà client đăng ký
                jsonArray.toString() // Tin nhắn bạn muốn gửi
        );
        //System.out.println("đã gửi playeronline" + jsonArray);
    }
}












