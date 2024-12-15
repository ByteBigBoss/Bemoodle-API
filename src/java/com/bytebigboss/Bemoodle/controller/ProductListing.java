package com.bytebigboss.Bemoodle.controller;

import com.bytebigboss.Bemoodle.dto.ResponseDTO;
import com.bytebigboss.Bemoodle.dto.UserDTO;
import com.bytebigboss.Bemoodle.entity.Artisan;
import com.bytebigboss.Bemoodle.entity.Category;
import com.bytebigboss.Bemoodle.entity.Product;
import com.bytebigboss.Bemoodle.entity.ProductStatus;
import com.bytebigboss.Bemoodle.entity.Store;
import com.bytebigboss.Bemoodle.entity.SubCategory;
import com.bytebigboss.Bemoodle.entity.User;
import com.bytebigboss.Bemoodle.util.HibernateUtil;
import com.bytebigboss.Bemoodle.util.Validator;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "ProductListing", urlPatterns = {"/ProductListing"})
public class ProductListing extends HttpServlet {

    //CURRENT STORE [USE TO ADD PRODUCT]
    private Store productStore;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        ResponseDTO resDTO = new ResponseDTO();

        Gson gson = new Gson();

        //BASIC DETAILS
        String storeName = req.getParameter("storeName");

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String price = req.getParameter("price");
        String qty = req.getParameter("quantity");
        String status = req.getParameter("status");

        //PRODUCT CATEGORY
        String categoryId = req.getParameter("category");
        String subCategoryId = req.getParameter("subCategory");

        //PRODUCT IMAGES
        Part img1 = req.getPart("img1");
        Part img2 = req.getPart("img2");
        Part img3 = req.getPart("img3");

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        if (req.getSession().getAttribute("user") == null) {
            resDTO.setContent("SignIn");
        } else {
            if (storeName.isEmpty()) {
                resDTO.setContent("SignIn");
            } else {

                if (title.isEmpty()) {

                    resDTO.setContent("Please Enter Product Title");

                } else if (description.isEmpty()) {

                    resDTO.setContent("Please Enter Product Description");

                } else if (!Validator.VALIDATE_INTEGER(categoryId)) {
                    resDTO.setContent("Invalid Category");

                } else if (!Validator.VALIDATE_INTEGER(subCategoryId)) {
                    resDTO.setContent("Invalid Sub Category");
                } else if (price.isEmpty()) {
                    resDTO.setContent("Please fill Price");

                } else if (!Validator.VALIDATE_DOUBLE(price)) {
                    resDTO.setContent("Invalid price");

                } else if (Double.parseDouble(price) <= 0) {
                    resDTO.setContent("Price must be greater than 0");

                } else if (qty.isEmpty()) {
                    resDTO.setContent("Please fill Quantity");

                } else if (!Validator.VALIDATE_INTEGER(qty)) {
                    resDTO.setContent("Invalid Quantity");

                } else if (Integer.parseInt(qty) <= 0) {
                    resDTO.setContent("Quantity must be greater than 0");

                } else if (!Validator.VALIDATE_INTEGER(status)) {
                    resDTO.setContent("Invalid Status");

                } else if (img1.getSubmittedFileName() == null) {
                    resDTO.setContent("Please upload Image 1");

                } else if (img2.getSubmittedFileName() == null) {
                    resDTO.setContent("Please upload Image 2");

                } else if (img3.getSubmittedFileName() == null) {
                    resDTO.setContent("Please upload Image 3");

                } else {

                    Category category = (Category) session.load(Category.class, Integer.parseInt(categoryId));

                    if (category == null) {
                        //CATEGORY NOT IN DB
                        resDTO.setContent("Please select a valid Category");
                    } else {

                        SubCategory subCategory = (SubCategory) session.load(SubCategory.class, Integer.parseInt(subCategoryId));

                        if (subCategory == null) {
                            //CATEGORY NOT IN DB
                            resDTO.setContent("Please select a valid Sub Category");
                        } else {

                            //COMPARE IS REQUESTED SUB_CATEGORY IN CATEGORY
                            if (subCategory.getCategory().getId() != category.getId()) {
                                //COMPARE FAILD
                                resDTO.setContent("Please select a valid Sub Category");
                            } else {

                                //====> ALL VALIDATED [0]=> [28::VALIDATES] <====//
                                Product product = new Product();
                                //FIELDS
                                product.setTitle(title);
                                product.setDescription(description);
                                product.setPrice(Double.parseDouble(price));
                                product.setQty(Integer.parseInt(qty));

                                //JOINS
                                product.setSubCategory(subCategory);

                                //APPROVED PRODUCT BY ADMIN == Active != Inactive
                                //===> GET ACTIVE STATUS <===//
                                Criteria findStatus = session.createCriteria(ProductStatus.class);
                                findStatus.add(Restrictions.eq("id", Integer.parseInt(status)));

                                if (findStatus.list().isEmpty()) {
                                    ProductStatus product_Status = (ProductStatus) session.get(ProductStatus.class, 1);
                                    product.setProductStatus(product_Status);
                                } else {
                                    
                                    ProductStatus productStatus = (ProductStatus) findStatus.uniqueResult();
                                    product.setProductStatus(productStatus);
                                }

                                //*******************************************//
                                //GET SESSION USER
                                UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");
                                //SEARCH SESSION USER IN DB
                                Criteria findUser = session.createCriteria(User.class);
                                findUser.add(Restrictions.eq("username", userDTO.getUsername()));
                                //CAST USER FROM SEARCH
                                User user = (User) findUser.uniqueResult();

                                //SEARCH SESSION ARTISAN IN DB
                                Criteria findArtisan = session.createCriteria(Artisan.class);
                                findArtisan.add(Restrictions.eq("user", user));
                                //CAST ARTISAN FROM SEARCH
                                Artisan artisan = (Artisan) findArtisan.uniqueResult();

                                //FIND ARTISAN'S STORES
                                Criteria findStores = session.createCriteria(Store.class);
                                findStores.add(Restrictions.eq("artisan", artisan));
                                //GET STORE LIST
                                List<Store> storeList = findStores.list();

                                for (Store store : storeList) {
                                    if (store.getName().equals(storeName)) {
                                        this.productStore = store;
                                    }
                                }
                                //*******************************************//

                                if (this.productStore == null) {
                                    resDTO.setContent("Store not found for the Artisan");
                                } else {

                                    product.setStore(productStore);

                                    //SAVE NEW PRODUCT TO RAM
                                    int pid = (int) session.save(product); // RETURN AUTO INCREMENT ID AS SERIALIZABLE ==> CAST INTO INT

                                    //ADD PRODUCT TO DATABASE
                                    session.beginTransaction().commit();

                                    //APPLICATION PATH
                                    String applicationPath = req.getServletContext().getRealPath("");
                                    String newAplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

                                    //STORE FOLDER
                                    File storeFolder = new File(newAplicationPath, "//stores//" + storeName);
                                    storeFolder.mkdir();

                                    //ALL PRODUCTS FOLDER
                                    File allProductsFolder = new File(storeFolder, "products");
                                    allProductsFolder.mkdir();

                                    //PRODUCT FOLDER
                                    File productFolder = new File(allProductsFolder, "p-" + pid);
                                    productFolder.mkdir();

                                    //FILE 1
                                    File file1 = new File(productFolder, "image1.png");// CREATE NEW FILE (IMAGE)
                                    InputStream inputStream1 = img1.getInputStream(); //IMAGE 1 STREAM
                                    Files.copy(inputStream1, file1.toPath(), StandardCopyOption.REPLACE_EXISTING); // COPY IMAGE

                                    //FILE 2
                                    File file2 = new File(productFolder, "image2.png");// CREATE NEW FILE (IMAGE)
                                    InputStream inputStream2 = img2.getInputStream(); //IMAGE 1 STREAM
                                    Files.copy(inputStream2, file2.toPath(), StandardCopyOption.REPLACE_EXISTING); // COPY IMAGE

                                    //FILE 3
                                    File file3 = new File(productFolder, "image3.png");// CREATE NEW FILE (IMAGE)
                                    InputStream inputStream3 = img3.getInputStream(); //IMAGE 1 STREAM
                                    Files.copy(inputStream3, file3.toPath(), StandardCopyOption.REPLACE_EXISTING); // COPY IMAGE

                                    //PRODUCT LISTING COMPLETE
                                    resDTO.setSuccess(true);
                                    resDTO.setContent("New Product Added");
                                    

                                }

                            }

                        }
                    }

                }
            }
        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));
        session.close();
    }

}
