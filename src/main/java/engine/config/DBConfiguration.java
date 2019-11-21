package engine.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.AccountedYearDAO;
import dao.CountDAO;
import dao.FileReader;
import dao.IndexesDAO;
import dao.SectorDAO;
import dao.StagedTransactionDAO;
import dao.TransactionDAO;
import dao.file.FileReaderImpl;
import dao.jdbc.JDBCTemplateAccountedYearDAO;
import dao.jdbc.JDBCTemplateCountDAO;
import dao.jdbc.JDBCTemplateIndexesDAO;
import dao.jdbc.JDBCTemplateSectorDAO;
import dao.jdbc.JDBCTemplateStagedTransactionDAO;
import dao.jdbc.JDBCTemplateTransactionDAO;
// import dao.jdbc.JDBCTemplateTransactionDAO;

@Configuration
@PropertySource("classpath:application.properties")
public class DBConfiguration {

	@Value("${spring.datasource.url}")
	private String DS_URL;

	@Value("${spring.datasource.username}")
	private String DS_USERNAME;

	@Value("${spring.datasource.password}")
	private String DS_PWD;

	@Value("${spring.datasource.driver-class-name}")
	private String DS_DRIVER_CLASSNAME;

	@Bean
	public DataSource dataSource() {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(DS_DRIVER_CLASSNAME);
		bds.setUrl(DS_URL);
		bds.setUsername(DS_USERNAME);
		bds.setPassword(DS_PWD);
		bds.setInitialSize(2);
		bds.setMaxTotal(5);
		return bds;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	// @Bean
	// public TransactionDAO transactionDAO() {
	// JDBCTemplateTransactionDAO tdao = new JDBCTemplateTransactionDAO();
	// tdao.setDataSource(dataSource());
	// return tdao;
	// }

	@Bean
	public SectorDAO sectorDAO() {
		JDBCTemplateSectorDAO sdao = new JDBCTemplateSectorDAO();
		sdao.setDataSource(dataSource());
		return sdao;
	}

	@Bean
	public CountDAO countDAO() {
		JDBCTemplateCountDAO sdao = new JDBCTemplateCountDAO();
		sdao.setDataSource(dataSource());
		return sdao;
	}

	@Bean
	public TransactionDAO transactionDAO() {
		JDBCTemplateTransactionDAO sdao = new JDBCTemplateTransactionDAO();
		sdao.setDataSource(dataSource());
		return sdao;
	}

	@Bean
	public StagedTransactionDAO stagedTransactionDAO() {
		JDBCTemplateStagedTransactionDAO sdao = new JDBCTemplateStagedTransactionDAO();
		sdao.setDataSource(dataSource());
		return sdao;
	}

	@Bean
	public AccountedYearDAO accountedYearDAO() {
		JDBCTemplateAccountedYearDAO sdao = new JDBCTemplateAccountedYearDAO();
		sdao.setDataSource(dataSource());
		return sdao;
	}

	@Bean
	public IndexesDAO indexDAO() {
		JDBCTemplateIndexesDAO sdao = new JDBCTemplateIndexesDAO();
		sdao.setDataSource(dataSource());
		return sdao;
	}

	@Bean
	public FileReader fileReader() {
		return new FileReaderImpl();
	}
}
