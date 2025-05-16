package in.intranet.springbootmongodb.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import in.intranet.springbootmongodb.dto.CertificateImportDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadPfx(File file, String companyName) throws IOException {
        String folder = "certificados/" + sanitizeFolder(companyName);

        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "raw",
                "use_filename", true,
                "unique_filename", false
        ));

        return uploadResult.get("secure_url").toString();
    }

    private String sanitizeFolder(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}

