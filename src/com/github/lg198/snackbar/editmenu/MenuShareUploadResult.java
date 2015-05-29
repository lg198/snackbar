package com.github.lg198.snackbar.editmenu;

import com.github.lg198.snackbar.ReportableException;

public class MenuShareUploadResult {

    private boolean success;
    private ReportableException error;
    private String code;

    public MenuShareUploadResult(String c) {
        success = true;
        code = c;
    }

    public MenuShareUploadResult(ReportableException e) {
        success = false;
        error = e;
    }

    public boolean wasSuccessful() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public ReportableException getError() {
        return error;
    }

}
