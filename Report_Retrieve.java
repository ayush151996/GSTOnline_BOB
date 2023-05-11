package com.newgen.iforms.GSTOnline;



import static java.lang.Integer.toHexString;



import java.io.File;

import java.io.IOException;

import java.io.StringReader;

import java.io.UnsupportedEncodingException;

import java.security.InvalidKeyException;

import java.security.KeyPair;

import java.security.NoSuchAlgorithmException;

import java.security.PrivateKey;

import java.security.PublicKey;

import java.security.Security;

import java.security.Signature;

import java.security.SignatureException;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;



import org.apache.commons.codec.digest.DigestUtils;

import org.bouncycastle.openssl.PEMReader;

import org.bouncycastle.util.encoders.Hex;



import okhttp3.OkHttpClient;

import okhttp3.Request;

import okhttp3.Response;

import okhttp3.ResponseBody;

import okio.Buffer;

import okio.BufferedSink;

import okio.BufferedSource;

import okio.Okio;



public class Report_Retrieve {

	//Added by Ayush

   static String vendor = InitiateTransaction_API.vendor;

   static String Server = InitiateTransaction_API.Server;

   static String Host = InitiateTransaction_API.Host;

   static final String ENCRYPTION_ALGO = InitiateTransaction_API.ENCRYPTION_ALGO;

   static String privateKey = InitiateTransaction_API.privateKey;



   // Need to update run time

   static String PerfiosTransactionId = "PGT3DZJXX16GH8JBEWJ3X";  //

   static String ReportFormat = "zip";

   static String DownloadReportAtLocation = "C:\\Users\\Saurabh\\Documents\\downloadReport";    //Please make sure if the location path exists



   public static void main(String[] args) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {



       Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

       System.out.println("Inside Main");
       System.out.println("Outside RetrieveReport_API");

       RetrieveReport_API();
       System.out.println("Inside Main After");
       System.out.println("RetrieveReport_API");



   }





   public static void RetrieveReport_API() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {

       String xPerfiosDate = "20200525T123214Z";





       //Process Statement call made here



       String RetrieveReport_Payload = "";



       String RetrieveReport_URL = Server + "/KYCServer/api/gst/v2/report/" + vendor + "/" + PerfiosTransactionId;



       String RetrieveReport_Signature = SignatureCreator("/KYCServer/api/gst/v2/report/" + vendor + "/" + PerfiosTransactionId, RetrieveReport_Payload, xPerfiosDate, PerfiosTransactionId);

       System.out.println("After RetrieveReport_Signature");

       RetrieveReport(RetrieveReport_URL, RetrieveReport_Signature, xPerfiosDate, DownloadReportAtLocation + "/" + PerfiosTransactionId + "." + ReportFormat);



   }



   public static String SignatureCreator(String URL, String Payload, String Date, String PerfiosTransactionId) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {



       String Method = "GET";



       String uriEncodedQuery = "";


       String sha256Payload = enc_dec_Impl.SHA256(Payload);



       // System.out.println("sha256Payload= "+sha256Payload+"\n");



       String xPerfiosDate = Date;
       System.out.println("Inside Signature Creator");


       //  System.out.println("Encoded Quaery Parameter:"+uriEncode(QueryParam) + "\n");





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



       //Creating a String to sign using Conanical Request



       String StringToSign = "PERFIOS-RSA-SHA256" + "\n"



               + xPerfiosDate + "\n"



               + enc_dec_Impl.SHA256(CanonicalRequest);



       System.out.println("StringToSign:" + "\n" + StringToSign + "\n");
System.out.println("Before checksum");





       //Create a checksum using String to sign



       String Checksum = enc_dec_Impl.SHA256(StringToSign);



       System.out.println("After checksum");

       //Encryption the String to sign using RSA private key



       String Signature = enc_dec_Impl.encrypt(Checksum, enc_dec_Impl.buildPrivateKey(privateKey), enc_dec_Impl.buildPublicKey(privateKey));//error line



       System.out.println("Signature2");



       return Signature;



   }





   public static void RetrieveReport(String URL, String Sinature, String xPerfiosDate, String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {



       OkHttpClient client = new OkHttpClient();



       String result = "";



       Request request = new Request.Builder()



               .url(URL)



               .get()



               .addHeader("Content-Type", "application/json")   // application/zip



               .addHeader("Content-Disposition", "attachment; filename=" + PerfiosTransactionId + ".zip")   // application/zip

               .addHeader("X-Perfios-Date", xPerfiosDate)



               .addHeader("Host", Host)



               .addHeader("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256")



               .addHeader("X-Perfios-Content-Sha256", enc_dec_Impl.SHA256(""))



               .addHeader("X-Perfios-Signature", Sinature)



               .addHeader("X-Perfios-Signed-Headers", "host;x-perfios-content-sha256;x-perfios-date")



               .addHeader("Accept", "application/json")



               .addHeader("cache-control", "no-cache")

               .addHeader("X-Perfios-Source", "client")





               .build();



       System.out.println("Retrieve Report : endPointURL : " + URL);

       System.out.println("Method :       GET");



       System.out.println("\nBelow are the headers values : \n");



       System.out.println("content-type                application/json");

       System.out.println("Accept      application/json");

       System.out.println("Host                         " + Host);

       System.out.println("X-Perfios-Algorithm          PERFIOS-RSA-SHA256");

       System.out.println("X-Perfios-Content-Sha256       " + enc_dec_Impl.SHA256(""));

       System.out.println("X-Perfios-Date                 " + xPerfiosDate);

       System.out.println("X-Perfios-Signature           " + Sinature);

       System.out.println("X-Perfios-Signed-Headers      host;x-perfios-content-sha256;x-perfios-date");

       System.out.println("cache-control                 no-cache");





       Response response = client.newCall(request).execute();



       ResponseBody body = response.body();

       System.out.println("Response Code : " + response.code());



       if (response.code() == 200) {

           File file = new File(filePath);



           BufferedSource source = body.source();



           BufferedSink sink = Okio.buffer(Okio.sink(file));



           Buffer sinkBuffer = sink.buffer();



           long totalBytesRead = 0;



           int bufferSize = 8 * 1024;



           for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {

               sink.emit();

               totalBytesRead += bytesRead;

           }



           sink.flush();



           sink.close();



           source.close();





           System.out.println("Total byte read:" + totalBytesRead + "\n");

           System.out.println("\nReport for Perfios Transaction(" + PerfiosTransactionId + ") Downloaded and saved at location: " + DownloadReportAtLocation + "\n");



       } else {

           String responseBody = response.body().string();

           System.out.println("Response :: " + responseBody);

       }

       System.out.println("Done\n");



   }





}

