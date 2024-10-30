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

    System.out.print("Data Before Bit Stuffing: ");
    for (long newLongBit : newLongBits) {
      System.out.print(newLongBit);
    }
    System.out.println("\n");

    System.out.println("String: " + new String(convertFromLongBitsToByteArray(newLongBits)));

    return newLongBits;
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

    System.out.print("Data After Bit Stuffing: ");
    for (int i = 0; i < newLongBits.length; i++) {
      if (onesBitsPos.contains(i)) {
        System.out.print(" (" + newLongBits[i] + ") ");
      } else {
        System.out.print(newLongBits[i]);
      }
    }
    System.out.println("\n");

    return newLongBits;
  }


  public static List<long[]> listOfLongBitsToSend(String string, String portName) {
    List<long[]> partsToSend = new LinkedList<>();
    long[] longBits = convertFromStringToLongBits(string);
    long[] bitStuffingLongBits = bitStuffingDataFrom(longBits);
    bitStuffingDataTo(bitStuffingLongBits);
    int bits = 0;
    long[] data = new long[56];

    int iterations = (bitStuffingLongBits.length % 56 != 0) ? bitStuffingLongBits.length / 56 + 1 :
        bitStuffingLongBits.length / 56;
    System.out.println(bitStuffingLongBits.length + " " + iterations);

    long[] allData = new long[0];
    for(int i = 0; i < iterations; i++){
      for(int j = 0; j < 56; j++){
        if(j < bitStuffingLongBits.length - (i * 56)) {
          data[j] = bitStuffingLongBits[i * 56 + j];
        }
        else{
          data[j] = 0;
        }
      }
      partsToSend.add(getLongBitPackage(data, portName));
    }

    System.out.println(convertFromLongBitsToString(allData));
//    for (int i = 0; i < bitStuffingLongBits.length; i++) {
//      if (i % 56 != 0 || i == 0) {
//        data[bits] = bitStuffingLongBits[i];
//        bits++;
//      } else {
//        partsToSend.add(getLongBitPackage(data, portName));
//        bits = 0;
//        data[bits] = bitStuffingLongBits[i];
//      }
//    }
    for (long[] bitsPackage : partsToSend) {
      System.out.println("bytesToSend: " + Arrays.toString(bitsPackage));
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
    long[] flag = convertFromStringToLongBits(new String(new byte[] {6}));
    long[] finalFlag = new long[flag.length + 56];
    for (int i = 0; i < finalFlag.length; i++) {
      if (i < flag.length) {
        finalFlag[i] = flag[i];
      } else {
        finalFlag[i] = 0;
      }
    }
    System.out.print("getLongBitPackage(flag): ");
    outputln(finalFlag);

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
        new long[finalFlag.length + destination.length + source.length + data.length +
            fcs.length];
    int index = 0;
    System.arraycopy(finalFlag, 0, datePackage, index, finalFlag.length);
    index += finalFlag.length;
    System.arraycopy(destination, 0, datePackage, index, destination.length);
    index += destination.length;
    System.arraycopy(source, 0, datePackage, index, source.length);
    index += source.length;
    System.arraycopy(data, 0, datePackage, index, data.length);
    index += data.length;
    System.arraycopy(fcs, 0, datePackage, index, fcs.length);
    System.out.println(Arrays.toString(datePackage));
    
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
    System.out.println("data: " + Arrays.toString(data));
    return data;
  }

  public static long[] getPortNumInLongBits(String portNum) {
    long[] portNumLongBits = convertFromBytesToLongBits(portNum.getBytes());
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

  public static long[] convertFromStringToLongBits(String string) {
    BitSet bitSet = BitSet.valueOf(string.getBytes());
    int longBitsSize =
        (int) (((bitSet.length() / 8.0) % 1 == 0) ?
            8 * (int) (bitSet.length() / 8.0) :
            8 * (int) (bitSet.length() / 8.0 + 1));

    long[] longBits = new long[longBitsSize];

    for (int i = 0; i < bitSet.length(); i++) {
      if (bitSet.get(i)) {
        longBits[i] = 1;
      } else {
        longBits[i] = 0;
      }
    }

    for (int i = bitSet.length(); i < longBitsSize; i++) {
      longBits[i] = 0;
    }

    return longBits;
  }

  public static byte[] convertFromLongBitsToByteArray(long[] longBits) {
    BitSet bitSet = new BitSet(longBits.length);
    for (int i = 0; i < longBits.length; i++) {
      bitSet.set(i, longBits[i] != 0);
    }
    return bitSet.toByteArray();
  }

  public static long[] convertFromBytesToLongBits(byte[] bytes) {
    BitSet bitSet = BitSet.valueOf(bytes);
    int longBitsSize =
        (int) (((bitSet.length() / 8.0) % 1 == 0) ?
            8 * (int) (bitSet.length() / 8.0) :
            8 * (int) (bitSet.length() / 8.0 + 1));

    long[] longBits = new long[longBitsSize];

    for (int i = 0; i < bitSet.length(); i++) {
      if (bitSet.get(i)) {
        longBits[i] = 1;
      } else {
        longBits[i] = 0;
      }
    }

    for (int i = bitSet.length(); i < longBitsSize; i++) {
      longBits[i] = 0;
    }

    return longBits;
  }

  public static int[] fromLongToInt(long[] longBytes) {
    return Arrays.stream(longBytes).mapToInt(value -> (int) value).toArray();
  }

  public static long[] fromIntToLong(int[] intBytes) {
    return Arrays.stream(intBytes).mapToLong(value -> (long) value).toArray();
  }

  public static String convertFromLongBitsToString(long[] longBits) {
    return new String(convertFromLongBitsToByteArray(longBits));
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
