package com.nvwa.framework.communication.tcp2.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient { 
	private String ip= "192.168.83.1";
	private int port = 10002;
	private Socket socket;
	private Thread heartBeatTimer;
	
	public static void main(String[] args) throws Exception { 
		 
		SocketClient client = new SocketClient();
		client.init();
	
	} 
	
	 public void init() throws IOException
     { 		
		  //this.ip = ip;
		  //this.port = port;
          createConnection();
          //   CreateQueue();
          //   isRun = true;
          heartBeatTimer = new Thread(new Runnable() {

			public void run() {
				try {
					while(true) { 
						HeartBeat heartbeat = new HeartBeat(); 
						send(heartbeat);
						System.out.println("Heart Beat Sent!");
						Thread.sleep(1000*5);
					} 
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
        	  
          });
          heartBeatTimer.start();
          //   this.notifyAction(true); 
     }
	 
	 public void createConnection() throws IOException {
		 socket = new Socket(this.ip, port); 
		 System.out.println("Socket connected!");
	 }
	 
    public void send(Request request) throws IOException {
    	 int length = request.getContent().length();
    	 byte[] data = request.getContent().getBytes("UTF-8");
		/* 
		
		 byte type = request.getType().getValue();
		 byte[] a = int2bytes(length);
		 byte[] packet = new byte[length+6];
		 
		 System.arraycopy(a, 0, packet, 0, 4);
		 packet[4]=  type;
		 System.arraycopy(data, 0, packet, 5, length);
		 packet[length+5]=(byte)'/';*/
		 byte[] packet = new byte[length];
		 OutputStream out = socket.getOutputStream();
		 out.write(data);
		 out.flush(); 
	 }
	 
    private static byte[] int2bytes(int num) {  
        byte[] b = new byte[4];  
        int mask = 0xff;  
        for (int i = 0; i < 4; i++) {  
            b[i] = (byte) (num >>> (24 - i * 8));  
        }  
        return b;  
    } 
	/* public void receive(Response response) {
		 
	 }*/ 
} 

 