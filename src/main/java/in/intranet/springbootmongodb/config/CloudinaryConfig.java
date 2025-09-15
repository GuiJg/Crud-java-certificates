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
                "cloud_name", "absoluta-data",
                "api_key", "783667495229236",
                "api_secret", "4pwEjaArJzPgs42KXl8SIbfKmQs"
        ));
    }
}
