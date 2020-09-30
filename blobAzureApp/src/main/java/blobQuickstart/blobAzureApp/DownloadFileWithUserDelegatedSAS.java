package blobQuickstart.blobAzureApp;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.implementation.util.BlobSasImplUtil;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

public class DownloadFileWithUserDelegatedSAS {

	public static void main(String args[]) throws IOException {
		Map<String, String> mapString = PropertyReaderUtility.loadPropertiesMap("/application.properties");
        
		
		String accountName = mapString.get("accountName");

		String blobEndpoint = mapString.get("endpoint");

		BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(blobEndpoint)
				.credential(new DefaultAzureCredentialBuilder().build()).buildClient();

		
		UserDelegationKey key = storageClient.getUserDelegationKey(OffsetDateTime.now(),
				OffsetDateTime.now().plusDays(5));

		BlobServiceSasSignatureValues signatureValues = new BlobServiceSasSignatureValues(
				OffsetDateTime.now().plusDays(5), BlobContainerSasPermission.parse("rwdl"));

		String userDelegationSas = new BlobSasImplUtil(signatureValues,"demo").generateUserDelegationSas(key,
				accountName);
		System.out.println("user sas " + userDelegationSas);

		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.endpoint("https://pramitstore.blob.core.windows.net/demo/" + "?" + userDelegationSas)
				.credential(new DefaultAzureCredentialBuilder().build()).buildClient();
		

		containerClient.listBlobs().forEach(blobItem -> {
			System.out.println("Item Name" + blobItem.getName());
			BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());
			blobClient.downloadToFile(
					"C:\\Users\\pramit.bhaumik\\playworkspace\\storage-blobs-java-quickstart\\blobAzureApp\\"
							+ blobItem.getName().replace("/", "-"));

		});
	}
}
