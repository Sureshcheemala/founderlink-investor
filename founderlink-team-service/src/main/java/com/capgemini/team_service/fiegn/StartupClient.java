package com.capgemini.team_service.fiegn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.capgemini.team_service.dto.StartupResponse;

@FeignClient(name = "startup-service")
public interface StartupClient {

    @GetMapping("/startups/{id}")
    StartupResponse getStartupById(@PathVariable Long id);
}
