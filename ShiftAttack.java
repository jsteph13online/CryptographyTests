/*
* Joscelyn Stephens
*
* CSC440 Winter 2022
* Assignment 1: Question 5
* 
*/

public class ShiftAttack {

public static void main(String[] args){

	
   
   //Create CipherText Object
   /*For this program, will use hard-coded default text, but could use a cstr with a string
   to set the cipher text to a different ciphertext*/
   CipherText cipher = new CipherText();
   System.out.println("Ciphertext received... beginning calculations: \n");
   CalculateLikelyLength(cipher);
	
}

public static void CalculateLikelyLength(CipherText c){
   System.out.println("==Likelihood of key length for each shift option==\n\n");
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
      System.out.println("Shift: " + shift + "  Coincidence Count: " + coincidenceCount);
      if (coincidenceCount > maxCoincidences){
         maxCoincidences = coincidenceCount;
         maxCoincidencesShift = shift;
      }
   }
   System.out.println("\nMost Likely Shift:");
   System.out.println("Shift: " + maxCoincidencesShift + "  Coincidence Count: " + maxCoincidences + "\n");
}

static class CipherText{
   String cipher;
   CipherText(){cipher = GetDefault();}
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
}
}