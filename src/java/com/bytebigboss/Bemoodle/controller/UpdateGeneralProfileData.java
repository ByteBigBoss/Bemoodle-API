package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.CheckLogin;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.bytebigboss.Bemoodle.util.Validator;
import com.bytebigboss.bcors.Bcors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "UpdateGeneralProfileData", urlPatterns = {"/UpdateGeneralProfileData"})
public class UpdateGeneralProfileData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", false);

        HttpSession httpSession = req.getSession();

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            if (httpSession != null && CheckLogin.check(httpSession)) {
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");
                JsonObject reqData = gson.fromJson(req.getReader(), JsonObject.class);

                String username = reqData.get("username").getAsString();
                String dName = reqData.get("dName").getAsString();
                String email = reqData.get("email").getAsString();

                // VALIDATIONS
                if (dName == null || dName.isEmpty()) {
                    jsonObject.addProperty("message", "Please enter your Display Name");
                } else if (username == null || username.isEmpty()) {
                    jsonObject.addProperty("message", "Please enter your Username");
                } else if (email == null || email.isEmpty()) {
                    jsonObject.addProperty("message", "Please enter your Email");
                } else if (!Validator.VALIDATE_EMAIL(email)) {
                    jsonObject.addProperty("message", "Please enter a valid Email");
                } else {

                    session.beginTransaction();

                    User user = (User) session.createCriteria(User.class)
                            .add(Restrictions.eq("id", userDTO.getId()))
                            .uniqueResult();

                    if (user != null) {

                        // UPDATE USER PROFILE
                        Criteria findUsername = session.createCriteria(User.class);
                        findUsername.add(Restrictions.eq("username", username));
                        findUsername.add(Restrictions.ne("id", user.getId()));

                        if (!findUsername.list().isEmpty()) {
                            jsonObject.addProperty("message", "Username Taken, use another.");
                        } else {

                            user.setUsername(username);
                            user.setDisplay_name(dName);
                            user.setEmail(email);

                            session.update(user);
                            session.getTransaction().commit();

                            jsonObject.addProperty("status", true);
                            jsonObject.addProperty("message", "Profile successfully updated.");
                        }
                    } else {
                        jsonObject.addProperty("message", "User not found.");
                    }

                }

            } else {
                jsonObject.addProperty("message", "login");

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(jsonObject));

    }

}
