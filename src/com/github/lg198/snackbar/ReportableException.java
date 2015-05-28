package com.github.lg198.snackbar;

public class ReportableException extends Exception {

    private Exception underlying;

    public boolean hasUnderlying() {
        return underlying != null;
    }

    public Exception getUnderlying() {
        return underlying;
    }

    public ReportableException(String s) {
        super(s);
    }

    public ReportableException(String s, Exception e) {
        super(s);
        underlying = e;
    }


    //GENERIC
    public static final String NETWORK_ERROR = "There was a problem with the network and your request could not be completed.";

    //SPECIFIC
    public static final String MENU_SHARE_DOWNLOAD_NOT_FOUND = "The menu you requested was not found!";

}
