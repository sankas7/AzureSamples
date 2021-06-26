package blobQuickstart.blobAzureApp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Base64.Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

public class SharedAccessSignature {
	private final String signature;

	private final String signedPermission;
	private final String signedStart;
	private final String signedExpiry;
	private final String signedIdentifier;
	private final String signedIp;
	private final String signedProtocol;
	private final String signedVersion;
	private final String signedResource;

	private SharedAccessSignature(SasBuilder builder) {
		signedPermission = formatAsUrlParameter("sp", builder.signedPermission);
		signedExpiry = formatAsUrlParameter("se", builder.signedExpiry);
		signedStart = formatAsUrlParameter("st", builder.signedStart);
	    signedIdentifier = formatAsUrlParameter("si", builder.signedIdentifier);
		signedIp = formatAsUrlParameter("sip", builder.signedIp);
		signedProtocol = formatAsUrlParameter("spr", builder.signedProtocol);
		signedVersion = formatAsUrlParameter("sv", builder.signedVersion);
		signedResource = formatAsUrlParameter("sr", builder.signedResource);

		 signature = "sig=" + new SasBuilder().encodeUtf8(builder.signature);
	
	}

	private String formatAsUrlParameter(String parameterKey, String parameterValue) {
		if (StringUtils.isNotBlank(parameterValue)) {
			return parameterKey + "=" + parameterValue + "&";
		}
		return "";
	}

	@Override
	public String toString() {
		return new StringBuilder().append(signedVersion).append(signedStart).append(signedExpiry).append(signedResource)
				.append(signedPermission).append(signedIp).append(signedProtocol).append(signedIdentifier)
				.append(signature).toString();
	}

	public static class SasBuilder {
		private String signature = "";

		private String signedPermission = "";
		private String signedStart = "";
		private String signedExpiry = "";
		private String canonicalizedResource = "";
		private String signedIdentifier = "";
		private String signedIp = "";
		private String signedProtocol = "";
		private String signedVersion = "";
		private String signedResource = "";

		public SasBuilder signedVersion(String signedVersion) {
			this.signedVersion = signedVersion;
			return this;
		}

		public SasBuilder signedPermission(String signedPermission) {
			this.signedPermission = signedPermission;
			return this;
		}

		public SasBuilder canonicalizedResource(String canonicalizedResource) {
			this.canonicalizedResource = canonicalizedResource;
			return this;
		}

		public SasBuilder signedIp(String signedIp) {
			this.signedIp = signedIp;
			return this;
		}

		public SasBuilder signedProtocol(String signedProtocol) {
			this.signedProtocol = signedProtocol;
			return this;
		}

		public SasBuilder signedIdentifier(String signedIdentifier) {
			this.signedIdentifier = signedIdentifier;
			return this;
		}

		public SasBuilder signedExpiry(String signedExpiry) {
			this.signedExpiry = signedExpiry;
			return this;
		}

		public SasBuilder signedStart(String signedStart) {
			this.signedStart = signedStart;
			return this;
		}

		public SasBuilder signedResource(String signedResource) {
			this.signedResource = signedResource;
			return this;
		}

		public SharedAccessSignature build() throws IOException {
			Map<String, String> mapString = PropertyReaderUtility.loadPropertiesMap("/application.properties");

			String accessKey = mapString.get("accessKey");

			signature = generateSasSignature(accessKey, stringToSign());
			checkPreconditions();
			return new SharedAccessSignature(this);
		}

		public static String generateSasSignature(String key, String input) {
			SecretKeySpec secret_key = new SecretKeySpec(Base64.getDecoder().decode(key), "HmacSHA256");
			Encoder encoder = Base64.getEncoder();
			Mac sha256_HMAC = null;
			String hash = null;

			try {
				sha256_HMAC = Mac.getInstance("HmacSHA256");
				sha256_HMAC.init(secret_key);
				hash = new String(encoder.encode(sha256_HMAC.doFinal(input.getBytes("UTF-8"))));
			} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException
					| UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return hash;
		}

		private String stringToSign() {
			StringBuilder strToSign = new StringBuilder();
			strToSign.append(signedPermission).append("\n");
			strToSign.append(signedStart).append("\n");
			strToSign.append(signedExpiry).append("\n");
			strToSign.append(canonicalizedResource).append("\n");
			strToSign.append(signedIdentifier).append("\n");
			strToSign.append(signedIp).append("\n");
			strToSign.append(signedProtocol).append("\n");
			strToSign.append(signedVersion).append("\n");
			strToSign.append("").append("\n");
			strToSign.append("").append("\n");
			strToSign.append("").append("\n");
			strToSign.append("").append("\n");
			strToSign.append("");
			return strToSign.toString();
		}

		private void checkPreconditions() {
			if (StringUtils.isBlank(signedVersion) || StringUtils.isBlank(signedResource)
					|| StringUtils.isBlank(signedPermission) || StringUtils.isBlank(signedExpiry)
					|| StringUtils.isBlank(signature)) {
				throw new IllegalStateException(
						"SAS Builder: SignedVersion, signedResource, SignedPermission, SignedExpiry, Signature must be set.");
			}
		}

		private String encodeUtf8(String textToBeEncoded) {
			try {
				return URLEncoder.encode(textToBeEncoded, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return textToBeEncoded;
		}
	}
}