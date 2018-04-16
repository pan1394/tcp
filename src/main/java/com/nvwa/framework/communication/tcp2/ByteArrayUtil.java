package com.nvwa.framework.communication.tcp2;

public class ByteArrayUtil {

	public static byte[] int2bytes(int num) {  
	        byte[] b = new byte[4];  
	        int mask = 0xff;  
	        for (int i = 0; i < 4; i++) {  
	            b[i] = (byte) (num >>> (24 - i * 8));  
	        }  
	        return b;  
	} 
	 
	public static int byteArrayToInt(byte[] b) {   
			return   	b[3] & 0xFF |   
			            (b[2] & 0xFF) << 8 |   
			            (b[1] & 0xFF) << 16 |   
			            (b[0] & 0xFF) << 24;   
	}   
		
	public static byte[] intToByteArray(int a) {   
			return new byte[] {   
		        (byte) ((a >> 24) & 0xFF),   
		        (byte) ((a >> 16) & 0xFF),      
		        (byte) ((a >> 8) & 0xFF),      
		        (byte) (a & 0xFF)   
		    };   
	}  
}
