package com.example.demo.Repository;

import com.example.demo.Enum.Role;
import com.example.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email and u.password=:password")
    Optional<User> logIn(@Param("email") String email, @Param("password") String password);

    @Query("SELECT u from User u where u.role=Role.RECEPTIONIST")
    static List<User> findReceptionists();

    @Query("SELECT u from User u where u.role=Role.Client")
    List<User> findClients();
}
