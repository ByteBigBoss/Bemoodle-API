package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Artisan;
import com.bytebigboss.Bemoodle.entity.Store;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "CheckStore", urlPatterns = {"/CheckStore"})
public class CheckStore extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonObject resObj = new JsonObject();
        resObj.addProperty("status", false);
        resObj.addProperty("isArtisan", false);
        resObj.addProperty("isSignIn", false);

        if (req.getSession().getAttribute("user") != null) {
            //ALREADY SIGNED IN

            UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");

            //FIND USER IN DB
            Criteria findUser = session.createCriteria(User.class);
            findUser.add(Restrictions.eq("username", userDTO.getUsername()));

            if (!findUser.list().isEmpty()) {
                //USER FOUND

                User user = (User) findUser.uniqueResult(); //GET USER FROM DB
                resObj.addProperty("isSignIn", true);

                //FIND USER HAVE ARTISAN PROFILE OR NOT IN DB
                Criteria findArtisan = session.createCriteria(Artisan.class);
                findArtisan.add(Restrictions.eq("user", user));

                if (!findArtisan.list().isEmpty()) {
                    //USER HAVE ARTISAN PROFILE

                    Artisan artisan = (Artisan) findArtisan.uniqueResult();
                    resObj.addProperty("isArtisan", true);

                    //FIND ARTISAN STORE'S IN DB
                    Criteria findArtisanStores = session.createCriteria(Store.class);
                    findArtisanStores.add(Restrictions.eq("artisan", artisan));

                    if (!findArtisanStores.list().isEmpty()) {
                        //ARTISAN HAVE STORE'S

                        List<Store> storeList = findArtisanStores.list();

                        resObj.addProperty("status", true);
                        resObj.add("storeList", gson.toJsonTree(storeList));

                    }

                }

            } else {
                //INVALID DETAILS
                resObj.addProperty("content", "Something went wrong. Please try again!");
            }

        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resObj));
        session.close();

    }

}
