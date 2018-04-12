package com.nvwa.framework.communication.tcp2;

import java.net.Socket;
import java.util.Date;

public class MessageConnection {

	 private String name; 
     private Date heartbeatLastTime; 
     //Client, Receive Max Buffer 
     private Socket ConnectionSocket;
     public int MaxBufferSize;
     private byte[] receivedDataBuffer;
/*
     public event EventHandlerForMessageReceived SocketReceiveEvent;
     public event EventHandlerForDisconnected SocketDisconnected;

     public MessageConnection()
     {
         MaxBufferSize = 1024 * 500;  //500KB
         receivedDataBuffer = new byte[MaxBufferSize];
     }

     public void ReadReceiveMessage(IAsyncResult status)
     {
         if (ConnectionSocket == null || !ConnectionSocket.) return;
         ConnectionSocket = (Socket)status.AsyncState;
         if (this.IsSocketConnected(ConnectionSocket))
         {
             try
             {
                 int Rend = ConnectionSocket.EndReceive(status);
                 status.AsyncWaitHandle.Close();
                 string messageReceived = Encoding.UTF8.GetString(receivedDataBuffer, 0, Rend);
                 if ((messageReceived.Length == MaxBufferSize && messageReceived[0] == Convert.ToChar(65533)) ||
                    messageReceived.Length == 0)
                 {
                     if (SocketDisconnected != null)
                         SocketDisconnected(this, EventArgs.Empty);
                 }
                 else
                 {
                     if (SocketReceiveEvent != null)
                         SocketReceiveEvent(this, messageReceived, this.Name, EventArgs.Empty);
                     Array.Clear(receivedDataBuffer, 0, receivedDataBuffer.Length);
                     ConnectionSocket.BeginReceive(receivedDataBuffer, 0, receivedDataBuffer.Length, 0, new AsyncCallback(ReadReceiveMessage), ConnectionSocket);
                 }
             }
             catch (Exception ex)
             {
                 if (SocketDisconnected != null)
                     SocketDisconnected(this, EventArgs.Empty);
             }
         }
         else
         {
             if (SocketDisconnected != null)
                 SocketDisconnected(this, EventArgs.Empty);
         }
     }

     private boolean IsSocketConnected(Socket s)
     {
    	return s.isConnected(); 
     }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getHeartbeatLastTime() {
		return heartbeatLastTime;
	}

	public void setHeartbeatLastTime(Date heartbeatLastTime) {
		this.heartbeatLastTime = heartbeatLastTime;
	}
     
     */
}
