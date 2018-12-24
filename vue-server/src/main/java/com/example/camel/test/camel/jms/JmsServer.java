package com.example.camel.test.camel.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class JmsServer extends Thread {
	private final Socket client;
	
	public JmsServer(Socket c) {
		this.client = c;
	}
	
	public Socket getClient() {
		return client;
	}
 
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream());
			while (true) {
				String str = in.readLine();
				if(str.contains("code:200")){ //200代表生产消息
					//生产消息
					JmsBuffer.produce(str);
				}
				if(str.contains("code:400")){ //400代表消费消息
					String messag=JmsBuffer.consume();
			             out.println(messag);
			             out.flush(); 	
				}
				System.out.println(str);
				out.flush();
				if (str.equals("end"))
					break;
			}
			client.close();
		} catch (IOException ex) {
			
		} finally {
			
		}
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(5678);
		while (true) {
			// transfer location change Single User or Multi User
 
			JmsServer mc = new JmsServer(server.accept());
			mc.start();
		}
	}
}
