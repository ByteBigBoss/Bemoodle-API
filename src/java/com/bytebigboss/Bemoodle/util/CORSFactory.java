package com.bytebigboss.Bemoodle.util;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class CORSFactory {

    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private String allowedHeaders = "Content-Type, Authorization";
    private String allowCredentials = "true";

    public CORSFactory(List<String> allowedOrigins, List<String> allowedMethods) {
        this.allowedOrigins = allowedOrigins;
        this.allowedMethods = allowedMethods;
    }
    

    public void setCORSHeaders(HttpServletRequest req, HttpServletResponse res) {

        String origin = req.getHeader("Origin");
       

        if (origin == null || !this.getAllowedOrigins().contains(origin)) {
            return;
        }

        res.setHeader("Access-Control-Allow-Origin", origin);
        
        res.setHeader("Access-Control-Allow-Methods", String.join(", ", this.getAllowedMethods()));

        res.setHeader("Access-Control-Allow-Headers", this.getAllowedHeaders());

        res.setHeader("Access-Control-Allow-Credentials", this.getAllowCredentials());
    }
    
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public String getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }


}
