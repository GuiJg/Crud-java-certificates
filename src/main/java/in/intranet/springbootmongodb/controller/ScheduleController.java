// --- ScheduleController.java ---

package in.intranet.springbootmongodb.controller;

import in.intranet.springbootmongodb.enums.Roles;
import in.intranet.springbootmongodb.model.ScheduleModel;
import in.intranet.springbootmongodb.model.UserModel;
import in.intranet.springbootmongodb.repository.ScheduleRepository;
import in.intranet.springbootmongodb.repository.UserRepository;
import in.intranet.springbootmongodb.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepo;

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

    private String extractEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Token JWT ausente ou inválido");
        }
        String jwt = authHeader.substring(7);
        return jwtService.extractEmail(jwt);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleModel>> getAllSchedules() {
        return ResponseEntity.ok(scheduleRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable String id) {
        Optional<ScheduleModel> schedule = scheduleRepository.findById(id);
        return schedule.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Agendamento não encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleModel model, HttpServletRequest request) {
        String email = extractEmail(request);
        model.setCreatedBy(email);
        model.setCreatedAt(new Date());
        model.setUpdatedAt(new Date());
        return ResponseEntity.status(201).body(scheduleRepository.save(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable String id, @RequestBody ScheduleModel model, HttpServletRequest request) {
        Optional<ScheduleModel> existing = scheduleRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body("Agendamento não encontrado");
        }

        ScheduleModel scheduleToUpdate = existing.get();
        scheduleToUpdate.setName(model.getName());
        scheduleToUpdate.setCompany(model.getCompany());
        scheduleToUpdate.setSubject(model.getSubject());
        scheduleToUpdate.setPhone(model.getPhone());
        scheduleToUpdate.setEmail(model.getEmail());
        scheduleToUpdate.setDate(model.getDate());
        scheduleToUpdate.setTime(model.getTime());
        scheduleToUpdate.setUpdatedAt(new Date());

        return ResponseEntity.ok(scheduleRepository.save(scheduleToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable String id) {
        if (!scheduleRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Agendamento não encontrado");
        }
        scheduleRepository.deleteById(id);
        return ResponseEntity.ok("Agendamento removido com sucesso");
    }
}
