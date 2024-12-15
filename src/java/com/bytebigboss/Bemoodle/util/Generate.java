package com.bytebigboss.Bemoodle.util;

import java.util.Random;
import java.util.UUID;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class Generate {
    
    public static int RANDOM_VERIFICATION_CODE(){
//        return UUID.randomUUID().toString();
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

}
