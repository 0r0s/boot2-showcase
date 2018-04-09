package nl.aoros.boot2showcase;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static nl.aoros.boot2showcase.service.ImageService.UPLOAD_ROOT;

@SpringBootApplication
public class SpringBoot2ShowcaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot2ShowcaseApplication.class, args);
	}

	@Bean
	public CommandLineRunner setUp() throws IOException {
		return (args) -> {
			FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
			Files.createDirectory(Paths.get(UPLOAD_ROOT));
		};
	}

	@Bean
	HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter();
	}
}
