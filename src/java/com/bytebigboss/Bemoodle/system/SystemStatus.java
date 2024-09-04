package com.bytebigboss.Bemoodle.system;

import com.bytebigboss.Bemoodle.dto.Status;
import com.bytebigboss.Bemoodle.util.CORSFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "SystemStatus", urlPatterns = {"/SystemStatus"})
public class SystemStatus extends HttpServlet {

    CORSFactory corsFactory = new CORSFactory(Arrays.asList("http://localhost:3000"), Arrays.asList("POST"));

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        corsFactory.setCORSHeaders(req, res);

        Gson gson = new Gson();

        Status status = new Status();
        status.setStatus(205);
        status.setMsg("All Systems Up To Date.");
        status.setAppName("Bemoodle");
        status.setTime(new Date());

        List<String> ls = new ArrayList();
        ls.add("UserRegistration#User");
        ls.add("UserLogin#User");

        status.setServlets(ls);

        res.getWriter().write(gson.toJson(status));
        System.out.println(gson.toJson(status));

    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        corsFactory.setCORSHeaders(req, res);

        res.setStatus(HttpServletResponse.SC_OK);
    }

}
