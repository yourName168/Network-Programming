package com.example.todolist.repositories;

import com.example.todolist.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);

  @Query("SELECT u.id, u.username, u.name, u.score, u.status FROM User u WHERE u.status <> 'offline'")
  List<Object[]> findAllUsersWithSelectedColumns();

  // Phương thức cập nhật điểm số
  @Modifying
  @Transactional
  @Query("UPDATE User u SET u.score = :score WHERE u.username = :username")
  void updateScoreById(String username, float score);

  @Query("SELECT u.name, u.score, u.status FROM User u ORDER BY u.score DESC")
  List<Object[]> findAllUsersWithSelectedColumnsforranh();

  // Phương thức cập nhật điểm số
  @Modifying
  @Transactional
  @Query("UPDATE User u SET u.status = :status WHERE u.username = :username")
  void updateStatus(String status,String username );

}

