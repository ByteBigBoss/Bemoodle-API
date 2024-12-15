package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.ResponseDTO;
import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.hibernate.Session;

/**
 *
 * @author ByteBigBoss
 */
@MultipartConfig
@WebServlet(name = "UpdateProfilePicture", urlPatterns = {"/UpdateProfilePicture"})
public class UpdateProfilePicture extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();

        ResponseDTO resDTO = new ResponseDTO();

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        Part img1 = req.getPart("profile");

        try {

            if (req.getSession().getAttribute("user") == null) {
                resDTO.setContent("SignIn");
            } else {

                //USER
                UserDTO user = (UserDTO) req.getSession().getAttribute("user");

                //APPLICATION PATH
                String applicationPath = req.getServletContext().getRealPath("");
                String newAplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

                //STORE FOLDER
                File userFolder = new File(newAplicationPath, "//user//" + user.getId());
                userFolder.mkdir();

                //PROFILE PICTURE FOLDER
                File profileFolder = new File(userFolder, "profile");
                profileFolder.mkdir();

                //FILE 1
                File file1 = new File(profileFolder, "avatar.png");// CREATE NEW FILE (IMAGE)
                InputStream inputStream1 = img1.getInputStream(); //IMAGE 1 STREAM
                Files.copy(inputStream1, file1.toPath(), StandardCopyOption.REPLACE_EXISTING); // COPY IMAGE

                //PRODUCT LISTING COMPLETE
                resDTO.setSuccess(true);
                resDTO.setContent("New Product Added");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

    }

}
