package com.nvwa.framework.communication.tcp2;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
	
	private ServerSocket server = null;
	private ExecutorService threadPool = null; 
	private int port = 10002;
	private int threadpoolNumber = 100;
	private boolean active = false; 
	private SocketServer() {
		try {
			server = new ServerSocket(port);
			threadPool = Executors.newFixedThreadPool(threadpoolNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static SocketServer getInstance() {
		return new SocketServer();
	}
	
	public void start() {
		if(!this.active) {
			this.active = true;
			execute();
		}
	}
	
	
	public void stop() {
		this.active = false; 
	}
	
	public void execute() {
		while (this.active) {
		    Socket accepted = null;
			try {
				accepted = server.accept();
			} catch (IOException e1) { 
				continue;
			} 
			final Socket socket = accepted; 
		    threadPool.submit(new Runnable() { 
				public void run() {
						InputStream inputStream = null;
						try {
				          // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
				          inputStream = socket.getInputStream();
				          byte[] bytes = new byte[1024];
				          int len;
				          StringBuilder sb = new StringBuilder();
				          while ((len = inputStream.read(bytes)) != -1) {
				            // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
				            sb.append(new String(bytes, 0, len, "UTF-8"));
				          }
				          System.out.println("get message from client: " + sb);
				          
				        } catch (Exception e) {
				        	e.printStackTrace();
				        }
						finally {
							try {
								if(inputStream != null) {
									inputStream.close();
									inputStream = null;
								}
								if(socket != null) {
									socket.close(); 
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					    
					}
		    	  
		      }); 
		}
	}
	
	
	
	
  public static void main(String args[]) throws Exception {
	  SocketServer server = SocketServer.getInstance();
	  server.start();
    
  }
}