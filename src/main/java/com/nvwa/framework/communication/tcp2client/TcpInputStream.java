package com.nvwa.framework.communication.tcp2client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public final class TcpInputStream
  extends InputStream
{
  //private final Logger logger = LoggerFactory.getLogger(TcpInputStream.class);
  private SocketChannel channel;
  private final TcpChunkHeader chunkHeader = new TcpChunkHeader();
  private final ByteBuffer buffer;
  private int remain = 0;
  private boolean end = false;
  private boolean ver3 = false;
  
  public TcpInputStream(SocketChannel channel)
    throws IOException
  {
    this.channel = channel;
    
    this.buffer = ThreadLocalBuffer.get(); //32k
    this.buffer.clear();
    this.buffer.flip();
    
    fillBuffer();
  }
  
  private void readChunkHeader()
    throws IOException
  {
    this.chunkHeader.readFrom(this.channel);
    if (this.chunkHeader.isEndChunk())
    {
      this.end = true;
      return;
    }
    this.ver3 = this.chunkHeader.isNeedUuidReturn();
    this.remain = this.chunkHeader.getLength();
  }
  
  private void fillBuffer()
    throws IOException
  {
    if (this.end) {
      return;
    }
    if (this.remain <= 0)
    {
      readChunkHeader();
      if (this.end) {
        return;
      }
    }
    this.buffer.compact();
    
    int limit = this.remain + this.buffer.position();
    if (limit < this.buffer.capacity())
    {
      //System.out.println("buffer limit set to {}", Integer.valueOf(limit));
      this.buffer.limit(limit);
    }
    int readLength = this.buffer.remaining();
    do
    {
      if (this.channel.read(this.buffer) == -1)
      {
       // this.logger.error("end of stream reached");
        throw new IOException("end of stream reached");
      }
    } while (this.buffer.hasRemaining());
    this.remain -= readLength;
    this.buffer.flip();
    
    //System.out.println("after fillBuffer remain {}, buffer remain {}", Integer.valueOf(this.remain), Integer.valueOf(this.buffer.remaining()));
  }
  
  public int available()
    throws IOException
  {
    if (this.end) {
      return 0;
    }
    if (this.buffer.remaining() == 0) {
      fillBuffer();
    }
    return this.buffer.remaining();
  }
  
  public int read()
    throws IOException
  {
    if (!this.buffer.hasRemaining()) {
      fillBuffer();
    }
    if ((!this.buffer.hasRemaining()) && (this.end)) {
      return -1;
    }
    return this.buffer.get() & 0xFF;
  }
  
  public int read(byte[] b, int off, int len)
    throws IOException
  {
    if (!this.buffer.hasRemaining()) {
      fillBuffer();
    }
    if ((!this.buffer.hasRemaining()) && (this.end)) {
      return -1;
    }
    if (len > this.buffer.remaining()) {
      len = this.buffer.remaining();
    }
    this.buffer.get(b, off, len);
    
    return len;
  }
  
  public void close()
    throws IOException
  {
    while (!this.end)
    {
      this.buffer.clear();
      
      fillBuffer();
    }
    this.channel = null;
  }
  
  public boolean isVer3()
  {
    return this.ver3;
  }
  
  public boolean markSupported()
  {
    return true;
  }
  
  public synchronized void mark(int readlimit)
  {
    this.buffer.mark();
  }
  
  public synchronized void reset()
    throws IOException
  {
    this.buffer.reset();
  }
}
