package engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

import restapi.transactionsoracle.api.StringSimilarityCalculator;
import restapi.transactionsoracle.api.StringTokenizer;
import restapi.transactionsoracle.service.TransactionsOracle;
import restapi.transactionsoracle.service.analyser.BankTransactionAnalyser;
import restapi.transactionsoracle.service.analyser.BasicTokenizer;
import restapi.transactionsoracle.service.analyser.TFIDFCalculator;

@Configuration
@PropertySource("classpath:application.properties")
public class TransactionsOracleConfiguration {

	@Bean
	public StringTokenizer stringTokenizer() {
		return new BasicTokenizer();
	}

	@Bean
	public StringSimilarityCalculator stringSimilarityCalculator() {
		return new TFIDFCalculator();
	}

	@Bean
	@DependsOn({ "stringTokenizer", "stringSimilarityCalculator" })
	public TransactionsOracle transactionOracle() {
		return new BankTransactionAnalyser();
	}

}
