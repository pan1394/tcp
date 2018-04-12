package com.nvwa.framework.communication.tcp2.client;

public enum RequestType {

	HEARTBEAT((byte)'H'),
	DATA((byte)'D');
	
	private byte index;
	
	private RequestType(byte index) {    
        this.index = index;  
    } 
	 
	public byte getValue() {
		 return this.index;
	}
	
	public static RequestType valueOf(byte b) {
		if(HEARTBEAT.index == b) return HEARTBEAT;
		if(DATA.index == b) return DATA;
		return null;
	}
}
