package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.CreateStoreResponseDTO;
import com.bytebigboss.Bemoodle.dto.StoreDTO;
import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Artisan;
import com.bytebigboss.Bemoodle.entity.ArtisanStatus;
import com.bytebigboss.Bemoodle.entity.Store;
import com.bytebigboss.Bemoodle.entity.StoreCategory;
import com.bytebigboss.Bemoodle.entity.StoreStatus;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Timestamp;
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
@WebServlet(name = "CreateStore", urlPatterns = {"/CreateStore"})
public class CreateStore extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //GSON BUILDER: => EXCLUDE EXPOSE ANNOTATED FIELD'S FROM SERIALIZATION (RESPONSE)
        Gson gson = new Gson();

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        //RESPONSE OBJ
        CreateStoreResponseDTO resDTO = new CreateStoreResponseDTO();

        //MAP REQUEST PARAMETER TO USER_DTO CLASS
        StoreDTO storeDTO = gson.fromJson(req.getReader(), StoreDTO.class);

        HttpSession httpSession = req.getSession();

        if (storeDTO.getName().isEmpty()) {
            resDTO.setMessage("Please enter your Store Name");

        } else if (storeDTO.getDescription().isEmpty()) {
            resDTO.setMessage("Please enter your Store Description");

        } else {

            try {

                //FIND IS STORE EXISTS
                Criteria findStore = session.createCriteria(Store.class);
                findStore.add(Restrictions.eq("name", storeDTO.getName()));

                if (findStore.list().isEmpty()) {
                    //NAME AVAILABLE

                    //FIND STORE CATEGORY IN DB
                    Criteria findStoreCategory = session.createCriteria(StoreCategory.class);
                    findStoreCategory.add(Restrictions.eq("id", storeDTO.getCategoryId()));

                    if (!findStoreCategory.list().isEmpty()) {
                        //CATEGORY FOUND
                        StoreCategory storeCategory = (StoreCategory) findStoreCategory.list().get(0);

                        if (httpSession.getAttribute("user") != null) {

                            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                            //FIND USER IN DB
                            Criteria findUser = session.createCriteria(User.class);
                            findUser.add(Restrictions.eq("username", userDTO.getUsername()));

                            User user = (User) findUser.uniqueResult();

                            //FIND USER HAVE ARTISAN PROFILE IN DB
                            Criteria findArtisan = session.createCriteria(Artisan.class);
                            findArtisan.add(Restrictions.eq("user", user));

                            //===> GET ACTIVE STATUS <===//
                            StoreStatus storeStatus = (StoreStatus) session.get(StoreStatus.class, 1);

                            if (!findArtisan.list().isEmpty()) {
                                //USER HAVE ARTISAN PROFILE

                                Artisan artisan = (Artisan) findArtisan.uniqueResult();

                                //===> CREATE NEW STORE <===//
                                createNewStore(session, storeDTO, artisan, storeCategory, storeStatus);

                                resDTO.setSuccess(true);
                                resDTO.setMessage("Your New Artisan Store Successfully Created.");

                            } else {
                                //OPEN ARTISAN PROFILE

                                //FIND ARTISAN STATUS
                                ArtisanStatus artisanStatus = (ArtisanStatus) session.get(ArtisanStatus.class, 1);

                                Artisan newProfile = new Artisan();
                                newProfile.setUser(user);
                                newProfile.setUpdated_at(new Timestamp(System.currentTimeMillis()));
                                newProfile.setArtisanStatus(artisanStatus);

                                //SAVE NEW ARTISAN PROFILE TO RAM
                                int profileId = (int) session.save(newProfile); // RETURN AUTO INCREMENT ID AS SERIALIZABLE ==> CAST INTO INT

                                // COMMIT PROFILE CREATION TRANSACTION
                                session.flush();
                                transaction.commit();
                                transaction = session.beginTransaction();//START NEW TRANSACTION

                                //GET NEW ARTISAN PROFILE
                                Artisan newArtisanProfile = (Artisan) session.get(Artisan.class, profileId);

                                //===> CREATE NEW STORE <===//
                                createNewStore(session, storeDTO, newArtisanProfile, storeCategory, storeStatus);

                                resDTO.setSuccess(true);
                                resDTO.setMessage("Your New Artisan Profile & Store Successfully Created.");

                            }

                        } else {
                            //USER NOT FOUND
                            resDTO.setMessage("Something went wrong, please try again!");
                        }

                    } else {
                        //CATEGORY NOT FOUND
                        resDTO.setMessage("Invalid Category");
                    }

                } else {
                    //STORE NAME ALREADY TAKEN
                    resDTO.setMessage("Store Name Taken");
                }

                transaction.commit();//COMMIT MAIN TRANSACTION

            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                resDTO.setMessage("Error creating store: " + e.getMessage());
                e.printStackTrace();
            }

        }

        //SEND RESPONSE
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));

    }

    private void createNewStore(Session session, StoreDTO storeDTO, Artisan artisan, StoreCategory storeCategory, StoreStatus storeStatus) {

        Store store = new Store();
        store.setName(storeDTO.getName());
        store.setDescription(storeDTO.getDescription());
        store.setStoreCategory(storeCategory);
        store.setArtisan(artisan);
        store.setStoreStatus(storeStatus);
        store.setUpdated_at(new Timestamp(System.currentTimeMillis()));

        session.save(store); // Save store

    }

}
