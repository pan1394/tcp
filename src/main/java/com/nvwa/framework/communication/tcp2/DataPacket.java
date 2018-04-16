package com.nvwa.framework.communication.tcp2;

public class DataPacket {

	private byte[] data;  
	private boolean headTag;
	private boolean tailTag;  
	private int contentLength; 
	
	public int getPacketLength() {
		return data.length;
	}
	  
	public int getContentLength() {
		if(headTag) {
			return contentLength;			
		}else {
			return -1;
		}
	}
 
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
 
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public boolean isComplete() {
		return headTag && tailTag;
	} 
	public boolean isHeadTag() {
		return headTag;
	}
	public void setHeadTag(boolean headTag) {
		this.headTag = headTag;
	}
	public boolean isTailTag() {
		return tailTag;
	}
	public void setTailTag(boolean tailTag) {
		this.tailTag = tailTag;
	} 
 
	
}

