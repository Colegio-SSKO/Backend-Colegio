package com.example.backendcol.api;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebFilter(filterName = "SetHeaderFilter" , urlPatterns = "/*")
public class SetHeaderFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("filteree wda he he");
        httpResponse.setContentType("application/json");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.addHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        httpResponse.addHeader("Access-Control-Allow-Methods" , "GET, POST, PUT, DELETE OPTIONS");
        httpResponse.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }
}
