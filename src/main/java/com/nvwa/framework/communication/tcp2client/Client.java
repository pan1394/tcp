package com.nvwa.framework.communication.tcp2client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class Client {  
	  
    public static void main(String[] args) {  
        
       /* if(args.length<2)  
        {  
            System.out.println("注意：请添加参数：<Server Ip> <Server Port>");  
            return;  
        }  */ 
        try {  
        	 // 
        	String strWord="{\"Fields\":{\"param\":\"tomcat\"},\"Subject\":null, \"ServiceID\":\"com.nvwa.framework.service.sample.Greeting*hello\"}";
            
        	String host = "192.168.83.1";
        	String port = "10001";
        	SocketChannel socketChannel = SocketChannel.open();
        	socketChannel.connect(new InetSocketAddress("192.168.83.1", 10001));
            OutputStream ops=new TcpOutputStream(socketChannel);  
            PrintWriter pw=new PrintWriter(ops,true);  
            pw.println(strWord);  
            pw.close();      
        } catch (Exception e)   
        {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }   
  
    }  
  
}  