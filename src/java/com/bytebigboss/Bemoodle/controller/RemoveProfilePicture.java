package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "RemoveProfilePicture", urlPatterns = {"/RemoveProfilePicture"})
public class RemoveProfilePicture extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", false);

        HttpSession httpSession = req.getSession();

        try {

            //** GET USER ===============================================
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");
            //CHECK AVATAR IMAGE
            //APPLICATION PATH
            String serverPath = req.getServletContext().getRealPath("");
            String folderPath = serverPath.replace("build" + File.separator + "web", "web");
            String userAvatarPath = folderPath + File.separator + "user" + File.separator + userDTO.getId() + File.separator + "profile" + File.separator + "avatar.png";
            File userAvatarFile = new File(userAvatarPath);

            if (userAvatarFile.exists()) {
                //AVATAR IMAGE FOUND
               userAvatarFile.delete();
               jsonObject.addProperty("status", true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
