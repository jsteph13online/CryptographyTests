/* Joscelyn Stephens
*
* CSC440 Winter 2022
* Assignment 7 2SDES Meet in the Middle Attack
* 
*/

import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List; 
import java.util.Set;

public class SDESMitm {

// ease-of-testing values
public final int DEFAULT_P1 = 0; // default plaintext 1
public final int DEFAULT_C1 = 247; // default ciphertext 1
public final int DEFAULT_P2 = 4095; // default plaintext 2
public final int DEFAULT_C2 = 2808; // default ciphertext 
public boolean fullTesting = false; // see round by round calculations of SDES
public boolean isTesting = false; // see full list of key pairs produced for each pair

// setting unchanging values for SDES aldogrithm 
public final int TEXT_BITS = 12;
public final int KEY_BITS = 9;
public final int SUB_BITS = 8;
public final int ROUNDS = 4;
public final String[][] SBOX_1 = {{"101", "010", "001", "110", "011", "100", "111", "000"}, {"001", "100", "110", "010", "000", "111", "101", "011"}};
public final String[][] SBOX_2 = {{"100", "000", "110", "101", "111", "001", "011", "010"}, {"101", "011", "000", "111", "110", "010", "001", "100"}};

// cipher/plaintext input values
public int p1;
public int c1;
public int p2;
public int c2;
public String p1str;
public String p2str;
public String c1str;
public String c2str;

public MITMObject obj1;
public MITMObject obj2;


public static void main(String[] args){
   SDESMitm mitm = new SDESMitm();
   mitm.start(args);}

public void start(String[] args){
      // Record and save input integer
      // init all values to defaults
      p1 = DEFAULT_P1;
      p2 = DEFAULT_P2;
      c1 = DEFAULT_C1;
      c2 = DEFAULT_C2;
      if(args.length > 3)
      {
         try{  // Record values from input          
            p1 = Integer.parseInt(args[0]);
            c1 = Integer.parseInt(args[1]);
            p2 = Integer.parseInt(args[2]);
            c2 = Integer.parseInt(args[3]);}
         catch(Exception e){ // if error reading values, reset values to use defaults
            p1 = DEFAULT_P1;
            p2 = DEFAULT_P2;
            c1 = DEFAULT_C1;
            c2 = DEFAULT_C2;
            System.out.println("ERROR: invalid input argument. Default values used. Plaintext 1 = " + p1+ ", Cipher 1 = " + c1 + ". Plaintext 2 = " + p2 + ", Cipher 2 = " + c2 + ".");}
      }
      else{
      System.out.println("NOTICE: no input argument. Default values used. Plaintext 1 = " + p1+ ", Cipher 1 = " + c1 + ". Plaintext 2 = " + p2 + ", Cipher 2 = " + c2 + ".");}

   
   System.out.println("\nBeginning MITM 2SDES Attack...");
   
   
   obj1 = new MITMObject(p1, c1); // PLAINTEXT/CIPHER Pair 1
   obj2 = new MITMObject(p2, c2); // PLAINTEXT/CIPHER Pair 2
   
   // Pair 1 Map of possibilities:	
   if(isTesting)System.out.println("\n==List of All Possible Encryptions==");
   EncryptionList(obj1);
   
   //if(isTesting)System.out.println("\n==List of All Possible Decryptions==");
   //DecryptionList(obj1);
   
   // Pair 2 Map of possibilities
   if(isTesting)System.out.println("\n==List of All Possible Encryptions 2==");
   EncryptionList(obj2);
   
   //if(isTesting)System.out.println("\n==List of All Possible Decryptions 2==");
   //DecryptionList(obj2);
   
   Compare(obj1, obj2);	
}

 void Compare(MITMObject o1, MITMObject o2){
      // get encrypt possibily lists for both pairs 
      HashMap<Integer, List<Integer>> o1List = o1.GetMapCipher();
      HashMap<Integer, List<Integer>> o2List = o2.GetMapCipher();
      
      // create map for holding any/all matches found
      HashMap<Integer, List<Integer>> matches = new HashMap<Integer, List<Integer>>();
      Set<Integer> keysP = o1List.keySet();      
       for(Integer k1 : keysP){
          if(!o2List.containsKey(k1))
           { //System.out.println("k1 not a match " + k1 + " vs " + o2List.get(k1));
           }
          else {
            //System.out.println("k1 match "+ k1 + " vs " + o2List.get(k1));
            // Add key1 if it is not already added to the matching list 
            if(matches.get(k1)==null){matches.put(k1, new ArrayList<Integer>());  //System.out.println("Adding key");
            }
            // see if there are any matches for k2
            boolean hasMatches = false;   
                    
            // Move k2 keys from the specifies k1 to hash maps for ease of search
            HashMap<Integer, Integer> o1k2 = new HashMap();
            HashMap<Integer, Integer> o2k2 = new HashMap();
            for(int i = 0; i < o1List.get(k1).size(); i++){
               o1k2.put(o1List.get(k1).get(i), i);  }
            for(int i = 0; i < o2List.get(k1).size(); i++){
               o2k2.put(o2List.get(k1).get(i), i);  }
               
            // check if the k2 value exists in both sets
            Set<Integer> k2vals = o1k2.keySet();
            for(Integer k2 : k2vals){
               if(!o2k2.containsKey(k2)){}
               else{
                  hasMatches = true; // if there is a k2 match, add it to the matches map
                  matches.get(k1).add(k2);  }
               }
            // if no k2 value matched, remove the k1 entry as well
            if (matches.get(k1).size()<1){
               matches.remove(k1); 
               //System.out.println("removing");
               } // */
            }
           }
   System.out.println("\nMatching Keys Between (plain, cipher): (" +p1+", "+c1+") and ("+p2+ ", " +c2+")" );   
   PrintList(matches); // print any key matches
}



// METHOD: 
 void EncryptionList(MITMObject obj){
   int num = obj.GetPlainInt();
   for (int i = 0; i < Math.pow(2,KEY_BITS); i++){
      Encrypt(num, i, obj); // SDES
    }    
    if (isTesting)PrintList(obj.GetMapCipher());   
}

/// ===== SDES ALGORITHM ENCRYPT METHODS ==========
public void Encrypt(int plain, int key, MITMObject obj){
   int output1 = SDESEncrypt(plain, key);
   int output2 = -1;
   int compValue = obj.GetCipherInt();
   for (int i = 0; i < Math.pow(2,KEY_BITS); i++){
      output2 = SDESEncrypt(output1, i); // Second round of SDES
      if(compValue == output2)
         obj.PutMap(key, i, output2, false);
   }
}

public int SDESEncrypt(int plain, int key){
      String keyString =  intToBits(key, KEY_BITS);
      String plaintextString = intToBits(plain, TEXT_BITS);
      
      // Run initial round to get first pass crypt text
      if(fullTesting==true)System.out.println("\n === BEGINNING ROUND: 0"); // FOR TESTING OUTPUT 
      int output = encryptRound(plaintextString, keyString, 0);
      
      // Run remaining rounds using output to get final crypt text
      for (int round = 1; round < ROUNDS; round++)
      {
         if(fullTesting==true)System.out.println("\n === BEGINNING ROUND: " + round); // FOR TESTING OUTPUT
         output = encryptRound(intToBits(output, TEXT_BITS), keyString, round);
      }
      
      if (fullTesting==true)System.out.println("CipherText: " + output);
      if (fullTesting==true)System.out.println("CipherText Bit String: " + intToBits(output, TEXT_BITS));
      return output;
      }
      
// METHOD: A single round of encryption of a given bit string, given a 9-bit key and a round (0-3)
   public int encryptRound(String ptString, String key, int round){

      //Step 1: Split plaintext into L0 and R0
      String L0 = getLeftHalf(ptString);
      String R0 = getRightHalf(ptString);
      if(fullTesting==true)System.out.println("Left: " + L0 + "  Right: " + R0); // FOR TESTING OUTPUT
   
      //run round(s) 
         // First, get subkey for the round
      String subkey = getSubkey(key, round);
         // run round with given subkey, Lo, and Ro
      String result = runARound(L0, R0, subkey);
     
      int resultInt = 0;
      
      // Convert bit string into decimal integer
      try{resultInt = Integer.parseInt(result, 2);}
      catch(Exception e){System.out.println("ERROR: Unable to convert encrypted binary string to integer");}
      
      // return resulting integer
      return resultInt;
   }   

///=====SDES ALGORITHM METHODS - UNIVERSAL ENCRYPTION AND DECRYPTION ========
   //METHOD - Run a SDES round with given L, R and subkey
   public String runARound(String L, String R, String key){
      String L1 = R;
      
      // 1: Expand 6bit string to 8bit string
      String ExpandedR = expand(R);
      
      if(fullTesting==true)System.out.println("R0 = " + R); // for testing
      if(fullTesting==true)System.out.println("Expanded R0 = " + ExpandedR); // for testing
      
      // 2: XOR comparisong between expanded R and key
      String XOR_R = XORStrings(ExpandedR, key, key.length());
     if(fullTesting==true) System.out.println("XORED: " + XOR_R);
      
      // 3: Split into 4-bit Left and 4-Bit Right
      String L_4bit = getLeftHalf(XOR_R);
      String R_4bit = getRightHalf(XOR_R);
      if(fullTesting==true)System.out.println("Left: " + L_4bit + "  Right: " + R_4bit); // FOR TESTING OUTPUT
      
      // 4: SBox Replacement
      String L_postS = sbox(L_4bit, true);
      String R_postS = sbox(R_4bit, false);
      if(fullTesting==true)System.out.println("Left Sbox: " + L_postS + "  Right Sbox: " + R_postS); // FOR TESTING OUTPUT
      
      // 5: Rejoin 
      String nearly_R1 = L_postS;
      nearly_R1 += R_postS; 
      //System.out.println("Recombined: " + R1); // FOR TESTING OUTPUT
      
      // 6: XOR sbox result with L0
      String R1 = XORStrings(nearly_R1, L, L.length());
      
      String result = L1;
      result += R1;
      if(fullTesting==true)System.out.println("Round Result Bit String: " + result); // FOR TESTING OUTPUT
            
      return result;
   }
   
   // METHOD - CALCULATE the subkey given the key and round
   public String getSubkey(String key, int round){
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
      
      if(fullTesting==true)System.out.println("Subkey: " + subkey);
      return subkey;
   }
   
 //METHOD: Convert 4-bit number to a number in one of 2 s boxes 
   public String sbox(String x, boolean isFirstBox){
      try{
         int row = Integer.parseInt(String.valueOf(x.charAt(0))); 
         int col = Integer.parseInt(x.substring(1), 2);   
         
         if(fullTesting==true)System.out.println("ROW: " + row + "  COL: " + col); // FOR TESTING OUTPUT
         
         if(isFirstBox) // Use Sbox 1 for Left side bit
            return SBOX_1[row][col];
         else // Use Sbox 2 for Right side 4-bit
            return SBOX_2[row][col];
      }
      catch(Exception e){System.out.println("ERROR: Issue retrieving SBOX value"); return "000";} 
   }
   
   // METHOD: EXPAND function 012345 -> 01323245
   public String expand(String R){
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
      }
      return ex;
   }
   
   //BETHOD: given a bit string, cut in hald and return left half of bits as a new bit string
   public String getLeftHalf(String bits){
      int half = bits.length()/2;
      return bits.substring(0, half);
   }
   
   //METHOD: given a bit string, cut in half and return right hald of bits as a new bit string
   public String getRightHalf(String bits){
      int half = bits.length()/2;
      return bits.substring(half);
   }
   
   // METHOD: XOR two given string bits 
   public String XORStrings(String a, String b, int size){
      if(fullTesting==true)System.out.println("a: " + a + "  b: " + b); // FOR TESTING OUTPUT
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
   public String intToBits(int x, int bits){
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
   
   void PrintList(HashMap<Integer, List<Integer>> printer){
       // Check if map is empty
       if (printer.isEmpty())
       {
         System.out.println("No Matching Keys Found."); return;
       }
       // if not empty, print any/all key pairs in map
       Set<Integer> setOfKeySet = printer.keySet();
       for(Integer k1 : setOfKeySet){
       if(fullTesting)System.out.println("(" + k1 + ")");
        for(Integer k2 : printer.get(k1)){
            System.out.println("(" + k1 + ", " + k2 + ")");
            }
         }
    }
    
////================== SDES ALGORITHM METHODS END ===========================

// == CLASS: MITM OBJect for storing plaintext, ciphertext and hashmap keypairs
 class MITMObject{
    String plain;
    int plainint;
    String cipher;
    int cipherint;
    int round1key = -1;
    int round2key = -1;
    public HashMap<Integer, List<Integer>> keyMapPlain; // cipher -> k2 -> k1 -> plaintext
    public HashMap<Integer, List<Integer>> keyMapCipher; // plain -> k1 -> k2 -> cipher
   
   MITMObject(){}
   
   MITMObject(int p, int c){
      plainint = p;
      cipherint = c;
      String plain = intToBits(p, TEXT_BITS);
      String cipher = intToBits(c, TEXT_BITS);
      keyMapPlain = new HashMap(); 
      keyMapCipher = new HashMap();   
   }
   
   //===GET/SET===
    int GetKey(int num){
      if(num == 1)
         return round1key;
      else 
         return round2key;}
    void SetKey(int num, int k){
      if(num == 1)
         round1key = k;
      else 
         round2key = k;}
   
    String GetCipher(){return cipher;}
    String GetPlain(){return plain;}
    int GetPlainInt(){return plainint;}
    int GetCipherInt(){return cipherint;}
    int GetLength(){return cipher.length();}
    HashMap GetMapCipher(){return keyMapCipher;}
    HashMap GetMapPlain(){return keyMapPlain;}
    
    void PutMap(int key1, int key2, int result, boolean isCipher){
      if (isCipher){
         if(keyMapPlain.get(key2)==null){
            keyMapPlain.put(key2, new ArrayList<Integer>());}
         keyMapPlain.get(key2).add(key1);
         }
      else {
         if(keyMapCipher.get(key1)==null){
            keyMapCipher.put(key1, new ArrayList<Integer>());}
         keyMapCipher.get(key1).add(key2);
         }
    }
}

/// ===== UNUSED:: SDES ALGORITHM DECRYPT METHODS ==========
// METHOD:  UNUSED
/*
 void DecryptionList(MITMObject obj){
   int num = obj.GetCipherInt();
   for (int i = 0; i < Math.pow(2,KEY_BITS); i++){
      Decrypt(num, i, obj); // SDES
    }
    if (isTesting)PrintList(obj.GetMapPlain());
}// */

/*
public void Decrypt(int plain, int key, MITMObject obj){
   int output1 = SDESDecrypt(plain, key);
   int output2 = -1;
   int compValue = obj.GetPlainInt();
   for (int i = 0; i < Math.pow(2,KEY_BITS); i++){
      output2 = SDESDecrypt(output1, i); // Second round of SDES
      if(compValue == output2)
         obj.PutMap(key, i, output2, true);
   }
}
//METHOD: 
   public int SDESDecrypt(int plain, int key){
      String keyString =  intToBits(key, KEY_BITS);
      String plaintextString = intToBits(plain, TEXT_BITS);   
   
   // Run initial round to get first pass crypt text
      if(fullTesting==true)System.out.println("\n === BEGINNING ROUND: 3"); // FOR TESTING OUTPUT 
      int output = decryptRound(plaintextString, keyString, (ROUNDS-1));
      
      // Run remaining rounds using output to get final crypt text
      for (int round = (ROUNDS-2); round > -1; round--)
      {
         if(fullTesting==true)System.out.println("\n === BEGINNING ROUND: " + round); // FOR TESTING OUTPUT
         output = decryptRound(intToBits(output, TEXT_BITS), keyString, round);
      }
      
      if (fullTesting==true)System.out.println("Plaintext: " + output);
      if (fullTesting==true)System.out.println("Plaintext Bit String: " + intToBits(output, TEXT_BITS));
      return output;
   }

// METHOD: A single round of decryption of a given bit string, given a 9-bit key and a round (0-3)
   public int decryptRound(String ptString, String key, int round){

      //Step 1: Split  into L0 and R0
      String L0 = getLeftHalf(ptString);
      String R0 = getRightHalf(ptString);
      if(fullTesting==true)System.out.println("Left: " + L0 + "  Right: " + R0); // FOR TESTING OUTPUT
   
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
   } // */

}