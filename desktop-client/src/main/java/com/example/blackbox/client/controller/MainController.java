package com.example.blackbox.client.controller;

import com.example.blackbox.client.model.*;
import com.example.blackbox.client.service.ApiClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.Instant;
import java.util.*;

public class MainController implements Initializable {

    private final ApiClient apiClient = new ApiClient();

    // Navigation buttons
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavAgents;
    @FXML private Button btnNavTasks;
    @FXML private Button btnNavTimeline;
    @FXML private Button btnNavGraph;
    @FXML private Button btnNavEvidence;
    @FXML private Button btnNavIntegrity;
    @FXML private Button btnNavComparison;
    @FXML private Button btnNavStats;
    @FXML private Button btnNavDemo;

    // View containers
    @FXML private VBox viewDashboard;
    @FXML private VBox viewAgents;
    @FXML private VBox viewTasks;
    @FXML private VBox viewTimeline;
    @FXML private VBox viewGraph;
    @FXML private VBox viewEvidence;
    @FXML private VBox viewIntegrity;
    @FXML private VBox viewComparison;
    @FXML private VBox viewStats;
    @FXML private VBox viewDemo;

    // Header
    @FXML private Label lblBackendStatus;

    // Dashboard controls
    @FXML private Label lblMetricTotalExecutions;
    @FXML private Label lblMetricSuccess;
    @FXML private Label lblMetricFailed;
    @FXML private Label lblMetricTotalEvents;
    @FXML private Label lblMetricIntegrityViolations;
    @FXML private TableView<ExecutionModel> tblDashboardExecutions;
    @FXML private TableColumn<ExecutionModel, String> colDashExecId;
    @FXML private TableColumn<ExecutionModel, String> colDashExecStatus;
    @FXML private TableColumn<ExecutionModel, String> colDashExecDuration;
    @FXML private TableColumn<ExecutionModel, String> colDashExecEvents;
    @FXML private TableColumn<ExecutionModel, String> colDashExecHash;
    @FXML private TableColumn<ExecutionModel, String> colDashExecStarted;

    @FXML private BarChart<String, Number> chartActivity;
    @FXML private CategoryAxis axisActivityX;
    @FXML private NumberAxis axisActivityY;

    @FXML private TableView<AgentEventModel> tblRecentErrors;
    @FXML private TableColumn<AgentEventModel, String> colErrSeq;
    @FXML private TableColumn<AgentEventModel, String> colErrTime;
    @FXML private TableColumn<AgentEventModel, String> colErrType;
    @FXML private TableColumn<AgentEventModel, String> colErrExecId;
    @FXML private TableColumn<AgentEventModel, String> colErrStatus;
    @FXML private TableColumn<AgentEventModel, String> colErrDetails;

    // Agents controls
    @FXML private TableView<AgentModel> tblAgents;
    @FXML private TableColumn<AgentModel, String> colAgentName;
    @FXML private TableColumn<AgentModel, String> colAgentRole;
    @FXML private TableColumn<AgentModel, String> colAgentModel;
    @FXML private TableColumn<AgentModel, String> colAgentConfigHash;
    @FXML private TableColumn<AgentModel, String> colAgentCreated;
    @FXML private TextField txtNewAgentName;
    @FXML private ComboBox<String> cmbNewAgentRole;
    @FXML private TextField txtNewAgentModel;

    // Tasks controls
    @FXML private TableView<TaskModel> tblTasks;
    @FXML private TableColumn<TaskModel, String> colTaskTitle;
    @FXML private TableColumn<TaskModel, String> colTaskDesc;
    @FXML private TableColumn<TaskModel, String> colTaskStatus;
    @FXML private TableColumn<TaskModel, String> colTaskCreated;
    @FXML private Label lblSelectedTaskTitle;
    @FXML private ComboBox<String> cmbScenario;
    @FXML private TextField txtNewTaskTitle;
    @FXML private TextArea txtNewTaskDesc;
    @FXML private ComboBox<AgentModel> cmbTaskAgent;

    // Timeline controls
    @FXML private ComboBox<String> cmbTimelineExecution;
    @FXML private ComboBox<String> cmbFilterEventType;
    @FXML private TextField txtFilterStatus;
    @FXML private TableView<AgentEventModel> tblTimelineEvents;
    @FXML private TableColumn<AgentEventModel, String> colTimeSeq;
    @FXML private TableColumn<AgentEventModel, String> colTimeType;
    @FXML private TableColumn<AgentEventModel, String> colTimeDesc;
    @FXML private TableColumn<AgentEventModel, String> colTimeTool;
    @FXML private TableColumn<AgentEventModel, String> colTimeStatus;
    @FXML private TableColumn<AgentEventModel, String> colTimeDuration;
    @FXML private TableColumn<AgentEventModel, String> colTimePrevHash;
    @FXML private TableColumn<AgentEventModel, String> colTimeCurrHash;
    @FXML private TextArea txtTimelineDetail;

    // Graph controls
    @FXML private ComboBox<String> cmbGraphExecution;
    @FXML private VBox boxGraphCanvas;
    @FXML private TreeView<String> treeExecutionGraph;
    @FXML private TextArea txtGraphNodeDetails;

    // Evidence controls
    @FXML private ComboBox<String> cmbEvidenceExecution;
    @FXML private ListView<String> listEvidenceEvents;
    @FXML private Label lblEvId;
    @FXML private Label lblEvTraceId;
    @FXML private Label lblEvSpanId;
    @FXML private Label lblEvPrevHash;
    @FXML private Label lblEvCurrHash;
    @FXML private TextArea txtEvInput;
    @FXML private TextArea txtEvOutput;
    @FXML private TextArea txtEvPolicy;
    private List<AgentEventModel> currentEvidenceEvents = new ArrayList<>();

    // Integrity controls
    @FXML private ComboBox<String> cmbIntegrityExecution;
    @FXML private VBox boxIntegrityBanner;
    @FXML private Label lblIntegrityStatusText;
    @FXML private Label lblIntegrityReason;
    @FXML private Label lblIntegTotalEvents;
    @FXML private Label lblIntegInvalidSeq;
    @FXML private Label lblIntegExpectedHash;
    @FXML private Label lblIntegActualHash;
    @FXML private Label lblIntegDownstreamBroken;

    // Comparison controls
    @FXML private ComboBox<String> cmbCompareExec1;
    @FXML private ComboBox<String> cmbCompareExec2;
    @FXML private Label lblCompTitle1;
    @FXML private Label lblCompTitle2;
    @FXML private TextArea txtCompDetails1;
    @FXML private TextArea txtCompDetails2;

    // Stats controls (Real JavaFX charts & metric labels)
    @FXML private Label lblStatsAvgDuration;
    @FXML private Label lblStatsSuccessRate;
    @FXML private Label lblStatsTotalTools;
    @FXML private Label lblStatsTotalPolicies;
    @FXML private PieChart chartExecutionStatus;
    @FXML private PieChart chartPolicyDecisions;
    @FXML private BarChart<String, Number> chartToolUsage;
    @FXML private CategoryAxis axisToolX;
    @FXML private NumberAxis axisToolY;
    @FXML private BarChart<String, Number> chartEventDistribution;
    @FXML private CategoryAxis axisEventX;
    @FXML private NumberAxis axisEventY;

    // Demo controls
    @FXML private TextArea txtDemoConsole;

    private TaskModel selectedTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTables();
        initComboBoxes();
        handleRefreshAll();

        // Selection listeners
        tblTasks.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedTask = newV;
                lblSelectedTaskTitle.setText(newV.getTitle() + " (ID: " + newV.getId().substring(0, Math.min(8, newV.getId().length())) + "...)");
            }
        });

        tblTimelineEvents.getSelectionModel().selectedItemProperty().addListener((obs, oldV, event) -> {
            if (event != null) {
                txtTimelineDetail.setText(
                        "Event #" + event.getSequenceNumber() + " [" + event.getEventType() + "]\n" +
                        "Description: " + event.getDescription() + "\n" +
                        "Tool: " + (event.getToolName() != null ? event.getToolName() : "N/A") + " | Status: " + event.getStatus() + "\n" +
                        "Input: " + event.getSanitizedInput() + "\n" +
                        "Output: " + event.getSanitizedOutput() + "\n" +
                        "Prev Hash: " + event.getPreviousEventHash() + "\n" +
                        "Curr Hash: " + event.getCurrentEventHash()
                );
            }
        });

        listEvidenceEvents.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, index) -> {
            if (index != null && index.intValue() >= 0 && index.intValue() < currentEvidenceEvents.size()) {
                displayEvidenceDetail(currentEvidenceEvents.get(index.intValue()));
            }
        });
    }

    private void initTables() {
        // Dashboard table
        colDashExecId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDashExecStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDashExecDuration.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDurationMs() != null ? cell.getValue().getDurationMs() + " ms" : "-"));
        colDashExecEvents.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getEventCount() != null ? cell.getValue().getEventCount().toString() : "0"));
        colDashExecHash.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFinalHash() != null ? cell.getValue().getFinalHash().substring(0, Math.min(24, cell.getValue().getFinalHash().length())) + "..." : "pending"));
        colDashExecStarted.setCellValueFactory(new PropertyValueFactory<>("startedAt"));

        // Recent Errors table on Dashboard
        colErrSeq.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getSequenceNumber() != null ? "#" + cell.getValue().getSequenceNumber() : ""));
        colErrTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colErrType.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        colErrExecId.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getExecutionId() != null ?
                        cell.getValue().getExecutionId().substring(0, Math.min(10, cell.getValue().getExecutionId().length())) + "..." : ""));
        colErrStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colErrDetails.setCellValueFactory(cell -> {
            AgentEventModel e = cell.getValue();
            StringBuilder sb = new StringBuilder();
            if (e.getDescription() != null) sb.append(e.getDescription());
            if (e.getErrorDetails() != null && !e.getErrorDetails().isBlank()) sb.append(" | ").append(e.getErrorDetails());
            if (e.getPolicyReason() != null && !e.getPolicyReason().isBlank()) sb.append(" | Reason: ").append(e.getPolicyReason());
            return new SimpleStringProperty(sb.toString());
        });

        // Agents table
        colAgentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAgentRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colAgentModel.setCellValueFactory(new PropertyValueFactory<>("modelIdentifier"));
        colAgentConfigHash.setCellValueFactory(new PropertyValueFactory<>("configurationHash"));
        colAgentCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Tasks table
        colTaskTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTaskDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTaskStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTaskCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Timeline table
        colTimeSeq.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getSequenceNumber() != null ? cell.getValue().getSequenceNumber().toString() : ""));
        colTimeType.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        colTimeDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTimeTool.setCellValueFactory(new PropertyValueFactory<>("toolName"));
        colTimeStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTimeDuration.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDurationMs() != null ? cell.getValue().getDurationMs() + " ms" : "-"));
        colTimePrevHash.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getPreviousEventHash() != null ? cell.getValue().getPreviousEventHash().substring(0, Math.min(14, cell.getValue().getPreviousEventHash().length())) + "..." : ""));
        colTimeCurrHash.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCurrentEventHash() != null ? cell.getValue().getCurrentEventHash().substring(0, Math.min(14, cell.getValue().getCurrentEventHash().length())) + "..." : ""));
    }

    private void initComboBoxes() {
        cmbNewAgentRole.setItems(FXCollections.observableArrayList(
                "ORCHESTRATOR_AGENT", "RESEARCH_AGENT", "SUMMARY_AGENT", "CALCULATOR_AGENT", "SECURITY_AGENT"));
        cmbNewAgentRole.setValue("RESEARCH_AGENT");

        cmbScenario.setItems(FXCollections.observableArrayList(
                "STANDARD", "FAILURE_RETRY", "POLICY_DENIED", "DELEGATION"));
        cmbScenario.setValue("STANDARD");

        cmbFilterEventType.setItems(FXCollections.observableArrayList(
                "ALL", "TASK_STARTED", "CONTEXT_CAPTURED", "ACTION_BOUNDARY", "POLICY_EVALUATION",
                "TOOL_INVOCATION", "TOOL_RESULT", "STATE_CHANGE", "DELEGATION_STARTED",
                "DELEGATION_COMPLETED", "RETRY_ATTEMPT", "ERROR_OCCURRED", "TASK_COMPLETED", "TASK_FAILED"));
        cmbFilterEventType.setValue("ALL");
    }

    // ==========================================
    // NAVIGATION SWITCHING
    // ==========================================
    private void hideAllViews() {
        viewDashboard.setVisible(false);
        viewAgents.setVisible(false);
        viewTasks.setVisible(false);
        viewTimeline.setVisible(false);
        viewGraph.setVisible(false);
        viewEvidence.setVisible(false);
        viewIntegrity.setVisible(false);
        viewComparison.setVisible(false);
        viewStats.setVisible(false);
        viewDemo.setVisible(false);

        List<Button> buttons = List.of(btnNavDashboard, btnNavAgents, btnNavTasks, btnNavTimeline,
                btnNavGraph, btnNavEvidence, btnNavIntegrity, btnNavComparison, btnNavStats, btnNavDemo);
        for (Button b : buttons) {
            b.getStyleClass().remove("nav-button-active");
        }
    }

    @FXML public void showDashboard() { hideAllViews(); viewDashboard.setVisible(true); btnNavDashboard.getStyleClass().add("nav-button-active"); }
    @FXML public void showAgents() { hideAllViews(); viewAgents.setVisible(true); btnNavAgents.getStyleClass().add("nav-button-active"); }
    @FXML public void showTasks() { hideAllViews(); viewTasks.setVisible(true); btnNavTasks.getStyleClass().add("nav-button-active"); }
    @FXML public void showTimeline() { hideAllViews(); viewTimeline.setVisible(true); btnNavTimeline.getStyleClass().add("nav-button-active"); }
    @FXML public void showGraph() { hideAllViews(); viewGraph.setVisible(true); btnNavGraph.getStyleClass().add("nav-button-active"); }
    @FXML public void showEvidence() { hideAllViews(); viewEvidence.setVisible(true); btnNavEvidence.getStyleClass().add("nav-button-active"); }
    @FXML public void showIntegrity() { hideAllViews(); viewIntegrity.setVisible(true); btnNavIntegrity.getStyleClass().add("nav-button-active"); }
    @FXML public void showComparison() { hideAllViews(); viewComparison.setVisible(true); btnNavComparison.getStyleClass().add("nav-button-active"); }
    @FXML public void showStats() { hideAllViews(); viewStats.setVisible(true); btnNavStats.getStyleClass().add("nav-button-active"); }
    @FXML public void showDemo() { hideAllViews(); viewDemo.setVisible(true); btnNavDemo.getStyleClass().add("nav-button-active"); }

    // ==========================================
    // REFRESH & ASYNCHRONOUS DATA LOADING
    // ==========================================
    @FXML
    public void handleRefreshAll() {
        // 1. Load Statistics
        apiClient.getStatistics().thenAccept(stats -> Platform.runLater(() -> {
            lblBackendStatus.setText("● ONLINE (http://localhost:8080)");
            lblBackendStatus.getStyleClass().removeAll("status-badge-offline");
            lblBackendStatus.getStyleClass().add("status-badge");

            lblMetricTotalExecutions.setText(String.valueOf(stats.getTotalExecutions()));
            lblMetricSuccess.setText(String.valueOf(stats.getSuccessfulExecutions()));
            lblMetricFailed.setText(String.valueOf(stats.getFailedExecutions()));
            lblMetricTotalEvents.setText(String.valueOf(stats.getTotalEvents()));
            lblMetricIntegrityViolations.setText(String.valueOf(stats.getIntegrityFailures()));

            renderStatisticsView(stats);
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                lblBackendStatus.setText("● OFFLINE (Connection Failed)");
                lblBackendStatus.getStyleClass().removeAll("status-badge");
                lblBackendStatus.getStyleClass().add("status-badge-offline");
            });
            return null;
        });

        // 2. Load Executions
        apiClient.getExecutions().thenAccept(execs -> Platform.runLater(() -> {
            tblDashboardExecutions.setItems(FXCollections.observableArrayList(execs));
            updateDashboardActivityChart(execs);

            List<String> ids = execs.stream().map(ExecutionModel::getId).toList();
            cmbTimelineExecution.setItems(FXCollections.observableArrayList(ids));
            cmbGraphExecution.setItems(FXCollections.observableArrayList(ids));
            cmbEvidenceExecution.setItems(FXCollections.observableArrayList(ids));
            cmbIntegrityExecution.setItems(FXCollections.observableArrayList(ids));
            cmbCompareExec1.setItems(FXCollections.observableArrayList(ids));
            cmbCompareExec2.setItems(FXCollections.observableArrayList(ids));

            if (!ids.isEmpty()) {
                if (cmbTimelineExecution.getValue() == null) cmbTimelineExecution.setValue(ids.get(0));
                if (cmbGraphExecution.getValue() == null) cmbGraphExecution.setValue(ids.get(0));
                if (cmbEvidenceExecution.getValue() == null) cmbEvidenceExecution.setValue(ids.get(0));
                if (cmbIntegrityExecution.getValue() == null) cmbIntegrityExecution.setValue(ids.get(0));
                if (cmbCompareExec1.getValue() == null) cmbCompareExec1.setValue(ids.get(0));
                if (ids.size() > 1 && cmbCompareExec2.getValue() == null) cmbCompareExec2.setValue(ids.get(1));
            }
        }));

        // 3. Load Recent Errors / Denials on Dashboard
        apiClient.getRecentErrors().thenAccept(errors -> Platform.runLater(() -> {
            tblRecentErrors.setItems(FXCollections.observableArrayList(errors));
        })).exceptionally(ex -> null);

        // 4. Load Agents
        apiClient.getAgents().thenAccept(agents -> Platform.runLater(() -> {
            tblAgents.setItems(FXCollections.observableArrayList(agents));
            cmbTaskAgent.setItems(FXCollections.observableArrayList(agents));
            if (!agents.isEmpty() && cmbTaskAgent.getValue() == null) {
                cmbTaskAgent.setValue(agents.get(0));
            }
        }));

        // 5. Load Tasks
        apiClient.getTasks().thenAccept(tasks -> Platform.runLater(() -> {
            tblTasks.setItems(FXCollections.observableArrayList(tasks));
            if (!tasks.isEmpty() && selectedTask == null) {
                tblTasks.getSelectionModel().select(0);
            }
        }));
    }

    private void updateDashboardActivityChart(List<ExecutionModel> execs) {
        chartActivity.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Events Count");

        List<ExecutionModel> recent = execs.size() > 8 ? execs.subList(Math.max(0, execs.size() - 8), execs.size()) : execs;
        for (ExecutionModel e : recent) {
            String label = e.getId().substring(0, Math.min(8, e.getId().length()));
            int count = e.getEventCount() != null ? e.getEventCount() : 0;
            series.getData().add(new XYChart.Data<>(label, count));
        }
        chartActivity.getData().add(series);
    }

    // ==========================================
    // AGENT & TASK ACTIONS
    // ==========================================
    @FXML
    public void handleCreateAgent() {
        String name = txtNewAgentName.getText();
        String role = cmbNewAgentRole.getValue();
        String model = txtNewAgentModel.getText();

        if (name == null || name.isBlank()) return;

        apiClient.createAgent(name, role, model).thenAccept(created -> Platform.runLater(() -> {
            logDemo("Registered new agent: " + created.getName() + " [" + created.getRole() + "]");
            txtNewAgentName.clear();
            handleRefreshAll();
        }));
    }

    @FXML
    public void handleCreateTask() {
        String title = txtNewTaskTitle.getText();
        String desc = txtNewTaskDesc.getText();
        AgentModel agent = cmbTaskAgent.getValue();

        if (title == null || title.isBlank() || agent == null) return;

        apiClient.createTask(title, desc, agent.getId()).thenAccept(created -> Platform.runLater(() -> {
            logDemo("Created new task: " + created.getTitle() + " assigned to agent " + agent.getName());
            txtNewTaskTitle.clear();
            txtNewTaskDesc.clear();
            handleRefreshAll();
        }));
    }

    @FXML
    public void handleLaunchExecution() {
        if (selectedTask == null) {
            logDemo("Please select a task from the table to launch.");
            return;
        }

        String scenario = cmbScenario.getValue() != null ? cmbScenario.getValue() : "STANDARD";
        logDemo("Launching execution for task: " + selectedTask.getTitle() + " (Scenario: " + scenario + ")...");

        apiClient.executeTask(selectedTask.getId(), scenario).thenAccept(exec -> Platform.runLater(() -> {
            logDemo("Execution FINISHED: ID=" + exec.getId() + " | Status=" + exec.getStatus() +
                    " | Events=" + exec.getEventCount() + " | Final Hash=" +
                    (exec.getFinalHash() != null ? exec.getFinalHash().substring(0, Math.min(16, exec.getFinalHash().length())) + "..." : ""));
            handleRefreshAll();
            cmbTimelineExecution.setValue(exec.getId());
            handleLoadTimeline();
            showTimeline();
        })).exceptionally(ex -> {
            Platform.runLater(() -> logDemo("Execution failed: " + ex.getMessage()));
            return null;
        });
    }

    // ==========================================
    // TIMELINE ACTIONS
    // ==========================================
    @FXML
    public void handleLoadTimeline() {
        String execId = cmbTimelineExecution.getValue();
        if (execId == null) return;

        apiClient.getExecutionTimeline(execId).thenAccept(events -> Platform.runLater(() -> {
            tblTimelineEvents.setItems(FXCollections.observableArrayList(events));
            if (!events.isEmpty()) {
                tblTimelineEvents.getSelectionModel().select(0);
            }
        }));
    }

    @FXML
    public void handleApplyTimelineFilter() {
        String execId = cmbTimelineExecution.getValue();
        if (execId == null) return;

        String selectedType = cmbFilterEventType.getValue();
        String statusFilter = txtFilterStatus.getText();

        apiClient.getExecutionTimeline(execId).thenAccept(events -> Platform.runLater(() -> {
            var filtered = events.stream().filter(e -> {
                boolean matchType = selectedType == null || "ALL".equals(selectedType) || selectedType.equalsIgnoreCase(e.getEventType());
                boolean matchStatus = statusFilter == null || statusFilter.isBlank() ||
                        (e.getStatus() != null && e.getStatus().toLowerCase().contains(statusFilter.toLowerCase().trim()));
                return matchType && matchStatus;
            }).toList();
            tblTimelineEvents.setItems(FXCollections.observableArrayList(filtered));
        }));
    }

    // ==========================================
    // EXECUTION GRAPH (VISUAL DIAGRAM + HIERARCHY)
    // ==========================================
    @FXML
    public void handleRenderGraph() {
        String rootId = cmbGraphExecution.getValue();
        if (rootId == null) return;

        apiClient.getExecution(rootId).thenAccept(rootExec -> {
            apiClient.getChildExecutions(rootId).thenAccept(children -> {
                apiClient.getExecutionTimeline(rootId).thenAccept(events -> Platform.runLater(() -> {
                    // 1. Populate Tree Outline
                    TreeItem<String> rootItem = new TreeItem<>("Root Flight: " + rootExec.getId() + " [" + rootExec.getStatus() + "]");
                    rootItem.setExpanded(true);

                    if (children.isEmpty()) {
                        rootItem.getChildren().add(new TreeItem<>("No child delegations (Single agent execution)"));
                    } else {
                        for (ExecutionModel child : children) {
                            TreeItem<String> childItem = new TreeItem<>("Delegated Child: " + child.getId() + " [" + child.getStatus() + "]");
                            childItem.getChildren().add(new TreeItem<>("Result: " + (child.getFinalResult() != null ? child.getFinalResult() : "None")));
                            rootItem.getChildren().add(childItem);
                        }
                    }
                    treeExecutionGraph.setRoot(rootItem);

                    // 2. Set Provenance Inspector Details
                    txtGraphNodeDetails.setText(
                            "EXECUTION PROVENANCE HIERARCHY:\n" +
                            "Root ID: " + rootExec.getId() + "\n" +
                            "Correlation ID: " + rootExec.getCorrelationId() + "\n" +
                            "Status: " + rootExec.getStatus() + "\n" +
                            "Duration: " + rootExec.getDurationMs() + " ms\n" +
                            "Events Count: " + rootExec.getEventCount() + "\n" +
                            "Child Delegations: " + children.size() + "\n\n" +
                            "Final Observable Result:\n" + rootExec.getFinalResult()
                    );

                    // 3. Render Rich Visual Interactive Node Cards in Canvas
                    renderVisualGraph(rootExec, children, events);
                }));
            });
        });
    }

    private void renderVisualGraph(ExecutionModel rootExec, List<ExecutionModel> children, List<AgentEventModel> events) {
        boxGraphCanvas.getChildren().clear();

        // 1. Root Agent Card
        VBox rootCard = new VBox(10);
        rootCard.getStyleClass().add("graph-card-root");
        rootCard.setOnMouseClicked(e -> txtGraphNodeDetails.setText(
                "ROOT AGENT FLIGHT NODE:\n" +
                "ID: " + rootExec.getId() + "\n" +
                "Status: " + rootExec.getStatus() + "\n" +
                "Started: " + rootExec.getStartedAt() + "\n" +
                "Duration: " + rootExec.getDurationMs() + " ms\n" +
                "Events Recorded: " + rootExec.getEventCount() + "\n" +
                "Final Cryptographic Seal:\n" + rootExec.getFinalHash() + "\n\n" +
                "Final Result:\n" + rootExec.getFinalResult()
        ));

        // Root Card Header
        HBox rootHeader = new HBox(12);
        rootHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblRootBadge = new Label("🤖 ROOT FLIGHT RECORDER");
        lblRootBadge.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-font-size: 13px;");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label lblRootStatus = new Label(rootExec.getStatus());
        lblRootStatus.setStyle("FAILED".equalsIgnoreCase(rootExec.getStatus())
                ? "-fx-background-color: #7f1d1d; -fx-text-fill: #fca5a5; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-weight: bold;"
                : "-fx-background-color: #064e3b; -fx-text-fill: #6ee7b7; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-weight: bold;");
        rootHeader.getChildren().addAll(lblRootBadge, spacer1, lblRootStatus);

        Label lblRootMeta = new Label("Flight ID: " + rootExec.getId() + "  |  Duration: " + (rootExec.getDurationMs() != null ? rootExec.getDurationMs() + " ms" : "-") + "  |  Total Events: " + rootExec.getEventCount());
        lblRootMeta.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        // Embedded Policy Evaluations & Tool Invocations
        VBox actionsBox = new VBox(6);
        actionsBox.setStyle("-fx-padding: 6px 0 0 0;");

        boolean hasSubactions = false;
        for (AgentEventModel ev : events) {
            if ("POLICY_EVALUATION".equalsIgnoreCase(ev.getEventType())) {
                hasSubactions = true;
                HBox polBox = new HBox(8);
                polBox.setAlignment(Pos.CENTER_LEFT);
                polBox.getStyleClass().add("graph-subcard-policy");
                Label pIcon = new Label("🛡 POLICY CHECK:");
                pIcon.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 11px;");
                Label pVal = new Label(ev.getPolicyResult() + " (" + ev.getPolicyReason() + ")");
                pVal.setStyle("DENIED".equalsIgnoreCase(ev.getPolicyResult())
                        ? "-fx-text-fill: #f87171; -fx-font-size: 11px; -fx-font-weight: bold;"
                        : "-fx-text-fill: #34d399; -fx-font-size: 11px;");
                polBox.getChildren().addAll(pIcon, pVal);
                actionsBox.getChildren().add(polBox);
            } else if ("TOOL_INVOCATION".equalsIgnoreCase(ev.getEventType()) || "TOOL_RESULT".equalsIgnoreCase(ev.getEventType())) {
                hasSubactions = true;
                HBox toolBox = new HBox(8);
                toolBox.setAlignment(Pos.CENTER_LEFT);
                toolBox.getStyleClass().add("graph-subcard-tool");
                Label tIcon = new Label("🔧 TOOL:");
                tIcon.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 11px;");
                Label tName = new Label((ev.getToolName() != null ? ev.getToolName() : "Tool") + " [" + ev.getStatus() + "]");
                tName.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 11px;");
                Label tLatency = new Label(ev.getDurationMs() != null ? "(" + ev.getDurationMs() + " ms)" : "");
                tLatency.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
                toolBox.getChildren().addAll(tIcon, tName, tLatency);
                actionsBox.getChildren().add(toolBox);
            }
        }

        rootCard.getChildren().addAll(rootHeader, lblRootMeta);
        if (hasSubactions) {
            Label lblSubTitle = new Label("Intercepted Actions, Policies & Tool Invocations:");
            lblSubTitle.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 11px; -fx-font-weight: bold;");
            rootCard.getChildren().addAll(lblSubTitle, actionsBox);
        }

        boxGraphCanvas.getChildren().add(rootCard);

        // 2. Multi-Agent Delegation Links & Child Cards
        if (children != null && !children.isEmpty()) {
            for (ExecutionModel child : children) {
                // Delegation Connector Arrow
                VBox connectorBox = new VBox(2);
                connectorBox.setAlignment(Pos.CENTER);
                Label arrow = new Label("⬇  🔀 MULTI-AGENT SUBTASK DELEGATION  ⬇");
                arrow.setStyle("-fx-text-fill: #a78bfa; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8px 0;");
                connectorBox.getChildren().add(arrow);
                boxGraphCanvas.getChildren().add(connectorBox);

                // Child Node Card
                VBox childCard = new VBox(8);
                childCard.getStyleClass().add("graph-card-child");
                childCard.setOnMouseClicked(e -> txtGraphNodeDetails.setText(
                        "DELEGATED CHILD FLIGHT NODE:\n" +
                        "Child Execution ID: " + child.getId() + "\n" +
                        "Parent Execution ID: " + child.getParentExecutionId() + "\n" +
                        "Status: " + child.getStatus() + "\n" +
                        "Duration: " + child.getDurationMs() + " ms\n" +
                        "Events Count: " + child.getEventCount() + "\n" +
                        "Final Cryptographic Seal:\n" + child.getFinalHash() + "\n\n" +
                        "Final Observable Output:\n" + child.getFinalResult()
                ));

                HBox childHeader = new HBox(10);
                childHeader.setAlignment(Pos.CENTER_LEFT);
                Label lblChildBadge = new Label("👥 DELEGATED SUB-AGENT FLIGHT");
                lblChildBadge.setStyle("-fx-text-fill: #a78bfa; -fx-font-weight: bold; -fx-font-size: 12px;");
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                Label lblChildStatus = new Label(child.getStatus());
                lblChildStatus.setStyle("-fx-background-color: #064e3b; -fx-text-fill: #6ee7b7; -fx-padding: 2px 6px; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;");
                childHeader.getChildren().addAll(lblChildBadge, spacer2, lblChildStatus);

                Label lblChildMeta = new Label("Child ID: " + child.getId() + "  |  Parent: " + child.getParentExecutionId() + "  |  Duration: " + (child.getDurationMs() != null ? child.getDurationMs() + " ms" : "-"));
                lblChildMeta.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

                Label lblChildRes = new Label("Result: " + (child.getFinalResult() != null ? child.getFinalResult() : "None"));
                lblChildRes.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 11px; -fx-font-family: monospace;");

                childCard.getChildren().addAll(childHeader, lblChildMeta, lblChildRes);
                boxGraphCanvas.getChildren().add(childCard);
            }
        } else {
            // Note for single agent
            VBox noteBox = new VBox();
            noteBox.setAlignment(Pos.CENTER);
            noteBox.setStyle("-fx-padding: 12px; -fx-background-color: #0f172a; -fx-background-radius: 6px; -fx-border-color: #1e293b; -fx-border-radius: 6px;");
            Label lblNote = new Label("ℹ Single Agent Execution — All tools and policy evaluations executed directly by the primary agent without subtask delegation.");
            lblNote.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
            noteBox.getChildren().add(lblNote);
            boxGraphCanvas.getChildren().add(noteBox);
        }
    }

    // ==========================================
    // EVIDENCE INSPECTOR
    // ==========================================
    @FXML
    public void handleInspectEvidence() {
        String execId = cmbEvidenceExecution.getValue();
        if (execId == null) return;

        apiClient.getExecutionTimeline(execId).thenAccept(events -> Platform.runLater(() -> {
            currentEvidenceEvents = events;
            List<String> eventLabels = events.stream()
                    .map(e -> "#" + e.getSequenceNumber() + " [" + e.getEventType() + "] " + e.getDescription())
                    .toList();
            listEvidenceEvents.setItems(FXCollections.observableArrayList(eventLabels));
            if (!events.isEmpty()) {
                listEvidenceEvents.getSelectionModel().select(0);
            }
        }));
    }

    private void displayEvidenceDetail(AgentEventModel event) {
        lblEvId.setText(event.getId());
        lblEvTraceId.setText(event.getTraceId() != null ? event.getTraceId() : "N/A");
        lblEvSpanId.setText(event.getSpanId() != null ? event.getSpanId() : "N/A");
        lblEvPrevHash.setText(event.getPreviousEventHash());
        lblEvCurrHash.setText(event.getCurrentEventHash());
        txtEvInput.setText(event.getSanitizedInput() != null ? event.getSanitizedInput() : "(none)");
        txtEvOutput.setText(event.getSanitizedOutput() != null ? event.getSanitizedOutput() : "(none)");
        txtEvPolicy.setText(event.getPolicyResult() != null ?
                "Decision: " + event.getPolicyResult() + " | Reason: " + event.getPolicyReason() : "No policy rule triggered for this event.");
    }

    // ==========================================
    // INTEGRITY VERIFICATION
    // ==========================================
    @FXML
    public void handleVerifyIntegrity() {
        String execId = cmbIntegrityExecution.getValue();
        if (execId == null) return;

        apiClient.verifyIntegrity(execId).thenAccept(result -> Platform.runLater(() -> {
            boolean verified = "INTEGRITY_VERIFIED".equalsIgnoreCase(result.getStatus());
            boxIntegrityBanner.setStyle(verified
                    ? "-fx-background-color: #064e3b; -fx-padding: 20px; -fx-background-radius: 8px; -fx-border-color: #34d399; -fx-border-radius: 8px;"
                    : "-fx-background-color: #7f1d1d; -fx-padding: 20px; -fx-background-radius: 8px; -fx-border-color: #f87171; -fx-border-radius: 8px;"
            );

            lblIntegrityStatusText.setText(verified ? "✔ INTEGRITY VERIFIED (ALL HASHES MATCH)" : "✖ INTEGRITY COMPROMISED (TAMPERING DETECTED)");
            lblIntegrityStatusText.setStyle(verified ? "-fx-text-fill: #34d399; -fx-font-size: 20px; -fx-font-weight: bold;" : "-fx-text-fill: #f87171; -fx-font-size: 20px; -fx-font-weight: bold;");
            lblIntegrityReason.setText(result.getReason() + "\n" + result.getDetails());

            lblIntegTotalEvents.setText(String.valueOf(result.getTotalEventsVerified()));
            lblIntegInvalidSeq.setText(result.getFirstInvalidSequenceNumber() != null ? "#" + result.getFirstInvalidSequenceNumber() : "None (Chain Intact)");
            lblIntegExpectedHash.setText(result.getExpectedHash() != null ? result.getExpectedHash() : "N/A");
            lblIntegActualHash.setText(result.getActualHash() != null ? result.getActualHash() : "N/A");
            lblIntegDownstreamBroken.setText(String.valueOf(result.getSubsequentInvalidEvents()));

            logDemo("Integrity Verification for " + execId + ": " + result.getStatus() +
                    (verified ? "" : " (Violation at seq #" + result.getFirstInvalidSequenceNumber() + ")"));
        }));
    }

    @FXML
    public void handleTamperCurrentExecution() {
        String execId = cmbIntegrityExecution.getValue();
        if (execId == null) return;

        apiClient.tamperEvent(execId, 2, "[TAMPER DEMO] Injected malicious prompt to modify flight instructions").thenAccept(v -> Platform.runLater(() -> {
            logDemo("DEV TAMPER EXECUTED on execution " + execId + " at sequence #2. Click 'VERIFY INTEGRITY' to observe cryptographic detection!");
            handleVerifyIntegrity();
        }));
    }

    // ==========================================
    // EXECUTION COMPARISON
    // ==========================================
    @FXML
    public void handleCompareExecutions() {
        String exec1 = cmbCompareExec1.getValue();
        String exec2 = cmbCompareExec2.getValue();

        if (exec1 == null || exec2 == null) return;

        apiClient.compareExecutions(exec1, exec2).thenAccept(comp -> Platform.runLater(() -> {
            lblCompTitle1.setText("Execution 1: " + comp.getExecution1().getId().substring(0, Math.min(8, comp.getExecution1().getId().length())) + "...");
            lblCompTitle2.setText("Execution 2: " + comp.getExecution2().getId().substring(0, Math.min(8, comp.getExecution2().getId().length())) + "...");

            txtCompDetails1.setText(
                    "Status: " + comp.getExecution1().getStatus() + "\n" +
                    "Duration: " + comp.getExecution1().getDurationMs() + " ms\n" +
                    "Events Count: " + comp.getExecution1().getEventCount() + "\n" +
                    "Tools Used: " + comp.getToolsUsedExecution1() + "\n" +
                    "Policy Decisions: " + comp.getPolicyDecisionsExecution1() + "\n" +
                    "Errors: " + comp.getErrorsExecution1().size() + "\n" +
                    "Integrity Status: " + (comp.getIntegrityExecution1() != null ? comp.getIntegrityExecution1().getStatus() : "N/A") + "\n\n" +
                    "Final Observable Result:\n" + comp.getExecution1().getFinalResult()
            );

            txtCompDetails2.setText(
                    "Status: " + comp.getExecution2().getStatus() + "\n" +
                    "Duration: " + comp.getExecution2().getDurationMs() + " ms\n" +
                    "Events Count: " + comp.getExecution2().getEventCount() + "\n" +
                    "Tools Used: " + comp.getToolsUsedExecution2() + "\n" +
                    "Policy Decisions: " + comp.getPolicyDecisionsExecution2() + "\n" +
                    "Errors: " + comp.getErrorsExecution2().size() + "\n" +
                    "Integrity Status: " + (comp.getIntegrityExecution2() != null ? comp.getIntegrityExecution2().getStatus() : "N/A") + "\n\n" +
                    "Final Observable Result:\n" + comp.getExecution2().getFinalResult()
            );
        }));
    }

    // ==========================================
    // STATISTICS VIEW (REAL JAVAFX CHARTS)
    // ==========================================
    private void renderStatisticsView(StatisticsModel stats) {
        lblStatsAvgDuration.setText(Math.round(stats.getAverageDurationMs()) + " ms");
        long total = stats.getTotalExecutions();
        long succ = stats.getSuccessfulExecutions();
        String rate = total > 0 ? (Math.round(((double) succ / total) * 100.0) + "%") : "100%";
        lblStatsSuccessRate.setText(rate);

        long totalTools = 0;
        if (stats.getToolUsageCounts() != null) {
            totalTools = stats.getToolUsageCounts().values().stream().mapToLong(Long::longValue).sum();
        }
        lblStatsTotalTools.setText(String.valueOf(totalTools));

        long totalPolicies = 0;
        if (stats.getPolicyDecisionCounts() != null) {
            totalPolicies = stats.getPolicyDecisionCounts().values().stream().mapToLong(Long::longValue).sum();
        }
        lblStatsTotalPolicies.setText(String.valueOf(totalPolicies));

        // Execution Status Pie Chart
        chartExecutionStatus.getData().clear();
        chartExecutionStatus.getData().add(new PieChart.Data("Success (" + stats.getSuccessfulExecutions() + ")", stats.getSuccessfulExecutions()));
        chartExecutionStatus.getData().add(new PieChart.Data("Failed (" + stats.getFailedExecutions() + ")", stats.getFailedExecutions()));

        // Policy Decisions Pie Chart
        chartPolicyDecisions.getData().clear();
        if (stats.getPolicyDecisionCounts() != null) {
            stats.getPolicyDecisionCounts().forEach((dec, count) -> {
                if (count > 0) {
                    chartPolicyDecisions.getData().add(new PieChart.Data(dec + " (" + count + ")", count));
                }
            });
        }

        // Tool Usage Bar Chart
        chartToolUsage.getData().clear();
        XYChart.Series<String, Number> toolSeries = new XYChart.Series<>();
        toolSeries.setName("Invocations");
        if (stats.getToolUsageCounts() != null) {
            stats.getToolUsageCounts().forEach((tool, count) -> {
                toolSeries.getData().add(new XYChart.Data<>(tool, count));
            });
        }
        chartToolUsage.getData().add(toolSeries);

        // Event Distribution Bar Chart
        chartEventDistribution.getData().clear();
        XYChart.Series<String, Number> eventSeries = new XYChart.Series<>();
        eventSeries.setName("Events");
        if (stats.getEventTypeCounts() != null) {
            stats.getEventTypeCounts().forEach((type, count) -> {
                String shortType = type.replace("TASK_", "").replace("TOOL_", "").replace("DELEGATION_", "");
                eventSeries.getData().add(new XYChart.Data<>(shortType, count));
            });
        }
        chartEventDistribution.getData().add(eventSeries);
    }

    // ==========================================
    // DEMO SCENARIO SHORTCUTS
    // ==========================================
    @FXML
    public void handleDemoStandard() {
        logDemo("Running Demo Scenario: STANDARD...");
        apiClient.runDemoScenario("STANDARD").thenAccept(exec -> Platform.runLater(() -> {
            logDemo("Standard Scenario COMPLETED: " + exec.getId() + " | Events: " + exec.getEventCount());
            handleRefreshAll();
        }));
    }

    @FXML
    public void handleDemoRetry() {
        logDemo("Running Demo Scenario: FAILURE_RETRY...");
        apiClient.runDemoScenario("FAILURE_RETRY").thenAccept(exec -> Platform.runLater(() -> {
            logDemo("Failure & Retry Scenario COMPLETED: " + exec.getId() + " | Individual retries recorded in Black Box.");
            handleRefreshAll();
        }));
    }

    @FXML
    public void handleDemoPolicyDenied() {
        logDemo("Running Demo Scenario: POLICY_DENIED...");
        apiClient.runDemoScenario("POLICY_DENIED").thenAccept(exec -> Platform.runLater(() -> {
            logDemo("Policy Denied Scenario COMPLETED: " + exec.getId() + " | Destructive action blocked by Policy Engine.");
            handleRefreshAll();
        }));
    }

    @FXML
    public void handleDemoDelegation() {
        logDemo("Running Demo Scenario: DELEGATION (Multi-Agent)...");
        apiClient.runDemoScenario("DELEGATION").thenAccept(exec -> Platform.runLater(() -> {
            logDemo("Multi-Agent Delegation COMPLETED: " + exec.getId() + " | Orchestrator delegated subtask to SummaryAgent.");
            handleRefreshAll();
        }));
    }

    @FXML
    public void handleDemoTamper() {
        if (cmbIntegrityExecution.getValue() == null) {
            logDemo("Please run a demo scenario first to have an execution to tamper with.");
            return;
        }
        handleTamperCurrentExecution();
        showIntegrity();
    }

    @FXML
    public void handleDemoVerifyChain() {
        showIntegrity();
        handleVerifyIntegrity();
    }

    private void logDemo(String msg) {
        String logLine = "[" + Instant.now().toString().substring(11, 19) + "] " + msg + "\n";
        txtDemoConsole.appendText(logLine);
    }
}
