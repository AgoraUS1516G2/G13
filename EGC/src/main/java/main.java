/*
This file is part of the project Elliptic_SDK, which is an elliptical cryptographic 
library under GPL license v3.
Copyright (C) 2013  Olivier Goutay

Elliptic_SDK is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Elliptic_SDK is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Elliptic_SDK.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.java;

import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;

public class main {
	public static void main(String[] args) throws BadPaddingException, UnsupportedEncodingException {
		System.out.println("Test begins");

		//testElGamal();
		
		//testPostKeys("999999989");
		
		//testGetPublicKey("999999995");
		
		//testGetPrivateKey("999999995");
		
		testEncrypt("999999991", "hola hola hola hola hola hola hola hola hola hola hola");
	}

	private static void testElGamal() {
		CryptoEngine cryptoEngine = new CryptoEngine("9999999998");
		cryptoEngine.generateKeyPair();

		String encoded = cryptoEngine.encodeString("test test test test test test t", cryptoEngine.getKeyPair().getPublicKey());
		System.out.println("El cifrado del test " + encoded);

		System.out.println(cryptoEngine.decodeString(encoded));
	}
	
	private static void testPostKeys(String id){
		
		Authority auth;
		
		auth = new AuthorityImpl();
		
		System.out.println(auth.postKey(id));
		
	}
	
	private static void testGetPublicKey(String id){
		
		Authority auth;
		
		auth = new AuthorityImpl();
		
		System.out.println(auth.getPublicKey(id));
		
	}
	
	private static void testGetPrivateKey(String id){
		
		Authority auth;
		
		auth = new AuthorityImpl();
		
		System.out.println(auth.getPrivateKey(id));
		
	}
	
	private static void testEncrypt(String id, String text) throws BadPaddingException, UnsupportedEncodingException{
		Authority auth;
		byte[] encrypt;
		String text2;
		
		auth = new AuthorityImpl();
		
		encrypt = auth.encrypt(id, text);
		
		text2 = auth.decrypt(id, encrypt);
		
		System.out.println(text2);
	}
}
