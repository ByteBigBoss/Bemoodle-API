package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.CartDTO;
import com.bytebigboss.Bemoodle.dto.ResponseDTO;
import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Cart;
import com.bytebigboss.Bemoodle.entity.Product;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.bytebigboss.Bemoodle.util.Validator;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ResponseDTO response_DTO = new ResponseDTO();

        String id = request.getParameter("id");
        String qty = request.getParameter("qty");

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {

            if (!Validator.VALIDATE_INTEGER(id)) {
                //product not found
                response_DTO.setContent("Product not found");

            } else if (!Validator.VALIDATE_INTEGER(qty)) {
                //invalid quantity
                response_DTO.setContent("Invalid quantity");

            } else {

                int productId = Integer.parseInt(id);
                int productQty = Integer.parseInt(qty);

                if (productQty <= 0) {

                    //Quantity must be greater than 0
                    response_DTO.setContent("Quantity must be greater than 0");
                } else {

                    Product product = (Product) session.get(Product.class, productId);

                    if (product != null) {

                        //product found
                        if (request.getSession().getAttribute("user") != null) {

                            //DB Cart
                            UserDTO user_DTO = (UserDTO) request.getSession().getAttribute("user");

                            //get db user
                            Criteria criteria1 = session.createCriteria(User.class);
                            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                            User user = (User) criteria1.uniqueResult();

                            //check in db cart
                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            criteria2.add(Restrictions.eq("product", product));

                            if (criteria2.list().isEmpty()) {

                                //cart item not found
                                if (productQty <= product.getQty()) {

                                    //add to cart
                                    Cart cart = new Cart();
                                    cart.setProduct(product);
                                    cart.setQty(productQty);
                                    cart.setUser(user);

                                    session.save(cart);
                                    session.getTransaction().commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product added to the Cart");

                                } else {

                                    //qty exceed
                                    response_DTO.setContent("Quantity limit exceed");
                                }

                            } else {

                                //item already found
                                Cart cartItem = (Cart) criteria2.uniqueResult();

                                if (cartItem.getQty() + productQty <= product.getQty()) {

                                    //add to cart
                                    cartItem.setQty(productQty);

                                    session.update(cartItem);
                                    transaction.commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product update successfully");

                                } else {

                                    //qty limit excced, qty not available
                                    response_DTO.setContent("Quantity limit exceed, not available");
                                }

                            }

                        } else {

                            //Session Cart
                            HttpSession httpSession = request.getSession();

                            if (httpSession.getAttribute("sessionCart") != null) {

                                //session cart found
                                ArrayList<CartDTO> sessionCart = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                                CartDTO foundCart_DTO = null;

                                for (CartDTO cart_DTO : sessionCart) {

                                    if (cart_DTO.getProduct().getId() == product.getId()) {

                                        foundCart_DTO = cart_DTO;
                                        break;
                                    }

                                }

                                if (foundCart_DTO != null) {

                                    //product found
                                    if (foundCart_DTO.getQty() + productQty <= product.getQty()) {

                                        //update qty
                                        foundCart_DTO.setQty(foundCart_DTO.getQty() + productQty);

                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Product update successfully");

                                    } else {

                                        //qty limit excced, qty not available
                                        response_DTO.setContent("Quantity not available, limit excced");
                                    }

                                } else {

                                    //product not found
                                    if (productQty <= product.getQty()) {

                                        //add to session cart
                                        CartDTO cart_DTO = new CartDTO();
                                        cart_DTO.setProduct(product);
                                        cart_DTO.setQty(productQty);
                                        sessionCart.add(cart_DTO);

                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Product added to the Session Cart");

                                    } else {

                                        //quantity not available
                                        response_DTO.setContent("Quantity not available");
                                    }

                                }

                            } else {

                                //session cart not found
                                if (productQty <= product.getQty()) {

                                    //add to session cart
                                    ArrayList<CartDTO> sessionCart = new ArrayList<>();

                                    CartDTO cart_DTO = new CartDTO();
                                    cart_DTO.setProduct(product);
                                    cart_DTO.setQty(productQty);
                                    sessionCart.add(cart_DTO);

                                    httpSession.setAttribute("sessionCart", sessionCart);

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product added to the Cart");

                                } else {

                                    //quantity not available
                                    response_DTO.setContent("Quantity not available");
                                }

                            }

                        }

                    } else {

                        //product not found
                        response_DTO.setContent("Product not found");
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            response_DTO.setContent("Unable to process your request");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        session.close();
    }

}
