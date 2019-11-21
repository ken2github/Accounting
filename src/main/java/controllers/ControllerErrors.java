package controllers;

import java.util.List;

public class ControllerErrors {

	public static void checkIfAnyErrorAndThrowException(List<APIError> errors) {
		if (errors.size() > 0) {
			throw new RuntimeException("NOT_IMPLEMENTED");
		}
	}

}
