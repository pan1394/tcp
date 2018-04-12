package com.nvwa.framework.communication.tcp2.client;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class Request {

	private String requestId ="";
	private String content;
	private RequestType type;
	{
		requestId = UUID.randomUUID().toString().replaceAll("-", ""); 
		type = RequestType.DATA;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public RequestType getType() {
		return type;
	}
	public void setType(RequestType type) {
		this.type = type;
	}
	
	public byte[] getBytes() throws UnsupportedEncodingException { 
		 byte[] data =getContent().getBytes("UTF-8");
		 int length = data.length;
		 byte type =  getType().getValue();
		 
		 byte[] a = int2bytes(length);
		 byte[] packet = new byte[length+7];
		 
		 packet[0] = (byte)1;
		 System.arraycopy(a, 0, packet, 1, 4);
		 packet[5]=  type;
		 System.arraycopy(data, 0, packet, 6, length);
		 packet[length+6]=(byte)2;
		 return packet;
	}
	
	 private static byte[] int2bytes(int num) {  
	        byte[] b = new byte[4];  
	        int mask = 0xff;  
	        for (int i = 0; i < 4; i++) {  
	            b[i] = (byte) (num >>> (24 - i * 8));  
	        }  
	        return b;  
	} 
}
