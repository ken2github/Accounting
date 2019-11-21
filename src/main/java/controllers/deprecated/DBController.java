package controllers.deprecated;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dao.SectorDAO;
import model2.DetailedSector;

@RestController
@RequestMapping("/dashboards")
public class DBController {

	Logger logger = LoggerFactory.getLogger(DBController.class);

	@Autowired
	private DataSource ds;

	@Autowired
	private SectorDAO sectorDAO;

	@RequestMapping(method = RequestMethod.GET, value = "/status")
	public List<DetailedSector> method() throws Exception {
		List<DetailedSector> lds = new ArrayList<>();
		try {
			// Class.forName("com.mysql.cj.jdbc.Driver");
			// Connection con =
			// DriverManager.getConnection("jdbc:mysql://localhost:3306/balance.db?serverTimezone=UTC",
			// "root", "secret!1234");
			// here sonoo is database name, root is username and password

			// Connection con = ds.getConnection();
			// Statement stmt = con.createStatement();
			// ResultSet rs = stmt.executeQuery("select * from transactions");

			// while (rs.next())
			// result.add(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
			// con.close();

			DetailedSector ds = sectorDAO.findByName("SPS");
			System.out.println(ds.getName());

			lds = sectorDAO.findAll();

			for (DetailedSector detailedSector : lds) {
				System.out.println(detailedSector.getName());
			}

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}

		return lds;
	}

}
