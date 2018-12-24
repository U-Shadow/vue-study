package com.example.camel.test.camel.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class JmsClient {
	
	static Socket server;
	public static void creaSockeet() throws UnknownHostException, IOException{
		server = new Socket(InetAddress.getLocalHost(), 5678); 
	}
	
	/**
	 * 发布消息
	 * @param message
	 * @throws UnknownHostException
	 * @throws IOException
	 */
    public static void pub(String message) throws UnknownHostException, IOException{ 
         BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
         PrintWriter out = new PrintWriter(server.getOutputStream());
         out.println(message);
         out.flush();
         System.out.println(in.readLine());
         server.close(); 
    }
    
    /**
     * 订阅消息
     * @param code
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    public static String sub(String code) throws UnknownHostException, IOException{ 
         BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));  
         PrintWriter out = new PrintWriter(server.getOutputStream());  
         out.println(code);
         out.flush(); 	
         String str = in.readLine();
         System.out.println(str); 
         System.out.println("获取的消息为:"+str);
         //server.close();  
         return str;
    }
 
}
