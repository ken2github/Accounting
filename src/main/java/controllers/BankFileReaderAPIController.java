package controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiException;
import restapi.bankfileconverter.api.InpuImplicitFileInfo;
import restapi.bankfileconverter.api.InputBase64FileInfo;
import restapi.bankfileconverter.api.InputImplicitBase64FileInfo;
import restapi.bankfileconverter.api.OutputFileInfo;
import restapi.bankfileconverter.service.BankFileReaderService;
import restapi.bankfileconverter.service.reader.ReaderException;

@RestController
@RequestMapping("/bankfilereader")
public class BankFileReaderAPIController {

	Logger logger = LoggerFactory.getLogger(BankFileReaderAPIController.class);

	@Value("${com.verdino.balancing.bankfile.reader.uploadDir}")
	private String uploadDir;

	@Autowired
	private BankFileReaderService bfrService;

	@PostMapping("/")
	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
		String fileName = new FileStorageService().storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileName).toUriString();

		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@RequestMapping(method = RequestMethod.POST, value = "/implicit/")
	public OutputFileInfo convertToTransactions(@RequestParam("file") MultipartFile file) {
		System.out.println(file.getName());
		System.out.println(file.getOriginalFilename());
		System.out.println(file.getOriginalFilename().replaceAll("[.]\\w*", ""));
		System.out.println(file.getOriginalFilename().replaceFirst("[.]\\w*", ""));
		ControllerErrors.checkIfAnyErrorAndThrowException(
				ControllerHelper.validateImplicitFileName(file.getOriginalFilename()));

		String fileName = new FileStorageService().storeFile(file);

		try {
			return bfrService.convertToTransactions(new InpuImplicitFileInfo().setCanonicalFileName(fileName)
					.setFile(new File(uploadDir + "\\" + fileName)));
		} catch (ReaderException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromBankFileReaderError(e.getBankFileError()));
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/b64/")
	public OutputFileInfo convertToTransactions(@RequestBody InputBase64FileInfo ifi) {
		ControllerErrors.checkIfAnyErrorAndThrowException(ControllerHelper.validateInputFileInfo(ifi));
		try {
			return bfrService.convertToTransactions(ifi);
		} catch (ReaderException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromBankFileReaderError(e.getBankFileError()));
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/b64/implicit/")
	public OutputFileInfo convertToTransactions(@RequestBody InputImplicitBase64FileInfo iifi) {
		ControllerErrors.checkIfAnyErrorAndThrowException(ControllerHelper.validateInputImplicitFileInfo(iifi));
		try {
			return bfrService.convertToTransactions(iifi);
		} catch (ReaderException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromBankFileReaderError(e.getBankFileError()));
		}
	}

	public class UploadFileResponse {
		private String fileName;
		private String fileDownloadUri;
		private String fileType;
		private long size;

		public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
			this.fileName = fileName;
			this.fileDownloadUri = fileDownloadUri;
			this.fileType = fileType;
			this.size = size;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileDownloadUri() {
			return fileDownloadUri;
		}

		public void setFileDownloadUri(String fileDownloadUri) {
			this.fileDownloadUri = fileDownloadUri;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

	}

	public class FileStorageService {

		private final Path fileStorageLocation;

		public FileStorageService() {
			this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

			try {
				Files.createDirectories(this.fileStorageLocation);
			} catch (Exception ex) {
				throw new FileStorageException(
						"Could not create the directory where the uploaded files will be stored.", ex);
			}
		}

		public String storeFile(MultipartFile file) {
			// Normalize file name
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());

			try {
				// Check if the file's name contains invalid characters
				if (fileName.contains("..")) {
					throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
				}

				// Copy file to the target location (Replacing existing file with the same name)
				Path targetLocation = this.fileStorageLocation.resolve(fileName);
				Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

				return fileName;
			} catch (IOException ex) {
				throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
			}
		}

		public Resource loadFileAsResource(String fileName) {
			try {
				Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
				Resource resource = new UrlResource(filePath.toUri());
				if (resource.exists()) {
					return resource;
				} else {
					throw new MyFileNotFoundException("File not found " + fileName);
				}
			} catch (MalformedURLException ex) {
				throw new MyFileNotFoundException("File not found " + fileName, ex);
			}
		}
	}

	public class FileStorageException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2833724901991385485L;

		public FileStorageException(String message) {
			super(message);
		}

		public FileStorageException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	public class MyFileNotFoundException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 984067596058748434L;

		public MyFileNotFoundException(String message) {
			super(message);
		}

		public MyFileNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
