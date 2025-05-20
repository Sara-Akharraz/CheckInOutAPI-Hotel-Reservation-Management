package com.api.apicheck_incheck_out.Repository;

import com.api.apicheck_incheck_out.Entity.DocumentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentScanRepository extends JpaRepository<DocumentScan,Long> {
}
