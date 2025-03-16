package com.profdev.bank;

import com.profdev.bank.config.AppProperties;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Locale;

@SpringBootApplication
@EnableConfigurationProperties({
		AppProperties.class
})
public class BankingApplication {

	public static void main(String[] args) {
		Locale.setDefault(LocaleUtils.toLocale("en_GB"));
		SpringApplication.run(BankingApplication.class, args);
	}
}
