package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Address;
import com.bytebigboss.Bemoodle.entity.Cart;
import com.bytebigboss.Bemoodle.entity.City;
import com.bytebigboss.Bemoodle.entity.Orders;
import com.bytebigboss.Bemoodle.entity.OrderItem;
import com.bytebigboss.Bemoodle.entity.OrderStatus;
import com.bytebigboss.Bemoodle.entity.Product;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.bytebigboss.Bemoodle.util.PayHere;
import com.bytebigboss.Bemoodle.util.Validator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
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

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);

        JsonObject resObj = new JsonObject();
        resObj.addProperty("success", false);

        HttpSession httpSession = req.getSession();

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            Transaction transaction = session.beginTransaction();

            if (httpSession.getAttribute("user") != null) {
                //USER FOUND

                boolean isCurrentAddress = requestJsonObject.get("isCurrentAddress").getAsBoolean();
                String fname = requestJsonObject.get("firstName").getAsString();
                String lname = requestJsonObject.get("lastName").getAsString();
                String cityId = requestJsonObject.get("cityId").getAsString();
                String line1 = requestJsonObject.get("line1").getAsString();
                String line2 = requestJsonObject.get("line2").getAsString();
                String pcode = requestJsonObject.get("postalCode").getAsString();
                String mobile = requestJsonObject.get("mobile").getAsString();

//                resObj.addProperty("message", "FOUND"+isCurrentAddress+fname+lname+cityId+line1+line2+pcode+mobile);
                //user signed in
                //get user from db
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");
                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
                User user = (User) criteria1.uniqueResult();

                if (isCurrentAddress) {

                    //get current address
                    Criteria criteria2 = session.createCriteria(Address.class);
                    criteria2.add(Restrictions.eq("user", user));
                    criteria2.addOrder(org.hibernate.criterion.Order.desc("id"));
                    criteria2.setMaxResults(1);

                    if (criteria2.list().isEmpty()) {

                        //current address not found
                        resObj.addProperty("message", "Current address not found. Please create a new address");

                    } else {

                        //current address found
                        //complete
                        Address address = (Address) criteria2.list().get(0);

                        //complete checkout process
                        saveOrders(session, transaction, address, user, resObj);

                    }

                } else {

                    //create new address
                    if (fname.isEmpty()) {

                        resObj.addProperty("message", "Please fill first name");

                    } else if (lname.isEmpty()) {

                        resObj.addProperty("message", "Please fill last name");

                    } else if (!Validator.VALIDATE_INTEGER(cityId)) {

                        resObj.addProperty("message", "Invalid city");

                    } else {

                        //check city from db
                        Criteria criteria3 = session.createCriteria(City.class);
                        criteria3.add(Restrictions.eq("id", Integer.valueOf(cityId)));

                        if (criteria3.list().isEmpty()) {

                            resObj.addProperty("message", "Invalid city");

                        } else {

                            //city found
                            City city = (City) criteria3.list().get(0);

                            if (line1.isEmpty()) {

                                resObj.addProperty("message", "Please fill address line 1");

                            } else if (line2.isEmpty()) {

                                resObj.addProperty("message", "Please fill address line 2");

                            } else if (pcode.isEmpty()) {

                                resObj.addProperty("message", "Please fill postal code");

                            } else if (pcode.length() != 5) {

                                resObj.addProperty("message", "Invalid postal code");

                            } else if (!Validator.VALIDATE_INTEGER(pcode)) {

                                resObj.addProperty("message", "Invalid postal code");

                            } else if (mobile.isEmpty()) {

                                resObj.addProperty("message", "Please fill mobile number");

                            } else if (!Validator.VALIDATE_MOBILE(mobile)) {

                                resObj.addProperty("message", "Invalid mobile number");

                            } else {

                                //create new address
                                Address address = new Address();
                                address.setCity(city);
                                address.setFirst_name(fname);
                                address.setLast_name(lname);
                                address.setLine1(line1);
                                address.setLine2(line2);
                                address.setMobile(mobile);
                                address.setPostal_code(pcode);
                                address.setUser(user);

                                session.save(address);

                                //complete checkout process
                                saveOrders(session, transaction, address, user, resObj);

                            }

                        }

                    }

                }

            } else {
                //NOT SIGNED IN
                resObj.addProperty("message", "login");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resObj));
    }

    private void saveOrders(Session session, Transaction transaction, Address address, User user, JsonObject resObj) {

        try {

            //create order to db
            Orders orders = new Orders();
            orders.setAddress(address);
            orders.setUser(user);

            int order_id = (int) session.save(orders);

            //get cart items
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();

            //get ordrer_status (5 - paid) from db
            OrderStatus order_Status = (OrderStatus) session.get(OrderStatus.class, 1); //1 - payment pending

            //create order item in db
            double amount = 0;
            String items = "";

            for (Cart cartItem : cartList) {

                //calculate amount
                amount += cartItem.getQty() * cartItem.getProduct().getPrice();
                if (address.getCity().getId() == 1) {
                    amount += 350;
                } else {
                    amount += 500;
                }

                //get item details
                items += cartItem.getProduct().getTitle() + "x" + cartItem.getQty();

                //get product
                Product product = cartItem.getProduct();

                OrderItem order_Item = new OrderItem();
                order_Item.setOrder(orders);
                order_Item.setOrder_status(order_Status);
                order_Item.setProduct(product);
                order_Item.setQty(cartItem.getQty());

                session.save(order_Item);

                //update product qty in db
                product.setQty(product.getQty() - cartItem.getQty());
                session.update(product);

                //delete cart item in db
                session.delete(cartItem);

            }

            transaction.commit();

            //set payment data
            String merchant_id = "1227426";
            String formatedAmount = new DecimalFormat("0.00").format(amount);
            String currency = "LKR";
            String merchantSecret = PayHere.generateMD5("NTE2MzQxMjkyMzIyMDE4NzAzMTM0ODcxODIyMjE3NzIwNTIyMQ==");

            JsonObject payhere = new JsonObject();
            payhere.addProperty("merchant_id", merchant_id);

            payhere.addProperty("return_url", "");
            payhere.addProperty("cancel_url", "");
            payhere.addProperty("notify_url", "VerifyPayments");

            payhere.addProperty("first_name", address.getFirst_name());
            payhere.addProperty("last_name", address.getLast_name());
            payhere.addProperty("email", user.getEmail());
            payhere.addProperty("phone", "0743837327");
            payhere.addProperty("address", "No. 132/A Mederikanaththa, Ehalape, Maliduwa");
            payhere.addProperty("city", "Akuressa");
            payhere.addProperty("country", "Sri-Lanka");
            payhere.addProperty("order_id", String.valueOf(order_id));
            payhere.addProperty("items", items);
            payhere.addProperty("currency", currency);
            payhere.addProperty("amount", formatedAmount);
            payhere.addProperty("sandbox", true);

            //generate md5
            String md5Hash = PayHere.generateMD5(merchant_id + order_id + formatedAmount + currency + merchantSecret);
            payhere.addProperty("hash", md5Hash);

            resObj.addProperty("success", true);
            resObj.addProperty("message", "Checkout Completed");

            Gson gson = new Gson();
            resObj.add("payhereJson", gson.toJsonTree(payhere));

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            resObj.addProperty("message", "An error occurred. Please try again.");
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

}
