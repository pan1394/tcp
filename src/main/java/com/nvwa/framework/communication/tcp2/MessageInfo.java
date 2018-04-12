package com.nvwa.framework.communication.tcp2;

import java.util.List;

public class MessageInfo {

	private String key;
    private String ip;
    private String port;
    private int timeout;
    private int intervaltime;
    private int checklimit;
    private int backlog;
    private /*ManualResetEvent*/ Object signal;
    private List<Object> datalist;
    private String replymessage;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getIntervaltime() {
		return intervaltime;
	}
	public void setIntervaltime(int intervaltime) {
		this.intervaltime = intervaltime;
	}
	public int getChecklimit() {
		return checklimit;
	}
	public void setChecklimit(int checklimit) {
		this.checklimit = checklimit;
	}
	public int getBacklog() {
		return backlog;
	}
	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}
	public Object getSignal() {
		return signal;
	}
	public void setSignal(Object signal) {
		this.signal = signal;
	}
	public List<Object> getDatalist() {
		return datalist;
	}
	public void setDatalist(List<Object> datalist) {
		this.datalist = datalist;
	}
	public String getReplymessage() {
		return replymessage;
	}
	public void setReplymessage(String replymessage) {
		this.replymessage = replymessage;
	}
    
    
}
