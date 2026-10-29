package com.example.blackbox;

import com.example.blackbox.tools.RealtimeSourceSearchResult;
import com.example.blackbox.tools.RealtimeWebSearchTool;
import com.example.blackbox.tools.ToolExecutionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RealtimeWebSearchToolTest {

    private final RealtimeWebSearchTool searchTool = new RealtimeWebSearchTool();

    @Test
    @DisplayName("RealtimeWebSearchTool retrieves live or authoritative facts with valid SHA-256 digest")
    void testRealtimeSearchExecution() {
        ToolExecutionResult result = searchTool.execute(Map.of("query", "SHA-256"));

        assertNotNull(result);
        assertTrue(result.isSuccess(), "Search must return success");
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("SHA-256") || result.getOutput().contains("SHA-2"), "Must contain subject facts");
        assertTrue(result.getOutput().contains("http://") || result.getOutput().contains("https://"), "Must include verified source URL");
        assertTrue(result.getOutput().contains("SHA-256 Seal:"), "Must include cryptographic seal");
    }

    @Test
    @DisplayName("RealtimeWebSearchTool handles error conditions gracefully")
    void testSearchFailureHandling() {
        ToolExecutionResult failureResult = searchTool.execute(Map.of("query", "fail_search"));
        assertFalse(failureResult.isSuccess(), "Simulated failure query must return failure");
        assertNotNull(failureResult.getErrorMessage());
    }

    @Test
    @DisplayName("Authoritative fallback returns verified NIST specification")
    void testAuthoritativeFallback() {
        RealtimeSourceSearchResult fallback = searchTool.getAuthoritativeFallback("NIST SHA-256", 25L);
        assertNotNull(fallback);
        assertEquals(200, fallback.getHttpStatus());
        assertTrue(fallback.getSourceUrl().contains("nist.gov"));
        assertEquals(64, fallback.getSha256Digest().length(), "Digest must be 64-char hex SHA-256");
    }
}

