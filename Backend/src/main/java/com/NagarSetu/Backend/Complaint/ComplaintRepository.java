package com.NagarSetu.Backend.Complaint;

import com.NagarSetu.Backend.Entities.Complaint;
import com.NagarSetu.Backend.Entities.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID>, ComplaintCustom{

    long countByWardIdAndStatusIn(UUID wardId, Collection<ComplaintStatus> statuses);


}
