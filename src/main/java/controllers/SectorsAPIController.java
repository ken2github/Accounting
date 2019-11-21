package controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiError.ApiErrorCode;
import controllers.helpers.DateHelper;
import controllers.helpers.ValidationHelper;
import controllers.exceptionhandler.ApiException;
import dao.DAOException;
import dao.DAONotFoundException;
import dao.SectorDAO;
import model2.DetailedSector;
import model2.Sector;

@RestController
@RequestMapping("/sectors")
public class SectorsAPIController {

	Logger logger = LoggerFactory.getLogger(SectorsAPIController.class);

	@Autowired
	private SectorDAO sectorDAO;

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public List<DetailedSector> findAll() {
		List<DetailedSector> lds = new ArrayList<>();
		try {
			lds = sectorDAO.findAll();
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return lds;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/name={name}")
	public DetailedSector findByName(@PathVariable String name) {
		DetailedSector ds = null;
		try {
			ds = sectorDAO.findByName(name);
		} catch (DAONotFoundException e) {
			throw new ApiException(new ApiError().setErrorCode(ApiErrorCode.SECTOR_NOT_FOUND)
					.setExternalMessage(e.getDBError().getMessage()).setHttpStatus(HttpStatus.NOT_FOUND));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public DetailedSector findById(@PathVariable String id) {
		DetailedSector ds = null;
		try {
			ds = sectorDAO.findById(id);
			// } catch (DataAccessException dae) {
			// SQLException sqle = (SQLException) dae.getCause();
			// System.out.println("Error code : " + sqle.getErrorCode());
			// System.out.println("SQL state : " + sqle.getSQLState());
		} catch (DAONotFoundException e) {
			throw new ApiException(new ApiError().setErrorCode(ApiErrorCode.SECTOR_NOT_FOUND)
					.setExternalMessage(e.getDBError().getMessage()).setHttpStatus(HttpStatus.NOT_FOUND));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{id}")
	public DetailedSector createSector(@RequestBody Sector sector) {
		ValidationHelper.checkIfAnyErrorAndThrowApiException(ValidationHelper.validateSector(sector));

		DetailedSector ds = null;
		try {
			ds = sectorDAO.insert(DateHelper.resetTimeToNoon_InSector(sector));
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public DetailedSector updateSector(@RequestBody DetailedSector detailedSector) {
		ValidationHelper.checkIfAnyErrorAndThrowApiException(ValidationHelper.validateSector(detailedSector));

		DetailedSector ds = null;
		try {
			ds = sectorDAO.update(DateHelper.resetTimeToNoon_InSector(detailedSector));
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return ds;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public void deleteSector(@PathVariable String id) {
		try {
			sectorDAO.deleteById(id);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/name={name}")
	public void deleteByName(@PathVariable String name) {
		try {
			sectorDAO.deleteByName(name);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

}
