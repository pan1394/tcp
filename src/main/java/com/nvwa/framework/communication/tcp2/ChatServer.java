package com.nvwa.framework.communication.tcp2;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nvwa.framework.communication.tcp2.client.RequestType;

public class ChatServer {
	boolean started = false;
	private List<Client> clients = new ArrayList<Client>();
	private ServerSocket ss = null;
	private ExecutorService threadPool = null; 
	private int port = 8888;
	private int threadpoolNumber = 100;
	
	private ChatServer() { 
		threadPool = Executors.newFixedThreadPool(threadpoolNumber); 
	}
	
	public static ChatServer getInstance() {
		return new ChatServer();
	}
	
	public static void main(String[] args) {
		ChatServer.getInstance().start();
	}

	public void start() {
		try {
			ss = new ServerSocket(this.port);
			started = true;
			System.out.println("端口已开启,占用8888端口号....");
		} catch (BindException e) {
			System.out.println("端口使用中....");
			System.out.println("请关掉相关程序并重新运行服务器！");
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
		private Queue<byte[]> complete = new LinkedList<byte[]>();
		private List InComplete = new ArrayList();
		
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
				int i = dis.available();
				if(i==0) return;
				byte[] data =new  byte[i];
				dis.read(data);
				analysis(data);
				print();
				
				//send(str); 
			/*for (int i = 0; i < clients.size(); i++) {
				Client c = clients.get(i);
				c.send(str);
			}*/
		}
		private void print() throws IOException {
				byte[] item = complete.poll();
				while(item == null) {
					DataInputStream is =  new DataInputStream(new ByteArrayInputStream(item));
					byte head = is.readByte();
					int length = is.readInt();
					byte[] real = new byte[length];
					RequestType type = RequestType.valueOf(is.readByte());
					//if(type == RequestType.HEARTBEAT) return;
					is.read(real);
					String str = new String(real, "UTF-8");
					System.out.println("------------来自本地服务器:" + str);
				} 
		}
		private void analysis(byte[] data) {
			 int size = data.length;
			 int head = indexOf(data, (byte)1);
			 int tail = indexOf(data,(byte)2);
			 
			 
			 while(head != -1 && tail != -1) {
				 byte[] packet = new byte[tail-head + 1];
				 System.arraycopy(data, head, packet, 0, tail-head + 1);
				 complete.offer(packet);
				 head = indexOf(data, (byte)1,tail+1);
				 tail = indexOf(data,(byte)2,tail+1);
				 if(head != -1 && tail == -1) {
					 
				 }
				 if(head == -1 && tail == -1) {
					 
				 }
				 if(head == -1 && tail != -1) {
					 
				 }
				 
			 }
			 
		}
		private int indexOf(byte[] source, byte key, int fromindex) {
			if(fromindex < source.length) {
				for(int i=fromindex; i<source.length;i++) {
					if(key == source[i]) return i; 
				}
			}
			return -1;
		}
		
		private int indexOf(byte[] source, byte key ) {
			 return this.indexOf(source, key, 0);
		}

		public void run() {
			try {
				while (s.isConnected()) {
					handle(dis);
					//String str = dis.readUTF(); 
					//if(!"do~".equals(str)) 
					
				}
			} catch (EOFException e) {
				System.out.println("Client closed!");
			}catch (SocketException e) {
				System.out.println("Client closed!" + this);
			}catch (IOException e) {
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
