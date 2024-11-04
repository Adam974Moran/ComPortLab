package org.example;

import java.util.LinkedList;
import java.util.List;

public class HemmingCodeClass {

  public static long[] getFCSHemmingCode(long[] dataLongBits){
    long[] fcsBits = new long[8];

    //???? ????? ??? ????? data

    int newLength = dataLongBits.length;
    for(int i = 1; newLength / i > 0; i *= 2){
      newLength++;
    }

    //????????? ????? data

    long[] hemmingData = new long[newLength];
    for(int i = 0; i < hemmingData.length; i++){
      if(i < dataLongBits.length){
        hemmingData[i] = dataLongBits[i];
      }
      else{
        hemmingData[i] = 0;
      }
    }
    List<Integer> posisions = new LinkedList<>();
    for(int i = 1; i < hemmingData.length; i *= 2){
      for(int j = hemmingData.length - 1; j > i - 1; j--){
        hemmingData[j] = hemmingData[j-1];
      }
      hemmingData[i - 1] = 0;
      posisions.add(i-1);
    }

    //????????? ????? ??? ??????????? ?????

    List<Integer> sums = new LinkedList<>();
    for (int i = 1; i < hemmingData.length; i *= 2){
      int  currentSum = 0;
      for (int j = 1; j <= hemmingData.length / i; j += 2){
        for(int k = i * j - 1; k < i * (j + 1) - 1 && k < hemmingData.length; k++) {
          if(hemmingData[k] == 1) {
            currentSum++;
          }
        }
      }
      sums.add(currentSum);
    }

    for(int i = 0; i < sums.size(); i++){
      if(sums.get(i) % 2 == 0){
        fcsBits[i] = 0;
      }
      else {
        fcsBits[i] = 1;
      }
    }

    return fcsBits;
  }


  public static long[] checkHemmingCode(long[] dataLongBits, long[] fcs){
    long[] newFcs = getFCSHemmingCode(dataLongBits);
    int sum = 0;

    for(int i = 0; i < fcs.length; i++){
      if(newFcs[i] != fcs[i]){
        sum += Math.pow((double) 2, (double) i);
      }
    }

    if (sum == 0) {
      return dataLongBits;
    }
    else {
      int power = 1;
      for (; (int) (sum / Math.pow(2, power)) > 0; power++) ;
      dataLongBits[sum - power - 1] = (dataLongBits[sum - power - 1] == 1) ? 0 : 1;
      return dataLongBits;
    }
  }
}
