# AI Agent Black Box — Flight Recorder for Autonomous AI Agents

> **A Production-Grade Java 21–26 / Spring Boot 3.3 / Spring AI / JavaFX Application for Observable AI Agent Telemetry, Real-Time Web Provenance, Correlated Evidence, and SHA-256 Tamper-Evident Hash Chain Integrity Verification.**

---

## 1. Abstract

**AI Agent Black Box** is a flight recorder system designed for autonomous AI agents. Much like an aviation flight data recorder (FDR) observes and captures critical aircraft telemetry without piloting the aircraft, the AI Agent Black Box non-intrusively monitors agent execution and produces structured, chronologically sequenced, cryptographically chained evidence. 

It records:
- **Observable Agent Actions & Step-by-Step Plans** (`PLAN_FORMULATED`, `ACTION_BOUNDARY`)
- **Real-Time Knowledge Sources & Citations** (`SOURCE_REFERENCED`: Live Wikipedia REST API, NIST specifications, FAA regulations)
- **Tool Invocations & Execution Telemetry** (`TOOL_INVOCATION`, `TOOL_RESULT`: arguments, latencies, HTTP status)
- **Security Policy Gates & Interceptions** (`POLICY_EVALUATION`: PII redaction, access boundaries)
- **Multi-Agent Parent-Child Delegations** (`DELEGATION_STARTED`, `DELEGATION_COMPLETED`: correlation tracking)
- **Failures, Retries & Error Tracing** (`ERROR_OCCURRED`, `RETRY_ATTEMPT`: backoff latencies preserved)
- **Cryptographic Sealing & Verification** (SHA-256 genesis-to-leaf hash chaining with OpenTelemetry W3C TraceContext)

---

## 2. Key Problem Solved

Modern software architectures increasingly rely on autonomous AI agents to search external APIs, execute tools, synthesize reports, and delegate tasks to sub-agents. When an agent malfunctions, hallucinates, violates security boundaries, or yields unexpected results, developers cannot reconstruct the sequence of events because:
- **Scattered Logs:** Inputs, tool calls, and model outputs are fragmented across disparate services.
- **External Source Gaps:** Developers do not know what real-time sources or web URLs the agent consulted.
- **Provenance Disconnections:** Multi-agent parent-child delegations lose correlation across distributed processes.
- **Ephemeral Retries:** Failed tool calls and subsequent retry attempts are overwritten or silently swallowed.
- **Lack of Auditability & Integrity:** Conventional database records and text logs can be altered without detection.

The **AI Agent Black Box** provides full observability, provenance tracking, and cryptographic integrity.

---

## 3. Technology Stack & Architecture

| Layer | Technology | Version | Purpose & Justification |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 21 LTS (Java 25/26 compatible) | High-performance, virtual threads, pattern matching, strong typing. |
| **Backend Framework** | Spring Boot | 3.3.5 | Enterprise REST API, Spring Data JPA, Actuator health & metrics. |
| **Agent Architecture** | Spring AI & ReAct Engine | 1.0 Ready | Autonomous reasoning and tool-calling execution pipeline. |
| **Database** | PostgreSQL / H2 | 16 (Pg) / 2.3 (H2) | Relational storage; Flyway migrations; zero-config in-memory H2 fallback. |
| **Messaging** | Apache Kafka | 3.7 (KRaft) | High-throughput distributed event streaming with partition ordering. |
| **Observability** | OpenTelemetry SDK | 1.43.0 | W3C trace context propagation (`traceId`, `spanId`) bound into hash chain. |
| **Security & Integrity** | Java Security APIs | SHA-256 | Standard `MessageDigest` SHA-256 with deterministic canonical strings. |
| **Real-Time Sources** | Live Wikipedia REST API & NIST | HTTP/2 | Live external knowledge retrieval with citation and SHA-256 digests. |
| **Desktop Client** | JavaFX | 21.0.6 | Modern dark aerospace-themed UI with charts, visual flow graphs, and source cards. |
| **Testing** | JUnit 5 & Testcontainers | 5.10.3 / 1.20.1 | 27 automated unit, integration, and containerized tests. |

---

## 4. Complete Execution Lifecycle (12 Stages)

```
[1. TASK_STARTED]
       │
       ▼
[2. CONTEXT_CAPTURED] (Environment & Configuration Hash)
       │
       ▼
[3. PLAN_FORMULATED] (Autonomous AI Deliberation Plan)
       │
       ▼
[4. ACTION_BOUNDARY] (Transition from Internal Thought to External Gate)
       │
       ▼
[5. POLICY_EVALUATION] (Security Policy Engine: ALLOWED / DENIED)
       │
       ├─────────────────────────────────┐
       ▼ (if allowed)                    ▼ (if denied)
[6. TOOL_INVOCATION]              [ERROR_OCCURRED / HALTED]
       │
       ▼
[7. TOOL_RESULT] (Output, Latency, Status)
       │
       ▼
[8. SOURCE_REFERENCED] (Realtime Web URL, Snippet, SHA-256 Evidence Digest)
       │
       ├─────────────────────────────────┐
       ▼ (if multi-agent)                │
[9. DELEGATION_STARTED & COMPLETED]      │
       │                                 │
       ▼                                 ▼
[10. SYNTHESIS_GENERATED] (AI Conclusion with Cited Proof)
       │
       ▼
[11. STATE_CHANGE] (Agent State Transition)
       │
       ▼
[12. TASK_COMPLETED / TASK_FAILED] (Final SHA-256 Flight Seal)
```

---

## 5. Quick Start — One-Click Execution

### Option A: The Easiest Way (One-Click Master Launcher)
Double-click `run.bat` (or `scripts\run-all.bat`):
```cmd
run.bat
```
This automatically:
1. Detects your installed Java (`JAVA_HOME`) and Maven (`mvn`).
2. Starts the Spring Boot backend on **http://localhost:8081** (free of port 8080 conflicts).
3. Waits for the backend to become healthy.
4. Automatically opens the JavaFX Desktop Flight Recorder UI!

---

### Option B: Individual Terminal Startup

#### 1. Start the Spring Boot Backend (Port 8081):
```cmd
scripts\start-backend.bat
```
*(Or via Maven: `mvn spring-boot:run -pl backend`)*

#### 2. Start the JavaFX Desktop Client:
```cmd
scripts\start-client.bat
```
*(Or via Maven: `mvn javafx:run -pl desktop-client`)*

---

## 6. How to Use the JavaFX Flight Recorder Dashboard

1. **Dashboard (📊):**
   - Live backend health indicator (`● ONLINE http://localhost:8081`).
   - 4 Top Metric Cards (Total Flights, Success/Fail Ratio, Total Chained Events, Integrity Anomalies).
   - Recent Flights table and Event Volume BarChart.
   - Live Security Policy Interceptions and Error table.

2. **Agents Directory (🤖):**
   - Inspect registered autonomous agents (`OrchestratorAgent`, `ResearchAgent`, `SummaryAgent`, `SecurityAgent`).
   - Register new agents with model identifiers and configuration hashes.

3. **Tasks & Interactive Flight Launchpad (📋):**
   - Click one of the **Quick Presets**:
     - `🌐 SHA-256 Cryptography`: Live research on NIST FIPS 180-4 standard and hash chaining.
     - `✈️ Flight FDR Telemetry`: Audit flight recorder sensor integrity and FAA AC 20-141B compliance.
     - `🤖 Spring AI Architecture`: Explore ReAct prompt templates, tool security, and OTel tracing.
     - `🛡️ Policy Violation Test`: Attempts unauthorized command (`DROP TABLE users`) and verifies policy blockage.
     - `⚡ Retry & Recovery`: Simulates network failure with automatic retry backoff.
   - Or type **ANY custom question** in the prompt box!
   - Ensure `🌐 Search Live Internet Sources` is checked.
   - Click **▶ LAUNCH AGENT FLIGHT**.

4. **Sources & Referrals Inspector (🌐):**
   - Displays all external real-time sources consulted by the agent during the task.
   - Shows source badge (`LIVE_WIKIPEDIA_API`, `NIST_SPECIFICATION`, `FAA_REGULATION`), HTTP 200 OK status, retrieval latency (ms), clickable URL, verified excerpt, and SHA-256 digest!

5. **Execution Flow Graph (🕸):**
   - Select an execution and click **Render Pipeline**.
   - Displays an interactive step-by-step visual diagram of the agent's decision flow.
   - Click any stage node to inspect its exact inputs, outputs, OpenTelemetry span, and hashes in the side inspector.

6. **Forensic Evidence Inspector (🔍):**
   - Deep cryptographic inspection of each event block.
   - Displays W3C `trace_id` and `span_id`, sanitized inputs, sanitized outputs, and policy reasons.

7. **SHA-256 Integrity Verification (🔒):**
   - Click **🔒 VERIFY INTEGRITY** to verify the hash chain from the Genesis block (64 zeros).
   - Displays a green banner `INTEGRITY_VERIFIED (CRYPTOGRAPHICALLY INTACT)`.
   - Click **⚠ Tamper Event (Dev Demonstration)** to simulate unauthorized tampering in the database.
   - Click **VERIFY INTEGRITY** again: the banner turns red `INTEGRITY_COMPROMISED (TAMPER DETECTED)`, pinpointing the exact sequence number that was altered and calculating downstream broken blocks!

8. **Analytics & Performance Charts (📈):**
   - Execution Outcome Distribution Pie Chart
   - Security Policy Clearance Pie Chart
   - Tool & Source Invocations Frequency Bar Chart
   - Event Stage Distribution Bar Chart
   - Average flight latency and success rate gauges.

---

## 7. Automated Test Suite

Run all automated unit, integration, and Testcontainers tests:
```bash
mvn test
```
**Results: 27 Tests Run, 0 Failures, 0 Errors, 100% Passing!**
- `IntegrityServiceTest` (5 tests): SHA-256 determinism, collision avoidance, valid chain verification, payload tampering detection, sequence gap anomaly.
- `RealtimeWebSearchToolTest` (3 tests): Live Wikipedia API search, error handling, and NIST authoritative fallback with SHA-256 digests.
- `AgentExecutionServiceTest` (4 tests): 12-stage lifecycle, failure & retry preservation, policy denial halt, and multi-agent child delegation.
- `BlackBoxIntegrationTest` (4 tests): Agents API, Tasks API, Statistics API, and Demo Scenario & Tampering REST endpoint.
- `PolicyEvaluationServiceTest` (3 tests): ALLOWED, DENIED, and REQUIRES_APPROVAL rules.
- `ToolExecutionTest` (2 tests): Arithmetic calculator and search tool execution.
- `RedactionServiceTest` (1 test): Sensitive credential and token sanitization.
- `EventPersistenceAndDeduplicationTest` (1 test): Kafka duplicate delivery prevention.
- `BlackBoxTestcontainersTest` (1 test): PostgreSQL ephemeral container test.
- `ApiClientTest` (3 tests): Desktop client model deserialization.

---

## 8. Documentation Index

- [`docs/architecture.md`](docs/architecture.md) — Layered architecture, component responsibilities, and data flows.
- [`docs/database-schema.md`](docs/database-schema.md) — PostgreSQL table definitions, relationships, indexes, and constraints.
- [`docs/event-lifecycle.md`](docs/event-lifecycle.md) — The 12 stages of the execution lifecycle and event transitions.
- [`docs/integrity-design.md`](docs/integrity-design.md) — SHA-256 canonicalization, hash chain algorithm, and threat model.
- [`docs/api-reference.md`](docs/api-reference.md) — Complete REST endpoint documentation with request/response payloads.
- [`docs/viva-questions.md`](docs/viva-questions.md) — 26 comprehensive viva examination questions with detailed answers.
