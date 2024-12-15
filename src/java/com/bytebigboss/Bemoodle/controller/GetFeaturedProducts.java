package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.entity.Product;
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

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "GetFeaturedProducts", urlPatterns = {"/GetFeaturedProducts"})
public class GetFeaturedProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //GSON OBJECT
        Gson gson = new Gson();

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        try {

            Criteria findProducts = session.createCriteria(Product.class);
            findProducts.addOrder(Order.asc("title"));
            findProducts.setMaxResults(8);

            List<Product> products = findProducts.list();

            jsonObject.add("products", gson.toJsonTree(products));
            jsonObject.addProperty("success", true);

            res.setContentType("application/json");
            res.getWriter().write(gson.toJson(jsonObject));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

    }

}
