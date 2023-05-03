/*Joscelyn Stephens
CSC 440 Winter 2022
Assignment 4 - Question 3:
Prompt: Simplified DES Algorithm - 1 round*/

import java.math.BigInteger;
import java.util.Random;
import java.util.Arrays;

public class SDESRound {
   static final int PLAINTEXT_BITS = 12;
   static final int KEY_BITS = 9;
   static final int SUB_BITS = 8;
   static final String[][] SBOX_1 = {{"101", "010", "001", "110", "011", "100", "111", "000"}, {"001", "100", "110", "010", "000", "111", "101", "011"}};
   static final String[][] SBOX_2 = {{"100", "000", "110", "101", "111", "001", "011", "010"}, {"101", "011", "000", "111", "110", "010", "001", "100"}};

   static int plaintext = 0; // Input integer (default 0 if no argument given at launch)
   static int subkey = 0;
   
   static boolean testOutput = true; // <<< TEST MODE OUTPUT: set to true for test output to print
   
   public static void main(String[] args){
      // Record and save input integer
      if(args.length> 1)
      {
         try{
            plaintext = Integer.parseInt(args[0]);
            subkey = Integer.parseInt(args[1]);}
         catch(Exception e){System.out.println("ERROR: invalid input argument. Using default of plaintext = 0, subkey = 0.");}
      }
      else{System.out.println("NOTICE: no input argument. Using default plaintext = 0, subkey = 0.");}
      
      // PRINT INPUTS RECIEVED (integer and binary representation
      System.out.println("Input integer: " + plaintext + " Input subkey: " + subkey); 
      String plaintextString = intToBits(plaintext, PLAINTEXT_BITS);
      String keyString = intToBits(subkey, KEY_BITS);
      if(testOutput==true)System.out.println("Input integer bits: " + plaintextString + " Input key bits: " + keyString);
      
      int output = encryptRound(plaintextString, keyString);
      
      System.out.println("CipherText: " + output);
   
   }
   
   public static int encryptRound(String ptString, String key){
      //Step 1: Split plaintext into L0 and R0
      String L0 = getLeftHalf(ptString);
      String R0 = getRightHalf(ptString);
      if(testOutput==true)System.out.println("Left: " + L0 + "  Right: " + R0); // FOR TESTING OUTPUT
   
      //run round(s) -- just one in this program:
      String subkey1 = key.substring(0, key.length()-1);
      if(testOutput==true)System.out.println("Subkey: " + subkey1);
      String result = runARound(L0, R0, subkey1);
      
      int resultInt = 0;
      
      // Convert bit string into decimal integer
      try{resultInt = Integer.parseInt(result, 2);}
      catch(Exception e){System.out.println("ERROR: Unable to convert encrypted binary string to integer");}
      
      // return resulting integer
      return resultInt;
   }
   
   public static String runARound(String L, String R, String key){
      String L1 = R;
      
      // 1: Expand 6bit string to 8bit string
      String ExpandedR = expand(R);
      
      if(testOutput==true)System.out.println("R0 = " + R); // for testing
      if(testOutput==true)System.out.println("Expanded R0 = " + ExpandedR); // for testing
      
      // 2: XOR comparisong between expanded R and key
      String XOR_R = XORStrings(ExpandedR, key, key.length());
     if(testOutput==true) System.out.println("XORED: " + XOR_R);
      
      // 3: Split into 4-bit Left and 4-Bit Right
      String L_4bit = getLeftHalf(XOR_R);
      String R_4bit = getRightHalf(XOR_R);
      if(testOutput==true)System.out.println("Left: " + L_4bit + "  Right: " + R_4bit); // FOR TESTING OUTPUT
      
      // 4: SBox Replacement
      String L_postS = sbox(L_4bit, true);
      String R_postS = sbox(R_4bit, false);
      if(testOutput==true)System.out.println("Left Sbox: " + L_postS + "  Right Sbox: " + R_postS); // FOR TESTING OUTPUT
      
      // 5: Rejoin 
      String nearly_R1 = L_postS;
      nearly_R1 += R_postS; 
      //System.out.println("Recombined: " + R1); // FOR TESTING OUTPUT
      
      // 6: XOR sbox result with L0
      String R1 = XORStrings(nearly_R1, L, L.length());
      
      String result = L1;
      result += R1;
      System.out.println("Round 1 Bit String: " + result); // FOR TESTING OUTPUT
            
      return result;
   }
   
   //METHOD: Convert 4-bit number to a number in one of 2 s boxes 
   public static String sbox(String x, boolean isFirstBox){
      try{
         int row = Integer.parseInt(String.valueOf(x.charAt(0))); 
         int col = Integer.parseInt(x.substring(1), 2);   
         
         if(testOutput==true)System.out.println("ROW: " + row + "  COL: " + col); // FOR TESTING OUTPUT
         
         if(isFirstBox) // Use Sbox 1 for Left side bit
            return SBOX_1[row][col];
         else // Use Sbox 2 for Right side 4-bit
            return SBOX_2[row][col];
      }
      catch(Exception e){System.out.println("ERROR: Issue retrieving SBOX value"); return "000";} 
   }
   
   // METHOD: EXPAND function 012345 -> 01323245
   public static String expand(String R){
      String ex = "";
      char temp2 = R.charAt(2);
      char temp3 = R.charAt(3);

      for(int i = 0; i<6 ; i++){
         if(i == 0 || i == 1 || i ==4 || i == 5){
            ex += R.charAt(i);             
         }
         else if(i == 3)
         {
            ex += temp3;
            ex += temp2;
            ex += temp3;
            ex += temp2;
            
         }
         else {
         }
         //System.out.println("i = " + i + " String : " + ex);
      }
      return ex;
   }
   
   //BETHOD: given a bit string, cut in hald and return left half of bits as a new bit string
   public static String getLeftHalf(String bits){
      int half = bits.length()/2;
      return bits.substring(0, half);
   }
   
   //METHOD: given a bit string, cut in half and return right hald of bits as a new bit string
   public static String getRightHalf(String bits){
      int half = bits.length()/2;
      return bits.substring(half);
   }
   
   // METHOD: XOR two given string bits 
   public static String XORStrings(String a, String b, int size){
      String result = "";     
 
       // Loop to iterate over the
       // Binary Strings
       for (int i = 0; i < size; i++)
        {
            // If the Character matches
            if (a.charAt(i) == b.charAt(i))
                result += "0";
            else
                result += "1";
        }
        return result;
   }
   
   
   // METHOD: RETURNS 4, 8, 12 or 16-DIGIT BINARY REPRESENTATION OF various ints
   public static String intToBits(int x, int bits){
      /*Add a 1 in n+1th place to ensure any leading 0s appear, 
      then return the substring which does not contain that leftmost extra bit*/
      if(bits == 4)
         return Integer.toBinaryString(0x10 | x).substring(1); // if 4 bit
      else if(bits == 8)
            return Integer.toBinaryString(0x100 | x).substring(1); // if 8 bit
      else if(bits == 9)
            return Integer.toBinaryString(0x200 | x).substring(1); // if 9 bit
      else if(bits == 12)
         return Integer.toBinaryString(0x1000 | x).substring(1); // if 12 bit
      else     
         return Integer.toBinaryString(0x10000 | x).substring(1); // if 16 bit
   }

}