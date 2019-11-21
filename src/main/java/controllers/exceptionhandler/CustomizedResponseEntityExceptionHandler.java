package controllers.exceptionhandler;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import controllers.exceptionhandler.ApiError.ApiErrorCode;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	Logger logger = LoggerFactory.getLogger(CustomizedResponseEntityExceptionHandler.class);

	public static class ErrorDetails {
		public String error;
		public String error_code;
		public String error_description;

		public ErrorDetails(String error_http, String error_code, String error_description) {
			super();
			this.error = error_http;
			this.error_code = error_code;
			this.error_description = error_description;
		}

		public String getError() {
			return error;
		}

		public String getError_code() {
			return error_code;
		}

		public String getError_description() {
			return error_description;
		}

	}

	@ExceptionHandler(ApiException.class)
	public final ResponseEntity<ErrorDetails> handleApiException(ApiException ex, WebRequest request) {
		logger.error(String.format(
				"EXCEPTION_HANDLER='handleApiException' HTTP_STATUS='%s' ERROR_CODE=='%s' EXTERNAL_MSG='%s' INTERNAL_MSG='%s'",
				ex.getApiError().getHttpStatus(), ex.getApiError().getErrorCode(),
				ex.getApiError().getInternalMessage(), ex.getApiError().getInternalMessage()));
		return new ResponseEntity<>(
				new ErrorDetails(ex.getApiError().getHttpStatus().getReasonPhrase(),
						ex.getApiError().getErrorCode().name(), ex.getApiError().getExternalMessage()),
				ex.getApiError().getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorDetails> handleUnexpectedException(Exception ex, WebRequest request) {
		logger.error(String.format("EXCEPTION_HANDLER='handleUnexpectedException' EXCEPTION='%s' STACKTRACE='%s'", ex,
				Arrays.asList(ex.getStackTrace()).stream().map(StackTraceElement::toString)
						.collect(Collectors.joining(", "))));
		return handleApiException(new ApiException(new ApiError().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
				.setErrorCode(ApiErrorCode.UNEXPECTED_EXCEPTION_INTERNAL_ERROR).setExternalMessage(ex.getMessage())),
				request);
	}

	@ExceptionHandler(DataAccessException.class)
	public final ResponseEntity<ErrorDetails> handleUnexpectedDataAccessException(DataAccessException ex,
			WebRequest request) {
		logger.error(
				String.format("EXCEPTION_HANDLER='handleUnexpectedDataAccessException' EXCEPTION='%s' STACKTRACE='%s'",
						ex, Arrays.asList(ex.getStackTrace()).stream().map(StackTraceElement::toString)
								.collect(Collectors.joining(", "))));
		// System.out.println(ex.getMessage());
		// System.out.println(ex.getRootCause());
		// System.out.println(ex.getRootCause().getMessage());
		return handleApiException(
				new ApiException(new ApiError().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
						.setErrorCode(ApiErrorCode.UNEXPECTED_DATA_ACCESS_EXCEPTION_ERROR).setExternalMessage(
								(ex.getMessage() != null) ? ex.getMessage() : ex.getRootCause().getMessage())),
				request);
	}

}
