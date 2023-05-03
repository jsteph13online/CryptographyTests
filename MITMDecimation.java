/* Joscelyn Stephens
*
* CSC440 Winter 2022
* Assignment 5: Question 2: MITMDecimation
* 
*/

import java.util.ArrayList;
import java.util.List; 

public class MITMDecimation {

//POSSIBLE A VALUES AND A^-1 VALUES (mod 26) 
public final int[][] A_OPTIONS = new int[][]{{1,3,5,7,9,11,15,17,19,21,23,25}, {1,9,21,15,3,19,7,23,11,5,17,25}};
public final char[] ABC = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

public final String PLAINTEXT = "eavesdropper"; // plaintext used for assignment 5
public final String CIPHER = "carcwvpubbcp"; // ciphertext used for assignment 5
public MITMObject obj;

public boolean isTesting = false;

public static void main(String[] args){
   MITMDecimation mitmdecimation = new MITMDecimation();
   mitmdecimation.start();}

public void start(){
   
   System.out.println("Beginning MITM 2x Decimation Attack...");
   
   
   obj = new MITMObject(PLAINTEXT, CIPHER);

   System.out.println("Known Plaintext: " + obj.GetPlain());
   System.out.println("Known Ciphertext: " + obj.GetCipher());
	
   if(isTesting)System.out.println("\n==List of All Possible Encryptions==");
   EncryptionList();
   
   if(isTesting)System.out.println("\n==List of All Possible Decryptions==");
   DecryptionList();
   
   InitialCompare();
	
}

 void InitialCompare(){
  // if(isTesting)System.out.println("\nStarting Comparisons\n");
   // Grabbing the lists of possibilities
   String[][] plain = obj.GetList2(true); 
   String[][] cipher = obj.GetList2(false);
   // getting the length of the possibilities
   int len = cipher[0].length;
   if (plain[0].length < cipher[0].length)
      len = plain.length; // this shouldn't happen, it would be an issue, but just in case
      
   //INIT the list to hold the matches
   List<Pair> PlainKeys = new ArrayList<Pair>();
   List<Pair> CipherKeys = new ArrayList<Pair>();
   
   //IF TEXT AND A VALUE MATCHES, THERE IS A PAIR
   try{
      for(int i = 0; i< len; i++){
         if(plain[0][i].equals(CIPHER))
         {
            Pair p = new Pair(Integer.parseInt(plain[2][i]), Integer.parseInt(plain[1][i]));
            PlainKeys.add(p);
         }
      }
   }
   catch(Exception e){System.out.println("ERROR: Unable to complete comparison of CipherList");}
   
   //IF TEXT AND A VALUE MATCHES, THERE IS A PAIR
   try{
      for(int i = 0; i< len; i++){
         if(cipher[0][i].equals(PLAINTEXT))
         {
            Pair p = new Pair(Integer.parseInt(cipher[2][i]), Integer.parseInt(cipher[1][i]));
            CipherKeys.add(p);
         }
      }
   }
   catch(Exception e){System.out.println("ERROR: Unable to complete comparison of CipherList");}
   int temp = PlainKeys.size();
   if (CipherKeys.size() < temp) temp = CipherKeys.size();
   for(int i = 0; i < temp; i++){
      if(PlainKeys.get(i).getk1() != CipherKeys.get(i).getk2() && PlainKeys.get(i).getk2() != CipherKeys.get(i).getk1()){
         System.out.println("ERROR: Bad Match");
         System.out.println(PlainKeys.get(i).getk1() + " " +  CipherKeys.get(i).getk1() + " "+ PlainKeys.get(i).getk2() +" "+ CipherKeys.get(i).getk2());
      }
      else
         if(isTesting)System.out.println("Match! " + i);
   }
   
   PrintResults(PlainKeys, CipherKeys);
  
}

void PrintResults(List<Pair> plain, List<Pair> cipher)
{
   System.out.println("\n== Possible Keys and Encryption/Decryptions ==");
   System.out.println("Total Key Pair Possibilities: " + plain.size());
   System.out.println("Format: (First Round Key, Second Round Key)");
   for(int i = 0; i<plain.size(); i++){
      int k1 = plain.get(i).getk1();
      int k2 = plain.get(i).getk2();
      int ck1 = cipher.get(i).getk1();
      int ck2 = cipher.get(i).getk2();
      int enIndex = 0;
      int deIndex = 0;
      for(int j = 0; j < A_OPTIONS[0].length; j++){
         if(k1 == A_OPTIONS[0][j])
           { enIndex = j; }
         if(ck2 == A_OPTIONS[0][j])
           { deIndex = j; }
      }
      System.out.println("(" + k1 + ", " + k2 + ") : " +  PLAINTEXT + " -> " + obj.GetList(false)[enIndex] + " -> " + CIPHER + " -> " + obj.GetList(true)[deIndex] + " -> " + PLAINTEXT);

   }
}

// METHOD: CREATE LISTS FOR SINGLE AND SECOND ROUND OF ALL POSSIBLE CIPHERTEXTS FOR GIVEN PLAINTEXT
 void EncryptionList(){
   //y=ax
   //Create a list with all possible encryptions
   String[] possibilities = new String[12];
   // Go for all possible A values
   for(int i = 0; i < A_OPTIONS[0].length; i++){
      int a = A_OPTIONS[0][i];
      String cipher = "";
      for(int j = 0; j < obj.GetLength(); j++){
          cipher = cipher + Encrypt(obj.GetPlain(), j, a); 
      } 
      possibilities[i] = cipher; 
   }
   
   // FIRST ROUND OF POSSIBILITIES -> SAVED 
   obj.SetList(possibilities, false);
   
   if(isTesting){
      System.out.println("=== 1 Round of Encryption ===");
      for (int i = 0; i < possibilities.length; i++){
         System.out.println("A: " + A_OPTIONS[0][i] + "   Encrypted: " + obj.GetList(false)[i]);}
   }
   
   // DOUBLE ENCRYPTION
    //For Each possibility, there are 12 more possibilities
    String[][] r2 = new String[3][144];
    int counter = 0;
    for(int i = 0; i < possibilities.length; i++) // 1. FOR EACH OF ROUND 1 POSSIBILIEIES...
    {
      String start = possibilities[i];
      for(int k = 0; k < A_OPTIONS[0].length; k++) // 2. FOR EACH POSSIBLE A VALUE...
      {
         int a = A_OPTIONS[0][k];
         String cipher = "";
         for(int j = 0; j < start.length(); j++){
            cipher = cipher + Encrypt(start, j, a); // 3. ENCRYPT EACH LETTER
         } 
         r2[2][counter] = Integer.toString(A_OPTIONS[0][i]); // key 1 value
         r2[1][counter] = Integer.toString(a); // key 2 valu
         r2[0][counter] = cipher; 
         counter++;
      }
    }
    
    // SECOND ROUND OF POSSIBILITIES -> SAVED 
   obj.SetList2(r2, false);
    
    if(isTesting){
      System.out.println("=== 2 Rounds of Encryption ===");
      for (int i = 0; i < r2[0].length; i++){
         System.out.println("Count: " + i + " A1: " + obj.GetList2(false)[2][i] + "   A2: " + obj.GetList2(false)[1][i] + "   Encrypted: " + obj.GetList2(false)[0][i]);}
   }   
}

// METHOD: CREATE LISTS FOR SINGLE AND SECOND ROUND OF ALL POSSIBLE PLAINTEXTS FOR GIVEN CIPHER
 void DecryptionList(){
   //y=ax
   //Create a list with all possible encryptions
   String[] possibilities = new String[12];
   // Go for all possible A INVERSE values
   for(int i = 0; i < A_OPTIONS[1].length; i++){
      int inva = A_OPTIONS[1][i]; // inversion of a
      String plain = "";
      for(int j = 0; j < obj.GetLength(); j++){
          plain = plain + Encrypt(obj.GetCipher(), j, inva); 
      } 
      possibilities[i] = plain; 
   }
   
   // FIRST ROUND OF POSSIBILITIES -> SAVED 
   obj.SetList(possibilities, true);
   
   if(isTesting){
      System.out.println("=== 1 Round of Encryption ===");
      for (int i = 0; i < possibilities.length; i++){
         System.out.println("A: " + A_OPTIONS[0][i] + "   DECRYPTED: " + obj.GetList(true)[i]);}
   }
   
   // DOUBLE ENCRYPTION
    //For Each possibility, there are 12 more possibilities
    String[][] r2 = new String[3][144];
    int counter = 0;
    for(int i = 0; i < possibilities.length; i++) // 1. FOR EACH OF ROUND 1 POSSIBILIEIES...
    {
      String start = possibilities[i];
      for(int k = 0; k < A_OPTIONS[1].length; k++) // 2. FOR EACH POSSIBLE A VALUE...
      {
         int inva = A_OPTIONS[1][k]; // inversion of a
         String plain = "";
         for(int j = 0; j < start.length(); j++){
            plain = plain + Encrypt(start, j, inva); // 3. ENCRYPT EACH LETTER
         } 
         r2[2][counter] = Integer.toString(A_OPTIONS[1][i]); // key 1 value
         r2[1][counter] = Integer.toString(inva); // key 2 value
         r2[0][counter] = plain; // text
         counter++;
      }
    }
    
    // SECOND ROUND OF POSSIBILITIES -> SAVED 
   obj.SetList2(r2, true);
    
    if(isTesting){
      System.out.println("=== 2 Rounds of Encryption ===");
      for (int i = 0; i < r2[0].length; i++){
         System.out.println("Count: " + i + "  A1: " + obj.GetList2(true)[2][i] + "   A2: " + obj.GetList2(true)[1][i] + "   DECRYPTED: " + obj.GetList2(true)[0][i]);}
   }
   
}



// HELPER: ENCRYPT FOR SINGLE CHAR - RETURN AS STRING
 String Encrypt(String plain, int index, int a){
   //y = ax
   char xChar = plain.charAt(index);
   int x = 0;
   for(int i = 0; i<ABC.length; i++){
      if(xChar == ABC[i]){
         x = i;
         break;
      }   
   }
   int value = (a*x)%26;
   // ERROR message if resulting value is not a valid ABC value
   if(value > 25){System.out.println("ERROR: ENCRYPTION ERROR AT " + index); return "a";}
   
   // return resulting char as a string value
   char resultChar = ABC[value];
   return Character.toString(resultChar);
   
}

// HELPER: DECRYPT FOR SINGLE CHAR - RETURN AS STRING
 String Decrypt(String cipher, int index, int inva){
   // x = a^-1 * y
   char yChar = cipher.charAt(index);
   int y = 0;
   for(int i = 0; i<ABC.length; i++){
      if(yChar == ABC[i]){
         y = i;
         break;
      }   
   }
   int value = (inva*y)%26;
   // ERROR message if resulting value is not a valid ABC value
   if(value > 25){System.out.println("ERROR: DECRYPTION ERROR AT " + index); return "a";}
   
   // return resulting char as a string value
   char resultChar = ABC[value];
   return Character.toString(resultChar);
   
}


 class MITMObject{
    String plain;
    String cipher;
    int round1key = -1;
    int round2key = -1;
    String[] round1plain = new String[12];
    String[][] round2plain = new String[3][144];
    String[] round1cipher = new String[12];
    String[][] round2cipher = new String[3][144];
   
   MITMObject(){}
   
   MITMObject(String p, String c){
      plain = p.toLowerCase();
      cipher = c.toLowerCase();     
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
    int GetLength(){return cipher.length();}
   
    String[] GetList(boolean isCipher){
      if (!isCipher)
         return round1plain;
      else
         return round1cipher;}
    void SetList(String[] sList, boolean isCipher){
      if (!isCipher)
         round1plain = sList;
      else 
         round1cipher = sList;}  
    String[][] GetList2(boolean isCipher){
      if (!isCipher)
         return round2plain;
      else
         return round2cipher;}
    void SetList2(String[][] sList, boolean isCipher){
      if (!isCipher)
         round2plain = sList;
      else 
         round2cipher = sList;}     
}

public class Pair{
      int key1;
      int key2;
      Pair (int k1, int k2){key1 = k1; key2 = k2;};
      public int getk1(){return key1;}
      public int getk2(){return key2;}
      }
}
