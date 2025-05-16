package in.intranet.springbootmongodb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipExtractService {

    public Map<String, File> extract(MultipartFile zipFile) throws IOException {
        Map<String, File> fileMap = new HashMap<>();
        Path tempDir = Files.createTempDirectory("certificados");

        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                String fileName = Paths.get(entry.getName()).getFileName().toString();
                File file = new File(tempDir.toFile(), fileName);

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    zis.transferTo(fos);
                }

                fileMap.put(fileName, file);
            }
        }

        return fileMap;
    }
}
