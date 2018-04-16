package com.nvwa.framework.communication.tcp2.client;

import java.awt.Frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient extends Frame {
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	private boolean bConnected = false;  
	ExecutorService threadPool = Executors.newFixedThreadPool(4); 
	Thread tRecv = new Thread(new RecvThread());
	Thread heartbeat = new Thread(new HeartBeatThread()); 
	
	private int port ;
	private String hostname ;
	private int CONNECTION_TIMEOUT ;
	private int HEARTBEAT_INTERVAL ;
	
	public static void main(String[] args) { 
		TCPClient.getInstance().launch2();
	}
	
	private static TCPClient getInstance() {
		return new TCPClient();
	}
	
	private TCPClient() {
		ConnectionConfiguration config = new ConnectionConfiguration();
		port = config.getPort();
		hostname = config.getHostname();
		CONNECTION_TIMEOUT = config.getConnectionTimeout();
		HEARTBEAT_INTERVAL = config.getHeartbeatInterval();
	}
 
	public void launch2() { 
		
		connect(port); 
		threadPool.submit(new MonitorThread());
		if(this.bConnected == true) {
			threadPool.submit(new RecvThread());
			threadPool.submit(new HeartBeatThread());  
			threadPool.submit(new ServicesThread()); 
		} 
	}
	 
	public void connect(int port) {
		try { 
			s = new Socket();
			SocketAddress address = new InetSocketAddress("192.168.83.1", port);
			s.connect(address, CONNECTION_TIMEOUT);
			s.setTcpNoDelay(true);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			System.out.println("~~~~~~~~连接成功~~~~~~~~!");
			bConnected = true;
		} catch (UnknownHostException e) {
			System.out.println(e.getLocalizedMessage());
		}catch (ConnectException e) {
			System.out.println(e.getLocalizedMessage());
			System.out.println("Cannot connect to the server:" + this.hostname);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			dos.close();
			dis.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reconnect() {
		connect(port);
		if(bConnected) {
			threadPool.submit(new RecvThread());
			threadPool.submit(new HeartBeatThread()); 
			threadPool.submit(new ServicesThread()); 
		}
	}
    
	private class RecvThread implements Runnable {

	 
		 public void run() {
			try {
				while (bConnected) {
					String str = dis.readUTF();
					System.out.println("Get response: " + str);
				}
			} catch (SocketException e) {
				System.out.println("退出了3，bye!");
			} catch (EOFException e) {
				e.printStackTrace();
				System.out.println("退出了4，bye!");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} 

	}
	
	private class HeartBeatThread implements Runnable { 
		public void run() {
			try {
				while (bConnected) { 
					Request request = new HeartBeat();
					dos.write(request.getBytes());
					dos.flush();
					System.out.println("sent heart beat...");
					Thread.sleep(HEARTBEAT_INTERVAL);
				}
			} catch (SocketException e) {
				System.out.println(e.getMessage());
				System.out.println("退出了1，bye!"); 
			} catch (EOFException e) {
				System.out.println("退出了2，bye!");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				bConnected = false;
				try {
					if(dos!= null) {
						dos.close();
					}
					dos =null;
				} catch (IOException e) { 
					e.printStackTrace();
				}
			} 
		} 
	}
	 
	private class MonitorThread implements Runnable { 
		public void run() { 
			int threshold = 0;
			 while(true) {
				 synchronized (this) {
					 if(!bConnected) {
						 reconnect(); 
						 threshold++;
					 }else {
						 threshold = 0;
					 }
				 }
				 if(threshold > 10) {
					 System.out.println("Alert, cannot reconnect. Send Mail.");
				 }
				 if(threshold > 100) {
					 System.out.println("Text Message Send Out to Manager!");
				 }
				 try {
						Thread.sleep(1000*5);
					} catch (InterruptedException e) {
						e.printStackTrace();
				}
			 } 
		} 
	}
	
	private class ServicesThread implements Runnable { 
		public void run() {
		 
			try {
				while(true) {
					Request request = new Request();
					request.setContent("中午好,呵呵");  
					dos.write(request.getBytes());
					dos.flush();
					Thread.sleep(30*1000);
				}
			} catch (IOException e) { 
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(dos!= null) {
						dos.close();
					}
					dos =null;
				} catch (IOException e) { 
					e.printStackTrace();
				}
			}
		}   
	}
}