package com.nvwa.framework.communication.tcp2client;

public final class CrcCcitt
{
  private static final int polynomial = 4129;
  private static final int[] table = new int['Ā'];
  public int crc;
  
  static
  {
    for (int i = 0; i < 256; i++)
    {
      int b = i << 8;
      for (int j = 0; j < 8; j++) {
        b = (b & 0x8000) != 0 ? b << 1 ^ 0x1021 : b << 1;
      }
      table[i] = (b & 0xFFFF);
    }
  }
  
  public CrcCcitt()
  {
    reset();
  }
  
  public void update(byte b)
  {
    this.crc = ((this.crc << 8 ^ table[((this.crc >>> 8 ^ b) & 0xFF)]) & 0xFFFF);
  }
  
  public int getCrc()
  {
    return this.crc;
  }
  
  public void reset()
  {
    this.crc = 65535;
  }
  
  public static int calculate(byte[] bytes)
  {
    return calculate(bytes, 0, bytes.length);
  }
  
  public static int calculate(byte[] bytes, int offset, int length)
  {
    if (bytes == null) {
      throw new IllegalArgumentException("bytes must not null");
    }
    int crc = 65535;
    int end = offset + length;
    for (int i = offset; i < end; i++) {
      crc = (crc << 8 ^ table[((crc >>> 8 ^ bytes[i]) & 0xFF)]) & 0xFFFF;
    }
    return crc;
  }
}
