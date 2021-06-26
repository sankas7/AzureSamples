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
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

public class DownloadFileWithAccountSAS {

	public static void main(String argsp[]) throws IOException {

		Map<String, String> mapString = PropertyReaderUtility.loadPropertiesMap("/application.properties");

		String accessKey = mapString.get("accessKey");
		String accountName = mapString.get("accountName");

		StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accessKey);

		String sasToken = getAccountSAS(mapString.get("accountName"), mapString.get("accessKey"),
				mapString.get("endpoint"), mapString.get("startTime"), mapString.get("endTime"),
				mapString.get("apiversion"));

		/*
		 * BlobServiceClient storageClient = new
		 * BlobServiceClientBuilder().endpoint(sasEndpoint).credential(credential).
		 * buildClient();
		 */

		BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(mapString.get("endpoint"))
				.sasToken(sasToken).buildClient();

		BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient("demo");

		blobContainerClient.listBlobs().forEach(blobItem -> {
			System.out.println("Item Name" + blobItem.getName());
			BlobClient blobClient = blobContainerClient.getBlobClient(blobItem.getName());
			blobClient.downloadToFile(
					"C:\\Users\\pramit.bhaumik\\playworkspace\\storage-blobs-java-quickstart\\blobAzureApp\\"
							+ blobItem.getName().replace("/", "-"));

		});

	}

	public static String getAccountSAS(String accountName, String accountKey, String endpoint, String startTime,
			String endTime, String apiVersion) throws UnsupportedEncodingException {

		String stringToSign = accountName + "\n" + "r\n" + "b\n" + "sco\n" + startTime + "\n" + endTime + "\n" 
				+ "https\n" + apiVersion + "\n";

		String signature = getHMAC256(accountKey, stringToSign);

		String sasToken = "sv=" + apiVersion + "&ss=b" + "&srt=sco" + "&sp=r" + "&st="
				+ URLEncoder.encode(startTime, "UTF-8") + "&se=" + URLEncoder.encode(endTime, "UTF-8") + "&spr=https"
				+ "&sig=" + URLEncoder.encode(signature, "UTF-8");

		// endpoint + "?" + sasToken;
		return sasToken;
	}

	private static String getHMAC256(String accountKey, String signStr) {
		String signature = null;
		try {
			SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(accountKey), "HmacSHA256");
			Mac sha256HMAC = Mac.getInstance("HmacSHA256");
			sha256HMAC.init(secretKey);
			signature = Base64.getEncoder().encodeToString(sha256HMAC.doFinal(signStr.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signature;
	}
}
