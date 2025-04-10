package com.api.apicheck_incheck_out.DocumentScanMock.Repository;

import com.api.apicheck_incheck_out.DocumentScanMock.Entity.DocumentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentScanRepository extends JpaRepository<DocumentScan,Long> {
}
