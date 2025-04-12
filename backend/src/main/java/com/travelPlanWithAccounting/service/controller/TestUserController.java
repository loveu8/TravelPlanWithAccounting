package com.travelPlanWithAccounting.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.travelPlanWithAccounting.service.entity.TestUser;
import com.travelPlanWithAccounting.service.service.TestUserService;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class TestUserController {

    @Autowired
    private TestUserService testUserService;

    @GetMapping
    public List<TestUser> getAllUsers() {
        return testUserService.getAllUsers();
    }
}