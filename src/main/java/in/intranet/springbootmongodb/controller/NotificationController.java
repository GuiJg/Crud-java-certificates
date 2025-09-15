package in.intranet.springbootmongodb.controller;

import in.intranet.springbootmongodb.enums.Roles;
import in.intranet.springbootmongodb.model.NotificationModel;
import in.intranet.springbootmongodb.model.UserModel;
import in.intranet.springbootmongodb.repository.NotificationRepository;
import in.intranet.springbootmongodb.repository.UserRepository;
import in.intranet.springbootmongodb.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtService jwtService;

    private boolean isAdminOrDirector(Set<Roles> roles) {
        return roles.contains(Roles.ADMINISTRADOR) || roles.contains(Roles.DIRETOR);
    }

    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(notificationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody NotificationModel model) {
        model.setDate(new Date());
        NotificationModel saved = notificationRepository.save(model);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id, HttpServletRequest request) {
        Set<Roles> roles = extractRolesFromToken(request);
        if (!isAdminOrDirector(roles)) {
            throw new AccessDeniedException("Apenas administradores ou diretores podem deletar notificações");
        }

        if (!notificationRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Notificação não encontrada");
        }

        notificationRepository.deleteById(id);
        return ResponseEntity.ok("Notificação deletada com sucesso");
    }

    private Set<Roles> extractRolesFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Token JWT ausente ou inválido");
        }

        String jwt = authHeader.substring(7);
        String email = jwtService.extractEmail(jwt);
        Optional<UserModel> user = userRepo.findByEmail(email);

        return user.map(u -> new HashSet<>(u.getRoles()))
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado"));
    }
}
