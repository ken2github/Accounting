package engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

import dao.AccountedYearDAO;
import dao.SectorDAO;
import dao.StagedTransactionDAO;
import dao.TransactionDAO;
import restapi.transactionsoracle.api.Cacher_1;
import restapi.transactionsoracle.api.StringSimilarityCalculator;
import restapi.transactionsoracle.api.StringTokenizer;
import restapi.transactionsoracle.service.TransactionsOracle;
import restapi.transactionsoracle.service.analyser.BankTransactionAnalyser_Refactored;
import restapi.transactionsoracle.service.analyser.BasicTokenizer;
import restapi.transactionsoracle.service.analyser.Cache_ObserverImpl;
import restapi.transactionsoracle.service.analyser.TFIDFCalculator;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("com.baeldung.dependson")
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
	@DependsOn({ "accountedYearDAO", "sectorDAO", "transactionDAO", "stagedTransactionDAO", "stringTokenizer" })
	public Cacher_1 cacher_1(AccountedYearDAO accountedYearDAO, SectorDAO sectorDAO, TransactionDAO transactionDAO,
			StagedTransactionDAO stagedTransactionDAO, StringTokenizer stringTokenizer) {
		return new Cache_ObserverImpl(accountedYearDAO, sectorDAO, transactionDAO, stagedTransactionDAO,
				stringTokenizer);
	}

	@Bean
	@DependsOn({ "stringTokenizer", "stringSimilarityCalculator", "cacher_1" })
	public TransactionsOracle transactionOracle() {
		return new BankTransactionAnalyser_Refactored();
	}

}
