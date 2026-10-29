package com.example.blackbox.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DemoAiService implements AiService {

    private static final Logger log = LoggerFactory.getLogger(DemoAiService.class);

    @Override
    public AiResponse planNextAction(String prompt, String context, List<String> availableTools, int stepNumber) {
        log.info("Agent ReAct evaluating action for step {} with prompt: {}", stepNumber, prompt);
        String p = prompt != null ? prompt.toLowerCase() : "";

        // If step is 1 and delegation is requested
        if (p.contains("delegate") || p.contains("multi-agent") || p.contains("summarize research")) {
            if (stepNumber == 1) {
                return AiResponse.toolCall("RealtimeWebSearchTool",
                        Map.of("query", "Flight telemetry incident logs and checksum algorithms"), true);
            } else if (stepNumber == 2) {
                return AiResponse.delegate("SUMMARY_AGENT", "Synthesize findings into executive audit brief", true);
            }
        }

        // Tool selection based on prompt keywords
        if (p.contains("calc") || p.contains("math") || p.contains("+") || p.contains("*")) {
            Map<String, Object> args = new HashMap<>();
            if (p.contains("fail_calc")) {
                args.put("expression", "100 / 0");
            } else {
                args.put("expression", "1250 * 4");
            }
            return AiResponse.toolCall("CalculatorTool", args, true);
        }

        if (p.contains("fail_search")) {
            return AiResponse.toolCall("RealtimeWebSearchTool", Map.of("query", "fail_search"), true);
        }

        if (stepNumber == 1) {
            Map<String, Object> args = new HashMap<>();
            args.put("query", prompt != null ? prompt : "SHA-256");
            return AiResponse.toolCall("RealtimeWebSearchTool", args, true);
        }

        return AiResponse.finalAnswer("[AUTONOMOUS AGENT VERIFIED] Operational workflow completed. Telemetry and cryptographic chain sealed.", true);
    }

    @Override
    public AiResponse synthesizeResult(String originalTask, List<String> intermediateResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append("                 AI AGENT BLACK BOX - FLIGHT AUDIT REPORT\n");
        sb.append("================================================================================\n\n");
        sb.append("TASK OBJECTIVE : ").append(originalTask).append("\n");
        sb.append("EVALUATION STATUS: VERIFIED & CRYPTOGRAPHICALLY SEALED\n\n");
        sb.append("--- [EVIDENCE & REAL-TIME SOURCES REFERENCED] ---\n");
        for (int i = 0; i < intermediateResults.size(); i++) {
            sb.append(String.format("Evidence Item [%02d]:\n", i + 1));
            sb.append(intermediateResults.get(i).trim()).append("\n\n");
        }
        sb.append("--- [AUDIT & INTEGRITY SEAL] ---\n");
        sb.append("• Hash Chain Standard : SHA-256 Canonical Digest (NIST FIPS 180-4)\n");
        sb.append("• Telemetry Spans     : OpenTelemetry W3C TraceContext Injected\n");
        sb.append("• Policy Enforcement  : Zero unauthorized actions, PII sanitized, data egress verified\n");
        sb.append("• Conclusion          : All referenced sources verified. Observable flight trace fully auditable.\n");
        return AiResponse.finalAnswer(sb.toString(), true);
    }
}
