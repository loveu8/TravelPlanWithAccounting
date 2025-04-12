package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestUserRepository extends JpaRepository<TestUser, Long> {}
