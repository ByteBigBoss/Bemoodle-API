package com.bytebigboss.Bemoodle.system;

import com.bytebigboss.Bemoodle.dto.Status;
import com.bytebigboss.bcors.Bcors;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

//        req.getSession();
        
        Gson gson = new Gson();

        Status status = new Status();
        status.setStatus(205);
        status.setMsg("All Systems Up To Date.");
        status.setAppName("Bemoodle");
        status.setTime(new Date());

        List<String> ls = new ArrayList();
        ls.add("SignUp#User | Artisan");
        ls.add("Verification#User | Artisan");
        ls.add("SignIn#User | Artisan | Admin");
        ls.add("SignOut#User | Artisan | Admin");
        ls.add("LoadHome#User | Artisan");
        
        status.setServlets(ls);

        res.getWriter().write(gson.toJson(status));
        System.out.println(gson.toJson(status));

    }

    
}
