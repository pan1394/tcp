package com.nvwa.framework.communication.tcp2client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public final class TcpChunkHeader
{
  private static final int FlagProgress = 32;
  private static final int FlagNeedUuidReturn = 16;
  private static final int FlagEndChunk = 8;
  private static final int FlagFragmentBegin = 4;
  private static final int FlagFragmentEnd = 2;
  private static final int FlagHeartbeat = 1;
  private static final byte[] heartbeatChunkHeader = new byte[8];
  
  static
  {
    ByteBuffer buf = ByteBuffer.wrap(heartbeatChunkHeader);
    buf.putInt(0);
    
    byte flag = 0;
    flag = (byte)(flag | 0x8);
    flag = (byte)(flag | 0x4);
    flag = (byte)(flag | 0x4);
    flag = (byte)(flag | 0x1);
    
    buf.put(flag);
    buf.put((byte)0);
    buf.putShort((short)CrcCcitt.calculate(heartbeatChunkHeader, 0, 6));
  }
  
  public static ByteBuffer heartbeat()
  {
    return ByteBuffer.wrap(heartbeatChunkHeader);
  }
  
 // private final Logger logger = LoggerFactory.getLogger(TcpChunkHeader.class);
  private int length;
  private byte typeFlags;
  private int fieldNameLength;
  private final byte[] headerBytes = new byte[8];
  private final ByteBuffer headerBuffer = ByteBuffer.wrap(this.headerBytes);
  
  public TcpChunkHeader()
  {
    reset();
  }
  
  public void reset()
  {
    this.length = 0;
    this.typeFlags = 0;
    this.fieldNameLength = 0;
  }
  
  public int getLength()
  {
    return this.length;
  }
  
  public void setLength(int length)
  {
    this.length = length;
  }
  
  public boolean isEndChunk()
  {
    return (this.typeFlags & 0x8) != 0;
  }
  
  public void setEndChunk(boolean endChunk)
  {
    if (endChunk) {
      this.typeFlags = ((byte)(this.typeFlags | 0x8));
    } else {
      this.typeFlags = ((byte)(this.typeFlags & 0xFFFFFFF7));
    }
  }
  
  public boolean isFragmentBegin()
  {
    return (this.typeFlags & 0x4) != 0;
  }
  
  public void setFragmentBegin(boolean fragmentBegin)
  {
    if (fragmentBegin) {
      this.typeFlags = ((byte)(this.typeFlags | 0x4));
    } else {
      this.typeFlags = ((byte)(this.typeFlags & 0xFFFFFFFB));
    }
  }
  
  public boolean isFragmentEnd()
  {
    return (this.typeFlags & 0x2) != 0;
  }
  
  public void setFragmentEnd(boolean fragmentEnd)
  {
    if (fragmentEnd) {
      this.typeFlags = ((byte)(this.typeFlags | 0x2));
    } else {
      this.typeFlags = ((byte)(this.typeFlags & 0xFFFFFFFD));
    }
  }
  
  public boolean isHeartbeat()
  {
    return (this.typeFlags & 0x1) != 0;
  }
  
  public void setHeartbeat(boolean heartbeat)
  {
    if (heartbeat) {
      this.typeFlags = ((byte)(this.typeFlags | 0x1));
    } else {
      this.typeFlags = ((byte)(this.typeFlags & 0xFFFFFFFE));
    }
  }
  
  public boolean isNeedUuidReturn()
  {
    return (this.typeFlags & 0x10) == 16;
  }
  
  public int getFieldNameLength()
  {
    return this.fieldNameLength;
  }
  
  public void setFieldNameLength(int fieldNameLength)
  {
    this.fieldNameLength = fieldNameLength;
  }
  
  public boolean isProgress()
  {
    return (this.typeFlags & 0x20) == 32;
  }
  
  public void setProgress(boolean progress)
  {
    if (progress) {
      this.typeFlags = ((byte)(this.typeFlags | 0x20));
    } else {
      this.typeFlags = ((byte)(this.typeFlags & 0xFFFFFFDF));
    }
  }
  
  public void readFrom(ReadableByteChannel channel)
    throws IOException
  {
    this.headerBuffer.clear();
    do
    {
      if (channel.read(this.headerBuffer) == -1)
      {
        if (this.headerBuffer.position() == 0) {
         // throw new PeerDisconnectedException();
        }
        System.out.printf("end of stream reached on chunk header read (count={})", Integer.valueOf(this.headerBuffer.position()));
        System.out.println();
        throw new IOException("end of stream reached on chunk header read");
      }
    } while (this.headerBuffer.hasRemaining());
    this.headerBuffer.flip();
    this.length = this.headerBuffer.getInt();
    this.typeFlags = this.headerBuffer.get();
    this.fieldNameLength = (this.headerBuffer.get() & 0xFF);
    int checksum = this.headerBuffer.getShort() & 0xFFFF;
    int calculated = CrcCcitt.calculate(this.headerBytes, 0, 6);
    if (checksum != calculated)
    {
      System.out.printf("checksum not matched on chunk header read : read={} caclulated={}", Integer.valueOf(checksum), Integer.valueOf(calculated));
      System.out.println();
      throw new IOException("checksum not matched on chunk header read");
    }
    System.out.printf("read chunk header {}", this);
    System.out.println();
  }
  
  public void writeTo(WritableByteChannel channel)
    throws IOException
  {
    this.headerBuffer.clear();
    
    System.out.printf("write chunk header {}", this);
    System.out.println();
    this.headerBuffer.putInt(this.length);
    this.headerBuffer.put(this.typeFlags);
    this.headerBuffer.put((byte)this.fieldNameLength);
    this.headerBuffer.putShort((short)CrcCcitt.calculate(this.headerBytes, 0, 6));
    
    this.headerBuffer.flip();
    do
    {
      channel.write(this.headerBuffer);
    } while (this.headerBuffer.hasRemaining());
  }
  
  public String toString()
  {
    return String.format("ChunkHeader[length=%d,typeFlags=%d,fieldNameLength=%d]", new Object[] { Integer.valueOf(this.length), Byte.valueOf(this.typeFlags), Integer.valueOf(this.fieldNameLength) });
  }
}
