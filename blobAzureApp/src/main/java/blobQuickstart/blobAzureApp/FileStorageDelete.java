package blobQuickstart.blobAzureApp;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;


import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.azure.storage.file.share.models.ShareFileItem;

public class FileStorageDelete {

	
	public static void main(String args[]) throws IOException
	{
	
		Map<String, String> mapString = PropertyReaderUtility.loadPropertiesMap("/application.properties");
		String shareServiceURL = String.format("https://%s.file.core.windows.net", mapString.get("accountName"));
		
		
		deleteFilesRecusively("assure-stage",shareServiceURL);
	}
	
	private static void deleteFilesRecusively(String resourcePath,String shareServiceURL)
	{

		ShareDirectoryClient directoryClient = new ShareFileClientBuilder().endpoint(shareServiceURL)
			    .sasToken("sv=2019-12-12&ss=f&srt=sco&sp=rwdlc&se=2020-11-03T19:36:47Z&st=2020-11-03T11:36:47Z&spr=https&sig=zPaCZKUav80w7s%2BU%2BZbMkIE%2BDAfyr%2B%2BHn7k0G9lcUHM%3D").shareName("clu-dump")
			    .resourcePath(resourcePath).buildDirectoryClient();
		 for(ShareFileItem fileItems:directoryClient.listFilesAndDirectories())
		   {
			   
			   System.out.println("File Item " + fileItems.getName());
			   if(fileItems.isDirectory())
			   {
				   deleteFilesRecusively(resourcePath+ "/" + fileItems.getName(),shareServiceURL);
			   }
			   else
			   {
				   ShareFileClient destFileClient = new ShareFileClientBuilder().sasToken("sv=2019-12-12&ss=f&srt=sco&sp=rwdlc&se=2020-11-03T19:36:47Z&st=2020-11-03T11:36:47Z&spr=https&sig=zPaCZKUav80w7s%2BU%2BZbMkIE%2BDAfyr%2B%2BHn7k0G9lcUHM%3D").endpoint(shareServiceURL).shareName("clu-dump")
				            .resourcePath(resourcePath + "/" + fileItems.getName()).buildFileClient();
				   if(destFileClient.getProperties().getLastModified().isAfter(OffsetDateTime.now().minusDays(1)) && !fileItems.getName().contains("smokeTest"))
				   {
					   destFileClient.delete();
				   }
			   }
		   }
		   
		
	}
}
