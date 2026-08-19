package com.yarossa.remotecontrol.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yarossa.remotecontrol.dto.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

	private static final String API_PREFIX = "/api/";
	private static final String HEALTH_PATH = "/api/health";
	private static final String API_KEY_HEADER = "X-API-Key";

	private final AppProperties appProperties;
	private final ObjectMapper objectMapper;

	public ApiKeyAuthFilter(AppProperties appProperties, ObjectMapper objectMapper) {
		this.appProperties = appProperties;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String path = request.getRequestURI();
		if (!path.startsWith(API_PREFIX) || HEALTH_PATH.equals(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String apiKey = request.getHeader(API_KEY_HEADER);
		if (!appProperties.apiKey().equals(apiKey)) {
			writeUnauthorized(response);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void writeUnauthorized(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), new ApiResponse(false, "Unauthorized"));
	}
}
