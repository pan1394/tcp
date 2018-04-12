package com.nvwa.framework.communication.tcp2;

import java.io.Serializable;

public class HeartBeatPackage implements Serializable{
   
	private static final long serialVersionUID = 1L;
	
	private String endPoint;
    private String stime;
    private String state;
    
	public String getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	public String getStime() {
		return stime;
	}
	public void setStime(String stime) {
		this.stime = stime;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
    
    
}
