package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.entity.StoreCategory;
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
@WebServlet(name = "LoadStoreCategory", urlPatterns = {"/LoadStoreCategory"})
public class LoadStoreCategory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //GSON OBJECT
        Gson gson = new Gson();

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        //====> CATEGORY <====//
        //SELECT ALL CATEGORIES
        Criteria findStoreCategories = session.createCriteria(StoreCategory.class);
        findStoreCategories.addOrder(Order.asc("name"));
        List<StoreCategory> storeCategoryList = findStoreCategories.list(); //CATEGORY LIST

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("storeCategoryList", gson.toJsonTree(storeCategoryList));

        //SEND RESPONSE
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(jsonObject));

        //SESSION CLOSE
        session.close();

    }

}
