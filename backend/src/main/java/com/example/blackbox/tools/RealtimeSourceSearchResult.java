package com.example.blackbox.tools;

public class RealtimeSourceSearchResult {
    private final String sourceUrl;
    private final String sourceTitle;
    private final String sourceType;
    private final int httpStatus;
    private final long latencyMs;
    private final String snippet;
    private final String sha256Digest;

    public RealtimeSourceSearchResult(String sourceUrl,
                                      String sourceTitle,
                                      String sourceType,
                                      int httpStatus,
                                      long latencyMs,
                                      String snippet,
                                      String sha256Digest) {
        this.sourceUrl = sourceUrl;
        this.sourceTitle = sourceTitle;
        this.sourceType = sourceType;
        this.httpStatus = httpStatus;
        this.latencyMs = latencyMs;
        this.snippet = snippet;
        this.sha256Digest = sha256Digest;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getSourceTitle() {
        return sourceTitle;
    }

    public String getSourceType() {
        return sourceType;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getSha256Digest() {
        return sha256Digest;
    }

    @Override
    public String toString() {
        return "[" + sourceType + "] " + sourceTitle + " (" + sourceUrl + " | " + httpStatus + " OK, " + latencyMs + "ms)\n" +
                "Summary: " + snippet + "\n" +
                "SHA-256 Seal: " + sha256Digest;
    }
}

