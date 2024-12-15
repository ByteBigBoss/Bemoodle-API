package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.entity.Category;
import com.bytebigboss.Bemoodle.entity.SubCategory;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
@WebServlet(name = "LoadCategories", urlPatterns = {"/LoadCategories"})
public class LoadCategories extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonArray arr = new JsonArray();

        try {

            Criteria findCategories = session.createCriteria(Category.class);
            List<Category> CategoryList = findCategories.list(); //CATEGORY LIST

            for (Category category : CategoryList) {

                JsonObject ctJson = new JsonObject();
                ctJson.addProperty("id", category.getId());
                ctJson.addProperty("name", category.getName());
                ctJson.addProperty("description", category.getDescription());

                Criteria findSubCategories = session.createCriteria(SubCategory.class);
                findSubCategories.add(Restrictions.eq("category", category));
                List<SubCategory> subCategoryList = findSubCategories.list(); //SUB CATEGORY LIST
                
                JsonArray subArr = new JsonArray();
                for(SubCategory subCategory: subCategoryList){
                    
                    JsonObject subCtObj = new JsonObject();
                    subCtObj.addProperty("id", subCategory.getId());
                    subCtObj.addProperty("name", subCategory.getName());
                    subCtObj.addProperty("description", subCategory.getDescription());
                    subArr.add(subCtObj);
                    
                }
                
                ctJson.add("sub", subArr);
                
                arr.add(ctJson);

            }

            //SEND RESPONSE
            res.setContentType("application/json");
            res.getWriter().write(gson.toJson(arr));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //SESSION CLOSE
            session.close();
        }
    }

}
