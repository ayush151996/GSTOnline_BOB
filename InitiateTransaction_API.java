package com.newgen.iforms.GSTOnline;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStream;

import java.io.StringReader;

import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;

import java.security.Key;

import java.security.KeyPair;

import java.security.NoSuchAlgorithmException;

import java.security.PublicKey;

import java.security.Security;

import java.security.SignatureException;
import java.util.Properties;

import javax.crypto.Cipher;



import org.bouncycastle.openssl.PEMReader;

import org.bouncycastle.util.encoders.Hex;

import org.json.JSONObject;

import com.newgen.iforms.common.PropertiesFileData;//uncommented by Ayush



public class InitiateTransaction_API {



   //Update the Organization name as Vendor (Provided by Perfios)



   static String vendor = "bankOfBarodaMSME"; //Update vendor ID here

   static String txnID = "bankOfBarodaMSME_UniqueTxnID";// Transaction ID to be passed by client for mapping/reference

   static String transactionCompleteUrl = "https://webhook.site/26751269-21b2-4af5-881a-dc75fa90c124";

	Properties prop=PropertiesFileData.commonPoperties;//Added by Ayush



   //The Host and server

   static String Server = "https://apidemo.perfios.com";     // https://apidemo.perfios.com  ,  http://marmot.hinagro.com

   static String Host = "apidemo.perfios.com";       // apidemo.perfios.com   ,   marmot.hinagro.com      //



   //Initiate Transaction URL



   private static final String Initiate_Transaction_URL = Server + "/KYCServer/api/gst/v2/start/" + vendor;

   //The padding and algorithm used for Signature generation



   static final String ENCRYPTION_ALGO = "SHA256withRSA/PSS";//SHA256withRSA/PSS

   static final String ENCRYPTION_ALGO_PAN = "RSA/ECB/PKCS1Padding";



   //update the payload request for Initiate Transaction API

   String privateKeyPath = prop.getProperty("privateKeyPathGST");//Added Newly
   static String privateKey;
   {  //Added by Ayush - Newly
 //String privateKeyPath = prop.getProperty("privateKeyITAPath");
   
 File keyFile = new File(privateKeyPath);
  
 try {
	privateKey = new String(Files.readAllBytes(keyFile.toPath()), Charset.defaultCharset());
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
   }
   String privateKeyPathPANPath = prop.getProperty("privateKeyPathPANGST");//Added newly
  static  String privateKeyPAN;
   {  //Added by Ayush - Newly
 //String privateKeyPath = prop.getProperty("privateKeyITAPath");
   
 File keyFile = new File(privateKeyPathPANPath);
  
 try {
	 privateKeyPAN = new String(Files.readAllBytes(keyFile.toPath()), Charset.defaultCharset());
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
   }




//   static String privateKeyPAN =  "-----BEGIN RSA PRIVATE KEY-----\r\n"
//   		+ "MIIBOwIBAAJBAOtWuoQsiPaKquVrphAyFSd/T9WphDokyKaXtGVDaI688KD6zKa0\r\n"
//   		+ "LcKc3ds1yHunf8rczWwsFolWEVim6ICxbVkCAwEAAQJAHLp0X9yw5onEdWen+Luo\r\n"
//   		+ "Ze8ub3p3VAK4BNApmx/4tE3DzL4/+7RVeyB6iL9wrwUujp+9k80GJjZ8zajPch5H\r\n"
//   		+ "9QIhAP0Oq49TkxWeS90xHsoc3Stv7OsOHkE6a6d0sh5zYKOdAiEA7hNP2SlL4xSt\r\n"
//   		+ "nlkmL/ZFGWUw8qErL5gHbZ8jgy88Oe0CIQDfetYtxFvvypUq6VdMnMMBul2blCNJ\r\n"
//   		+ "7aLnBtrKo5AjzQIhAOtWHbUt1D0JgMIpn80DZLTAyYzd8kGex6D+EW2o8KDVAiBf\r\n"
//   		+ "eV/dljZTufzQOGCnVYPZ1lq9ut3AVtao/icsr381/g==\r\n"
//   		+ "-----END RSA PRIVATE KEY-----\r\n";



   //--------Main method-----------------



   public static void main(String[] args) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {



       Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());



       String InitiateTarnsactionRequest = "";

       InitiateTarnsactionRequest = GSTID();//returns GST Payload as InitiateTarnsactionRequest
       System.out.println("InitiateTarnsactionRequest : " + InitiateTarnsactionRequest);
       
       InitiateAPI(InitiateTarnsactionRequest);



   }
//Added by Ayush
public static String GSTID() {
	System.out.println("Inside GST");
	 String InitiateTarnsactionRequest = "";



     JSONObject obj = new JSONObject();

     try {

         String gstNumber = "29ABCDE1234F1ZW";

         String gstNumberEncrypted = encryptWithPublic(gstNumber, "RSA/ECB/PKCS1Padding",

                 buildPublicKey(privateKeyPAN));

         System.out.println("GSTNuMBER:" + gstNumberEncrypted);



         String Payload =



                 "{" +

                         "\"clientTransactionId\":\"TEWTPNTTU463\"," +

                         "\"gstNumberList\":" + "[\"" + gstNumberEncrypted + "\"]" + "," +

                         "\"dataFetchMode\":\"API\"," +

                         "\"transactionCompleteUrl\":\"https://www.example.com\"," +

                         "\"redirectUrl\":\"https://www.google.com\"," +

                         "\"productType\":\"Default\"}";

         InitiateTarnsactionRequest = Payload;
         return InitiateTarnsactionRequest;
     }catch(Exception e) {
	System.out.println(e);
}
     return InitiateTarnsactionRequest;
}

   //-------------------All API Calls------------------------------



   public static void InitiateAPI(String InitiateTarnsactionRequest) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {



       String xPerfiosDate = enc_dec_Impl.Date(); // This method returns the current date and time in YYYYMMDD’T’HHMMSS’Z’ format

       //Initiate transaction call made here----



       String signature = SignatureCreator("/KYCServer/api/gst/v2/start/" + vendor, InitiateTarnsactionRequest, xPerfiosDate, "");



       String PerfiosTransactionId = Make_Request(Initiate_Transaction_URL, signature, InitiateTarnsactionRequest, xPerfiosDate);

       System.out.println(PerfiosTransactionId);

   }





   //----------Request creator-------------------------------



   public static String SignatureCreator(String URL, String Payload, String Date, String PerfiosTransactionId) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {

       //  System.out.println("InitiateTarnsactionRequest : "+Payload);



       String Method = "POST";



       String uriEncodedQuery = "";



       String sha256Payload = enc_dec_Impl.SHA256(Payload);



       System.out.println("Print for reference ::: sha256Payload= " + sha256Payload + "\n");



       String xPerfiosDate = Date;



       //Creating a Conanical Request



       String CanonicalRequest = Method + "\n"



               + enc_dec_Impl.uriEncode(URL) + "\n"



               + uriEncodedQuery + "\n"



               + "host:" + Host + "\n"



               + "x-perfios-content-sha256:" + sha256Payload + "\n"



               + "x-perfios-date:" + xPerfiosDate + "\n"



               + "host;x-perfios-content-sha256;x-perfios-date" + "\n"



               + sha256Payload;



       System.out.println("CanonicalRequest:" + "\n" + CanonicalRequest + "\n");



       System.out.println("Print for reference :::   CanonicalRequest256 : " + enc_dec_Impl.SHA256(CanonicalRequest) + "\n");



       //Creating a String to sign using Conanical Request



       String StringToSign = "PERFIOS-RSA-SHA256" + "\n"



               + xPerfiosDate + "\n"



               + enc_dec_Impl.SHA256(CanonicalRequest);



       System.out.println("StringToSign:" + "\n" + StringToSign + "\n");





       //Create a checksum using String to sign



       String Checksum = enc_dec_Impl.SHA256(StringToSign);



       System.out.println("Checksum : " + Checksum);





       //Encryption the String to sign using RSA private key



       String Signature = enc_dec_Impl.encrypt(Checksum, enc_dec_Impl.buildPrivateKey(privateKey), enc_dec_Impl.buildPublicKey(privateKey));





       System.out.println("Signature= " + Signature + "\n");



       return Signature;



   }





   //---------------------------------Initiate_Transaction_URL method----------------------



   public static String Make_Request(String URL, String Signature, String payload, String Date) throws IOException, NoSuchAlgorithmException {

 System.out.println("Inside Make Request");

       System.out.println(URL);



       String Method = "POST";



       URL obj = new URL(URL);



       HttpURLConnection con = (HttpURLConnection) obj.openConnection();





       if (URL.contains("types")) {



           Method = "GET";

           System.out.println("GET Done Here");



       }



       con.setRequestMethod(Method);

       System.out.println("Method : " + Method);



       System.out.println("Below are the headers values : ");



       con.addRequestProperty("content-type", "application/json");

       System.out.println("content-type     application/json");



       con.addRequestProperty("Accept", "application/json");

       System.out.println("Accept      application/json");



       con.setRequestProperty("Host", Host);

       System.out.println("Host      " + Host);



       con.setRequestProperty("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256");

       System.out.println("X-Perfios-Algorithm          PERFIOS-RSA-SHA256");





       con.setRequestProperty("X-Perfios-Content-Sha256", enc_dec_Impl.SHA256(payload));

       System.out.println("X-Perfios-Content-Sha256  " + enc_dec_Impl.SHA256(payload));



       con.setRequestProperty("X-Perfios-Date", Date);

       System.out.println("X-Perfios-Date     " + Date);





       con.setRequestProperty("X-Perfios-Signature", Signature);

       System.out.println("X-Perfios-Signature  " + Signature);



       con.setRequestProperty("X-Perfios-Source", "client");

       System.out.println("X-Perfios-Source :: client");



       con.setRequestProperty("X-Perfios-Signed-Headers", "host;x-perfios-content-sha256;x-perfios-date");

       System.out.println("X-Perfios-Signed-Headers    host;x-perfios-content-sha256;x-perfios-date");



       con.setDoOutput(true);



       OutputStream os = con.getOutputStream();

       System.out.println("body : " + payload);

       os.write(payload.getBytes());



       os.flush();

       os.close();



       int responseCode = con.getResponseCode();



       System.out.println("Response Code :: " + responseCode);



       if (responseCode == HttpURLConnection.HTTP_OK) {

           // success



           BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));



           String inputLine;



           StringBuffer response = new StringBuffer();



           while ((inputLine = in.readLine()) != null) {

               response.append(inputLine + "\n");

           }



           in.close();

           // print result



           System.out.println(response.toString());



           return response.substring(90, 107);    // Perfios transaction Id

       } else {



           System.out.println("Request failed");



           BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));

           String inputLine;



           StringBuffer response = new StringBuffer();



           while ((inputLine = in.readLine()) != null) {

               response.append(inputLine);

           }



           in.close();



           // print result



           System.out.println(response.toString());



           return response.toString();

       }



   }



   public static PublicKey buildPublicKey(String privateKeySerialized) {
    System.out.println(privateKeySerialized);
       StringReader reader = new StringReader(privateKeySerialized);
       //Added by Ayush to test
       System.out.println("In test");
       int k = 0;
       try {
		while((k=reader.read())!=-1){  
		       System.out.print((char)k);  
		   }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
       
//System.out.println(reader.toString());
       PublicKey pKey = null;
       
       try {

           PEMReader pemReader = new PEMReader(reader);
           System.out.println("pemreader in after stringreader");
           System.out.println(pemReader);
           System.out.println("readobject");
           System.out.println(pemReader.readObject());
                System.out.println("before pem");
                KeyPair keyPair = (KeyPair) pemReader.readObject();
                System.out.println("after pem, keypair:"+keyPair);
           pKey = keyPair.getPublic();
 System.out.println(pKey);
           pemReader.close();

       } catch (IOException i) {

           i.printStackTrace();

       }

       return pKey;

   }



   //RSA encryption using the methods from Bouncycastle.jar

   public static String encryptPAN(String raw, String encryptAlgo, Key k) {

       String strEncrypted = "";

       try {

           Cipher cipher = Cipher.getInstance(encryptAlgo);

           cipher.init(Cipher.ENCRYPT_MODE, k);

           byte[] encrypted = cipher.doFinal(raw.getBytes("UTF-8"));

           byte[] encoded = Hex.encode(encrypted);

           strEncrypted = new String(encoded);

       } catch (Exception ex) {

           ex.printStackTrace();

       }

       return strEncrypted;

   }



   public static String encryptWithPublic(String raw, String encryptAlgo, Key k) {

       String strEncrypted = "";

       try {

           Cipher cipher = Cipher.getInstance(encryptAlgo);

           cipher.init(Cipher.ENCRYPT_MODE, k);

           byte[] encrypted = cipher.doFinal(raw.getBytes("UTF-8"));

           byte[] encoded = Hex.encode(encrypted);

           strEncrypted = new String(encoded);

       } catch (Exception ex) {

           ex.printStackTrace();

       }

       return strEncrypted;

   }

}

