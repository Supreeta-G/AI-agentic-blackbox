package com.example.blackbox.tools;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InformationSearchTool implements AgentTool {

    private final RealtimeWebSearchTool realtimeWebSearchTool;

    public InformationSearchTool() {
        this(new RealtimeWebSearchTool());
    }

    public InformationSearchTool(RealtimeWebSearchTool realtimeWebSearchTool) {
        this.realtimeWebSearchTool = realtimeWebSearchTool;
    }

    @Override
    public String getName() {
        return "InformationSearchTool";
    }

    @Override
    public String getDescription() {
        return "Searches domain knowledge repositories, black box incident archives, and live realtime external references.";
    }

    @Override
    public ToolExecutionResult execute(Map<String, Object> params) {
        // Delegate to RealtimeWebSearchTool for verified live search and cryptographic evidence
        return realtimeWebSearchTool.execute(params);
    }
}
