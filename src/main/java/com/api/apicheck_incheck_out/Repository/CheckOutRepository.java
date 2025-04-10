package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.Check_Out;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckOutRepository extends JpaRepository<Check_Out, Long> {
}
