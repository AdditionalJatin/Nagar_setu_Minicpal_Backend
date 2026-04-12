package com.NagarSetu.Backend.Complaint;


import com.NagarSetu.Backend.Entities.ComplaintPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComplaintPhotoRepository extends JpaRepository<ComplaintPhoto, UUID> {



}
