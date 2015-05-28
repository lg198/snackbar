package com.github.lg198.snackbar.editmenu;

import com.github.lg198.snackbar.ReportableException;

public class MenuShareDownloadResult {

    private boolean success;
    private ReportableException error;

    public MenuShareDownloadResult(ReportableException e) {
        success = false;
        error = e;
    }

    public MenuShareDownloadResult() {
        success = true;
    }

    public boolean wasSuccessful() {
        return success;
    }

    public ReportableException getError() {
        return error;
    }
}
