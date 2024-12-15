package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.entity.Category;
import com.bytebigboss.Bemoodle.entity.ProductStatus;
import com.bytebigboss.Bemoodle.entity.SubCategory;
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
@WebServlet(name = "LoadFeatures", urlPatterns = {"/LoadFeatures"})
public class LoadFeatures extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria findCategories = session.createCriteria(Category.class);
        findCategories.addOrder(Order.asc("name"));
        List<Category> categoryList = findCategories.list();

        Criteria findSubCategories = session.createCriteria(SubCategory.class);
        findSubCategories.addOrder(Order.asc("name"));
        List<SubCategory> subCategoryList = findSubCategories.list();

        Criteria productStatus = session.createCriteria(ProductStatus.class);
        productStatus.addOrder(Order.asc("name"));
        List<ProductStatus> productStatusList = productStatus.list();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        jsonObject.add("subCategoryList", gson.toJsonTree(subCategoryList));
         jsonObject.add("productStatusList", gson.toJsonTree(productStatusList));

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(jsonObject));

        session.close();

    }

}
