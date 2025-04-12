package com.travelPlanWithAccounting.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.travelPlanWithAccounting.service.entity.TestUser;

@Repository
public interface TestUserRepository extends JpaRepository<TestUser, Long> {
}