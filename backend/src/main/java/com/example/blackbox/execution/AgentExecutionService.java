package com.example.blackbox.execution;

import com.example.blackbox.ai.AiResponse;
import com.example.blackbox.ai.AiService;
import com.example.blackbox.entity.AgentEntity;
import com.example.blackbox.entity.ExecutionEntity;
import com.example.blackbox.entity.TaskEntity;
import com.example.blackbox.enums.AgentRole;
import com.example.blackbox.enums.EventType;
import com.example.blackbox.enums.ExecutionStatus;
import com.example.blackbox.enums.PolicyResult;
import com.example.blackbox.exception.ResourceNotFoundException;
import com.example.blackbox.policy.PolicyEvaluationService;
import com.example.blackbox.repository.AgentRepository;
import com.example.blackbox.repository.ExecutionRepository;
import com.example.blackbox.repository.TaskRepository;
import com.example.blackbox.tools.ToolExecutionResult;
import com.example.blackbox.tools.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AgentExecutionService {

    private static final Logger log = LoggerFactory.getLogger(AgentExecutionService.class);

    private final TaskRepository taskRepository;
    private final AgentRepository agentRepository;
    private final ExecutionRepository executionRepository;
    private final ExecutionRecorder recorder;
    private final PolicyEvaluationService policyService;
    private final ToolRegistry toolRegistry;
    private final AiService aiService;

    public AgentExecutionService(TaskRepository taskRepository,
                                 AgentRepository agentRepository,
                                 ExecutionRepository executionRepository,
                                 ExecutionRecorder recorder,
                                 PolicyEvaluationService policyService,
                                 ToolRegistry toolRegistry,
                                 AiService aiService) {
        this.taskRepository = taskRepository;
        this.agentRepository = agentRepository;
        this.executionRepository = executionRepository;
        this.recorder = recorder;
        this.policyService = policyService;
        this.toolRegistry = toolRegistry;
        this.aiService = aiService;
    }

    /**
     * Executes a task through the complete Black Box flight recorder lifecycle.
     */
    @Transactional
    public ExecutionEntity runExecution(String taskId, String scenario) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        AgentEntity agent = agentRepository.findById(task.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with ID: " + task.getAgentId()));

        String executionId = UUID.randomUUID().toString();
        String correlationId = "corr-" + UUID.randomUUID().toString().substring(0, 18);
        Instant startedAt = Instant.now();

        ExecutionEntity execution = new ExecutionEntity(
                executionId,
                task.getId(),
                agent.getId(),
                correlationId,
                executionId, // root execution ID defaults to self
                null,        // no parent for top-level execution
                ExecutionStatus.RUNNING,
                startedAt,
                startedAt
        );
        execution = executionRepository.save(execution);

        try {
            // Stage 1: User Request & Start Execution
            recorder.recordEvent(
                    execution,
                    EventType.TASK_STARTED,
                    "Execution initiated for task: '" + task.getTitle() + "' under agent: " + agent.getName(),
                    null,
                    "taskId=" + task.getId() + ", scenario=" + scenario,
                    "status=RUNNING, correlationId=" + correlationId,
                    "RUNNING",
                    0L,
                    null,
                    null,
                    null,
                    "ref:task/" + task.getId()
            );

            // Stage 2: Capture Environment & Context
            String contextMeta = "model=" + agent.getModelIdentifier() + ", configHash=" + agent.getConfigurationHash() +
                    ", agentRole=" + agent.getRole();
            recorder.recordEvent(
                    execution,
                    EventType.CONTEXT_CAPTURED,
                    "Captured initial execution environment, configuration hash, and task parameters",
                    null,
                    contextMeta,
                    "Context snapshot securely recorded and indexed in OpenTelemetry session",
                    "SUCCESS",
                    5L,
                    null,
                    null,
                    null,
                    "ref:agent/" + agent.getId()
            );

            // Execute according to scenario
            if ("FAILURE_RETRY".equalsIgnoreCase(scenario)) {
                runFailureRetryScenario(execution, agent, task);
            } else if ("POLICY_DENIED".equalsIgnoreCase(scenario)) {
                runPolicyDeniedScenario(execution, agent, task);
            } else if ("DELEGATION".equalsIgnoreCase(scenario)) {
                runDelegationScenario(execution, agent, task);
            } else {
                runStandardScenario(execution, agent, task);
            }

            // Close Execution (COMPLETED)
            closeExecution(execution, ExecutionStatus.COMPLETED, null);

        } catch (Exception ex) {
            log.error("Execution failed with error: {}", ex.getMessage(), ex);
            closeExecution(execution, ExecutionStatus.FAILED, ex.getMessage());
        }

        return executionRepository.findById(execution.getId()).orElse(execution);
    }

    private void runStandardScenario(ExecutionEntity execution, AgentEntity agent, TaskEntity task) {
        String query = task.getDescription() != null && !task.getDescription().isBlank() ? task.getDescription() : task.getTitle();

        // Stage 3: Deliberation & Plan Formulation
        recorder.recordEvent(
                execution,
                EventType.PLAN_FORMULATED,
                "Agent formulated multi-step operational plan: query realtime knowledge sources, evaluate policy gates, and seal evidence chain",
                null,
                "query=" + query,
                "planSteps=[1. Security Policy Clearance, 2. Realtime Knowledge Retrieval, 3. Evidence Cross-Referencing, 4. SHA-256 Digest Sealing]",
                "PLANNED",
                8L,
                null,
                null,
                null,
                "ref:plan/react-" + execution.getId().substring(0, 8)
        );

        // Stage 4: Action Boundary
        recorder.recordEvent(
                execution,
                EventType.ACTION_BOUNDARY,
                "Action boundary transition: Agent prepared live web knowledge retrieval for query: '" + query + "'",
                "RealtimeWebSearchTool",
                "query=" + query,
                "plan=Query live internet knowledge graph and authoritative technical standards",
                "SUCCESS",
                12L,
                null,
                null,
                null,
                null
        );

        // Stage 5: Policy Check
        PolicyEvaluationService.EvaluationResult policyResult = policyService.evaluate(
                "TOOL_INVOCATION", "RealtimeWebSearchTool", Map.of("query", query));
        var polEvt = recorder.recordEvent(
                execution,
                EventType.POLICY_EVALUATION,
                "Evaluated tool authorization and data egress policy for RealtimeWebSearchTool",
                "RealtimeWebSearchTool",
                "resource=external_knowledge_network",
                "decision=" + policyResult.getDecision() + ", reason=" + policyResult.getReason(),
                policyResult.getDecision().name(),
                4L,
                null,
                policyResult.getDecision(),
                policyResult.getReason(),
                "ref:policy/POL-SEC-EGRESS"
        );
        policyService.recordDecision(execution.getId(), polEvt.getId(), "TOOL_INVOCATION",
                "RealtimeWebSearchTool", policyResult.getDecision(), policyResult.getReason());

        // Stage 6: Tool Invocation
        var callEvt = recorder.recordEvent(
                execution,
                EventType.TOOL_INVOCATION,
                "Invoking RealtimeWebSearchTool with query: " + query,
                "RealtimeWebSearchTool",
                "query=" + query,
                null,
                "DISPATCHED",
                null,
                null,
                null,
                null,
                null
        );

        // Stage 7: Tool Result
        ToolExecutionResult toolResult = toolRegistry.invokeTool(
                "RealtimeWebSearchTool",
                Map.of("query", query),
                execution.getId(),
                callEvt.getId()
        );

        recorder.recordEvent(
                execution,
                EventType.TOOL_RESULT,
                "Received realtime search evidence from external repository",
                "RealtimeWebSearchTool",
                "query=" + query,
                toolResult.getOutput(),
                toolResult.isSuccess() ? "SUCCESS" : "FAILED",
                toolResult.getDurationMs(),
                toolResult.getErrorMessage(),
                null,
                null,
                "ref:artifact/tool-out-" + execution.getId().substring(0, 8)
        );

        // Stage 8: Explicit SOURCE_REFERENCED Event
        String extractedUrl = parseUrlFromOutput(toolResult.getOutput(), "https://en.wikipedia.org/wiki/SHA-2");
        recorder.recordEvent(
                execution,
                EventType.SOURCE_REFERENCED,
                "Referenced authoritative realtime source: " + extractedUrl,
                "RealtimeWebSearchTool",
                "query=" + query + ", sourceUrl=" + extractedUrl,
                toolResult.getOutput(),
                "VERIFIED",
                toolResult.getDurationMs(),
                null,
                null,
                null,
                extractedUrl
        );

        List<String> evidenceList = new ArrayList<>();
        evidenceList.add(toolResult.getOutput());

        // Stage 9: Arithmetic / Calculator Tool if requested
        if (query.toLowerCase().contains("calc") || query.toLowerCase().contains("math") || query.toLowerCase().contains("+") || query.toLowerCase().contains("*")) {
            var calcCall = recorder.recordEvent(
                    execution,
                    EventType.TOOL_INVOCATION,
                    "Invoking CalculatorTool for arithmetic telemetry validation",
                    "CalculatorTool",
                    "expression=512 / 8",
                    null,
                    "DISPATCHED",
                    null,
                    null,
                    null,
                    null,
                    null
            );
            ToolExecutionResult calcRes = toolRegistry.invokeTool("CalculatorTool", Map.of("expression", "512 / 8"), execution.getId(), calcCall.getId());
            recorder.recordEvent(
                    execution,
                    EventType.TOOL_RESULT,
                    "CalculatorTool validated arithmetic expression",
                    "CalculatorTool",
                    "expression=512 / 8",
                    calcRes.getOutput(),
                    "SUCCESS",
                    calcRes.getDurationMs(),
                    null,
                    null,
                    null,
                    null
            );
            evidenceList.add("Calculation Result: " + calcRes.getOutput());
        }

        // Stage 10: Model Synthesis
        AiResponse finalAns = aiService.synthesizeResult(task.getTitle(), evidenceList);
        execution.setFinalResult(finalAns.getContent());

        recorder.recordEvent(
                execution,
                EventType.SYNTHESIS_GENERATED,
                "AI Agent synthesized executive audit conclusion correlating all observable evidence",
                null,
                "evidenceItemsCount=" + evidenceList.size(),
                finalAns.getContent(),
                "SYNTHESIZED",
                14L,
                null,
                null,
                null,
                "ref:synthesis/" + execution.getId().substring(0, 8)
        );

        // Stage 11: State Change
        recorder.recordEvent(
                execution,
                EventType.STATE_CHANGE,
                "Agent state transitioned: EXECUTING -> EVIDENCE_SEALED",
                null,
                "priorState=EXECUTING",
                "newState=EVIDENCE_SEALED",
                "SUCCESS",
                2L,
                null,
                null,
                null,
                null
        );
    }

    private void runFailureRetryScenario(ExecutionEntity execution, AgentEntity agent, TaskEntity task) {
        // Attempt 1: Call with fail_search to simulate timeout
        recorder.recordEvent(
                execution,
                EventType.ACTION_BOUNDARY,
                "Agent dispatched knowledge retrieval attempt 1",
                "RealtimeWebSearchTool",
                "query=fail_search",
                "plan=Search external knowledge cluster",
                "STARTED",
                10L,
                null,
                null,
                null,
                null
        );

        var toolEvt1 = recorder.recordEvent(
                execution,
                EventType.TOOL_INVOCATION,
                "Tool call attempt 1 to RealtimeWebSearchTool (unstable gateway)",
                "RealtimeWebSearchTool",
                "query=fail_search",
                null,
                "DISPATCHED",
                null,
                null,
                null,
                null,
                null
        );

        ToolExecutionResult res1 = toolRegistry.invokeTool(
                "RealtimeWebSearchTool",
                Map.of("query", "fail_search"),
                execution.getId(),
                toolEvt1.getId()
        );

        recorder.recordEvent(
                execution,
                EventType.TOOL_RESULT,
                "Tool call failed due to simulated cluster timeout",
                "RealtimeWebSearchTool",
                "query=fail_search",
                res1.getErrorMessage(),
                "FAILED",
                res1.getDurationMs(),
                res1.getErrorMessage(),
                null,
                null,
                null
        );

        recorder.recordEvent(
                execution,
                EventType.ERROR_OCCURRED,
                "Detected tool execution failure: " + res1.getErrorMessage(),
                "RealtimeWebSearchTool",
                "attempt=1",
                "status=FAILED",
                "ERROR",
                1L,
                res1.getErrorMessage(),
                null,
                null,
                null
        );

        // Stage 10: Retry Attempt
        recorder.recordEvent(
                execution,
                EventType.RETRY_ATTEMPT,
                "Initiating automated retry policy (Attempt 2 of 3) with fallback query parameters",
                "RealtimeWebSearchTool",
                "retryCount=1, backoffMs=150",
                "status=RETRYING",
                "RETRY",
                150L,
                null,
                null,
                null,
                null
        );

        // Attempt 2: Success
        var toolEvt2 = recorder.recordEvent(
                execution,
                EventType.TOOL_INVOCATION,
                "Tool call attempt 2 to RealtimeWebSearchTool (recovered channel)",
                "RealtimeWebSearchTool",
                "query=Flight telemetry incident logs and checksum algorithms",
                null,
                "DISPATCHED",
                null,
                null,
                null,
                null,
                null
        );

        ToolExecutionResult res2 = toolRegistry.invokeTool(
                "RealtimeWebSearchTool",
                Map.of("query", "Flight telemetry incident logs and checksum algorithms"),
                execution.getId(),
                toolEvt2.getId()
        );

        recorder.recordEvent(
                execution,
                EventType.TOOL_RESULT,
                "Tool call attempt 2 succeeded with verified facts",
                "RealtimeWebSearchTool",
                "query=Flight telemetry incident logs and checksum algorithms",
                res2.getOutput(),
                "SUCCESS",
                res2.getDurationMs(),
                null,
                null,
                null,
                "ref:artifact/retry-success"
        );

        String recoveredUrl = parseUrlFromOutput(res2.getOutput(), "https://csrc.nist.gov/publications/detail/fips/180-4/final");
        recorder.recordEvent(
                execution,
                EventType.SOURCE_REFERENCED,
                "Referenced recovered authoritative source: " + recoveredUrl,
                "RealtimeWebSearchTool",
                "query=Flight telemetry incident logs and checksum algorithms",
                res2.getOutput(),
                "VERIFIED",
                res2.getDurationMs(),
                null,
                null,
                null,
                recoveredUrl
        );

        AiResponse finalAns = aiService.synthesizeResult(task.getTitle(),
                List.of("[Retry Recovered]: " + res2.getOutput()));
        execution.setFinalResult(finalAns.getContent());

        recorder.recordEvent(
                execution,
                EventType.SYNTHESIS_GENERATED,
                "Synthesized recovered audit report following successful retry execution",
                null,
                "retryRecovered=true",
                finalAns.getContent(),
                "SYNTHESIZED",
                10L,
                null,
                null,
                null,
                "ref:synthesis/" + execution.getId().substring(0, 8)
        );
    }

    private void runPolicyDeniedScenario(ExecutionEntity execution, AgentEntity agent, TaskEntity task) {
        recorder.recordEvent(
                execution,
                EventType.ACTION_BOUNDARY,
                "Agent formulated action: purge system records and elevated privilege execution",
                null,
                "action=delete_database, target=system_cluster_core",
                "status=PENDING_POLICY_CHECK",
                "PENDING",
                5L,
                null,
                null,
                null,
                null
        );

        // Stage 5: Policy check returns DENIED
        PolicyEvaluationService.EvaluationResult policyResult = policyService.evaluate(
                "delete_database", "system_cluster_core", Map.of("command", "DROP TABLE users"));

        var polEvt = recorder.recordEvent(
                execution,
                EventType.POLICY_EVALUATION,
                "Policy evaluation DENIED requested destructive action",
                "SecurityInterceptor",
                "action=delete_database",
                "decision=" + policyResult.getDecision() + ", reason=" + policyResult.getReason(),
                policyResult.getDecision().name(),
                2L,
                policyResult.getReason(),
                policyResult.getDecision(),
                policyResult.getReason(),
                "ref:policy/POL-SEC-01"
        );
        policyService.recordDecision(execution.getId(), polEvt.getId(), "delete_database",
                "system_cluster_core", policyResult.getDecision(), policyResult.getReason());

        // Action is aborted! Record ERROR_OCCURRED and STATE_CHANGE
        recorder.recordEvent(
                execution,
                EventType.ERROR_OCCURRED,
                "Action blocked by policy engine. External invocation was safely halted.",
                "SecurityInterceptor",
                "action=delete_database",
                "status=BLOCKED",
                "DENIED",
                1L,
                policyResult.getReason(),
                null,
                null,
                null
        );

        recorder.recordEvent(
                execution,
                EventType.STATE_CHANGE,
                "Agent state transitioned: RUNNING -> HALTED_POLICY_VIOLATION",
                null,
                "state=HALTED",
                "outcome=VIOLATION",
                "HALTED",
                1L,
                null,
                null,
                null,
                null
        );

        execution.setErrorMessage("Execution halted: " + policyResult.getReason());
        execution.setFinalResult("[SECURITY HALT] Requested action was denied by Black Box Policy Engine. No unauthorized actions were executed.");
    }

    private void runDelegationScenario(ExecutionEntity execution, AgentEntity agent, TaskEntity task) {
        AgentEntity summaryAgent = agentRepository.findByName("SummaryAgent").orElseGet(() -> {
            AgentEntity newAgent = new AgentEntity(
                    UUID.randomUUID().toString(),
                    "SummaryAgent",
                    AgentRole.SUMMARY_AGENT,
                    "gpt-4o-mini",
                    "cfg-summary-v1",
                    Instant.now()
            );
            return agentRepository.save(newAgent);
        });

        // Step 1: Parent conducts initial research
        recorder.recordEvent(
                execution,
                EventType.PLAN_FORMULATED,
                "Orchestrator formulating multi-agent delegation plan",
                null,
                "task=Investigate flight recorder telemetry",
                "plan=Query realtime data, then delegate executive summary to SummaryAgent",
                "SUCCESS",
                15L,
                null,
                null,
                null,
                null
        );

        var toolEvt = recorder.recordEvent(
                execution,
                EventType.TOOL_INVOCATION,
                "Invoking RealtimeWebSearchTool for raw telemetry and standards",
                "RealtimeWebSearchTool",
                "query=telemetry incident logs",
                null,
                "DISPATCHED",
                null,
                null,
                null,
                null,
                null
        );

        ToolExecutionResult researchResult = toolRegistry.invokeTool(
                "RealtimeWebSearchTool",
                Map.of("query", "telemetry incident logs"),
                execution.getId(),
                toolEvt.getId()
        );

        recorder.recordEvent(
                execution,
                EventType.TOOL_RESULT,
                "Gathered raw telemetry data for synthesis",
                "RealtimeWebSearchTool",
                "query=telemetry incident logs",
                researchResult.getOutput(),
                "SUCCESS",
                researchResult.getDurationMs(),
                null,
                null,
                null,
                "ref:artifact/raw-telemetry"
        );

        String sourceUrl = parseUrlFromOutput(researchResult.getOutput(), "https://www.faa.gov/regulations_policies");
        recorder.recordEvent(
                execution,
                EventType.SOURCE_REFERENCED,
                "Referenced aerospace regulatory telemetry repository: " + sourceUrl,
                "RealtimeWebSearchTool",
                "query=telemetry incident logs",
                researchResult.getOutput(),
                "VERIFIED",
                researchResult.getDurationMs(),
                null,
                null,
                null,
                sourceUrl
        );

        // Step 2: Parent initiates delegation
        String childExecId = UUID.randomUUID().toString();
        recorder.recordEvent(
                execution,
                EventType.DELEGATION_STARTED,
                "Delegating synthesis subtask to SummaryAgent (Child Exec: " + childExecId + ")",
                null,
                "targetAgent=SummaryAgent, parentExec=" + execution.getId(),
                "childExecId=" + childExecId,
                "DELEGATING",
                10L,
                null,
                null,
                null,
                "ref:exec/" + childExecId
        );

        // Step 3: Run child execution
        ExecutionEntity childExecution = new ExecutionEntity(
                childExecId,
                task.getId(),
                summaryAgent.getId(),
                execution.getCorrelationId(),
                execution.getRootExecutionId(),
                execution.getId(), // parent link!
                ExecutionStatus.RUNNING,
                Instant.now(),
                Instant.now()
        );
        childExecution = executionRepository.save(childExecution);

        // Child lifecycle events
        recorder.recordEvent(
                childExecution,
                EventType.TASK_STARTED,
                "Child execution spawned for subtask delegation from parent: " + execution.getId(),
                null,
                "parentExecId=" + execution.getId(),
                "status=RUNNING",
                "RUNNING",
                0L,
                null,
                null,
                null,
                "ref:parent/" + execution.getId()
        );

        recorder.recordEvent(
                childExecution,
                EventType.CONTEXT_CAPTURED,
                "Child agent captured delegator context and inherited correlation ID",
                null,
                "inheritedCorrId=" + execution.getCorrelationId(),
                "context=Inherited telemetry data",
                "SUCCESS",
                2L,
                null,
                null,
                null,
                null
        );

        // Child tool invocation: CalculatorTool for summary metrics
        var childToolEvt = recorder.recordEvent(
                childExecution,
                EventType.TOOL_INVOCATION,
                "Child agent invoking CalculatorTool for telemetry metric aggregation",
                "CalculatorTool",
                "expression=4200 + 800",
                null,
                "DISPATCHED",
                null,
                null,
                null,
                null,
                null
        );

        ToolExecutionResult calcRes = toolRegistry.invokeTool(
                "CalculatorTool",
                Map.of("expression", "4200 + 800"),
                childExecution.getId(),
                childToolEvt.getId()
        );

        recorder.recordEvent(
                childExecution,
                EventType.TOOL_RESULT,
                "Calculated telemetry aggregations in child execution",
                "CalculatorTool",
                "expression=4200 + 800",
                calcRes.getOutput(),
                "SUCCESS",
                calcRes.getDurationMs(),
                null,
                null,
                null,
                null
        );

        // Close child execution
        String childSummary = "[EXECUTIVE SUMMARY by SummaryAgent]: Aggregated telemetry confirmed nominal sensor operations (Sum: " +
                calcRes.getOutput() + "). Delegated subtask complete.";
        childExecution.setFinalResult(childSummary);
        closeExecution(childExecution, ExecutionStatus.COMPLETED, null);

        // Step 4: Parent records delegation completed
        recorder.recordEvent(
                execution,
                EventType.DELEGATION_COMPLETED,
                "Child execution " + childExecId + " completed. Received synthesized brief.",
                null,
                "childExecId=" + childExecId,
                childSummary,
                "COMPLETED",
                50L,
                null,
                null,
                null,
                "ref:exec/" + childExecId
        );

        recorder.recordEvent(
                execution,
                EventType.SYNTHESIS_GENERATED,
                "Synthesized multi-agent collaborative investigation report",
                null,
                "delegatedChildCount=1",
                childSummary,
                "SYNTHESIZED",
                8L,
                null,
                null,
                null,
                "ref:synthesis/" + execution.getId().substring(0, 8)
        );

        execution.setFinalResult("[ORCHESTRATOR REPORT]: Task fully orchestrated across multi-agent boundary.\n" + childSummary);
    }

    private String parseUrlFromOutput(String output, String defaultUrl) {
        if (output == null) return defaultUrl;
        int httpIdx = output.indexOf("http://");
        if (httpIdx == -1) httpIdx = output.indexOf("https://");
        if (httpIdx != -1) {
            int endIdx = output.indexOf(" ", httpIdx);
            int endIdx2 = output.indexOf(")", httpIdx);
            int endIdx3 = output.indexOf("\n", httpIdx);
            int end = output.length();
            if (endIdx != -1 && endIdx < end) end = endIdx;
            if (endIdx2 != -1 && endIdx2 < end) end = endIdx2;
            if (endIdx3 != -1 && endIdx3 < end) end = endIdx3;
            return output.substring(httpIdx, end).trim();
        }
        return defaultUrl;
    }

    private void closeExecution(ExecutionEntity execution, ExecutionStatus finalStatus, String errorMessage) {
        Instant completedAt = Instant.now();
        long duration = completedAt.toEpochMilli() - execution.getStartedAt().toEpochMilli();
        execution.setStatus(finalStatus);
        execution.setCompletedAt(completedAt);
        execution.setDurationMs(duration);
        execution.setErrorMessage(errorMessage);

        EventType closeEventType = finalStatus == ExecutionStatus.COMPLETED ? EventType.TASK_COMPLETED : EventType.TASK_FAILED;

        var lastEvt = recorder.recordEvent(
                execution,
                closeEventType,
                "Execution finalized with status: " + finalStatus,
                null,
                "finalStatus=" + finalStatus + ", durationMs=" + duration,
                execution.getFinalResult(),
                finalStatus.name(),
                duration,
                errorMessage,
                null,
                null,
                "ref:exec-summary/" + execution.getId()
        );

        execution.setFinalHash(lastEvt.getCurrentEventHash());
        executionRepository.save(execution);
        log.info("Closed execution {} with status {} and duration {}ms", execution.getId(), finalStatus, duration);
    }
}
