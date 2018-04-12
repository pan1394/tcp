package com.nvwa.framework.communication.tcp2.client;

public class HeartBeat extends Request {

	public HeartBeat() {
		this.setType(RequestType.HEARTBEAT);
		this.setContent("heartbeat"); 
	}
}
