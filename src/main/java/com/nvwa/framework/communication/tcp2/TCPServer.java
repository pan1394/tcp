package com.nvwa.framework.communication.tcp2;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nvwa.framework.communication.tcp2.client.RequestType;

public class TCPServer {
	
	boolean started = false;
	private List<Client> clients = new ArrayList<Client>();
	private ServerSocket ss = null;
	private ExecutorService threadPool = null; 
	
	private int port = 8888;
	private int threadpoolNumber = 100;
	private int MAX_BUFFER=1024*16;
	private int SO_TIME_OUT = 1000*30;
	
	private TCPServer() { 
		threadPool = Executors.newFixedThreadPool(threadpoolNumber); 
	}
	
	public static TCPServer getInstance() {
		return new TCPServer();
	}
	
	public static void main(String[] args) {
		TCPServer.getInstance().start();
	}

	public void start() {
		try {
			ss = new ServerSocket(this.port); 
			//ss.setSoTimeout(SO_TIME_OUT);
			started = true;
			System.out.println("Port occupied :" + this.port);
		} catch (BindException e) {
			System.out.println("Port are using..." + this.port);
			System.out.println("Please check any program occupy the port and close it.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while (started) {
				Socket s = ss.accept();
				Client c = new Client(s);
				System.out.println("a client connected!");
				//new Thread(c).start();
				threadPool.submit(c);
				clients.add(c);
				System.out.printf("there is %d clients(s) connected", clients.size());
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class Client implements Runnable {
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bConnected = false;
		private Queue<DataPacket> completeQueue = new LinkedList<DataPacket>();
		private Queue<DataPacket> fragementQueue = new LinkedList<DataPacket>();
		private byte[] buffer = new byte[MAX_BUFFER]; ;
		
		public Client(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = s.isConnected(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("对方退出了！我从List里面去掉了！");
			}
		}

		public void handle(DataInputStream dis) throws IOException {
				//read directly
				/*int i = dis.available();
				if(i==0) return; 
				byte[] data =new  byte[i];
				dis.read(data);
				analysis(data); //--> 
				print(); */
				
				//use buffer
				clearBuffer();
				int length = -1; 
				while((length = dis.read(buffer)) != -1) {
					analysis(buffer);
					clearBuffer();
					print(); 
				}
				
		} 
		 
		public void clearBuffer() {
			buffer = new byte[MAX_BUFFER]; 
			Arrays.fill(buffer, 0, MAX_BUFFER, (byte)3);
		}
		
		public boolean validateData(byte[] source) {
			if(source[0] != (byte)3 && source[source.length-1] != (byte)3) {
				return true;
			}
			return false;
		}
		
		private void print() throws IOException {
				DataPacket packet = completeQueue.poll();
				while(packet != null) {
					DataInputStream is =  new DataInputStream(new ByteArrayInputStream(packet.getData()));
					byte head = is.readByte();
					int length = is.readInt();
					byte[] real = new byte[length];
					RequestType type = RequestType.valueOf(is.readByte());
					//if(type == RequestType.HEARTBEAT) return;
					is.read(real);
					String str = new String(real, "UTF-8");
					System.out.println("------------来自本地服务器:" +s.getInetAddress().getHostName() + "~"+ str);
					send(str);
					packet = completeQueue.poll();
				} 
		}
		
		private void analysis(byte[] block) { 
				byte[] remaining = null;
				DataPacket packet = new DataPacket();
				remaining  = DataPacketUtil.read(block, packet); 
				while(packet.getData() != null && validateData(packet.getData())){
					if(packet.isComplete()) completeQueue.offer(packet);
					else {
						if(packet.isHeadTag()) {
							fragementQueue.offer(packet) ;
						}else {
							if(fragementQueue.size() == 1) {
								DataPacket headPacket = fragementQueue.peek();
								DataPacket combined = DataPacketUtil.combine(headPacket,  packet);
								if(combined.isComplete()) {
									fragementQueue.poll();
									completeQueue.offer(combined);
								}else {
									fragementQueue.poll();
									fragementQueue.offer(combined);
								}
							}else {
								System.err.println("not only one fragement in the server side");
							}
						} 
					}
					packet = new DataPacket();
					remaining  = DataPacketUtil.read(remaining, packet);
				
			} 
		}
		  
		public void run() {
			try {
				while (true) {
					handle(dis); 
				}
			} catch (EOFException e) {
				System.out.println("Client closed!");
			}catch (SocketException e) {
				System.out.println("Client closed!" + this);
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if (dis != null)
						dis.close();
					if (dos != null)
						dos.close();
					if (s != null) {
						s.close();
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				clients.remove(this);
			}
		}
	}
}
