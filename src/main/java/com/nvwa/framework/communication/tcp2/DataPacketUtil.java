package com.nvwa.framework.communication.tcp2;

public class DataPacketUtil {
	//format
	//start tag (byte)1
	//int       length
	//type      (byte) 'H', 'D'
	//byte[]    content
	//end tag   (byte)2
	public static byte[] read(byte[] block, DataPacket packet) {
		
		if(block == null ||block.length==0 ) {
			return null;
		}  
		byte[] data = null;
		int head = indexOf(block, (byte)1);
		boolean hasHead =  head != -1 ? true:false;
		int tail = indexOf(block,(byte)2);
		boolean hasTail = tail != -1 ? true:false;
		int dataLength = -1; 
		if(head > tail && hasHead && hasTail) {
			hasHead = false;
		}
		if(hasHead && hasTail) {
			int sublen = tail - head + 1;
			data = new byte[sublen];
			System.arraycopy(block, head, data, 0, sublen);

			int restlen = block.length - sublen;
			byte[] swap = new byte[restlen];
			System.arraycopy(block, tail+1, swap, 0, restlen);
			block = swap;
		}
		if(hasHead && !hasTail) {
			int sublen = block.length - head;
			data = new byte[sublen];
			System.arraycopy(block, head, data, 0, sublen);
			block = null;
		}
		if(!hasHead && !hasTail) {
			data = block;
			block = null;
		}
		if(!hasHead && hasTail) {
			int sublen = tail - 0 + 1;
			data = new byte[sublen];
			System.arraycopy(block, 0, data, 0, sublen);
			
			int restlen = block.length - sublen;
			byte[] swap = new byte[restlen];
			System.arraycopy(block, tail+1, swap, 0, restlen);
			block = swap;
		}
		if(hasHead) {
			dataLength = readLength(data, head);
		}
		
		packet.setData(data); 
		packet.setHeadTag(hasHead);
		packet.setTailTag(hasTail); 
		packet.setContentLength(dataLength);
		return block;
	}
	
	 
	
	private static int indexOf(byte[] source, byte key, int fromindex) {
		if(fromindex < source.length) {
			for(int i=fromindex; i<source.length;i++) {
				if(key == source[i]) return i; 
			}
		}
		return -1;
	}
	
	private static int indexOf(byte[] source, byte key ) {
		 return indexOf(source, key, 0);
	}

	 
	public static int readLength(byte[] source, int head) {
		if(source.length > 5) {
			byte[] des = new byte[4];
			System.arraycopy(source, head+1, des, 0, 4);
			return ByteArrayUtil.byteArrayToInt(des);
		}
		return -1;
	}

	public static DataPacket combine(DataPacket headPacket, DataPacket fragement) {
		DataPacket combined = new DataPacket();
		byte[] a = headPacket.getData();
		byte[] b = fragement.getData();
		byte[] dest = new byte[headPacket.getPacketLength() + fragement.getPacketLength()];
		System.arraycopy(a, 0, dest, 0, headPacket.getPacketLength());
		System.arraycopy(b, 0, dest, headPacket.getPacketLength(), fragement.getPacketLength());
		combined.setData(dest); 
		combined.setHeadTag(headPacket.isHeadTag());
		combined.setTailTag(fragement.isTailTag()); 
		combined.setContentLength(headPacket.getContentLength()); 
		return combined;
	}
}
