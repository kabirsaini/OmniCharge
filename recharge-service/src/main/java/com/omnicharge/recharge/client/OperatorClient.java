package com.omnicharge.recharge.client;

import com.omnicharge.recharge.dto.RechargeDtos.OperatorDto;
import com.omnicharge.recharge.dto.RechargeDtos.PlanDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "operator-service")
public interface OperatorClient {

    @GetMapping("/api/operators/{id}")
    OperatorDto getOperatorById(@PathVariable Long id);

    @GetMapping("/api/plans/{planId}")
    PlanDto getPlanById(@PathVariable Long planId);
}
