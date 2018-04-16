package com.nvwa.framework.communication.tcp2.client;

public class HeartBeat extends Request {

	private static int i=0;
	public HeartBeat() {
		this.setType(RequestType.HEARTBEAT);
		this.setContent("heartbeata" + (i++)); 
	}
}
