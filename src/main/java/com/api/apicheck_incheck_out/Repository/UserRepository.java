package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
