package dsem.backend.controller;

import dsem.backend.model.entity.Approval;
import dsem.backend.service.ApprovalService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping
    public ResponseEntity<List<Approval>> getAll() {
        return ResponseEntity.ok(approvalService.getAllApprovals());
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<Approval> approve(@PathVariable Long requestId,
                                            @RequestBody(required = false) CommentBody body) {
        String comment = (body != null) ? body.getComment() : "";
        return ResponseEntity.ok(approvalService.approveRequest(requestId, comment));
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Approval> reject(@PathVariable Long requestId,
                                           @RequestBody CommentBody body) {
        return ResponseEntity.ok(approvalService.rejectRequest(requestId, body.getComment()));
    }

    @Data
    static class CommentBody {
        private String comment;
    }
}
