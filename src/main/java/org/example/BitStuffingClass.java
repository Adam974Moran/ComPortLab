package org.example;

import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class BitStuffingClass {
  public static void main(String[] args) {
    listOfLongBitsToSend("1234567890abcdefghijklmnopqrstuvwxyz", "COM23");

  }

  public static long[] bitStuffingDataTo(long[] longBits) {
    long[] newLongBits = longBits;
    long[] permanent;
    int zeroes = 0;
    for (int i = 0; i < newLongBits.length; i++) {
      if (newLongBits[i] == 0 && zeroes < 4) {
        zeroes++;
      } else if (zeroes == 4) {
        permanent = new long[newLongBits.length - 1];
        System.arraycopy(newLongBits, 0, permanent, 0, i);
        permanent[i] = 1;
        for (int j = i; j < permanent.length - 1; j++) {
          permanent[j] = newLongBits[j + 1];
        }
        newLongBits = permanent;
        zeroes = 0;
      } else {
        zeroes = 0;
      }
    }

    System.out.println(newLongBits.length);

    int fullAmountOfBits = (newLongBits.length % 8 > 0) ? (newLongBits.length / 8 + 1) * 8 :
        newLongBits.length;

    System.out.println(fullAmountOfBits);
    long[] fullBitsArray = new long[fullAmountOfBits];
    for(int i = 0; i < fullBitsArray.length; i++){
      fullBitsArray[i] = 0;
    }
    for(int i = 0; i < fullAmountOfBits; i++){
      if(i < newLongBits.length) {
        fullBitsArray[i] = newLongBits[i];
      }
      else{
        fullBitsArray[i] = 0;
      }
    }

    return fullBitsArray;
  }

  public static long[] bitStuffingDataFrom(long[] longBits) {
    List<Integer> onesBitsPos = new LinkedList<>();
    long[] newLongBits = longBits;
    long[] permanent;
    int zeroes = 0;
    for (int i = 0; i < newLongBits.length; i++) {
      if (newLongBits[i] == 0 && zeroes < 4) {
        zeroes++;
      } else if (zeroes == 4) {
        permanent = new long[newLongBits.length + 1];
        for (int j = 0; j < newLongBits.length; j++) {
          permanent[j] = newLongBits[j];
        }
        zeroes = 0;
        for (int j = permanent.length - 1; j > i; j--) {
          permanent[j] = permanent[j - 1];
        }
        permanent[i] = 1;
        onesBitsPos.add(i);
        newLongBits = permanent;
      } else {
        zeroes = 0;
      }
    }
    for (int i = 0; i < newLongBits.length; i++){
      if(onesBitsPos.contains(i)){
        System.out.print("(" + 1 + ")");
      }
      else {
        System.out.print(newLongBits[i]);
      }
    }
    System.out.println();
    return newLongBits;
  }


  public static List<long[]> listOfLongBitsToSend(String string, String portName) {
    List<long[]> partsToSend = new LinkedList<>();
    long[] longBits = BytesBits.fromBytesArrayToBits(string.getBytes());
    for(long l : longBits){
      System.out.print(l);
    }
    System.out.println();
    long[] bitStuffingLongBits = bitStuffingDataFrom(longBits);
    outputln(bitStuffingLongBits);
    long[] data = new long[56];
    for(int i = 0; i < data.length; i++){
      data[i] = 0;
    }

    int iterations = (bitStuffingLongBits.length % 56 != 0) ? bitStuffingLongBits.length / 56 + 1 :
        bitStuffingLongBits.length / 56;

    for(int i = 0; i < iterations; i++){
      for(int j = 0; j < 56; j++){
        if(j < bitStuffingLongBits.length - (i * 56)) {
          data[j] = bitStuffingLongBits[i * 56 + j];
        }
        else {
          data[j] = 0;
        }
      }
      partsToSend.add(getLongBitPackage(data, portName));
    }
    return partsToSend;
  }

  public static void outputln(long[] args) {
    for (int i = args.length - 1; i >= 0; i--) {
      System.out.print(args[i]);
    }
    System.out.println();
  }

  public static long[] getLongBitPackage(long[] bitStuff, String portName) {
    long[] flag = BytesBits.fromBytesArrayToBits(new byte[]{0, 0, 0, 0, 0, 0, 0, 6});
    System.out.print("getLongBitPackage(flag): ");
    for(long l : flag){
      System.out.print(l);
    }
    System.out.println();

    long[] destination =
        new long[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0};
    System.out.print("getLongBitPackage(destination): ");
    outputln(destination);

    long[] source = getPortNumInLongBits(portName.replace("COM", ""));
    System.out.print("getLongBitPackage(source): ");
    outputln(source);

    long[] data = bitStuff;
    System.out.print("getLongBitPackage(data): ");
    outputln(data);

    long[] fcs = new long[] {0, 0, 0, 0, 0, 0, 0, 0};
    System.out.print("getLongBitPackage(fcs): ");
    outputln(fcs);

    System.out.println();

    long[] datePackage =
        new long[flag.length + destination.length + source.length + data.length +
            fcs.length];
    int index = 0;
    System.arraycopy(flag, 0, datePackage, index, flag.length);
    index += flag.length;
    System.arraycopy(destination, 0, datePackage, index, destination.length);
    index += destination.length;
    System.arraycopy(source, 0, datePackage, index, source.length);
    index += source.length;
    System.arraycopy(data, 0, datePackage, index, data.length);
    index += data.length;
    System.arraycopy(fcs, 0, datePackage, index, fcs.length);
    
    return datePackage;
  }

  public static long[] extractDataFromPackage(long[] allData, long[] receivedPackage) {
    long[] newAllData = new long[allData.length + receivedPackage.length];
    int j = 0;
    for (int i = 0; i < newAllData.length; i++) {
      if (i < allData.length) {
        newAllData[i] = allData[i];
      } else {
        newAllData[i] = receivedPackage[j];
        j++;
      }
    }
    return newAllData;
  }

  public static long[] dataFromPackage(long[] fullPackage) {
    long[] data = new long[56];
    int j = 0;
    for (int i = 128; i < fullPackage.length - 8; i++) {
      data[j] = fullPackage[i];
      j++;
    }
    return data;
  }

  public static long[] getPortNumInLongBits(String portNum) {
    long[] portNumLongBits = BytesBits.fromBytesArrayToBits(portNum.getBytes());
    long[] finalPortNumBits = new long[32];
    for (int i = 0; i < finalPortNumBits.length; i++) {
      if (i < portNumLongBits.length) {
        finalPortNumBits[i] = portNumLongBits[i];
      } else {
        finalPortNumBits[i] = 0;
      }
    }
    return finalPortNumBits;
  }

  public static long[] cutting(long[] args) {
    int i;
    for(i = args.length - 1; i >= 0; i--){
      if(args[i] == 1){
        break;
      }
    }
    i += 2;
    long[] newLongBits = new long[i];
    for(int j = 0; j < newLongBits.length; j++){
      newLongBits[j] = args[j];
    }
    return newLongBits;
  }
}
