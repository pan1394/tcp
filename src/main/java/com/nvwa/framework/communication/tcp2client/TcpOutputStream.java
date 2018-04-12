package com.nvwa.framework.communication.tcp2client;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public final class TcpOutputStream
  extends OutputStream
{
  //private final Logger logger = LoggerFactory.getLogger(TcpOutputStream.class);
  private SocketChannel channel;
  private final TcpChunkHeader chunkHeader = new TcpChunkHeader();
  private final ByteBuffer buffer;
  private boolean firstChunk = true;
  private final boolean progress;
  
  public TcpOutputStream(SocketChannel channel)
  {
    this(channel, false);
  }
  
  public TcpOutputStream(SocketChannel channel, boolean isProgress)
  {
    this.channel = channel;
    this.progress = isProgress;
    
    this.buffer = ThreadLocalBuffer.get();
    this.buffer.clear();
  }
  
  public void write(int b)
    throws IOException
  {
    if (!this.buffer.hasRemaining()) {
      flush();
    }
    this.buffer.put((byte)b);
  }
  
  public void write(byte[] b)
    throws IOException
  {
    write(b, 0, b.length);
  }
  
  public void write(byte[] b, int off, int len)
    throws IOException
  {
    //this.logger.debug("write byte array : offset={} length={}", Integer.valueOf(off), Integer.valueOf(len));
    int remain = len;
    while (remain > 0)
    {
      if (!this.buffer.hasRemaining()) {
        flush();
      }
      int length = this.buffer.remaining();
      //this.logger.trace("buffer remaining={}", Integer.valueOf(length));
      if (remain < length) {
        length = remain;
      }
      //this.logger.trace("buffer put : offset={} length={}", Integer.valueOf(off), Integer.valueOf(length));
      this.buffer.put(b, off, length);
      
      off += length;
      remain -= length;
    }
  }
  
  public void flush()
    throws IOException
  {
    flush(false);
  }
  
  private void flush(boolean endFragment)
    throws IOException
  {
    //this.logger.trace("flush(endFragment={}) called", Boolean.valueOf(endFragment));
    
    this.buffer.flip();
    if (!this.buffer.hasRemaining())
    {
      this.buffer.clear();
      return;
    }
    this.chunkHeader.reset();
    
    int length = this.buffer.remaining();
    //this.logger.debug("write chunk, length={}", Integer.valueOf(length));
    this.chunkHeader.setLength(length);
    if (this.firstChunk)
    {
      this.firstChunk = false;
      this.chunkHeader.setFragmentBegin(true);
    }
    if (endFragment) {
      this.chunkHeader.setFragmentEnd(true);
    }
    this.chunkHeader.setProgress(this.progress);
    
    //this.logger.trace("chunkHeader write start");
    this.chunkHeader.writeTo(this.channel);
    //this.logger.trace("chunkHeader write end");
    
    //this.logger.trace("chunk data write start");
    while (this.buffer.hasRemaining()) {
      this.channel.write(this.buffer);
    }
    //this.logger.trace("chunk data write end");
    
    this.buffer.clear();
  }
  
  public void close()
    throws IOException
  {
    flush(true);
    
    //this.logger.debug("write end chunk");
    
    this.chunkHeader.reset();
    this.chunkHeader.setEndChunk(true);
    this.chunkHeader.writeTo(this.channel);
    
    this.channel = null;
  }
}
