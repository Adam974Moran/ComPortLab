package org.example;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BytesBits {
  public static void main(String[] args) {
    String string = "google tracking";
    byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
    System.out.println(Arrays.toString(bytes));
    long[] bits = fromBytesArrayToBits(bytes);
    System.out.println(Arrays.toString(bits));
    bytes = fromBitsToByteArray(bits);
    System.out.println(Arrays.toString(bytes));
    System.out.println(new String(bytes));
  }

  public static byte[] fromBitsToByteArray (long[] bits) {
    int iterations = (bits.length % 8 > 0) ? bits.length / 8 + 1 : bits.length / 8;
    long[] newBits = new long[iterations * 8];
    for(int i = 0; i < newBits.length; i++){
      newBits[i] = 0;
    }
    byte[] byteArray = new byte[iterations];
    for(int i = 0; i < iterations; i++){
      long[] bitsGroup = new long[8];
      for(int j = 0; j < bitsGroup.length; j++){
        bitsGroup[j] = bits[i * 8 + j];
      }
      byteArray[i] = fromBitsToByte(bitsGroup);
    }
    return byteArray;
  }

  public static byte fromBitsToByte (long[] bits) {
    long[] powersOfTwo = {1, 2, 4, 8, 16, 32, 64, 128};
    int oneByte = 0;
    for(int i = 0; i < 8; i++){
      oneByte += (bits[i] * powersOfTwo[i]);
    }
    return (byte) oneByte;
  }

  public static  long[] fromBytesArrayToBits (byte[] bytes) {
    long[] bits = new long[bytes.length * 8];
    for(int i = 0; i < bits.length; i++){
      bits[i] = 0;
    }
    for(int i = 0; i < bytes.length; i++){
      long[] bitsGroup = fromByteToBits(bytes[i]);
      for(int j = 0; j < 8; j++){
        bits[i * 8 + j] = bitsGroup[j];
      }
    }
    return bits;
  }

  public static long[] fromByteToBits (byte oneByte) {
    int numerator  = Byte.valueOf(oneByte).intValue();
    if(numerator < 0){
      numerator = numerator + 256;
    }
    int integerPart;
    long[] bits = {0, 0, 0, 0, 0, 0, 0, 0};
    int i;
    for(i = 0; numerator >= 1; i ++){
      integerPart = numerator / 2;
      bits[i] = numerator - integerPart * 2;
      numerator /= 2;
    }
    return bits;
  }


}