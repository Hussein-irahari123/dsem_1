package dsem.backend.service;

import dsem.backend.model.entity.AuditLog;
import dsem.backend.model.entity.User;
import dsem.backend.repository.AuditLogRepository;
import dsem.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public void log(String action, String entityType, Long entityId, String details) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User performer = userRepository.findByUsername(username).orElse(null);

        AuditLog log = AuditLog.builder()
                .action(action)
                .performedBy(performer)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();

        auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsForEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
}
