package com.onedevapp.nativeinappupdate.plugin;


/**
 * Callback methods where update events are reported.
 */
public interface OnUpdateListener {
    /**
     * Version code on update available.
     *
     * @param versionCode the code
     */
    void onUpdateVersionCode(int versionCode);

    /**
     * Staleness Days on update available.
     *
     * @param days the days
     */
    void onUpdateStalenessDays(int days);

    /**
     * Returns update is available or not.
     *
     * @param isUpdateAvailable   the update available
     * @param isUpdateTypeAllowed the isUpdateTypeAllowed either flexible or immediate as requested
     */
    void onUpdateAvailable(boolean isUpdateAvailable, boolean isUpdateTypeAllowed);

    /**
     * Returns update install state from play store.
     *
     * @param state the install state
     */
    void onUpdateInstallState(int state);

    /**
     * While update is Downloading returns with total bytes to download and bytes downloaded.
     *
     * @param totalBytesToDownload the code
     * @param bytesDownloaded      the error
     */
    void onUpdateDownloading(long totalBytesToDownload, long bytesDownloaded);

    /**
     * While update got any error returns with error code and message.
     *
     * @param code  the code
     * @param error the error
     */
    void onUpdateError(int code, String error);
}
