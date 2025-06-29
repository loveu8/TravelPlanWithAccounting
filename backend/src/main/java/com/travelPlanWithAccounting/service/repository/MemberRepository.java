package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
  boolean existsByEmail(String email);

  Optional<Member> findByEmail(String email);
}
