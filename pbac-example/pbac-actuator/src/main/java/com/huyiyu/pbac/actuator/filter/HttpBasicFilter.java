package com.huyiyu.pbac.actuator.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class HttpBasicFilter extends OncePerRequestFilter {

    private static final Pattern BASIC_PATTERN = Pattern.compile("Basic (?<token>[A-Za-z0-9]+=+)");


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("HTTP Basic Authentication Filter");
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        Matcher matcher = BASIC_PATTERN.matcher(header);
        if (matcher.matches()) {
            String token = matcher.group("token");
            String[] split = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8).split(":");
            if (split.length == 2 && split[0].equals("huyiyu") && split[1].equals("huyiyu")) {
                filterChain.doFilter(request, response);
            }
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
