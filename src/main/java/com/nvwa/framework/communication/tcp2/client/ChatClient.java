package com.nvwa.framework.communication.tcp2.client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient extends Frame {
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	private boolean bConnected = false;
	private int port = 8888;
	private String hostname = "127.0.0.1";
	
	TextField tfTxt = new TextField();
	TextArea taContent = new TextArea();
	ExecutorService threadPool = Executors.newFixedThreadPool(4); 
	Thread tRecv = new Thread(new RecvThread());
	Thread heartbeat = new Thread(new HeartBeatThread()); 
	private static ChatClient getInstance() {
		return new ChatClient();
	}
	private ChatClient() {
		
	}
	public static void main(String[] args) { 
		ChatClient.getInstance().launch2();
	}

	public void launch2() { 
		connect(port); 
		threadPool.submit(new RecvThread());
		threadPool.submit(new HeartBeatThread()); 
		threadPool.submit(new Runnable() { 
			public void run() {
			for(;;) {
				try {
					Request request = new Request();
					request.setContent("中午好,呵呵");  
					dos.write(request.getBytes());
					dos.flush();
					
					Thread.sleep(30*1000);
				} catch (IOException e) { 
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}   
		}});
		/* try {
			 Request request = new Request();
			 request.setContent("你好"); 
			 dos.write(request.getBytes());
			 dos.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  */
		 monitor();
	}
	
	public void launchFrame() {
		setLocation(400, 300);
		this.setSize(300, 300);
		add(tfTxt, BorderLayout.SOUTH);
		add(taContent, BorderLayout.NORTH);
		pack();
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				disconnect();
				System.exit(0);
			}

		});
		tfTxt.addActionListener(new TFListener());
		setVisible(true);
		connect(port); 
		threadPool.submit(new RecvThread());
		threadPool.submit(new HeartBeatThread());
		//tRecv.start();
		//heartbeat.start();
		threadPool.submit(new Runnable() { 
			public void run() {
				for(int i=0; i<20;i++) {
					try {
						dos.writeUTF("asdfddasdfdddssss11ss");
						dos.flush();
					} catch (IOException e) { 
						e.printStackTrace();
					}
				} 
				
			}
		});
		monitor();
	}

	private void monitor() {
		 int threshold = 0;
		 while(true) {
			 synchronized (this) {
				 if(!bConnected) {
					 this.reconnect(); 
					 threshold++;
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
	
	public void connect(int port) {
		try {
			s = new Socket(hostname, port);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			System.out.println("~~~~~~~~连接成功~~~~~~~~!");
			bConnected = true;
		} catch (UnknownHostException e) {
			System.out.println(e.getLocalizedMessage());
		}catch (ConnectException e) {
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
		this.connect(port);
		if(bConnected) {
			threadPool.submit(new RecvThread());
			threadPool.submit(new HeartBeatThread()); 
		}
	}

	public void write() {
		try {
			dos.writeUTF("asdfddasdfdddssss11ss");
			dos.flush();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	private class TFListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String str = tfTxt.getText().trim();
			tfTxt.setText("");

			try { 
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}
 
	
	private class RecvThread implements Runnable {

		public void run() {
			// TODO Auto-generated method stub
			
		} 
		/*public void run() {
			try {
				while (bConnected) {
					String str = dis.readUTF();
					taContent.setText(taContent.getText() + str + '\n');
				}
			} catch (SocketException e) {
				System.out.println("退出了3，bye!");
			} catch (EOFException e) {
				e.printStackTrace();
				System.out.println("退出了4，bye!");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}*/

	}
	
	private class HeartBeatThread implements Runnable {

		public void run() {
			try {
				while (bConnected) {
					Request request = new HeartBeat();
					dos.write(request.getBytes());
					dos.flush();
					Thread.sleep(1000*5);
				}
			} catch (SocketException e) {
				System.out.println("退出了1，bye!"); 
				bConnected =false;
			} catch (EOFException e) {
				System.out.println("退出了2，bye!");
				bConnected =false;
			} catch (IOException e) {
				e.printStackTrace();
				bConnected =false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				bConnected =false;
			}

		}

	}
}