package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Artisan;
import com.bytebigboss.Bemoodle.entity.Product;
import com.bytebigboss.Bemoodle.entity.Store;
import com.bytebigboss.Bemoodle.entity.StoreCategory;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "LoadStoreProducts", urlPatterns = {"/LoadStoreProducts"})
public class LoadStoreProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //GSON OBJECT
        Gson gson = new Gson();

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        String storeName = req.getParameter("store");

        if (req.getSession().getAttribute("user") == null) {
            jsonObject.addProperty("message", "SignIn");
        } else {

            //GET SESSION USER
            UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");
            //SEARCH SESSION USER IN DB
            Criteria findUser = session.createCriteria(User.class);
            findUser.add(Restrictions.eq("username", userDTO.getUsername()));
            //CAST USER FROM SEARCH
            User user = (User) findUser.uniqueResult();

            //SEARCH SESSION ARTISAN IN DB
            Criteria findArtisan = session.createCriteria(Artisan.class);
            findArtisan.add(Restrictions.eq("user", user));
            //CAST ARTISAN FROM SEARCH
            Artisan artisan = (Artisan) findArtisan.uniqueResult();

            //FIND ARTISAN'S STORES
            Criteria findStores = session.createCriteria(Store.class);
            findStores.add(Restrictions.eq("artisan", artisan));
            //GET STORE LIST
            List<Store> storeList = findStores.list();

            if (storeList.isEmpty()) {
                jsonObject.addProperty("message", "Store Not Found");
            } else {

                //====> PRODUCTS <====//
                //SELECT ALL STORE PRODUCTS
                Criteria findStoreProducts = session.createCriteria(Product.class);
                for (Store store : storeList) {
                    if (store.getName().equals(storeName)) {
                        findStoreProducts.add(Restrictions.eq("store", store));
                    }
                }

                if (findStoreProducts.list().isEmpty()) {

                    jsonObject.addProperty("message", "Matching Products Not Founded From Results");

                } else {

                    findStoreProducts.addOrder(Order.asc("title"));
                    List<Product> storeProductList = findStoreProducts.list(); //PRODUCT LIST

                    jsonObject.add("storeProductList", gson.toJsonTree(storeProductList));
                    jsonObject.addProperty("success", true);
                }
            }

        }

        //SEND RESPONSE
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(jsonObject));

        //SESSION CLOSE
        session.close();

    }

}
