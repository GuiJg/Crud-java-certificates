package in.intranet.springbootmongodb.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dyyopyojy",
                "api_key", "714953697548858",
                "api_secret", "40sGWFDQPkq2NUykNcx4bJwHsAg"
        ));
    }
}
