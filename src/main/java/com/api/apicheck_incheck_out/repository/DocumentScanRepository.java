package com.api.apicheck_incheck_out.repository;

import com.api.apicheck_incheck_out.entity.DocumentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentScanRepository extends JpaRepository<DocumentScan,Long> {
}
