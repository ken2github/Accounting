package restapi.bankfileconverter.api;

public class InputImplicitBase64FileInfo {

	public String canonicalFileName;
	public String base64EncodedFileContent;

	public String getCanonicalFileName() {
		return canonicalFileName;
	}

	public InputImplicitBase64FileInfo setCanonicalFileName(String canonicalFileName) {
		this.canonicalFileName = canonicalFileName;
		return this;
	}

	public String getBase64EncodedFileContent() {
		return base64EncodedFileContent;
	}

	public InputImplicitBase64FileInfo setBase64EncodedFileContent(String base64EncodedFileContent) {
		this.base64EncodedFileContent = base64EncodedFileContent;
		return this;
	}

}
