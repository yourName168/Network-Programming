package com.example.todolist.schemas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSchema {
  private String name, username, oldPassword, newPassword;
  private Integer sex;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date birthday;
}
