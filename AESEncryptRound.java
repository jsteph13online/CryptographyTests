/* Joscelyn Stephens
*
* CSC440 Winter 2022
* Final Part F: AES Encrypt Round
* 
*/

public class AESEncryptRound {


// hard coded values
public final String PLAINTEXT_DEFAULT= "0x3243f6a8885a308d313198a2e0370734"; // 32-digit hex key
public final String KEY_DEFAULT = "0x2b7e151628aed2a6abf7158809cf4f3c"; // 32 digit hex key
public final int[][] MC_MULT = {{2, 3, 1, 1}, {1, 2, 3, 1}, {1, 1, 2, 3}, {3, 1, 1, 2}};

public String[] cipherStr = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}; // used only for setup
public String[] keyStr = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}; // used only for setup
public String[][] cipher = {{"", "", "", ""}, {"", "", "", ""}, {"", "", "", ""},  {"", "", "", ""}};


public static void main(String[] args){
   AESEncryptRound round = new AESEncryptRound();
   round.start(args);}

public void start(String[] args){
   // 0. Convert plaintext string into 2digit hex output format
   ConvertStringToArray(PLAINTEXT_DEFAULT, cipherStr);
   ConvertStringToArray(KEY_DEFAULT, keyStr);
   System.out.println("Original Plaintext");
   PrintStrArray(cipherStr);
      
   // 1. Perform a bitwise XOR with the plaintext and the key
   XORStep(cipherStr, keyStr);
   // == print state
   System.out.println("After ARK with Original Key");
   PrintStr(cipher);
   
   // 2. applying the byte substitution for each byte in the state
   BSStep(cipher);
   // == print state
   System.out.println("After SB");
   PrintStr(cipher);
   
   // 3. apply shift rose on the state table rows
   ShiftRows(cipher);
   // == print state
   System.out.println("After SR");
   PrintStr(cipher);
   
   // 4. applying the mix collumns on the state table collumns
   MixCol(cipher);
   // == print state
   System.out.println("After MC");
   PrintStr(cipher);
}

// STEP 0: Get original plaintext in byte format for output (and key)
public void ConvertStringToArray(String s, String[] array){
   String str = s.replaceAll("^0x", ""); // remove preceeding 0x   
   for (int i = 0; i < cipherStr.length; i++) {
      int ind = i * 2;
      String k = str.substring(ind, ind+2);
      String kbin = HexToBinary(k);
      array[i] = kbin;
   }  
}
// =========================================================

// === STEP 1 : XOR with KEY ====
public void XORStep(String[] a, String[] b){
   for(int i = 0; i<a.length; i++){
      String result = XORHelper(a[i], b[i]);
      a[i] = result;
   }
   OrganizeRows(a);
}

public String XORHelper(String a, String b){
       String result = "";
       for (int i = 0; i < a.length(); i++)
        {
            // If the Character matches
            if (a.charAt(i) == b.charAt(i))
                result += "0";
            else
                result += "1";
        }
        return result;
}

// change cipher format to be in row/col format
public void OrganizeRows(String[] s){
   String[][] result = {
      {s[0], s[4], s[8], s[12]}, 
      {s[1], s[5], s[9], s[13]}, 
      {s[2], s[6], s[10], s[14]},  
      {s[3], s[7], s[11], s[15]}};
   cipher = result;
}
// ==========================================

// ==== Step 2: Byte Substitute Step ====

public void BSStep(String[][] a){
   for(int i = 0; i < a.length; i++){
      for(int j = 0; j < a[i].length; j++){
         String left = GetLeftHalf(a[i][j]);
         String right = GetRightHalf(a[i][j]);
         String result = ByteSub(left, right);
         String resultBin = HexToBinary(result);
         a[i][j] = resultBin;
   }}
}

// =======================================

// ==== Step 3: Shift Rows Step ====
public void ShiftRows(String[][] a){
// Skip row 0, as it shifts by 0
for(int i = 1; i < a.length; i++){
   String temp0 = a[i][0];
   String temp1 = a[i][1];
   String temp2 = a[i][2];
   String temp3 = a[i][3];
   switch(i){
      case 1:
         a[i][0] = temp1;
         a[i][1] = temp2;
         a[i][2] = temp3;
         a[i][3] = temp0;  
         break;
      case 2:
         a[i][0] = temp2;
         a[i][1] = temp3;
         a[i][2] = temp0;
         a[i][3] = temp1;
         break;
      default:
         a[i][0] = temp3;
         a[i][1] = temp0;
         a[i][2] = temp1;
         a[i][3] = temp2;
         break;
   }   }
}
// ======================================

// ==== Step 4: Mix Col Step ====
public void MixCol(String[][] a){
   for(int c = 0; c<a[0].length; c++){ // for every column
     // init array where results will be saved
     String[] result = {"", "", "", ""};
     for(int r = 0; r<a.length; r++){ // for every row in the column
         // init the strings that will be part of the matrix multiplication for the row
         String s0 = a[0][c];
         String s1 = a[1][c];
         String s2 = a[2][c];
         String s3 = a[3][c];
         if(MC_MULT[r][0]>1){ // need to alter s value if multiplier value is 2 or 3
            s0 = MixColMultiply(s0, MC_MULT[r][0]);
         }
         if(MC_MULT[r][1]>1){ // need to alter s value if multiplier value is 2 or 3
            s1 = MixColMultiply(s1, MC_MULT[r][1]);
         }
         if(MC_MULT[r][2]>1){ // need to alter s value if multiplier value is 2 or 3
            s2 = MixColMultiply(s2, MC_MULT[r][2]);
         }
         if(MC_MULT[r][3]>1){ // need to alter s value if multiplier value is 2 or 3
            s3 = MixColMultiply(s3, MC_MULT[r][3]);
         }
         
         // XOR the resulting strings to get the value for this col/row after the mixcol
         String xored = XORHelper(s0, s1);
         xored = XORHelper(xored, s2);
         xored = XORHelper(xored, s3);
         result[r] = xored;
               } 
      // save results in the column 
      for(int i = 0; i<result.length; i++){
         a[i][c] = result[i];
      }
   }   
}

// get x2 or x3 string value by looking up in the tables
public String MixColMultiply(String s, int m){
   String result = "";
   if(m == 2)
      result = MultiplyBy2(s);
   else
      result = MultiplyBy3(s);
   String resultBin = HexToBinary(result);
   return resultBin;
}
// ======================================

// convert 8-bit binary string to 2-digit hex string
public String BinaryToHex(String s){
   int i = Integer.parseInt(s,2);
   String hex = Integer.toString(i,16);
   if(hex.length()<2){
      String hex2 = "0" + hex; // ensure that there are leading 0s if only 1 digit
      return hex2;
   }
   return hex;
}

// convert 2 digit hex string to 8-bit binary string
public String HexToBinary(String s){
    int i = Integer.parseInt(s,16); 
    String bin = Integer.toBinaryString(0x100 | i).substring(1); // ensures 0s dont get chopped
    return bin;
}

// Print the Bytes first time
public void PrintStrArray(String[] array){
   System.out.println(BinaryToHex(array[0]) + " " + BinaryToHex(array[4]) + " " + BinaryToHex(array[8]) + " " + BinaryToHex(array[12]));
   System.out.println(BinaryToHex(array[1]) + " " + BinaryToHex(array[5]) + " " + BinaryToHex(array[9]) + " " + BinaryToHex(array[13]));
   System.out.println(BinaryToHex(array[2]) + " " + BinaryToHex(array[6]) + " " + BinaryToHex(array[10]) + " " + BinaryToHex(array[14]));
   System.out.println(BinaryToHex(array[3]) + " " + BinaryToHex(array[7]) + " " + BinaryToHex(array[11]) + " " + BinaryToHex(array[15]) + "\n");
}

// Print the cipher row/col (Main print function)
public void PrintStr(String[][] arr){
   for(int i = 0; i < arr.length; i++){
      for(int j = 0; j < arr[i].length; j++){
         System.out.print(BinaryToHex(arr[i][j]) + " ");
      }
      System.out.print("\n");
   }
   System.out.println("");
}

// Method: Get left 4 bits of binary 8 bit string (ROW)
public String GetLeftHalf(String bits){
      int half = bits.length()/2;
      return bits.substring(0, half);
   }
   
   //METHOD: Get right 4 bits of binary 8 bit string (COL)
public String GetRightHalf(String bits){
      int half = bits.length()/2;
      return bits.substring(half);
   }

// AES tables from AESTables.txt 
// ith entry is the value of applying the given operation to i
public String MultiplyBy2(String s){
      final String[] by2 = {
      "0x00", "0x02", "0x04", "0x06", "0x08", "0x0a", "0x0c", "0x0e", "0x10", "0x12", "0x14", "0x16", "0x18", "0x1a", "0x1c", "0x1e",
      "0x20", "0x22", "0x24", "0x26", "0x28", "0x2a", "0x2c", "0x2e", "0x30", "0x32", "0x34", "0x36", "0x38", "0x3a", "0x3c", "0x3e", 
      "0x40", "0x42", "0x44", "0x46", "0x48", "0x4a", "0x4c", "0x4e", "0x50", "0x52", "0x54", "0x56", "0x58", "0x5a", "0x5c", "0x5e", 
      "0x60", "0x62", "0x64", "0x66", "0x68", "0x6a", "0x6c", "0x6e", "0x70", "0x72", "0x74", "0x76", "0x78", "0x7a", "0x7c", "0x7e", 
      "0x80", "0x82", "0x84", "0x86", "0x88", "0x8a", "0x8c", "0x8e", "0x90", "0x92", "0x94", "0x96", "0x98", "0x9a", "0x9c", "0x9e", 
      "0xa0", "0xa2", "0xa4", "0xa6", "0xa8", "0xaa", "0xac", "0xae", "0xb0", "0xb2", "0xb4", "0xb6", "0xb8", "0xba", "0xbc", "0xbe", 
      "0xc0", "0xc2", "0xc4", "0xc6", "0xc8", "0xca", "0xcc", "0xce", "0xd0", "0xd2", "0xd4", "0xd6", "0xd8", "0xda", "0xdc", "0xde", 
      "0xe0", "0xe2", "0xe4", "0xe6", "0xe8", "0xea", "0xec", "0xee", "0xf0", "0xf2", "0xf4", "0xf6", "0xf8", "0xfa", "0xfc", "0xfe", 
      "0x1b", "0x19", "0x1f", "0x1d", "0x13", "0x11", "0x17", "0x15", "0x0b", "0x09", "0x0f", "0x0d", "0x03", "0x01", "0x07", "0x05", 
      "0x3b", "0x39", "0x3f", "0x3d", "0x33", "0x31", "0x37", "0x35", "0x2b", "0x29", "0x2f", "0x2d", "0x23", "0x21", "0x27", "0x25", 
      "0x5b", "0x59", "0x5f", "0x5d", "0x53", "0x51", "0x57", "0x55", "0x4b", "0x49", "0x4f", "0x4d", "0x43", "0x41", "0x47", "0x45", 
      "0x7b", "0x79", "0x7f", "0x7d", "0x73", "0x71", "0x77", "0x75", "0x6b", "0x69", "0x6f", "0x6d", "0x63", "0x61", "0x67", "0x65", 
      "0x9b", "0x99", "0x9f", "0x9d", "0x93", "0x91", "0x97", "0x95", "0x8b", "0x89", "0x8f", "0x8d", "0x83", "0x81", "0x87", "0x85", 
      "0xbb", "0xb9", "0xbf", "0xbd", "0xb3", "0xb1", "0xb7", "0xb5", "0xab", "0xa9", "0xaf", "0xad", "0xa3", "0xa1", "0xa7", "0xa5",
      "0xdb", "0xd9", "0xdf", "0xdd", "0xd3", "0xd1", "0xd7", "0xd5", "0xcb", "0xc9", "0xcf", "0xcd", "0xc3", "0xc1", "0xc7", "0xc5", 
      "0xfb", "0xf9", "0xff", "0xfd", "0xf3", "0xf1", "0xf7", "0xf5", "0xeb", "0xe9", "0xef", "0xed", "0xe3", "0xe1", "0xe7", "0xe5"};
      
      int num=Integer.parseInt(s,2);  
      
      String result = by2[num];
      String str = result.replaceAll("^0x", ""); // remove preceeding 0x 
      
      return str;
}

public String MultiplyBy3(String s){    
      final String[] by3 = {
      "0x00", "0x03", "0x06", "0x05", "0x0c", "0x0f", "0x0a", "0x09", "0x18", "0x1b", "0x1e", "0x1d", "0x14", "0x17", "0x12", "0x11", 
      "0x30", "0x33", "0x36", "0x35", "0x3c", "0x3f", "0x3a", "0x39", "0x28", "0x2b", "0x2e", "0x2d", "0x24", "0x27", "0x22", "0x21", 
      "0x60", "0x63", "0x66", "0x65", "0x6c", "0x6f", "0x6a", "0x69", "0x78", "0x7b", "0x7e", "0x7d", "0x74", "0x77", "0x72", "0x71", 
      "0x50", "0x53", "0x56", "0x55", "0x5c", "0x5f", "0x5a", "0x59", "0x48", "0x4b", "0x4e", "0x4d", "0x44", "0x47", "0x42", "0x41", 
      "0xc0", "0xc3", "0xc6", "0xc5", "0xcc", "0xcf", "0xca", "0xc9", "0xd8", "0xdb", "0xde", "0xdd", "0xd4", "0xd7", "0xd2", "0xd1", 
      "0xf0", "0xf3", "0xf6", "0xf5", "0xfc", "0xff", "0xfa", "0xf9", "0xe8", "0xeb", "0xee", "0xed", "0xe4", "0xe7", "0xe2", "0xe1", 
      "0xa0", "0xa3", "0xa6", "0xa5", "0xac", "0xaf", "0xaa", "0xa9", "0xb8", "0xbb", "0xbe", "0xbd", "0xb4", "0xb7", "0xb2", "0xb1", 
      "0x90", "0x93", "0x96", "0x95", "0x9c", "0x9f", "0x9a", "0x99", "0x88", "0x8b", "0x8e", "0x8d", "0x84", "0x87", "0x82", "0x81", 
      "0x9b", "0x98", "0x9d", "0x9e", "0x97", "0x94", "0x91", "0x92", "0x83", "0x80", "0x85", "0x86", "0x8f", "0x8c", "0x89", "0x8a", 
      "0xab", "0xa8", "0xad", "0xae", "0xa7", "0xa4", "0xa1", "0xa2", "0xb3", "0xb0", "0xb5", "0xb6", "0xbf", "0xbc", "0xb9", "0xba", 
      "0xfb", "0xf8", "0xfd", "0xfe", "0xf7", "0xf4", "0xf1", "0xf2", "0xe3", "0xe0", "0xe5", "0xe6", "0xef", "0xec", "0xe9", "0xea", 
      "0xcb", "0xc8", "0xcd", "0xce", "0xc7", "0xc4", "0xc1", "0xc2", "0xd3", "0xd0", "0xd5", "0xd6", "0xdf", "0xdc", "0xd9", "0xda", 
      "0x5b", "0x58", "0x5d", "0x5e", "0x57", "0x54", "0x51", "0x52", "0x43", "0x40", "0x45", "0x46", "0x4f", "0x4c", "0x49", "0x4a", 
      "0x6b", "0x68", "0x6d", "0x6e", "0x67", "0x64", "0x61", "0x62", "0x73", "0x70", "0x75", "0x76", "0x7f", "0x7c", "0x79", "0x7a", 
      "0x3b", "0x38", "0x3d", "0x3e", "0x37", "0x34", "0x31", "0x32", "0x23", "0x20", "0x25", "0x26", "0x2f", "0x2c", "0x29", "0x2a", 
      "0x0b", "0x08", "0x0d", "0x0e", "0x07", "0x04", "0x01", "0x02", "0x13", "0x10", "0x15", "0x16", "0x1f", "0x1c", "0x19", "0x1a"};
      
      int num=Integer.parseInt(s,2);  
      
      String result = by3[num];
      String str = result.replaceAll("^0x", ""); // remove preceeding 0x 
      
      return str;
}

public String ByteSub(String l, String r){ 
   final String[][] sub = { 
      {"0x63", "0x7c", "0x77", "0x7b", "0xf2", "0x6b", "0x6f", "0xc5", "0x30", "0x01", "0x67", "0x2b", "0xfe", "0xd7", "0xab", "0x76"}, 
      {"0xca", "0x82", "0xc9", "0x7d", "0xfa", "0x59", "0x47", "0xf0", "0xad", "0xd4", "0xa2", "0xaf", "0x9c", "0xa4", "0x72", "0xc0"}, 
      {"0xb7", "0xfd", "0x93", "0x26", "0x36", "0x3f", "0xf7", "0xcc", "0x34", "0xa5", "0xe5", "0xf1", "0x71", "0xd8", "0x31", "0x15"}, 
      {"0x04", "0xc7", "0x23", "0xc3", "0x18", "0x96", "0x05", "0x9a", "0x07", "0x12", "0x80", "0xe2", "0xeb", "0x27", "0xb2", "0x75"}, 
      {"0x09", "0x83", "0x2c", "0x1a", "0x1b", "0x6e", "0x5a", "0xa0", "0x52", "0x3b", "0xd6", "0xb3", "0x29", "0xe3", "0x2f", "0x84"}, 
      {"0x53", "0xd1", "0x00", "0xed", "0x20", "0xfc", "0xb1", "0x5b", "0x6a", "0xcb", "0xbe", "0x39", "0x4a", "0x4c", "0x58", "0xcf"}, 
      {"0xd0", "0xef", "0xaa", "0xfb", "0x43", "0x4d", "0x33", "0x85", "0x45", "0xf9", "0x02", "0x7f", "0x50", "0x3c", "0x9f", "0xa8"}, 
      {"0x51", "0xa3", "0x40", "0x8f", "0x92", "0x9d", "0x38", "0xf5", "0xbc", "0xb6", "0xda", "0x21", "0x10", "0xff", "0xf3", "0xd2"}, 
      {"0xcd", "0x0c", "0x13", "0xec", "0x5f", "0x97", "0x44", "0x17", "0xc4", "0xa7", "0x7e", "0x3d", "0x64", "0x5d", "0x19", "0x73"}, 
      {"0x60", "0x81", "0x4f", "0xdc", "0x22", "0x2a", "0x90", "0x88", "0x46", "0xee", "0xb8", "0x14", "0xde", "0x5e", "0x0b", "0xdb"}, 
      {"0xe0", "0x32", "0x3a", "0x0a", "0x49", "0x06", "0x24", "0x5c", "0xc2", "0xd3", "0xac", "0x62", "0x91", "0x95", "0xe4", "0x79"}, 
      {"0xe7", "0xc8", "0x37", "0x6d", "0x8d", "0xd5", "0x4e", "0xa9", "0x6c", "0x56", "0xf4", "0xea", "0x65", "0x7a", "0xae", "0x08"}, 
      {"0xba", "0x78", "0x25", "0x2e", "0x1c", "0xa6", "0xb4", "0xc6", "0xe8", "0xdd", "0x74", "0x1f", "0x4b", "0xbd", "0x8b", "0x8a"}, 
      {"0x70", "0x3e", "0xb5", "0x66", "0x48", "0x03", "0xf6", "0x0e", "0x61", "0x35", "0x57", "0xb9", "0x86", "0xc1", "0x1d", "0x9e"},
      {"0xe1", "0xf8", "0x98", "0x11", "0x69", "0xd9", "0x8e", "0x94", "0x9b", "0x1e", "0x87", "0xe9", "0xce", "0x55", "0x28", "0xdf"}, 
      {"0x8c", "0xa1", "0x89", "0x0d", "0xbf", "0xe6", "0x42", "0x68", "0x41", "0x99", "0x2d", "0x0f", "0xb0", "0x54", "0xbb", "0x16"}};
      
      int row = Integer.parseInt(l, 2); // left string -> row
      int col = Integer.parseInt(r, 2); // right string -> col
      
      String s = sub[row][col];
      String str = s.replaceAll("^0x", ""); // remove preceeding 0x 
      
      return str;
}

}