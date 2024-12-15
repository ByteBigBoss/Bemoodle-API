package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.CartDTO;
import com.bytebigboss.Bemoodle.dto.ResponseDTO;
import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Cart;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.bytebigboss.Bemoodle.util.Validator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
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
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //RESPONSE OBJECT
        ResponseDTO resDTO = new ResponseDTO();

        //GSON BUILDER
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //MAP REQUEST PARAMETER TO USER_DTO CLASS
        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        //VALIDATE REQUEST PARAMETERS
        if (userDTO.getEmail().isEmpty()) {
            //EMAIL IS EMPTY
            resDTO.setContent("Please enter your Email");

        } else if (!Validator.VALIDATE_EMAIL(userDTO.getEmail())) {
            //EMAIL IS NOT VALID
            resDTO.setContent("Please enter a valid Email");

        } else if (userDTO.getPassword().isEmpty()) {
            //PASSWORD IS EMPTY
            resDTO.setContent("Please enter your Password");

        } else if (!Validator.VALIDATE_PASSWORD(userDTO.getPassword())) {
            //PASSWORD NOT VALID
            resDTO.setContent("Password must include at least one uppercase letter, number, "
                    + "special character, and be at least 8 characters long");

        } else {

            //FIND SESSION USER
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
            criteria1.add(Restrictions.eq("password", userDTO.getPassword()));

            if (!criteria1.list().isEmpty()) {

                User user = (User) criteria1.list().get(0);

                if (!user.getVerification().equals("Verified")) {
                    //NOT VERIFIED
                    req.getSession().setAttribute("email", userDTO.getEmail());
                    resDTO.setContent("Unverified");

                } else {
                    //VERIVIED
                    userDTO.setId(user.getId());
                    userDTO.setDisplay_name(user.getDisplay_name());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setPassword(null);
                    req.getSession().setAttribute("user", userDTO);

                    //Transfer session cart to db cart
                    if (req.getSession().getAttribute("sessionCart") != null) {

                        ArrayList<CartDTO> sessionCart = (ArrayList<CartDTO>) req.getSession().getAttribute("sessionCart");

                        Criteria criteria2 = session.createCriteria(Cart.class);
                        criteria1.add(Restrictions.eq("user", user));
                        List<Cart> dbCart = criteria2.list();

                        if (dbCart.isEmpty()) {

                            //db cart empty, add all session cart items in to db cart
                            for (CartDTO cart_DTO : sessionCart) {
                                Cart cart = new Cart();
                                cart.setProduct(cart_DTO.getProduct());
                                cart.setQty(cart_DTO.getQty());
                                cart.setUser(user);
                                session.save(cart);
                            }

                        } else {

                            //found items in db cart
                            for (CartDTO cart_DTO : sessionCart) {

                                boolean isFoundInDBCart = false;

                                for (Cart cart : dbCart) {

                                    if (cart_DTO.getProduct().getId() == cart.getProduct().getId()) {

                                        //same product found in db & session cart
                                        isFoundInDBCart = true;

                                        if ((cart_DTO.getQty() + cart.getQty()) <= cart.getProduct().getQty()) {

                                            //quantity available
                                            cart.setQty(cart_DTO.getQty() + cart.getQty());
                                            session.update(cart);

                                        } else {

                                            //quantity not available, set max available qty
                                            cart.setQty(cart.getProduct().getQty());
                                            session.update(cart);
                                        }

                                    }

                                }

                                if (isFoundInDBCart) {

                                    //not found in db cart
                                    Cart cart = new Cart();
                                    cart.setProduct(cart_DTO.getProduct());
                                    cart.setQty(cart_DTO.getQty());
                                    cart.setUser(user);
                                    session.save(cart);

                                }

                            }

                        }

                        req.getSession().removeAttribute("sessionCart");
                        session.getTransaction().commit();
                    }

                    resDTO.setSuccess(true);
                    resDTO.setContent("Sign In Success");

                }

            } else {
                resDTO.setContent("Invalid details! Please try again");
            }

            session.close();

        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));

    }

}
