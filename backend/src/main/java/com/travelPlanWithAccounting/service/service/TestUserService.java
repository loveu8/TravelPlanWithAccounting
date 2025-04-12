package com.travelPlanWithAccounting.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.travelPlanWithAccounting.service.entity.TestUser;
import com.travelPlanWithAccounting.service.repository.TestUserRepository;
import java.util.List;

@Service
public class TestUserService {

    @Autowired
    private TestUserRepository testUserRepository;

    public List<TestUser> getAllUsers() {
        try {
            return testUserRepository.findAll();
        } catch (Exception e) {
            // 添加日誌
            throw new RuntimeException("Error fetching users: " + e.getMessage());
        }
    }
}