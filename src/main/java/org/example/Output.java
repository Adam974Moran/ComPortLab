package org.example;

import java.util.LinkedList;
import java.util.List;

public class Output {
  public static void main(String[] args) {
    System.out.println(bitStuffingDataFrom(new long[]{0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0}));
  }

  public static String bitStuffingDataFrom(long[] longBits) {
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
    char[] outputLongBits = new char[newLongBits.length + 2*onesBitsPos.size()];
    int j = 0;
    for (int i = 0; i < newLongBits.length; i++){
      if(onesBitsPos.contains(i)){
        outputLongBits[i + j] = '(';
        j++;
        if(newLongBits[i] == 0){
          outputLongBits[i + j] = '0';
          j++;
        } else{
          outputLongBits[i + j] = '1';
          j++;
        }
        outputLongBits[i + j] = ')';
      }
      else {
        if(newLongBits[i] == 0){
          outputLongBits[i + j] = '0';
        } else{
          outputLongBits[i + j] = '1';
        }
      }
    }
    return new String(outputLongBits);
  }

  public static String listOfLongBitsToSend(String string, String portName) {
    StringBuilder fullOutput = new StringBuilder();
    List<String> partsToSend = new LinkedList<>();
    long[] longBits = BytesBits.fromBytesArrayToBits(string.getBytes());
    fullOutput.append(bitStuffingDataFrom(longBits)).append("\n\n");
    long[] bitStuffingLongBits = BitStuffingClass.bitStuffingDataFrom(longBits);
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

    for (String s : partsToSend) {
      fullOutput.append(s);
    }
    return fullOutput.toString();
  }

  public static String getLongBitPackage(long[] bitStuff, String portName) {
    StringBuilder output = new StringBuilder();
    long[] flag = BytesBits.fromBytesArrayToBits(new byte[] {6, 0, 0, 0, 0, 0, 0, 0});
    output.append("getLongBitPackage(flag): ");
    output.append(outputln(flag));

    long[] destination =
        new long[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0};
    output.append("getLongBitPackage(destination): ");
    output.append(outputln(destination));

    long[] source = getPortNumInLongBits(portName.replace("COM", ""));
    output.append("getLongBitPackage(source): ");
    output.append(outputln(source));

    long[] data = bitStuff;
    output.append("getLongBitPackage(data): ");
    output.append(outputln(data));

    long[] fcs = new long[] {0, 0, 0, 0, 0, 0, 0, 0};
    output.append("getLongBitPackage(fcs): ");
    output.append(outputln(fcs));
    output.append("\n");


    return output.toString();
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

  public static StringBuilder outputln(long[] args) {
    StringBuilder output = new StringBuilder();
    for (int i = args.length - 1; i >= 0; i--) {
      output.append(args[i]);
    }
    output.append("\n");
    return output;
  }

  public static String finalOutput(String string, String portName) {
    return listOfLongBitsToSend(string, portName);
  }
}
