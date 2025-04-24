package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
@Repository
public interface ServiceRepository extends JpaRepository<Services,Long> {
}