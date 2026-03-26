package com.founderlink.api_gateway.security;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	private final JwtUtil jwtUtil;

	public static final List<String> openApiEndpoints = List.of("/auth/","/swagger-ui",  "/v3/api-docs");

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String path = exchange.getRequest().getURI().getPath();

		if (openApiEndpoints.stream().anyMatch(path::startsWith)) {
			return chain.filter(exchange);
		}

		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String token = authHeader.substring(7);

		if (!jwtUtil.validateToken(token)) {
			exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}
		
		Claims claims = jwtUtil.extractAllClaims(token);

		String email	 = claims.getSubject();
		List<String> roles = jwtUtil.extractRolesFromClaims(claims);
		
		String rolesHeader = String.join(",", roles);

		ServerWebExchange mutatedExchange = exchange.mutate()
		        .request(exchange.getRequest().mutate()
		                .header("X-User-Email", email)
		                .header("X-User-Role", rolesHeader)
		                .build())
		        .build();

		return chain.filter(mutatedExchange);
	}

	@Override
	public int getOrder() {
		return -1; // high priority
	}
}