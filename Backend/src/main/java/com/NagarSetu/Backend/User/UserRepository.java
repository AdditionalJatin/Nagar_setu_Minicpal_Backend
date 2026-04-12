package com.NagarSetu.Backend.User;

import com.NagarSetu.Backend.Entities.User;
import com.NagarSetu.Backend.Entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;



import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> , UserRepoCustom {
         Optional<User> findByEmail(String email);
        Optional<User> findByPhone(String phone);
        boolean existsByPhone(String phone);
    Optional<User> findByIdAndRole(UUID id, UserRole role);


@Query(value = """
    SELECT
    u.id,
    u.phone,
    u.name,
    u.email,
    u.password,
    u.status,
    u.role,
    u.ward_id,
    u.city_id,
    ST_AsGeoJSON(u.location) AS location
    FROM users u
    WHERE u.phone = :phone
""", nativeQuery = true)
Map<String, Object> findUserForLogin(@Param("phone") String phone);




}
