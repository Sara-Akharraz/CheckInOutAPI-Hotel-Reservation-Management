package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.User;

import com.api.apicheck_incheck_out.Enums.Role;
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

    List<User> findByRole(Role role);
//    @Query("SELECT u FROM User u WHERE u.role = com.api.apicheck_incheck_out.Entity.Role.RECEPTIONIST")
//    List<User> findReceptionists();
//
//    // Query for clients using the Role Enum
//    @Query("SELECT u FROM User u WHERE u.role = com.api.apicheck_incheck_out.Entity.Role.CLIENT")
//    List<User> findClients();
}
