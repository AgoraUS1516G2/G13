package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.*;
import java.util.*;

public class RemoteDataBaseManager {
	
	/**
	 * Funci�n que almacena en la base de datos remota un par de claves de cifrado RSA
	 * asociadas a una votaci�n
	 * @param id La ide de la votaci�n.
	 * @param publicKey La clave p�blica de cifrado asociada a la votaci�n
	 * @param privateKey La clave privada de cifrado asociada a la votaci�n
	 * @return una variable booleana que ser� cierta si el guardado se realiza con �xito.
	 */
	public boolean postKeys(String id, String publicKey,String privateKey ){
		boolean success = false;
		Connection conn = null;
		Statement stmt = null;
	    String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";  
	    String DB_URL = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
		
		try {	
		
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		
		stmt = conn.createStatement();
		
		String sql = "INSERT INTO keysvotes (idvotation, publicKey, privateKey)" +
                "VALUES (?, ?, ?)";
		
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, new Integer(id));
        preparedStatement.setString(2, publicKey);
        preparedStatement.setString(3, privateKey);
        int r = preparedStatement.executeUpdate(); 
        
        if(r == 1){
        	success = true;
        }else if(r == 0){
        	success = false;
        }
	    
		} catch(SQLException se) {
	        se.printStackTrace();
	    } catch(Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if(stmt != null)
	                conn.close();
	        } catch(SQLException se) {
	        }
	        try {
	            if(conn != null)
	                conn.close();
	        } catch(SQLException se) {
	            se.printStackTrace();
	        }
	    }
		
		return success;
	}
	/**
	 * Funci�n que almacena en la base de datos una clave de cifrado que se usar�
	 * con el algoritmo AES.
	 * @param id La id de la votaci�n asociada a la clave de cifrado
	 * @param secretKey Clave de cifrado a almacenar
	 * @return Una variable booleana que indica el �xito o fracaso de la operaci�n
	 */
	public boolean postAESKey(String id, String secretKey){
		boolean success = false;
		try {
			
			//Codificamos las variables que se enviar�n en "UTF-8"
			id = URLEncoder.encode(id, "UTF-8");
			secretKey = URLEncoder.encode(secretKey, "UTF-8");
	        URL url;
			
	        //URL que atender� la petici�n HTTP y guardar� la clave
	        // en la base de datos remota
			url = new URL("http://egc.jeparca.com/AESdefault2.php");
			
	        URLConnection connection = url.openConnection();
	        connection.setDoOutput(true);
	
	        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
	        
	        //Escribimos los valores en las variables para la petici�n HTTP
	        out.write("id=" + id+"&");
	        out.write("secretKey=" + secretKey);
	        out.close();
	
	        //Obtenemos la respuesta de la petici�n
	        BufferedReader in = new BufferedReader(new InputStreamReader( connection.getInputStream()));
	        String decodedString;
	        String fullText="";
	        while ((decodedString = in.readLine()) != null) {
	        	fullText+=decodedString;
	        }
	        in.close();
	        //Comprobamos que en la respuesta est� contenido el mensaje de �xito
	        success = fullText.contains("New record created successfully");
	        
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return success;
	}
	/**
	 * Funci�n para leer los valores de las claves de cifrado asociadas a una votaci�n.
	 * @param id La id de la votaci�n cuyas claves queremos consultar
	 * @param method Variable que indica si se quieren consultar las claves RSA o la clave AES
	 * @return Cadena de texto con el resultado de la petici�n HTTP
	 */
	public String readPage(String id){
		BufferedReader in = null;
		URL url = null;
		String linea;
		String textoPagina="";

		//Hacemos una petici�n HTTP a una URL cuyo resultado podr� ser analizado
		// posteriormente para extraer las claves RSA o la clave AES
		try{
			
			url = new URL("http://egc.jeparca.com/AESdefault.php?id="+id);
			
		}catch (MalformedURLException e){
			
			e.printStackTrace();
		}
		
				
		try{
			
			in = new BufferedReader(new InputStreamReader(url.openStream()));
		}catch(IOException e){
			
			e.printStackTrace();
		}
		//Guardamos en la variable 'textoPagina' el resultado de la petici�n HTTP	
		try{
			while ((linea = in.readLine()) != null) {
			     textoPagina = textoPagina + linea;
			}
		}catch(IOException e){
		
			e.printStackTrace();
		}
		
		
		return textoPagina;
	}
	/**
	 * Funci�n usada para obtener la clave de cifrado AES asociada a una votaci�n.
	 * @param id La id de la votaci�n cuya clave de cifrado AES queremos conocer
	 * @return La clave de cifrado AES asociada a una votaci�n
	 */
	public String getSecretKey(String id){
		String res = "";
		Connection conn = null;
		String url = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
		String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";
	    
	    try {			
		  conn = DriverManager.getConnection(url, USER, PASS);
	      Statement select = conn.createStatement();
	      ResultSet result = select
	          .executeQuery("SELECT privateKey FROM keysvotes where idvotation="+id);
	      
	      result.next();
	      res = result.getString(1);
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	      if (conn != null) {
	        try {
	          conn.close();
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    }
	    
	    return res;
	}
	
	/**
	 * Funci�n usada para obtener la clave p�blica RSA asociada a una votaci�n.
	 * @param id La id de la votaci�n cuya clave p�blica RSA queremos conocer
	 * @return La clave p�blica asociada a una votaci�n
	 */
	public String getPublicKey(String id){
		String res = "";
		Connection conn = null;
		String url = "jdbc:mysql://egc.jeparca.com:3306/jeparcac_egc";
		String USER = "jeparcac_egc";
	    String PASS = "kqPTE8dLz3GVtks";
	    
	    try {			
		  conn = DriverManager.getConnection(url, USER, PASS);
	      Statement select = conn.createStatement();
	      ResultSet result = select
	          .executeQuery("SELECT publicKey FROM keysvotes where idvotation="+id);
	      
	      result.next();
	      res = result.getString(1);
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	      if (conn != null) {
	        try {
	          conn.close();
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    }
	    
	    return res;
	}
	
	/**
	 * Funci�n usada para obtener la clave privada RSA asociada a una votaci�n.
	 * @param id La id de la votaci�n cuya clave privada RSA queremos conocer
	 * @return La clave privada asociada a una votaci�n
	 */
	public String getPrivateKey(String id){
		String fullPage = readPage(id);
		String res = "";
		
		//En el bucle se extrae el valor de la clave analizando el resultado de llamar a la
		// funci�n readPage.
		for(int j = fullPage.indexOf("Privatekey: ") + 11; fullPage.charAt(j)!='<' && j< fullPage.length() ;j++){
			
			res += fullPage.charAt(j);
		}
		
		return res;
	}

}
