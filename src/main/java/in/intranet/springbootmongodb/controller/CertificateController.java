package in.intranet.springbootmongodb.controller;

import in.intranet.springbootmongodb.model.CertificateModel;
import in.intranet.springbootmongodb.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    @Autowired
    private CertificateRepository certificateRepo;

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

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificateById(@PathVariable String id) {
        Optional<CertificateModel> certificate = certificateRepo.findById(id);
        return certificate
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Certificado não encontrado"));
    }

    // POST (CREATE)
    @PostMapping
    public ResponseEntity<?> createCertificate(@RequestBody CertificateModel certificate) {
        certificate.setCreatedAt(new Date());
        certificate.setUpdatedAt(new Date());
        CertificateModel saved = certificateRepo.save(certificate);
        return ResponseEntity.status(201).body(saved);
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
