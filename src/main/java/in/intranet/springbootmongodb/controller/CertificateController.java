// Atualização completa com controle de permissões, validações e correções

package in.intranet.springbootmongodb.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.slugify.Slugify;
import in.intranet.springbootmongodb.dto.CertificateImportDto;
import in.intranet.springbootmongodb.enums.Roles;
import in.intranet.springbootmongodb.enums.Status;
import in.intranet.springbootmongodb.enums.Types;
import in.intranet.springbootmongodb.model.CertificateModel;
import in.intranet.springbootmongodb.model.UserModel;
import in.intranet.springbootmongodb.repository.CertificateRepository;
import in.intranet.springbootmongodb.repository.UserRepository;
import in.intranet.springbootmongodb.service.CloudinaryService;
import in.intranet.springbootmongodb.service.ExcelParserService;
import in.intranet.springbootmongodb.service.JwtService;
import in.intranet.springbootmongodb.service.ZipExtractService;
import in.intranet.springbootmongodb.utils.StatusUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateRepository certificateRepo;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final Cloudinary cloudinary;

    private boolean isAdminOrDirector(Set<Roles> roles) {
        return roles.contains(Roles.ADMINISTRADOR) || roles.contains(Roles.DIRETOR);
    }

    private Set<Roles> extractRolesFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Token JWT ausente ou inválido");
        }

        String jwt = authHeader.substring(7);
        String email = jwtService.extractEmail(jwt); // <- Certifique-se que este método existe
        Optional<UserModel> user = userRepo.findByEmail(email); // <- E que o repositório tem esse método

        return user.map(u -> new HashSet<>(u.getRoles()))
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado"));
    }

    private String extractPublicId(String fileUrl) {
        try {
            if (fileUrl == null || !fileUrl.contains("/certificados/")) return null;
            String path = fileUrl.substring(fileUrl.indexOf("/certificados/") + 1);
            return path.replaceAll("^raw/upload/v\\d+/", "");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair public_id: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CertificateModel>> getAllCertificates(HttpServletRequest request) {
        Set<Roles> roles = extractRolesFromToken(request);
        List<CertificateModel> certificates = certificateRepo.findAll();

        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getStatusSummary() {
        List<CertificateModel> certificates = certificateRepo.findAll();
        Map<String, Integer> summary = new HashMap<>();
        summary.put("noPrazo", 0);
        summary.put("aVencer", 0);
        summary.put("vencido", 0);
        for (CertificateModel cert : certificates) {
            if (cert.getStatus() == Status.NO_PRAZO) summary.put("noPrazo", summary.get("noPrazo") + 1);
            else if (cert.getStatus() == Status.A_VENCER) summary.put("aVencer", summary.get("aVencer") + 1);
            else if (cert.getStatus() == Status.VENCIDO) summary.put("vencido", summary.get("vencido") + 1);
        }
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{value}")
    public ResponseEntity<?> getCertificateByIdOrSlug(@PathVariable String value) {
        boolean isObjectId = value.matches("^[a-fA-F0-9]{24}$");
        Optional<CertificateModel> certificate = isObjectId
                ? certificateRepo.findById(value)
                : certificateRepo.findBySlug(value);
        return certificate.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Certificado não encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> createCertificate(@RequestBody CertificateModel certificate, HttpServletRequest request) {
        Set<Roles> roles = extractRolesFromToken(request);
        if (!isAdminOrDirector(roles))
            throw new AccessDeniedException("Apenas administradores ou diretores podem criar certificados");

        Slugify slugify = new Slugify();
        certificate.setSlug(slugify.slugify(certificate.getCompany()));
        certificate.setCreatedAt(new Date());
        certificate.setUpdatedAt(new Date());
        certificate.setStatus(StatusUtil.calculateStatus(certificate.getMaturityDate(), certificate.getCreatedAt()));

        return ResponseEntity.status(201).body(certificateRepo.save(certificate));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCertificate(@RequestPart("file") MultipartFile file,
                                               @RequestPart("certificate") String certificateJson,
                                               HttpServletRequest request) {
        Set<Roles> roles = extractRolesFromToken(request);
        if (!isAdminOrDirector(roles))
            throw new AccessDeniedException("Apenas administradores ou diretores podem enviar certificados");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
            CertificateModel certificate = objectMapper.readValue(certificateJson, CertificateModel.class);

            Slugify slugify = new Slugify();
            certificate.setSlug(slugify.slugify(certificate.getCompany()));

            String filename = file.getOriginalFilename().replaceAll("\\.pfx$", "");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "raw",
                    "public_id", "certificados/" + filename + ".pfx",
                    "overwrite", true));

            certificate.setFile(uploadResult.get("secure_url").toString());
            certificate.setCreatedAt(new Date());
            certificate.setUpdatedAt(new Date());
            certificate.setStatus(StatusUtil.calculateStatus(certificate.getMaturityDate(), certificate.getCreatedAt()));

            return ResponseEntity.status(201).body(certificateRepo.save(certificate));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao enviar certificado: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{value}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCertificate(@PathVariable String value,
                                               @RequestPart("certificate") String certificateJson,
                                               @RequestPart(value = "file", required = false) MultipartFile file,
                                               HttpServletRequest request) {
        Set<Roles> roles = extractRolesFromToken(request);
        if (!isAdminOrDirector(roles))
            throw new AccessDeniedException("Apenas administradores ou diretores podem editar certificados");

        boolean isObjectId = value.matches("^[a-fA-F0-9]{24}$");
        Optional<CertificateModel> existing = isObjectId
                ? certificateRepo.findById(value)
                : certificateRepo.findBySlug(value);

        if (existing.isEmpty()) return ResponseEntity.status(404).body("Certificado não encontrado para atualização");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
            CertificateModel input = objectMapper.readValue(certificateJson, CertificateModel.class);

            CertificateModel certToUpdate = existing.get();
            certToUpdate.setCompany(input.getCompany());
            certToUpdate.setCode(input.getCode());
            certToUpdate.setCpfCnpj(input.getCpfCnpj());
            certToUpdate.setMunicipality(input.getMunicipality());
            certToUpdate.setUf(input.getUf());
            certToUpdate.setType(input.getType());
            certToUpdate.setPassword(input.getPassword());
            certToUpdate.setMaturityDate(input.getMaturityDate());
            certToUpdate.setUpdatedAt(new Date());

            if (file != null && !file.isEmpty()) {
                String filename = file.getOriginalFilename().replaceAll("\\.pfx$", "");
                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                        "resource_type", "raw",
                        "public_id", "certificados/" + filename + ".pfx",
                        "overwrite", true));
                certToUpdate.setFile(uploadResult.get("secure_url").toString());
            }

            LocalDate today = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
            LocalDate maturityDate = certToUpdate.getMaturityDate().toInstant()
                    .atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(today, maturityDate);

            certToUpdate.setStatus(daysBetween < 0 ? Status.VENCIDO : daysBetween <= 5 ? Status.A_VENCER : Status.NO_PRAZO);

            return ResponseEntity.ok(certificateRepo.save(certToUpdate));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao atualizar certificado: " + e.getMessage());
        }
    }

    @DeleteMapping("/{value}")
    public ResponseEntity<?> deleteCertificate(@PathVariable String value, HttpServletRequest request) {
        Set<Roles> roles = extractRolesFromToken(request);
        if (!isAdminOrDirector(roles))
            throw new AccessDeniedException("Apenas administradores ou diretores podem deletar certificados");

        boolean isObjectId = value.matches("^[a-fA-F0-9]{24}$");
        Optional<CertificateModel> certificate = isObjectId
                ? certificateRepo.findById(value)
                : certificateRepo.findBySlug(value);

        return certificate.map(cert -> {
            try {
                String fileUrl = cert.getFile();
                String publicId = extractPublicId(fileUrl);
                if (publicId != null) {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
                }
                certificateRepo.deleteById(cert.getId());
                return ResponseEntity.ok("Certificado removido com sucesso.");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Erro ao excluir certificado: " + e.getMessage());
            }
        }).orElseGet(() -> ResponseEntity.status(404).body("Certificado não encontrado"));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMultipleCertificates(
            @RequestBody List<String> values,
            HttpServletRequest request
    ) {
        Set<Roles> roles = extractRolesFromToken(request);
        if (!isAdminOrDirector(roles))
            throw new AccessDeniedException("Apenas administradores ou diretores podem deletar certificados");

        List<String> erros = new ArrayList<>();

        for (String value : values) {
            boolean isObjectId = value.matches("^[a-fA-F0-9]{24}$");
            Optional<CertificateModel> certOpt = isObjectId
                    ? certificateRepo.findById(value)
                    : certificateRepo.findBySlug(value);

            if (certOpt.isPresent()) {
                try {
                    CertificateModel cert = certOpt.get();
                    String publicId = extractPublicId(cert.getFile());
                    if (publicId != null) {
                        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
                    }
                    certificateRepo.deleteById(cert.getId());
                } catch (Exception e) {
                    erros.add("Erro ao deletar " + value + ": " + e.getMessage());
                }
            } else {
                erros.add("Certificado não encontrado: " + value);
            }
        }

        if (erros.isEmpty()) {
            return ResponseEntity.ok("Todos os certificados foram removidos com sucesso.");
        } else {
            return ResponseEntity.status(207).body(erros); // HTTP 207 Multi-Status
        }
    }

}
