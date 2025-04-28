package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
}
