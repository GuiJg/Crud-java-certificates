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

        // Cria um diretório temporário para extrair os arquivos
        Path tempDir = Files.createTempDirectory("certificados_zip");

        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                File outFile = new File(tempDir.toFile(), entry.getName());

                // Garante que diretórios intermediários existam
                outFile.getParentFile().mkdirs();

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    zis.transferTo(fos);
                }

                // Salva no mapa com o nome exato do arquivo (ex: minerva.pfx)
                fileMap.put(entry.getName(), outFile);
            }
        }

        return fileMap;
    }
}
