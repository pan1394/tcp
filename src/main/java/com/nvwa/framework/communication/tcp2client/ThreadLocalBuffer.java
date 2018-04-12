package com.nvwa.framework.communication.tcp2client;

import java.nio.ByteBuffer;

final class ThreadLocalBuffer
{
  private static final int BUFFER_SIZE = 100;//32768;
  private static ThreadLocal<ByteBuffer> bufferPool = new ThreadLocal()
  {
    protected ByteBuffer initialValue()
    {
      return ByteBuffer.allocateDirect(BUFFER_SIZE);
    }
  };
  
  public static ByteBuffer get()
  {
    return (ByteBuffer)bufferPool.get();
  }
}
