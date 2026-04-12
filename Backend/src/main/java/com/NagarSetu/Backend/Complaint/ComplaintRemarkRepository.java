package com.NagarSetu.Backend.Complaint;

import com.NagarSetu.Backend.Entities.ComplaintRemark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComplaintRemarkRepository extends JpaRepository<ComplaintRemark, UUID> {
}
