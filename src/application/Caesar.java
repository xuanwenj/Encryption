package application;


public class Caesar {
	String alphabet;
	int key;

	public Caesar() {
		alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	}

	public String dencrypt(String cipherText, int key) {
		String plainText = "";
		int decrpyKey = alphabet.length() - (key % alphabet.length());

		for (int i = 0; i < cipherText.length(); i++) // loop through all characters
		{
			char plainCharacter = cipherText.charAt(i);

			int position = alphabet.indexOf(plainCharacter); // get the psoition in the alphabet

			int newPosition = (position + decrpyKey) % alphabet.length(); // position of the cipher character

			char cipherCharacter = alphabet.charAt(newPosition);

			plainText += cipherCharacter; // appending this cipher character to the cipherText

		}

		return plainText;
	}

	public String encrypt(String plainText, int key) {
		String cipherText = "";

		for (int i = 0; i < plainText.length(); i++) // loop through all characters
		{
			char plainCharacter = plainText.charAt(i);

			int position = alphabet.indexOf(plainCharacter); // get the position in the alphabet

			int newPosition = (position + key) % alphabet.length(); // position of the cipher character

			char cipherCharacter = alphabet.charAt(newPosition);// obtaining the character from the alphabet string at
																// the calculated newPosition.

			cipherText += cipherCharacter; // appending this cipher character to the cipherText

		}
		return cipherText;
	}

	public void setKey(int key) {
		this.key = key;
	}

}
