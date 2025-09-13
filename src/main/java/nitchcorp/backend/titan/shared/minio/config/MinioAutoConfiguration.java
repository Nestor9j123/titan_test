package nitchcorp.backend.titan.shared.minio.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "nitchcorp.backend.titan.shared.config.minio.config")
@ComponentScan(basePackages = "nitchcorp.backend.titan.shared.config.minio")
public class MinioAutoConfiguration {
}
