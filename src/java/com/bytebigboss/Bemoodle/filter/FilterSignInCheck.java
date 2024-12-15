//package com.bytebigboss.Bemoodle.filter;
//
//import com.bytebigboss.bcors.Bcors;
//import java.io.IOException;
//import java.util.Arrays;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// *
// * @author ByteBigBoss
// */
//@WebFilter(filterName = "FilterSignInCheck", urlPatterns = {"/*"})
//public class FilterSignInCheck implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        //Process when Filter init
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) request;
//        HttpServletResponse res = (HttpServletResponse) response;
//
//        //APPLY CORS HEADERS
//        Bcors.setCors(req, res);
//        Bcors.getInstance().setAllowedOrigins(Arrays.asList("localhost"));
//
//        //FOR OPTIONS REQUEST, END THE PROCESSING EARLY
//        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
//            res.setStatus(HttpServletResponse.SC_OK);
//            return;
//        }
//        System.out.println("FilterSignInCheck - Request received");
//
//        // Set CORS headers
//        if (req.getSession().getAttribute("user") != null) {
//            System.out.println("User found, proceeding with request");
//            chain.doFilter(request, response);  // Proceed with the request
//        } else {
//            System.out.println("No user found, redirecting to login");
//            res.sendRedirect("http://localhost:3000/auth/signin");  // Redirect to login
//        }
//    }
//
//    @Override
//    public void destroy() {
//        //Process when Filter destroy
//    }
//
//}
