package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import restapi.bankfileconverter.api.FileNamePatternConstants;
import restapi.bankfileconverter.api.InputBase64FileInfo;
import restapi.bankfileconverter.api.InputImplicitBase64FileInfo;

public class ControllerHelper {

	public static List<APIError> validateInputFileInfo(InputBase64FileInfo ifi) {
		List<APIError> errors = new ArrayList<>();

		if (ifi.count == null) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}

		if (ifi.base64EncodedFileContent == null) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}

		if (ifi.toDateBalance == null) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}

		if (ifi.fromDate == null) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}

		if (ifi.toDate == null) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}

		if (ifi.fromDate.after(ifi.toDate)) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}

		return errors;
	}

	public static List<APIError> validateInputImplicitFileInfo(InputImplicitBase64FileInfo iifi) {
		List<APIError> errors = new ArrayList<>();

		errors.addAll(validateImplicitFileName(iifi.canonicalFileName));

		if (iifi.base64EncodedFileContent == null) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}

		return errors;
	}

	public static List<APIError> validateImplicitFileName(String fileName) {
		List<APIError> errors = new ArrayList<>();

		if (fileName == null) {
			throw new RuntimeException("NOT_IMPLEMENTED : FileName is NULL");
		} else {
			Pattern p = Pattern.compile(FileNamePatternConstants.FILENAME_WITH_EXTENSION_PATTERN_REGEXP);
			Matcher m = p.matcher(fileName);

			if (!m.matches()) {
				throw new RuntimeException(String.format(
						"NOT_IMPLEMENTED : FileName '%s' does not match regexp path for implicit file name: %s",
						fileName, FileNamePatternConstants.FILENAME_WITH_EXTENSION_PATTERN_REGEXP));
			}
		}

		return errors;
	}
}
