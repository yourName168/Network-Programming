package com.example.todolist.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "Player")
@Data // Lombok annotation for getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok annotation for no-arguments constructor
@AllArgsConstructor // Lombok annotation for all-arguments constructor
@Builder // Lombok annotation to create builder pattern
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "username", length = 50, nullable = false, unique = true)
  private String username;

  @Column(name = "password", length = 255, nullable = false)
  private String password;

  @Column(name = "name", length = 255, nullable = false)
  private String name;

  @Column(name = "score", nullable = false)
  private Integer score;

  @Column(name = "position", nullable = false)
  private Integer position;

  @Column(name = "status", nullable = false)
  private String status;

}

