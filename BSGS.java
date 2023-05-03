/* Joscelyn Stephens
*
* CSC440 Winter 2022
* Final Part E: Discrete Logs Baby Step Giant Step
* 
*/

import java.lang.Math;
import java.math.BigInteger; 

public class BSGS {

// hard-coded values
public long p = 3411390473L;
public final long alpha = 3L;
public final long beta = 1788512386L;

public boolean isTesting = false; // extra information, including a maths double check after X calculation

public static void main(String[] args){
   BSGS step = new BSGS();
   step.start(args);}

public void start(String[] args){
   // beta = alpha ^ x (mod p) -> goal: find x
   System.out.println("Commencing Baby Step, Giant Step Calculation Given Values:");
   System.out.println("p = " + p + "   alpha = " + alpha + "   beta = " + beta);
   System.out.println("beta = alpha ^ x (mod p)\n\nBeginning Calculations...\n");
   // find int m, where m = floor of sqr (p-1)
   int m = (int) (Math.sqrt (p-1) +1);
   if(isTesting)System.out.println("M is" + m);
   
   //init list to store possible values for the baby steps list
   // calculate alpha ^ j (mod p)for 0 <= j < m
   long[] babyList = new long[m];
   
   // calculate baby steps list
   for(int i = 0; i<m; i++){
      long val = PowerCalc(i);
      babyList[i] = val;
   } 
   
   // calculate giant steps and compare
   // beta * alpha ^ (-m*i) (mod p) for o <= i < m
   long result = 0;
   int k = 0;
   int j = 0;
   boolean isMatch = false;
   while (!isMatch){
      for(int i = 0; i<m; i++){
         int pow = (-m * i);
         long val = PowerCalc(pow);
         result =(long) (beta * val);
         result = result % p;
         for (int f=0; f<m; f++) {
            if(babyList[f] == result){
               isMatch = true;
               if(isTesting)System.out.println("Match Found!");
               k = i;
               j = f;
               PrintResults(k, j, m);
               return;  
               }
         }  
      } 
      System.out.println("Error: No Match Found");
      return;
   }
   
   

} 

public void PrintResults(int k, int j, int m){
   // find x using match
   // x = j + mk
   long x = (long) (j + m*k);
   System.out.println("X = " + x);
   
   // if in testing mode, double check that Beta = a^x mod p with our found X value
   if(isTesting){
   long check = PowerCalc((int)x);
   System.out.println("Beta = " + beta + "\nA^X = " + check);
   if (beta == check) System.out.println("Double Checked!");
   }
}
   
public long PowerCalc(int i){

   BigInteger alphaBig = BigInteger.valueOf(alpha);
   BigInteger pBig = BigInteger.valueOf(p);
   BigInteger iBig = BigInteger.valueOf(i);
   BigInteger resultBig = alphaBig.modPow(iBig,pBig); //alpha ^ i mod p
   return resultBig.longValue();
} 
   

}