package com.example.demo.Entity;

import com.example.demo.Enum.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.management.Notification;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="user")
@Data
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="password")
    private String password;

    @Column(name="email", unique = true)
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    private Role role;
    public User() {

    }
}
