//Service call for GSTOnline
package com.newgen.iforms.GSTOnline;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Properties;

//import com.newgen.iforms.common.ExtLog;

public class GSTOnlineService {


private static Properties  portalProp = null;
InitiateTransaction_API inittransaction_obj = new InitiateTransaction_API();
Report_Retrieve Report_Retrieve_obj = new Report_Retrieve();
String fieldId="";
  
  
//static {
//	
//	portalProp = new Properties();
//	try(FileInputStream fin = new FileInputStream(System.getProperty("user.dir") + File.separator + "LOSExt_Config" + File.separator +"Integrations"+File.separator+  "integration.properties")) {
//		portalProp.load(fin);
//		////System.out.println("From properties--> "+portalProp.get("testProp"));
//	} catch (IOException e) {
//		e.printStackTrace();
//		////System.out.println(e.toString());
//	}
//	
//}
 
	
	public void startGSTOnline(){
		System.out.println("Inside startGSTOnline");
	String InitiateTarnsactionPayloadRequest="";
	InitiateTarnsactionPayloadRequest = inittransaction_obj.GSTID();
	System.out.println("After initiating InitiateTarnsactionPayloadRequest ");
	try {
		inittransaction_obj.InitiateAPI(InitiateTarnsactionPayloadRequest);
		System.out.println("After  try execution initiatetransaction");
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SignatureException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("Outside try for initiateAPI");
        try {
        	System.out.println("Inside try for reportAPI");
		Report_Retrieve_obj.RetrieveReport_API();
    	System.out.println("After try for reportAPI");
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SignatureException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    	System.out.println("Outside try for reportAPI");
	}
	
	public static void main(String[] args) {
   Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		System.out.println("inside main");
		GSTOnlineService gst = new GSTOnlineService();
		gst.startGSTOnline();
	}
	public static String StackTraceToString(Exception e)
	{
	    Writer result = new StringWriter();
	    PrintWriter printWriter = new PrintWriter(result);
	    e.printStackTrace(printWriter);
	    return result.toString();
	}
}
