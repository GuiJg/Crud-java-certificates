package in.intranet.springbootmongodb.controller;

import com.github.slugify.Slugify;
import in.intranet.springbootmongodb.dto.CertificateImportDto;
import in.intranet.springbootmongodb.enums.Types;
import in.intranet.springbootmongodb.model.CertificateModel;
import in.intranet.springbootmongodb.repository.CertificateRepository;
import in.intranet.springbootmongodb.service.CloudinaryService;
import in.intranet.springbootmongodb.service.ExcelParserService;
import in.intranet.springbootmongodb.service.ZipExtractService;
import in.intranet.springbootmongodb.utils.StatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/certificates/import")
public class CertificateImportController {

    @Autowired
    private ExcelParserService excelParserService;
    @Autowired
    private ZipExtractService zipExtractService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private CertificateRepository certificateRepo;
    private Slugify slugify;

    @PostMapping
    public ResponseEntity<?> importCertificates(
            @RequestParam("excel") MultipartFile excelFile,
            @RequestParam("zip") MultipartFile zipFile) {

        try {
            List<CertificateImportDto> certificados = excelParserService.parseExcel(excelFile);
            Map<String, File> arquivosPfx = zipExtractService.extract(zipFile);

            for (CertificateImportDto cert : certificados) {
                File pfx = arquivosPfx.get(cert.getFile());
                if (pfx == null) {
                    System.out.println("Arquivo não encontrado no ZIP: " + cert.getFile());
                    continue;
                }

                String cloudinaryUrl = cloudinaryService.uploadPfx(pfx, cert.getCompany());

                CertificateModel model = new CertificateModel();
                model.setFile(cloudinaryUrl);
                model.setCompany(cert.getCompany());
                model.setCode(cert.getCode());
                model.setCpfCnpj(cert.getCpfCnpj());
                model.setMunicipality(cert.getMunicipality());
                model.setUf(cert.getUf());
                model.setType(Types.valueOf(cert.getType().toUpperCase().trim()));
                model.setPassword(cert.getPassword());
                model.setMaturityDate(cert.getMaturityDate());
                model.setCreatedAt(new Date());
                model.setUpdatedAt(new Date());

                Slugify slugify = new Slugify();
                model.setSlug(slugify.slugify(cert.getCompany()));
                model.setStatus(StatusUtil.calculateStatus(cert.getMaturityDate(), new Date()));

                certificateRepo.save(model);
            }

            return ResponseEntity.ok("Importação concluída com sucesso");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro na importação: " + e.getMessage());
        }
    }
}
