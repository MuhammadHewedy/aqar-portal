package crawler.aqarmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EntityScan(basePackageClasses = { AqarmapCrawlerApplication.class, Jsr310JpaConverters.class })
public class AqarmapCrawlerApplication {


	public static void main(String[] args) {
		SpringApplication.run(AqarmapCrawlerApplication.class, args);
	}

}
