package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Address;
import com.bytebigboss.Bemoodle.entity.Cart;
import com.bytebigboss.Bemoodle.entity.City;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.bytebigboss.bcors.Bcors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadCheckout", urlPatterns = {"/LoadCheckout"})
public class LoadCheckout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Bcors.setCors(request, response);
        
        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        HttpSession httpSession = request.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();

        if (httpSession.getAttribute("user") != null) {

            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

            //get user from db
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            //get user last address from db
            Criteria criteria2 = session.createCriteria(Address.class);
            criteria2.add(Restrictions.eq("user", user));
            criteria2.addOrder(Order.desc("id"));
            criteria2.setMaxResults(1);
            Address address = (Address) criteria2.list().get(0);

            //get city from db
            Criteria criteria3 = session.createCriteria(City.class);
            criteria3.addOrder(Order.asc("name"));
            List<City> cityList = criteria3.list();

            //get cart items from db
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();

            //pack address in json obj
            address.setUser(null);
            jsonObject.add("address", gson.toJsonTree(address));

            //pack cityList in json obj
            jsonObject.add("cityList", gson.toJsonTree(cityList));

            //pack cartList in json obj
            for (Cart cart : cartList) {
                cart.setUser(null);
                cart.getProduct().getStore().getArtisan().setUser(null);
            }

            jsonObject.add("cartList", gson.toJsonTree(cartList));

            jsonObject.addProperty("success", true);

        } else {

            //not signed in
            jsonObject.addProperty("meesage", "login");
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
        session.close();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Bcors.setCors(req, res);
    }
    
    

}
