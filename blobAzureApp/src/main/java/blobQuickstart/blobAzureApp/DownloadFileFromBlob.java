package blobQuickstart.blobAzureApp;


import java.io.IOException;
import java.util.Map;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

public class DownloadFileFromBlob {


	
	public static void main(String argsp[]) throws IOException
	{
		
		
		Map<String,String> mapString=PropertyReaderUtility.loadPropertiesMap("/application.properties");
		
		String accessKey=mapString.get("accessKey");
		String accountName = mapString.get("accountName");
		
		StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accessKey);
		
		
		 
		BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(mapString.get("endpoint")).credential(credential).buildClient();

		BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient("demo");
		
		 blobContainerClient.listBlobs()
         .forEach(blobItem -> 
         {
         System.out.println("Item Name"+blobItem.getName());	 
         BlobClient blobClient=blobContainerClient.getBlobClient(blobItem.getName());
         blobClient.downloadToFile("C:\\Users\\pramit.bhaumik\\git\\blobAzureApp\\"+blobItem.getName().replace("/", "-"));
          
         });
	
		 
		
	}
	
	
	

}
