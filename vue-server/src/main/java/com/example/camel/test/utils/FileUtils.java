package com.example.camel.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
	
	/**
     * 以字节为单位读取文件
     * 一次读取所有的内容
     * 若文件过大，读取内容时可能造成内存溢出
	 * @throws IOException 
     */
    public static byte[] readFileByBytes(String fileName) throws IOException {
    	File file = new File(fileName);
        InputStream in = null;
        try {
            byte[] tempbytes = new byte[(int)file.length()];
            in = new FileInputStream(fileName);
            in.read(tempbytes);
            return tempbytes;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    
    public static String strReplace(String str, int beginIndex){
    	System.out.println(1);
    	String result = "a";
    	String repStr = str;
    	int beginInd = str.indexOf("${", beginIndex);
    	if(beginInd != -1) {
    		int endIndex = str.indexOf("}", beginInd);
    		String s1 = str.substring(beginInd + 2, endIndex);
    		String rep = "in.body.get('" + s1 + "')";
    		repStr = str.replace(s1 , rep);
    		result = strReplace(repStr, beginIndex + rep.length()+1);
    	}
    	return result;
    }
    
    public static void main(String[] args) {
    	String str = "${result}${adasd}${qssdf}";
    	String target = strReplace(str, 0);
    	System.out.println(target);
//    	System.out.println(str.replaceAll("\\$\\{", "\\$\\{in.body.get('"));
	}
}
