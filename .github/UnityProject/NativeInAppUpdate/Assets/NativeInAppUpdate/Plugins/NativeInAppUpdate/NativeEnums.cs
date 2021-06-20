namespace OneDevApp.InAppUpdate
{

    /// <summary>
    /// Update mode
    /// </summary>
    public enum UpdateMode
    {
        // Update from playstore
        PLAY_STORE = 0,
        // Update from third party website via download apk
        THIRD_PARTY = 1
    }

    /// <summary>
    /// Update type for playstore
    /// </summary>
    public enum UpdateType
    {
        // Flexible will let the user 
        // choose if he wants to update the app
        FLEXIBLE = 0,
        // Immediate will update right away
        IMMEDIATE = 1
    }

    /// <summary>
    /// Status of a download / install.
    /// </summary>
    public enum InstallStatus
    {
        DOWNLOADED = 11,
        CANCELED = 6,
        FAILED = 5,
        INSTALLED = 4,
        INSTALLING = 3,
        DOWNLOADING = 2,
        PENDING = 1,
        UNKNOWN = 0
    }

    /// <summary>
    /// Status of a error during update
    /// </summary>
    public enum InstallErrorCode
    {
        ERROR_DOWNLOAD_NOT_PRESENT = -7,
        ERROR_API_NOT_AVAILABLE = -3,
        ERROR_INSTALL_IN_PROGRESS = -8,
        ERROR_INSTALL_NOT_ALLOWED = -6,
        ERROR_INSTALL_UNAVAILABLE = -5,
        ERROR_INTERNAL_ERROR = -100,
        ERROR_INVALID_REQUEST = -4,
        ERROR_PLAY_STORE_NOT_FOUND = -9,
        ERROR_UNKNOWN = -2,
        NO_ERROR = 0,
        ERROR_LIBRARY = -1,
        ERROR_STORAGE_PERMISSION = -101,
        ERROR_NETWORK = -102
    }
}
