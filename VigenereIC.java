/*
* Joscelyn Stephens
*
* CSC440 Winter 2022
* Assignment 3: Question 1
* 
*/

public class VigenereIC {

public static void main(String[] args){

	
   
   //Create CipherText Object
   /*For this program, will use hard-coded default text, but could use a cstr with a string
   to set the cipher text to a different ciphertext*/
   CipherText cipher = new CipherText();
   System.out.println("Ciphertext received... beginning calculations: \n");
   
   // set length to max coincidences key length
   int length = CalculateLikelyLength(cipher);
   System.out.println("Key Length: " + length);
  
   String key = CalculateLikelyKey(cipher, length);
   System.out.println("Probable Key: " + key);
   
   
   
	
}

/*Calcualte likely key based on cipher and key length*/
public static String CalculateLikelyKey(CipherText c, int len){
     
   // init key based on key length
   char[] key = new char[len];
   for(int i = 0; i<len; i++)
      key[i] = 'A'; // init to some value (for testing?)
   
   int cipherLength = c.GetText().length();
   
   // FOR LOOP: for each letter in the key
   for(int i = 0; i<len; i++){
      // init values we will save max frequency to later on
      int maxIndex = 0;
      double maxValue = 0;
      
      // init letter frequency counter
      double[] letterFrequency = new double[26];
      for(int j = 0; j<26; j++)
         letterFrequency[j] = 0;
      
      // count letters in occuring in this instance
      for(int j = i; j < cipherLength; j = j+len){
         // Count number of each letter, increment counting vector
         char letter = c.GetText().charAt(j); 
         int letterIdx = CharToInt(letter); 
         letterFrequency[letterIdx]++;}
       
       /*TESTING == PRINT
       for(int j = 0; j<26; j++)
         System.out.print(letterFrequency[j] + " ,");         
       System.out.println();
       =*/
       
       // Divice by total numbers to get frequency spread
       for(int k = 0; k<26; k++){
              double newValue = letterFrequency[k];
              letterFrequency[k] = newValue/cipherLength;
       }
         // Get dot frequency based on the Alphabetic vector
         // init dotProd array
         double[] dotProd = new double[26];
         for(int k = 0; k<26; k++)
            dotProd[k] = 0;
         for(int k = 0; k<26; k++){
            double[] V = shiftABC(k);
            double result = 0;
            for(int d = 0; d < 26; d++){
               result += V[d] * letterFrequency[d]; 
            }
           dotProd[k]=result;
           
           // keep track of the max value and its index we're saving as we go
           if(result > maxValue)
           {   maxValue = result;
               maxIndex = k;}
        } 
        // Finished a key loop -> now let's get the largest max value!      
        char keyValue = IntToChar(maxIndex);
        key[i] = keyValue;
        maxValue = 0;
        maxIndex = 0;
      }          
   String keyString = new String(key);
   return keyString;
}

/*Calculate Length of Key
  Based on Coincidences Found
*/
public static int CalculateLikelyLength(CipherText c){
   //System.out.println("==Likelihood of key length for each shift option==\n\n");
   int maxCoincidences = 0;
   int maxCoincidencesShift = 1;
   int cipherLength = c.GetText().length();
   for(int shift = 1; shift < 15; shift ++){
      int coincidenceCount = 0;
      for(int index = 0; index < (cipherLength-1); index++){
         int shiftedIndex = (index + shift) % cipherLength;
         if(c.GetText().charAt(index) == (c.GetText().charAt(shiftedIndex)))
         {
            coincidenceCount++;
         }
      }
     // System.out.println("Shift: " + shift + "  Coincidence Count: " + coincidenceCount);
      if (coincidenceCount > maxCoincidences){
         maxCoincidences = coincidenceCount;
         maxCoincidencesShift = shift;
      }
   }
   //System.out.println("\nMost Likely Shift:");
   //System.out.println("Shift: " + maxCoincidencesShift + "  Coincidence Count: " + maxCoincidences + "\n");
   return maxCoincidencesShift;
}


static class CipherText{
   String cipher;
   CipherText(){cipher = GetDefault();}
   //CipherText(){cipher = GetTextbook();}
   CipherText(String S){cipher = S;}
   String GetText(){return cipher;}
   String GetDefault(){
      return "GCMKGKRDBYTZLRORMTZLROGFEXIKCORSNSDZHFAZWLNCLIZIMZFWOTIPJIJOBIVQSPYURVTCSKTSNZJC"+
      "RESEZMMUKZVMVCAXHFASTEXCYPAYNHIZIUUHUIMZFUAYPZQSBOAXKCSGRRMMZGRHKEXBFCGGXVJTMUXN"+
      "FTOLDYYWASPITKKCCSSGRUNCDCURWDRCNZVVGWEIURJDRCORSXDSQATHVXCLOSMTYCATXMEZGCVKVPCI"+
      "LTKVRIRDOXEXZFCVKVPCSPOGRUXCUAXHVQSPYIVVVHMRGRUYSQTXSPZFMFIMMDZGZGXZJBCVKVPFWLGG"+
      "RUKSYSGRKZJCRECFPBECUYGGSGNRSMZSTEXCDJHFEXEEYTYTNIICCNELYCXVGLJMEQSLTUVRIRCXVPFM"+
      "SPEBIITHCAILVMCDMUVRGGCVKVPXCPRATKKCJIZMTDOLEBIITGSPKVJOOPEBIITGSPXIDZZCAJIIZJCR"+
      "EWRDBRATHJDBLEXMEOVCHOWKJFWOLSLMGNEIMVNZGVKHKCSPEURRHCREUJUPGRSAWGZBBEJMEVGSNHIR"+
      "HHFEKEIOVGSGZVMMQMGPCNHYGKMEVJYSZGFNAGCGVVIORHORBJTRHKVZQSPSUJSGCMDYTZGZCDHCRGZR"+
      "HUWVBSLEXECNOLDKQGZFMRYWFOVYTORXGCPYGRUOFGUSTYOVCYISLGRZEISDZHFESSDZBRAXCDVGREXW"+
      "FAODRGGKDCLOLEUJHRHORBJTRHKIEYZCSYGIPSJTOIJQWQIZIUWMRHKMECOZIZEEOGMFURVXCPNKVFAH"+
      "FIYTZSSJOTXYZGAAXGVGMBIYXZIUSIYLRWZCITLRWWRATXJJTQOSIFOVCRISIISPHUAWMSOUKRKOVCIX"+
      "QZNILDKVJOOLDORXNVMWKEXZFRHKCRMSROQMCGCLEGRFOVCRNSNASPVKRKOVCIXLROFCDY";
   }
   
   // for testing
   String GetTextbook(){
   return "VVHQWVVRHMUSGJGTHKIHTSSEJCHLSFCBGVWCRLRYQTFSVGAHW" +
          "KCUHWAUGLQHNSLRLJSHBLTSPISPRDXLJSVEEGHLQWKASSKUWE" +
          "PWQTWVSPGOELKCQYFNSVWLJSNIQKGNRGYBWLWGOVIOKHKAZKQ" +
         "KXZGYHCECMEIUJOQKWFWVEFQHKIJRCLRLKBIENQFRJLJSDHGR" +
         "HLSFQTWLAUQRHWDMWLGUSGIKKFLRYVCWVSPGPMLKASSJVOQXE" +
         "GGVEYGGZMLJCXXLJSVPAIVWIKVRDRYGFRJLJSLVEGGVEYGGEI" +
         "APUUISFPBTGNWWMUCZRVTWGLRWUGUMNCZVILE";
   }
}

// HELPER METHOD: Get index for ABC vector based on char
static int CharToInt(char c){
char t = Character.toUpperCase(c);
switch (t){
   case 'A':
      return 0; 
   case 'B':
      return 1;
   case 'C':
      return 2;
   case 'D':
      return 3; 
   case 'E':
      return 4; 
   case 'F':
      return 5; 
   case 'G':
      return 6; 
   case 'H':
      return 7; 
   case 'I':
      return 8; 
   case 'J':
      return 9; 
   case 'K':
      return 10; 
   case 'L':
      return 11; 
   case 'M':
      return 12; 
   case 'N':
      return 13; 
   case 'O':
      return 14; 
   case 'P':
      return 15; 
   case 'Q':
      return 16; 
   case 'R':
      return 17; 
   case 'S':
      return 18; 
   case 'T':
      return 19; 
   case 'U':
      return 20; 
   case 'V':
      return 21; 
   case 'W':
      return 22; 
   case 'X':
      return 23; 
   case 'Y':
      return 24; 
   default:
      return 25; 
   }
}

// HELPER METHOD: Get ABC based on vector char
static char IntToChar(int i){
switch (i){
   case 0:
      return 'a'; 
   case 1:
      return 'b'; 
   case 2:
      return 'c'; 
   case 3:
      return 'd'; 
   case 4:
      return 'e'; 
   case 5:
      return 'f'; 
   case 6:
      return 'g'; 
   case 7:
      return 'h'; 
   case 8:
      return 'i'; 
   case 9:
      return 'j'; 
   case 10:
      return 'k'; 
   case 11:
      return 'l'; 
   case 12:
      return 'm'; 
   case 13:
      return 'n'; 
   case 14:
      return 'o'; 
   case 15:
      return 'p'; 
   case 16:
      return 'q'; 
   case 17:
      return 'r'; 
   case 18:
      return 's'; 
   case 19:
      return 't'; 
   case 20:
      return 'u'; 
   case 21:
      return 'v'; 
   case 22:
      return 'w'; 
   case 23:
      return 'x'; 
   case 24:
      return 'y'; 
   default:
      return 'z'; 
   }
}

static double[] shiftABC(int shift){
// vector for english language frequencies
   double[] EnglishFrequencies = {0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.020, 0.061, 0.070, 0.002, 0.008, 0.040, 0.024, 0.067, 0.075, 0.019, 0.001, 0.060, 0.063, 0.091, 0.028, 0.010, 0.023, 0.001, 0.020, 0.001};
   double[] shifted = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   //get Mod of shift just in case is too big
   int s = shift % 26;
   // if shift is 0, return straight ABC array
   if (s == 0)
      return EnglishFrequencies;
   // shift modulo 26 and move value into new vector   
   for (int i = 0; i<26; i++)
   {
       int newIndex = (i + s) % 26;
       shifted[newIndex] = EnglishFrequencies[i];
   }
   return shifted;
}

}