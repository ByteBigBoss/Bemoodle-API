package com.bytebigboss.Bemoodle.util;

import javax.servlet.http.HttpSession;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class CheckLogin {
    
    public static boolean check(HttpSession session){
       return session != null && session.getAttribute("user") != null;
    }
    
}
