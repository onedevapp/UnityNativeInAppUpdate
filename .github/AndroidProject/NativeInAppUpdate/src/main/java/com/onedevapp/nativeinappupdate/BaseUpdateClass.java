package com.onedevapp.nativeinappupdate;


/**
 * BaseUpdateClass a parent class for PlayStoreUpdate and ThirdPartyUpdate
 */
public abstract class BaseUpdateClass {
    // region Declarations
    public int mUpdateType; //update type wither flexible or immediate
    public String mUpdateLink;  //link to download apk for third party
    public OnUpdateListener mOnUpdateListener;  //Callback listener
    public UpdateManager mUpdateManager;    //Manager reference

    //endregion

    //region Constructor

    /**
     * Constructor
     *
     * @param updateManager the updateManager instance
     */
    public BaseUpdateClass(UpdateManager updateManager) {
        this.mUpdateManager = updateManager;
    }

    //endregion

    // region Setters

    /**
     * Set the update link for download
     *
     * @param mUpdateLink the link for third party URL
     */
    public void setUpdateLink(String mUpdateLink) {
        this.mUpdateLink = mUpdateLink;
    }

    /**
     * Set the update type.
     *
     * @param updateType the update type
     */
    public void setUpdateType(int updateType) {
        this.mUpdateType = updateType;
    }

    /**
     * Set the callback handler
     *
     * @param onUpdateListener the handler
     */
    public void setHandler(OnUpdateListener onUpdateListener) {
        this.mOnUpdateListener = onUpdateListener;
    }

    //endregion

    // region abstract Public functions
    public abstract void checkUpdate() throws Exception;

    public abstract void startUpdate() throws Exception;

    public abstract void completeUpdate() throws Exception;

    public abstract void continueUpdate() throws Exception;

    //endregion
}
