package dao;

import java.util.List;

import model2.DetailedSector;
import model2.Sector;

public interface SectorDAO {
	DetailedSector insert(Sector s);

	DetailedSector update(DetailedSector s);

	boolean deleteById(String id);

	boolean deleteByName(String id);

	DetailedSector findById(String id);

	DetailedSector findByName(String sectorName);

	List<DetailedSector> findAll();
}
