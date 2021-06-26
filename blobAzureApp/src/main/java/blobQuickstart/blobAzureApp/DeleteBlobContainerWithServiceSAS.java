package blobQuickstart.blobAzureApp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

public class DeleteBlobContainerWithServiceSAS {

	public static void main(String argsp[]) throws IOException {

		Map<String, String> mapString = PropertyReaderUtility.loadPropertiesMap("/application.properties");

	

		SharedAccessSignature sasToken = new SharedAccessSignature.SasBuilder().signedPermission("rwdlac")
				.signedStart(mapString.get("startTime")).signedExpiry(mapString.get("endTime"))
				.signedVersion(mapString.get("apiversion")).signedResource("c")
				.canonicalizedResource("/blob/pramitstore/upload").signedProtocol("https").build();

		System.out.println("SAS Token " + sasToken);

		
		
		  BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
		  .endpoint(mapString.get("endpoint")) .sasToken(sasToken.toString())
		  .containerName("upload") .buildClient();
		 
	
		
		BlobClient blobClient=blobContainerClient.getBlobClient("Pramit_Bhaumik.pdf");
		blobClient.uploadFromFile("C:\\Users\\pramit.bhaumik\\OneDrive - Nuance\\Desktop\\Pramit_Bhaumik.pdf");
		
	}


}
