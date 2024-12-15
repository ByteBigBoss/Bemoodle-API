package com.bytebigboss.Bemoodle.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "LoadHome", urlPatterns = {"/LoadHome"})
public class LoadHome extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

//        jsonObject.addProperty("content", "Bemoodle is Working!. SERVER CONNECTED: " + new Date());
//        jsonObject.addProperty("Servlet", "LoadHome");

        try {
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(jsonObject));

    }


}
