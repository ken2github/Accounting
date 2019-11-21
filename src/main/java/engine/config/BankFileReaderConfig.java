package engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import restapi.bankfileconverter.service.BankFileReaderService;
import restapi.bankfileconverter.service.reader.BankFileReaderServiceImpl_WithFileReaders;

@Configuration
@PropertySource("classpath:application.properties")
public class BankFileReaderConfig {

	// @Value("${spring.datasource.url}")
	// private String DS_URL;

	@Bean
	public BankFileReaderService bankFileReader() {
		return new BankFileReaderServiceImpl_WithFileReaders();
	}

}
