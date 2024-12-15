package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Address;
import com.bytebigboss.Bemoodle.entity.Artisan;
import com.bytebigboss.Bemoodle.entity.City;
import com.bytebigboss.Bemoodle.entity.Country;
import com.bytebigboss.Bemoodle.entity.State;
import com.bytebigboss.Bemoodle.entity.Store;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.CheckLogin;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
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

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "LoadProfile", urlPatterns = {"/LoadProfile"})
public class LoadProfile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", false);

        HttpSession httpSession = req.getSession();

        Session session = HibernateUtil.getSessionFactory().openSession();

        System.out.println("Session ID: " + httpSession.getId());
        System.out.println("User in session: " + httpSession.getAttribute("user"));

        try {

            if (CheckLogin.check(httpSession)) {

                //** GET USER ===============================================
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                Criteria findUser = session.createCriteria(User.class);
                findUser.add(Restrictions.eq("email", userDTO.getEmail()));
                User user = (User) findUser.uniqueResult();

                //*** GET ARTISAN => ARTISAN STORES ==========================
                Criteria findArtisan = session.createCriteria(Artisan.class);
                findArtisan.add(Restrictions.eq("user", user));
                Artisan artisan = (Artisan) findArtisan.uniqueResult();

                //FIND COUNTRIES, STATES, CITIES
                Criteria findCountries = session.createCriteria(Country.class);
                findCountries.addOrder(Order.asc("name"));
                List<Country> countryList = findCountries.list();

                Criteria findStates = session.createCriteria(State.class);
                findStates.addOrder(Order.asc("name"));
                List<State> stateList = findStates.list();

                Criteria findCities = session.createCriteria(City.class);
                findCities.addOrder(Order.asc("name"));
                List<City> cityList = findCities.list();

                if (artisan == null) {
                    jsonObject.addProperty("isArtisan", false);
                } else {
                    //STORES
                    Criteria findStores = session.createCriteria(Store.class);
                    findStores.add(Restrictions.eq("artisan", artisan));
                    List<Store> storeList = findStores.list();

                    jsonObject.add("stores", gson.toJsonTree(storeList));
                    jsonObject.addProperty("isArtisan", true);
                }

                //*** GET ARTISAN => ARTISAN STORES ==========================
                //*** GET USER ADDRESSES =====================================
                Criteria findAddress = session.createCriteria(Address.class);
                findAddress.add(Restrictions.eq("user", user));
                List<Address> addressList = findAddress.list();

                if (addressList.isEmpty()) {
                    jsonObject.addProperty("haveAddress", false);
                } else {
                    jsonObject.add("addressList", gson.toJsonTree(addressList));
                    jsonObject.addProperty("haveAddress", true);

                }

                //*** GET ORDERS ============================================
                Criteria findOrders = session.createCriteria(Order.class);
                findOrders.add(Restrictions.eq("user", user));
                List<Order> orderList = findOrders.list();

                if (orderList.isEmpty()) {
                    jsonObject.addProperty("haveOrders", false);

                } else {
                    jsonObject.add("orderList", gson.toJsonTree(orderList));
                    jsonObject.addProperty("haveOrders", true);
                }

                user.setPassword(null);

                //CHECK AVATAR IMAGE
                //APPLICATION PATH
                String serverPath = req.getServletContext().getRealPath("");
                String folderPath = serverPath.replace("build" + File.separator + "web", "web");
                String userAvatarPath = folderPath + File.separator + "user" + File.separator + user.getId() + File.separator + "profile" + File.separator + "avatar.png";
                File userAvatarFile = new File(userAvatarPath);

                if (userAvatarFile.exists()) {
                    //AVATAR IMAGE FOUND
                    jsonObject.addProperty("haveAvatar", true);
                } else {
                    //AVATAR IMAGE NOT FOUND
                    jsonObject.addProperty("haveAvatar", false);
                    jsonObject.addProperty("avatar_letters", user.getDisplay_name().charAt(0) + "" + user.getDisplay_name().charAt(1));
                }

                jsonObject.add("user", gson.toJsonTree(user));
                jsonObject.add("countryList", gson.toJsonTree(countryList));
                jsonObject.add("stateList", gson.toJsonTree(stateList));
                jsonObject.add("cityList", gson.toJsonTree(cityList));
                jsonObject.addProperty("status", true);

            } else {
                jsonObject.addProperty("message", "login");
            }

            res.setContentType("application/json");
            res.getWriter().write(gson.toJson(jsonObject));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

    }

}
