package com.nvwa.framework.communication.tcp2.client;

public class ConnectionConfiguration {

	private String hostname = "127.0.0.1";
	private int port = 8888;
	private int connectionTimeout = 1000 * 30;
	private int heartbeatInterval = 1000 * 5;
	private int maxBufferSize = 1024 *16;
	
	
	public int getMaxBufferSize() {
		return maxBufferSize;
	}
	public void setMaxBufferSize(int maxBufferSize) {
		this.maxBufferSize = maxBufferSize;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}
	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}
	
	
}
