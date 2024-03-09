package com.hwariot.javaproject;

public class MyClass {
    public static void main(String[] args){
//        System.out.print("hello");
//        String content = "V5/B/54/PS/10475/L/191217/LQ/0/Qty/120";
//        int buId = Integer.parseInt(getParsedString(content,"/B/","/PS/"));
//        int psId =  Integer.parseInt(getParsedString(content,"/PS/","/L/"));
//        String lotNo = getParsedString(content,"/L/","/LQ/");
//        int qty = Integer.parseInt(getParsedString(content,"/Qty/",""));
//        System.out.print("buid = " + buId);
//        System.out.print("psId = " + psId);
//        System.out.print("lotNo = " + lotNo);
//        System.out.print("qty = " + qty);
        String content = "/HRID/21618/HRNO/201710211";
        getParsedScannedString(content);
    }

    private static String getParsedString(String code,String part,String nextPart){
        if (!nextPart.isEmpty()){
            int p = code.indexOf(part) + part.length();
            int q = code.indexOf(nextPart);
            return code.substring(p,q );
        }else{
            int p = code.indexOf(part) + part.length();
            return code.substring(p);
        }
    }

    private static void getParsedScannedString(String contents){
        if (contents != null && contents.length() > 10 && contents.startsWith("/")){
            String[] splits = contents.split("/");
            if (splits.length == 5){
                String hrId = splits[2];
                String hrNO = splits[4];
                System.out.println("hrid=" + hrId +" hrno=" + hrNO);
            }else{
                System.out.println("error1");
            }
        }else{
            System.out.println("error2");
        }

    }
}
