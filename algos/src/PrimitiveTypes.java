/**
 * Questions related to bit manipulation and primitive types.
 * Tricky is the bit manipulation
 * Bit Operators: &(And),|(Or),^(XOR),~(compliment)
 * Bit Shift: 
 * 			1. Left Shift Operator: << (op1 << op2) :Shifts bits of op1 left by distance op2; fills with 0 bits on the right side. Excess bits shifted off to the left are discarded. Zero bits are shifted in from the right
 * 			2. Right Shift Operator: >> (op1 >> op2) :Shifts bits of op1 right by distance op2; fills with highest (sign) bit on the left side
 * 			3. Unsigned Right Shift Operator: >>> (op1 >>> op2) :Shifts bits of op1 right by distance op2; fills with 0 bit on the left side
 * 
 * Examples:
 * Left shift operator:
 * 					12<<2 : 00000000 00000000 00000000 00001100 << 2(times) 
 * 					Output:  00000000 00000000 00000000 00110000
 * 					-12<<2: 11111111 11111111 11111111 11110100 << 2 (Note: for -ve numbers complement digits and add 1; complement 0s and 1s from 12 and add 1 to binary)
 * 					Output: 11111111 11111111 11111111 11010000
 * 
 * Right Shift Operator:
 * 					12>>1 : 00000000 00000000 00000000 00001100 >> 1
 * 					Output: 00000000 00000000 00000000 00000011
 * 					-12>>2: 11111111 11111111 11111111 11110100 >> 2
 * 					Output: 11111111 11111111 11111111 11111101
 * 
 * Unsigned Right Shift Operator: 
 * 					12>>>2 : 00000000 00000000 00000000 00001100 >> 2
 * 					Output: 00000000 00000000 00000000 00000011
 * 					-12>>>2: 11111111 11111111 11111111 11110100 >> 2
 * 					Output: 00111111 11111111 11111111 11111101
 * 
 * 
 * Bitwise Operators:
 * 1. And (&): n1&n2: if both n1 and n2 is 1,1;0 otherwise
 * 2. Or (1): n1|n2: if both n1 and n2 is 0	,0;1 otherwise
 * 3. Xor (^): n1^n2: if n1 and n2 are different,1; 0 otherwise
 * 4. Compliment (~): ~n1: reverse bits		(2's compliment)		
 * 			
 * @author Sahibdeep Singh
 *
 */
public class PrimitiveTypes {

	/**
	 * Calculate the number of 1s in an integer
	 * 
	 * @param x
	 */
	public static short countBits(int x) {

		short count = 0;

		while (x != 0) {
			count += (x & 1);
			x >>>= 1;
		}
		return count;
	}

	/**
	 * parity bit is used in error detecting code. This bit is sit to 1 if the
	 * number of 1s in the word are odd and 0 if the number of 1s are even. It
	 * is added to the the leftmost bit. wiki article:
	 * https://en.wikipedia.org/wiki/Parity_bit
	 * 
	 * Problem statement: check the parity of a given number. Return 1 or 0
	 * based on the number of 1s in the given number.
	 * Hint: (num & num-1) will remove the rightmost 1.
	 * 
	 * @param num
	 */
	public static int checkParity(int num) {
		int parity = 0;
		while (num != 0) {
			num = (num & num - 1);
			parity ^= 1;
		}
		return parity;
	}


	/**
	 * Convert al rightmost 0 bits to 1. 
	 * Ex: 00100 -> 00111
	 */
	public static int rightProp(int num) {
		return (num | num - 1);
	}
	
	/**
	 * Calculate x modulus power of 2
	 * Ex: 77 mod 64=13
	 */
	public static int calcMod(int n1,int n2){
		
		return (n1 & (n2-1));
	}
	
	
	public static void main(String[] args) {
		// number of 1s
		System.out.println(countBits(4));
		// number of 0s
		System.out.println(32 - countBits(10));
		System.out.println(rightProp(4));
		System.out.println(checkParity(4));//1
		System.out.println(checkParity(6));//0
		System.out.println(calcMod(77,64));
	}

}
