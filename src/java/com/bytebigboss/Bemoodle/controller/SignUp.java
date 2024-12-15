package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.ResponseDTO;
import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.entity.UserStatus;
import com.bytebigboss.Bemoodle.util.Generate;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.bytebigboss.Bemoodle.util.Mail;
import com.bytebigboss.Bemoodle.util.Validator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
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
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {


        //RESPONSE OBJECT
        ResponseDTO resDTO = new ResponseDTO();
        
        
        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        //GSON BUILDER: => EXCLUDE EXPOSE ANNOTATED FIELD'S FROM SERIALIZATION (RESPONSE)
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //MAP REQUEST PARAMETER TO USER_DTO CLASS
        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        //VALIDATE PARAMETERS
        if (userDTO.getDisplay_name().isEmpty()) {
            //DISPLAY NAME IS EMPTY
            resDTO.setContent("Please enter your Display Name");

        } else if (userDTO.getUsername().isEmpty()) {
            //USERNAME IS EMPTY
            resDTO.setContent("Please enter your Username");

        } else {

            //CHECK USERNAME TAKEN OR NOT
            Criteria findUsername = session.createCriteria(User.class);
            findUsername.add(Restrictions.eq("username", userDTO.getUsername().toLowerCase()));

            if (!findUsername.list().isEmpty()) {
                //USERNAME TAKEN
                resDTO.setContent("Username Taken, use another.");

            } else if (userDTO.getEmail().isEmpty()) {
                //EMAIL IS EMPTY
                resDTO.setContent("Please enter your Email");

            } else if (!Validator.VALIDATE_EMAIL(userDTO.getEmail())) {
                //EMAIL IS NOT VALID
                resDTO.setContent("Please enter a valid Email");

            } else {

                //CHECK USER EXIST OR NOT
                Criteria findEmail = session.createCriteria(User.class);
                findEmail.add(Restrictions.eq("email", userDTO.getEmail()));

                if (!findEmail.list().isEmpty()) {
                    resDTO.setContent("User with this Email already exists");

                } else if (userDTO.getPassword().isEmpty()) {
                    //PASSWORD IS EMPTY
                    resDTO.setContent("Please enter your Password");

                } else if (!Validator.VALIDATE_PASSWORD(userDTO.getPassword())) {
                    //PASSWORD NOT VALID
                    resDTO.setContent("Password must include at least one uppercase letter, number, "
                            + "special character, and be at least 8 characters long");

                } else {

                    //GENERATE VERIFICATION CODE
                    int code = Generate.RANDOM_VERIFICATION_CODE();

                    //SET USER PROPERTIES
                    final User user = new User();
                    user.setEmail(userDTO.getEmail());
                    user.setDisplay_name(userDTO.getDisplay_name());
                    user.setUsername(userDTO.getUsername().toLowerCase());
                    user.setPassword(userDTO.getPassword());
                    user.setVerification(String.valueOf(code));

                    UserStatus status = new UserStatus();
                    status.setId(1);

                    user.setUserStatus(status);

                    //SEND VERIFICATION EMAIL
                    Thread sendMailThread = new Thread() {
                        @Override
                        public void run() {
                            Mail.sendMail(user.getEmail(), "Bemoodle Account Verification",
                                    "<!DOCTYPE html>"
                                    + "<html lang=\"en\">"
                                    + "<head>"
                                    + "    <meta charset=\"UTF-8\">"
                                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                    + "    <title>Bemoodle Account Verification</title>"
                                    + "</head>"
                                    + "<body style=\"font-family: Arial, sans-serif; color: #333; line-height: 1.6;\">"
                                    + "    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 10px;\">"
                                    + "        <h1 style=\"color:#6482AD; text-align: center;\">Bemoodle Account Verification</h1>"
                                    + "        <p style=\"text-align: center; font-size: 18px;\">"
                                    + "            Your verification code is:"
                                    + "        </p>"
                                    + "        <h1 style=\"color:#6482AD; text-align: center;\">" + user.getVerification() + "</h1>"
                                    + "        <p style=\"text-align: center; font-size: 16px;\">"
                                    + "            Please use this code to verify your account. If you did not request this verification, please ignore this email."
                                    + "        </p>"
                                    + "        <p style=\"text-align: center; font-size: 16px;\">Thank you for choosing Bemoodle!</p>"
                                    + "    </div>"
                                    + "</body>"
                                    + "</html>"
                            );
                        }

                    };
                    sendMailThread.start();

                    //SAVE NEW USER TO RAM
                    session.save(user);

                    //ADD USER TO DATABASE
                    session.beginTransaction().commit();

                    //REGISTRATION COMPLETE
                    resDTO.setSuccess(true);
                    resDTO.setContent("Registration Complete. Please check your inbox for Verification Code!");

                }

            }

        }

        //RETURN RESPONSE
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));

        //END SESSION
        session.close();

    }

}
