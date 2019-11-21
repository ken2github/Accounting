package restapi.bankfileconverter.api;

import java.io.File;

public class InpuImplicitFileInfo {

	public String canonicalFileName;
	public File file;

	public String getCanonicalFileName() {
		return canonicalFileName;
	}

	public InpuImplicitFileInfo setCanonicalFileName(String canonicalFileName) {
		this.canonicalFileName = canonicalFileName;
		return this;
	}

	public File getFile() {
		return file;
	}

	public InpuImplicitFileInfo setFile(File file) {
		this.file = file;
		return this;
	}

}
