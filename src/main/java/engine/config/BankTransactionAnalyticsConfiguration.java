package engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

import restapi.bankprovisioninghelper.api.SimilarityCalculator;
import restapi.bankprovisioninghelper.api.Tokenizer;
import restapi.bankprovisioninghelper.service.BankTransactionAnalytics;
import restapi.bankprovisioninghelper.service.analyser.BankTransactionAnalyser;
import restapi.bankprovisioninghelper.service.analyser.BasicTokenizer;
import restapi.bankprovisioninghelper.service.analyser.TFIDFCalculator;

@Configuration
@PropertySource("classpath:application.properties")
public class BankTransactionAnalyticsConfiguration {

	@Bean
	public Tokenizer tokenizer() {
		return new BasicTokenizer();
	}

	@Bean
	public SimilarityCalculator similarityCalculator() {
		return new TFIDFCalculator();
	}

	@Bean
	@DependsOn({ "tokenizer", "similarityCalculator" })
	public BankTransactionAnalytics bankTransactionAnalytics() {
		return new BankTransactionAnalyser();
	}

}
