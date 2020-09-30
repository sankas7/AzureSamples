package blobQuickstart.blobAzureApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

public class UploadFilesToBlob {

	
	public static void main(String args[]) throws IOException
	{
		
       Map<String,String> mapString=PropertyReaderUtility.loadPropertiesMap("/application.properties");
		
		String accessKey=mapString.get("accessKey");
		String accountName = mapString.get("accountName");
		
		StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accessKey);
		 
		BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(mapString.get("endpoint")).credential(credential).buildClient();
	
	    BlobContainerClient containerClient=storageClient.createBlobContainer("upload");
	     
	    
	    
	     loadFiles("C:\\Users\\pramit.bhaumik\\OneDrive - Nuance\\Desktop\\print", new ArrayList<File>(), "", containerClient);
	}
	
	
	public static void loadFiles(String directoryName, List<File> files,String directoryRootName,BlobContainerClient containerClient ) {
	    File directory = new File(directoryName);

	   
	    File[] fList = directory.listFiles();
	    if(fList != null)
	        for (File file : fList) {      
	            if (file.isFile()) {
	            	BlobClient blobClient=directoryRootName.isEmpty()?containerClient.getBlobClient(file.getName()):
	            		containerClient.getBlobClient(directoryRootName + "/" + file.getName());
	            	blobClient.uploadFromFile(file.getAbsolutePath());
	                files.add(file);
	            } else if (file.isDirectory()) {
	            	loadFiles(file.getAbsolutePath(), files,file.getName(),containerClient);
	            }
	        }
	    }
	
}
