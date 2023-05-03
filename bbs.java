/*Joscelyn Stephens
CSC 440 Winter 2022
Assignment 4 - Question 2:
Prompt: Write a program called BBS.java or bbs.py that takes as a command line parameter an integer in the range 0 to 215-1. 
Think of this integer as a plaintext stream of 16 bits. Your program will use the BBS PRNG to generate a sixteen bit key 
(and so another integer), XOR that with the input integer, and then print the resulting cipher integer. 
I will run your code with the command python bbs.py 0 or java bbs 0.*/

import java.math.BigInteger;
import java.util.Random;
import java.util.Arrays;

public class bbs {
   static int input = 0; // Input integer (default 0 if no argument given at launch)
   static BigInteger n;
   
   static final int BIT_NUM = 16;
   static final BigInteger BIG_4 = BigInteger.valueOf(4L);
   static final BigInteger BIG_3 = BigInteger.valueOf(3L);

   public static void main(String[] args){
      // Record and save input integer
      if(args.length> 0)
      {
         try{input = Integer.parseInt(args[0]);}
         catch(Exception e){System.out.println("ERROR: invalid input argument. Using default input of 0.");}
      }
      else{System.out.println("NOTICE: no input argument. Using default input of 0.");}
      
      // PRINT INPUTS RECIEVED (integer and binary representation
      System.out.println("Input integer: " + input); 
      String inputBitString = intTo16Bits(input);     
      System.out.println("Input integer bits: " + intTo16Bits(input));
      
      Random rando = new Random (input * System.currentTimeMillis());
      
      //BBS: Step 1: N = PQ, where P and Q are primes 3 mod 4   
      n = findN(BIT_NUM, rando);
      
      //BBS: Step 2: get x relatively prime to n
      BigInteger x = findX(BIT_NUM, rando);
      //System.out.println("X: " + x);
      
      //BBS: Step 3: get initial seed: x^2(mod n)
      BigInteger xSeed = findXSquare(x);
      //System.out.println("X Seed: " + xSeed);
      
      //BBS: Step 4: get least significant bits for 16 xSeeds
      String bbs_key = BBSGenerator(xSeed, BIT_NUM, rando);
      
      // Step 5: XOR input binary with BBS binary
      String finalBitString = XORStrings(bbs_key, inputBitString, BIT_NUM);
      
      try{
         int finalint = Integer.parseInt(finalBitString,2); // parse int with %2
         System.out.println("\nBBS Algorithm Output Integer: " + finalint);
      }
      catch(Exception e){System.out.println("ERROR: Unable to convert binary string " + finalBitString);}
   
   }
   
   public static String XORStrings(String bbs, String input, int size){
      String result = "";     
 
       // Loop to iterate over the
       // Binary Strings
       for (int i = 0; i < size; i++)
        {
            // If the Character matches
            if (bbs.charAt(i) == input.charAt(i))
                result += "0";
            else
                result += "1";
        }
        return result;
   }
   
   // METHOD: get least significant bit of generated seeds to breate 16-bit binary sequence
   public static String BBSGenerator(BigInteger xSeed, int size, Random r){
      int[] lsb = new int[size];
      int temp;
      temp = xSeed.getLowestSetBit();
      lsb[0] = temp%2;
      BigInteger j = new BigInteger(size, r);
      j = BigInteger.ZERO.add(xSeed);
            
      for(int i = 1; i< size; i++)
      {
         j = findXSquare(j);
         temp = j.getLowestSetBit();
         lsb[i] = temp%2;
      }
      String bitstring = "";
      for(int i = 0; i< size; i++){
         bitstring += lsb[i];
      }
      System.out.println("BBS Algorithm Bits: " + bitstring);
      return bitstring;
   }
   
   // generate initial seed for BBS generator
   public static BigInteger findXSquare(BigInteger x){
      BigInteger y = x.multiply(x).mod(n);
      return y;
   }
   
   
   //METHOD: find an X that is relatively prime to n
   public static BigInteger findX(int size, Random rando){
      BigInteger x = new BigInteger(size, rando);
      boolean valid = false;
      while(!valid){
         if(relativelyPrime(x, n))
            valid = true;
         else
            x = new BigInteger(size, rando);  
      }
      return x;
   }
   
   
   // HELPER METHOD: BIG INTEGER -> relatively prime
   public static boolean relativelyPrime(BigInteger y, BigInteger z) {
      // checks that gcd(y,z)=1 for two bigIntegers
     return y.gcd(z).equals(BigInteger.ONE); 
   }
   
   
   //METHOD: N = PQ
   public static BigInteger findN(int size, Random rando){
      BigInteger p = findBigPrime(size, rando);
      BigInteger q = findBigPrime(size, rando);
      boolean valid = false;
      while(!valid){
         if(!p.equals(q))
            valid = true;
         else
            q = findBigPrime(BIT_NUM, rando);
      }
      //System.out.println(p);
      //System.out.println(q);
      
      BigInteger n = p.multiply(q);
      //System.out.println(n);
      return n;
   }
   
   // METHOD: FIND LARGE PRIME NUMBER THAT IS 3mod4
   public static BigInteger findBigPrime(int size, Random r){
      BigInteger x;
      Boolean valid = false;
      x = new BigInteger(size, 100, r);
      if(x.mod(BIG_4).equals(BIG_3))
          valid = true;
      while(!valid){
         // size = number of bits in number, 100 = likelihood of prime number, r = Random int generator
         x = new BigInteger(size, 100, r);
         if(x.mod(BIG_4).equals(BIG_3))
            valid = true;
      }
      return x;
   }
   
   // METHOD: RETURNS 16-DIGIT BINARY REPRESENTATION OF INT (0 - 2^15-1)
   public static String intTo16Bits(int x){
      /*Add a 1 in 17th place to ensure any leading 0s appear, 
      then return the substring which does not contain that leftmost extra bit*/
      return Integer.toBinaryString(0x10000 | x).substring(1);
   }

}