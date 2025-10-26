package com.example.tasktube.server.api.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestApiLoggingFilter extends OncePerRequestFilter {
    public static final String X_TASK_TUBE_TRACE_ID_HEADER = "X-TaskTube-Trace-Id";
    public static final String TRACE_ID_MDC_KEY = "traceId";
    public static final String HOST_HEADER = "host";
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiLoggingFilter.class);
    private static final List<String> EXCLUDED_URI_PREFIX = List.of(
            "/readiness",
            "/liveness",
            "/favicon",
            "/swagger",
            "/api-docs",
            "/static",
            "/index"
    );

    private String getTraceId(final HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader(X_TASK_TUBE_TRACE_ID_HEADER))
                .orElse(Integer.toHexString(UUID.randomUUID().hashCode()));
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final String requestURI = request.getRequestURI();
        return EXCLUDED_URI_PREFIX.stream().anyMatch(requestURI::startsWith);
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        MDC.put(TRACE_ID_MDC_KEY, getTraceId(request));

        final long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            final String protocol = request.getProtocol();
            final String httpMethod = request.getMethod();
            final String host = request.getHeader(HOST_HEADER);
            final String uri = request.getRequestURI();
            final String query = Optional.ofNullable(request.getQueryString()).map("?%s"::formatted).orElse(StringUtils.EMPTY);
            final int httpStatus = response.getStatus();
            final long contentLength = ((ResponseFacade) response).getContentWritten();
            final long durationMillis = System.currentTimeMillis() - start;

            LOGGER.info("{} {} {}{}{} {} {}bytes {}ms ",
                    protocol,
                    httpMethod,
                    host,
                    uri,
                    query,
                    httpStatus,
                    contentLength,
                    durationMillis
            );

            MDC.clear();
        }
    }
}
