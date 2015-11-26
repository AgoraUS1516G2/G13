package cifrado;

import java.math.BigInteger;
import javax.crypto.BadPaddingException;
import javax.xml.bind.DatatypeConverter;

import main.java.RemoteDataBaseManager;

public class AuthorityImpl implements Authority{
	
	/**
	 * Esta funci�n obtiene las claves p�blica y privada de la votaci�n cuyo id es el pasado 
	 * como par�metro. Resaltar que hacemos uso del proyecto Elliptic_SDK, que  es una librer�a
	 * criptogr�fica el�ptica bajo la licensia GPL v3. M�s informaci�n en la clase CryptoEngine.java
	 * @param id. Corresponde al id de la votaci�n
	 * @return res. Boolean que indica si la operaci�n ha tenido �xito.
	 */
	public boolean postKey(String id) {
		boolean res;
		BigInteger secretKey;
		PointGMP publicKey;
		String encodedSecretKey, encodedPublicKey;
		res = false;
		
		try{
		
			CryptoEngine cryptoEngine = new CryptoEngine(id);
			cryptoEngine.generateKeyPair();
	
			secretKey = cryptoEngine.getKeyPair().getSecretKey();
			publicKey = cryptoEngine.getKeyPair().getPublicKey();
			
			encodedPublicKey = DatatypeConverter.printBase64Binary(publicKey.toString().getBytes());
			encodedSecretKey = DatatypeConverter.printBase64Binary(secretKey.toByteArray());
			
			RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
			 //Llamamos a la funci�n que se encarga de guardar el par de claves asociadas
			 // a la votaci�n cuya id se especifica como par�metro.
			if (rdbm.postKeys(id, encodedPublicKey, encodedSecretKey)){
				res = true;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return res;
	}

	public String getPublicKey(String id) {
		RemoteDataBaseManager rdbm=new RemoteDataBaseManager();
		//Llamamos a la funci�n que conecta con la base de datos remota y obtiene la clave p�blica.
		return rdbm.getPublicKey(id);
	}

	public String getPrivateKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean checkVote(byte[] votoCifrado, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] encrypt(String idVote, String textToEncypt) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String decrypt(String idVote, byte[] cipherText) throws BadPaddingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] cutVote(String votoEnClaro) {
		int arrayLength = votoEnClaro.length() / 31; 
		String[] result = new String[arrayLength + 1];
		String corte = "";
		
		if(votoEnClaro.length() > 31){
			//corto
			for(int i = 0; i < arrayLength + 1; i++){
				int from = i * 31;
				int to = from + 32;
				if(i == 0){
					corte = votoEnClaro.substring(from,to);
					result[i] = corte;
				}else{
					
					//si el ultimo trozo es menor a 31				
					if(votoEnClaro.length() - from < 31){
						corte = votoEnClaro.substring(from + i, votoEnClaro.length());
					}else{
						corte = votoEnClaro.substring(from + i, from + i + 32);
					}
					result[i] = corte;
				}
				
			}
		}else{
			result[0] = votoEnClaro;
		}
		
		return result;
	}

}
