package com.cms.repository;

import com.cms.model.Consultant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, Long> {

    Optional<Consultant> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT c FROM Consultant c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.technology) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Consultant> searchByNameOrTechnology(@Param("keyword") String keyword);

    @Query("SELECT c FROM Consultant c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.technology) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR c.status = :status)")
    Page<Consultant> findByKeywordAndStatus(
            @Param("keyword") String keyword, 
            @Param("status") String status, 
            Pageable pageable
    );

    long countByStatus(String status);

    long countByCreatedAtAfter(LocalDateTime dateTime);

    @Query("SELECT c.technology FROM Consultant c")
    List<String> findAllTechnologies();
}
