package in.intranet.springbootmongodb.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.github.slugify.Slugify;
import in.intranet.springbootmongodb.model.CertificateModel;
import in.intranet.springbootmongodb.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    @Autowired
    private CertificateRepository certificateRepo;

    @Autowired
    private Cloudinary cloudinary;

    // GET ALL
    @GetMapping
    public ResponseEntity<?> getAllCertificates() {
        List<CertificateModel> certificates = certificateRepo.findAll();
        if (!certificates.isEmpty()) {
            return ResponseEntity.ok(certificates);
        } else {
            return ResponseEntity.status(404).body("Nenhum Certificado Disponível");
        }
    }

    // POST (CREATE)
    @PostMapping
    public ResponseEntity<?> createCertificate(@RequestBody CertificateModel certificate) {
        try {
            Slugify slugify = new Slugify();
            String slug = slugify.slugify(certificate.getName()); // Gera o slug igual ao JS

            certificate.setSlug(slug);
            certificate.setCreatedAt(new Date());
            certificate.setUpdatedAt(new Date());

            CertificateModel saved = certificateRepo.save(certificate);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao criar certificado: " + e.getMessage());
        }
    }

    @GetMapping("/{value}")
    public ResponseEntity<?> getCertificateByIdOrSlug(@PathVariable String value) {
        // Verifica se o valor é um ObjectId válido (24 caracteres hexadecimais)
        boolean isObjectId = value.matches("^[a-fA-F0-9]{24}$");

        Optional<CertificateModel> certificate = isObjectId
                ? certificateRepo.findById(value)
                : certificateRepo.findBySlug(value);

        return certificate
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Certificado não encontrado"));
    }

    // POST com upload .pfx
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCertificate(
            @RequestPart("file") MultipartFile file,
            @RequestPart("certificate") CertificateModel certificate
    ) {
        try {
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".pfx")) {
                return ResponseEntity.badRequest().body("Apenas arquivos .pfx são permitidos");
            }

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "raw")); // raw = mantém formato original

            String fileUrl = uploadResult.get("secure_url").toString();

            certificate.setFile(fileUrl);
            certificate.setCreatedAt(new Date());
            certificate.setUpdatedAt(new Date());

            CertificateModel saved = certificateRepo.save(certificate);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro no upload: " + e.getMessage());
        }
    }

    // PUT (UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCertificate(@PathVariable String id, @RequestBody CertificateModel certificate) {
        Optional<CertificateModel> existing = certificateRepo.findById(id);

        if (existing.isPresent()) {
            CertificateModel certToUpdate = existing.get();
            certToUpdate.setName(certificate.getName());
            certToUpdate.setCode(certificate.getCode());
            certToUpdate.setCompany(certificate.getCompany());
            certToUpdate.setCnpj(certificate.getCnpj());
            certToUpdate.setMunicipality(certificate.getMunicipality());
            certToUpdate.setUf(certificate.getUf());
            certToUpdate.setType(certificate.getType());
            certToUpdate.setStatus(certificate.getStatus());
            certToUpdate.setMaturityDate(certificate.getMaturityDate());
            certToUpdate.setUpdatedAt(new Date());

            CertificateModel updated = certificateRepo.save(certToUpdate);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(404).body("Certificado não encontrado para atualização");
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificate(@PathVariable String id) {
        if (certificateRepo.existsById(id)) {
            certificateRepo.deleteById(id);
            return ResponseEntity.ok("Certificado deletado com sucesso");
        } else {
            return ResponseEntity.status(404).body("Certificado não encontrado para exclusão");
        }
    }

}
