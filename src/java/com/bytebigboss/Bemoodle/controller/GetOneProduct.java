package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.entity.Product;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
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
@WebServlet(name = "GetOneProduct", urlPatterns = {"/GetOneProduct"})
public class GetOneProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //GSON OBJECT
        Gson gson = new Gson();

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        try {

            String id = req.getParameter("id");

            if (id.isEmpty()) {
                jsonObject.addProperty("message", "ID Not Found");
            } else {

                Criteria findProduct = session.createCriteria(Product.class);
                findProduct.add(Restrictions.eq("id", Integer.parseInt(id)));

                if (findProduct.list().isEmpty()) {
                    jsonObject.addProperty("message", "Product with ID not Found");
                } else {
                    Product product = (Product) findProduct.uniqueResult();
                    User user = product.getStore().getArtisan().getUser();
                    user.setPassword(null);

                    jsonObject.add("product", gson.toJsonTree(product));
                    jsonObject.addProperty("success", true);

                    res.setContentType("application/json");
                    res.getWriter().write(gson.toJson(jsonObject));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();

        }

    }

}
