package dsem.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SMS Notification Service backed by Africa's Talking API.
 * Currently mocked — replace with real AT SDK when credentials are available.
 */
@Service
@Slf4j
public class SmsNotificationService {

    @Value("${africas.talking.username}")
    private String username;

    @Value("${africas.talking.api-key}")
    private String apiKey;

    /**
     * Send an SMS to the given phone number.
     * Replace the body of this method with AfricasTalking SDK call.
     */
    public void sendSms(String phoneNumber, String message) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            log.warn("SMS skipped — no phone number provided");
            return;
        }
        // TODO: Integrate AfricasTalking SDK
        // AfricasTalking at = new AfricasTalking(username, apiKey);
        // at.getSMS().send(message, new String[]{phoneNumber}, "DSEM");
        log.info("[SMS MOCK] To: {} | Msg: {}", phoneNumber, message);
    }

    public void notifyPendingApproval(String managerPhone, String requestedBy, String explosive) {
        sendSms(managerPhone,
            String.format("DSEM: New request from %s for %s awaiting your approval.", requestedBy, explosive));
    }

    public void notifyApprovalDecision(String userPhone, Long requestId, boolean approved, String reason) {
        String status = approved ? "APPROVED" : "REJECTED";
        String msg = approved
            ? String.format("DSEM: Your request #%d has been %s and is ready for dispatch.", requestId, status)
            : String.format("DSEM: Your request #%d was %s. Reason: %s", requestId, status, reason);
        sendSms(userPhone, msg);
    }

    public void notifyLowStock(String managerPhone, String explosiveName, double currentStock) {
        sendSms(managerPhone,
            String.format("DSEM ALERT: %s stock is low (%.1f units remaining). Please reorder.", explosiveName, currentStock));
    }
}
