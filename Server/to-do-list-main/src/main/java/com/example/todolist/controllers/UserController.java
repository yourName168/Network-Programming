package com.example.todolist.controllers;

import com.example.todolist.models.User;
import com.example.todolist.repositories.UserRepository;
import com.example.todolist.schemas.UserSchema;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("")
public class UserController {
  @Autowired
  private UserRepository userRepository;
  
  @PostMapping("/signin")
  public ResponseEntity<Map<String, ?>> signin(@RequestBody User inputUser) {
    System.out.println("Username nhận được: " + inputUser.getUsername());
    System.out.println("Mật khẩu nhận được: " + inputUser.getPassword());

    // Tìm user dựa trên username
    User checkUser = userRepository.findByUsername(inputUser.getUsername());

    // Kiểm tra xem user có tồn tại không
    if (checkUser != null) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("message", "Tên người dùng đã tồn tại!");
      return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Nếu đúng thông tin đăng nhập
    inputUser.setScore(0);  // Điểm khởi tạo là 0
    inputUser.setStatus("online");  // Trạng thái khởi tạo là "online"
    inputUser.setPosition(0);  // Điểm khởi tạo là 0

    userRepository.save(inputUser);
    Map<String, Object> successResponse = new HashMap<>();
    successResponse.put("user", inputUser);
    successResponse.put("list", userRepository.findAllUsersWithSelectedColumns());
    return new ResponseEntity<>(successResponse, HttpStatus.OK);
  }
  
  @PostMapping("/login")
  public ResponseEntity<Map<String, ?>> login(@RequestBody User inputUser) {
    System.out.println("Username nhận được: " + inputUser.getUsername());
    System.out.println("Mật khẩu nhận được: " + inputUser.getPassword());

    // Tìm user dựa trên username
    User checkUser = userRepository.findByUsername(inputUser.getUsername());

    // Kiểm tra xem user có tồn tại không
    if (checkUser == null) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("message", "Tên người dùng không tồn tại!");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Kiểm tra mật khẩu
    if (!inputUser.getPassword().equals(checkUser.getPassword())) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("message", "Sai mật khẩu!");
      return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    // Nếu đúng thông tin đăng nhập
    userRepository.updateStatus("online",checkUser.getUsername());
    Map<String, Object> successResponse = new HashMap<>();
    successResponse.put("user", checkUser);
    successResponse.put("list", userRepository.findAllUsersWithSelectedColumns());
    return new ResponseEntity<>(successResponse, HttpStatus.OK);
  }

  @GetMapping("/rankk")
  public ResponseEntity<Map<String, ?>> rankk() {
    Map<String, Object> successResponse = new HashMap<>();
    successResponse.put("list", userRepository.findAllUsersWithSelectedColumnsforranh());
    return new ResponseEntity<>(successResponse, HttpStatus.OK);
  }

  @GetMapping("/search")
  public ResponseEntity<Map<String, ?>> search(@RequestBody String input) {
    System.out.println("Username nhận được: " + input);
    Map<String, Object> successResponse = new HashMap<>();
    successResponse.put("list", userRepository.searchUser(input));
    return new ResponseEntity<>(successResponse, HttpStatus.OK);
  }
}
