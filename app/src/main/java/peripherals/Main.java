/*
$ cd jni/
$ $ javac peripherals/*.java
$ LD_LIBRARY_PATH=../build java -classpath ./ peripherals.Main
 */

package peripherals;

public class Main {
	public static void main(String args[]) {		
		int ret;
        NfcCard card = new NfcCard();
		
		ret = NfcHelper.PER_nfcInit();

		ret = NfcHelper.PER_nfcGetCard(card);
		System.out.println("Card type is " + card.cardType);
		System.out.println("Card number is " + card.cardNumber);

		ret = NfcHelper.PER_nfcDeinit();
	}
}
