package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.CartDTO;
import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Cart;
import com.bytebigboss.Bemoodle.entity.Product;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        HttpSession httpSession = request.getSession();

        ArrayList<CartDTO> cartDTOList = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            if (httpSession.getAttribute("user") != null) {

                //DB Cart
                UserDTO user_DTO = (UserDTO) httpSession.getAttribute("user");

                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                User user = (User) criteria1.uniqueResult();

                Criteria criteria2 = session.createCriteria(Cart.class);
                criteria2.add(Restrictions.eq("user", user));

                List<Cart> cartList = criteria2.list();

                for (Cart cart : cartList) {

                    CartDTO cart_DTO = new CartDTO();

                    Product product = cart.getProduct();
                    product.getStore().getArtisan().setUser(null);

                    cart_DTO.setProduct(product);
                    cart_DTO.setQty(cart.getQty());

                    cartDTOList.add(cart_DTO);

                }

            } else {

                //Session Cart
                if (httpSession.getAttribute("sessionCart") != null) {

                    cartDTOList = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                    for (CartDTO cart_DTO : cartDTOList) {
                        cart_DTO.getProduct().getStore().getArtisan().setUser(null);
                    }

                } else {

                    //cart empty
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(cartDTOList));
        session.close();
    }

}
