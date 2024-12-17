package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.CartDTO;
import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Cart;
import com.bytebigboss.Bemoodle.entity.Product;
import com.bytebigboss.Bemoodle.entity.Store;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadCartItems", urlPatterns = {"/LoadCartItems"})
public class LoadCartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();

        HttpSession httpSession = req.getSession();

        JsonArray dtoList = new JsonArray();

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            Criteria findStores = session.createCriteria(Store.class);
            List<Store> storeList = findStores.list();

            if (httpSession.getAttribute("user") != null) {

                //DB Cart
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
                User user = (User) criteria1.uniqueResult();

                Criteria criteria2 = session.createCriteria(Cart.class);
                criteria2.add(Restrictions.eq("user", user));
                List<Cart> cartList = criteria2.list();

                for (Store store : storeList) {

                    JsonObject jo = new JsonObject();
                    ArrayList<CartDTO> items = new ArrayList<>();

                    for (Cart cart : cartList) {

                        if (store.getName().equals(cart.getProduct().getStore().getName())) {
                            CartDTO cartDTO = new CartDTO();

                            Product product = cart.getProduct();
                            product.getStore().getArtisan().setUser(null);

                            cartDTO.setProduct(product);
                            cartDTO.setQty(cart.getQty());
                            items.add(cartDTO);
                        }

                    }

                    if (items != null) {
                        int totalPrice = 0;

                        for (CartDTO item : items) {
                            totalPrice += item.getProduct().getPrice() * item.getQty();
                        }

                        if (totalPrice != 0) {
                            jo.add("items", gson.toJsonTree(items));
                            jo.addProperty("totalPrice", totalPrice);
                            jo.add("store", gson.toJsonTree(store));
                            dtoList.add(jo);
                        }
                    }

                }

            } else {

                //Session Cart
                if (httpSession.getAttribute("sessionCart") != null) {

                    List<CartDTO> list = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                    for (Store store : storeList) {

                        JsonObject jo = new JsonObject();
                        ArrayList<CartDTO> items = new ArrayList<>();

                        for (CartDTO dto : list) {

                            if (store.getName().equals(dto.getProduct().getStore().getName())) {
                                CartDTO cartDTO = new CartDTO();

                                Product product = dto.getProduct();
                                product.getStore().getArtisan().setUser(null);

                                cartDTO.setProduct(product);
                                cartDTO.setQty(dto.getQty());
                                items.add(cartDTO);
                            }
                        }

                        if (items != null) {
                            int totalPrice = 0;

                            for (CartDTO item : items) {
                                totalPrice += item.getProduct().getPrice() * item.getQty();
                            }

                            if (totalPrice != 0) {
                                jo.add("items", gson.toJsonTree(items));
                                jo.addProperty("totalPrice", totalPrice);
                                jo.add("store", gson.toJsonTree(store));
                                dtoList.add(jo);
                            }
                        }
                    }

                } else {

                    //cart empty
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(dtoList));
        session.close();
    }

}
