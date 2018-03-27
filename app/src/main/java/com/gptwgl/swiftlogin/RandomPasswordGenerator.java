package com.gptwgl.swiftlogin;

import java.util.Random;
 
public class RandomPasswordGenerator {
    private static final String ALPHA_CAPS  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA   = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUM     = "0123456789";
    private static final String SPL_CHARS   = "!@#$%^&*_=+-/";
 
    public static String generatePswd(int maxLen, boolean noOfC, boolean noOfD, boolean noOfS) {
        int noOfCAPSAlpha=(noOfC)?(maxLen/4):0; int noOfDigits=(noOfD)?(maxLen/8):0; int noOfSplChars =(noOfS)?(maxLen/8):0;

        Random rnd = new Random();
        int len = maxLen;
        char[] pswd = new char[len];
        int index = 0;
        for (int i = 0; i < noOfCAPSAlpha; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = ALPHA_CAPS.charAt(rnd.nextInt(ALPHA_CAPS.length()));
        }
        for (int i = 0; i < noOfDigits; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = NUM.charAt(rnd.nextInt(NUM.length()));
        }
        for (int i = 0; i < noOfSplChars; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = SPL_CHARS.charAt(rnd.nextInt(SPL_CHARS.length()));
        }
        for(int i = 0; i < len; i++) {
            if(pswd[i] == 0) {
                pswd[i] = ALPHA.charAt(rnd.nextInt(ALPHA.length()));
            }
        }
        return new String(pswd);
    }
     
    private static int getNextIndex(Random rnd, int len, char[] pswd) {
        int index = rnd.nextInt(len);
        while(pswd[index = rnd.nextInt(len)] != 0);
        return index;
    }
   
} 
