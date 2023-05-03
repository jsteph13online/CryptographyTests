/*Joscelyn Stephens
CSC 440 Winter 2022
Assignment 6 - Question 1:
Prompt: Simplified DES Algorithm  DECRYPTION - 4 round*/

/*
Decryption Algorithm Steps:
1. Ri-1 = Li
2. Expand Ri-1, then XOR with ki
3. Ri-1 Left 3 bits -> Sbox 1
4. Ri-1 Right 3 bits -> Sbox 2
5. Li-1 = Ri XOR Sbox Results
*/


public class SDESDecrypt {
   static final int TEXT_BITS = 12;
   static final int KEY_BITS = 9;
   static final int SUB_BITS = 8;
   static final int ROUNDS = 4;
   static final String[][] SBOX_1 = {{"101", "010", "001", "110", "011", "100", "111", "000"}, {"001", "100", "110", "010", "000", "111", "101", "011"}};
   static final String[][] SBOX_2 = {{"100", "000", "110", "101", "111", "001", "011", "010"}, {"101", "011", "000", "111", "110", "010", "001", "100"}};

   static int ciphertext = 2699; // Input integer (default 0 if no argument given at launch)
   static int key = 85;
   
   static boolean testOutput = false; // <<< TEST MODE OUTPUT: set to true for test output to print
   
   public static void main(String[] args){
      // Record and save input integer
      if(args.length> 1)
      {
         try{
            ciphertext = Integer.parseInt(args[1]);
            key = Integer.parseInt(args[0]);}
         catch(Exception e){System.out.println("ERROR: invalid input argument. Using default of ciphertext = " + ciphertext+ ", key = " + key + ".");}
      }
      else{System.out.println("NOTICE: no input argument. Using default ciphertext = " + ciphertext+ ", key = " + key +".");}
      
      // PRINT INPUTS RECIEVED (integer and binary representation
      System.out.println("Input 12-bit Ciphertext Integer: " + ciphertext + " \nInput 9-bit Key: " + key); 
      String plaintextString = intToBits(ciphertext, TEXT_BITS);
      String keyString = intToBits(key, KEY_BITS);
      if(testOutput==true)System.out.println("Input integer bits: " + plaintextString + " Input key bits: " + keyString);
      
      // Run initial round to get first pass crypt text
      if(testOutput==true)System.out.println("\n === BEGINNING ROUND: 3"); // FOR TESTING OUTPUT 
      int output = decryptRound(plaintextString, keyString, (ROUNDS-1));
      
      // Run remaining rounds using output to get final crypt text
      for (int round = (ROUNDS-2); round > -1; round--)
      {
         if(testOutput==true)System.out.println("\n === BEGINNING ROUND: " + round); // FOR TESTING OUTPUT
         output = decryptRound(intToBits(output, TEXT_BITS), keyString, round);
      }
      
      System.out.println("Plaintext: " + output);
      System.out.println("Plaintext Bit String: " + intToBits(output, TEXT_BITS));
   
   }
   
    // METHOD: A single round of decryption of a given bit string, given a 9-bit key and a round (0-3)
   public static int decryptRound(String ptString, String key, int round){

      //Step 1: Split  into L0 and R0
      String L0 = getLeftHalf(ptString);
      String R0 = getRightHalf(ptString);
      if(testOutput==true)System.out.println("Left: " + L0 + "  Right: " + R0); // FOR TESTING OUTPUT
   
      //run round(s) 
         // First, get subkey for the round
      String subkey = getSubkey(key, round);
      String result = "";
      if(round > 2)
         result = runARound(R0, L0, subkey); // run round with given subkey, Lo, and Ro, switching L0 and R0
      else 
         result = runARound(L0, R0, subkey); // run round with given rubkey, don't switch L0/R0
     
      int resultInt = 0;
      
      // Convert bit string into decimal integer
      try{resultInt = Integer.parseInt(result, 2);}
      catch(Exception e){System.out.println("ERROR: Unable to convert encrypted binary string to integer");}
      
      // return resulting integer
      return resultInt;
   }
   
   
   // METHOD - CALCULATE the subkey given the key and round
   public static String getSubkey(String key, int round){
      //String subkey = key.substring(0, key.length()-1); // TEST - this is the correct one?
      //String subkey = key.substring(1, key.length()); // SDESRound.java get correct key
      if(key.length()==8)
      {System.out.println("ERROR - Key length incorrect. Adding leading 0.");
      key = "0" + key;}
      String subkey;
      switch(round){
         case(0):
            // 0 1 2 3 4 5 6 7
            subkey = key.substring(0, key.length()-1);
            break;
         case(1):
            // 1 2 3 4 5 6 7 8
            subkey = key.substring(1, key.length());
            break;
         case(2):
            // 2 3 4 5 6 7 8 0
            subkey = key.substring(2, key.length());
            subkey = subkey + key.substring(0, 1);
            break;
         case(3):
            // 3 4 5 6 7 8 0 1
            subkey = key.substring(3, key.length());
            subkey = subkey + key.substring(0, 2);
            break;
         default:
            // Just do case 0 if error -  0 1 2 3 4 5 6 7
            subkey = key.substring(0, key.length()-1);
            System.out.println("Why did I hit this code?");
            break;
      }
      
      if(testOutput==true)System.out.println("Subkey: " + subkey);
      return subkey;
   }
   
   //METHOD - Run a SDES round with given L, R and subkey
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
      if(testOutput==true)System.out.println("Round Result Bit String: " + result); // FOR TESTING OUTPUT
            
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
      if(testOutput==true)System.out.println("a: " + a + "  b: " + b); // FOR TESTING OUTPUT
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