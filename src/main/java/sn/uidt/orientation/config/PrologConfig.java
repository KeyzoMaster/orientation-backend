package sn.uidt.orientation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "prolog")
@Data
public class PrologConfig {
    private String executablePath = "swipl"; // Par défaut si dans le PATH
    private String rulesFilePath = "src/main/resources/prolog/regles_expert.pl";
    private String tempFolderPath = "target/prolog-temp";
}