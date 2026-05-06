package com.chinashb.www.mobileerp.palleshipload;

/***
 * @date 创建时间 4/17/26 1:34 PM
 * @author 作者: liweifeng
 * @description
 */
public class CodeGenerator {

    private static int containerCounter = 0;
    private static int palletCounter = 0;

    public static String generateContainerCode() {
        containerCounter++;
        return String.format("CONT-%04d", containerCounter);
    }

    public static String generatePalletCode() {
        palletCounter++;
        return String.format("PLT-%04d", palletCounter);
    }

    public static void reset() {
        containerCounter = 0;
        palletCounter = 0;
    }
}
