package com.api.apicheck_incheck_out.repository;

import com.api.apicheck_incheck_out.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Services,Long> {
}
