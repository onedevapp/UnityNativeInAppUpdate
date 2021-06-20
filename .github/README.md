# UnityNativeInAppUpdate
Unity plugin helps you pick an image fromCamera or Gallery/Photos from your device
<br><br>

### INSTALLATION
There are 4 ways to install this plugin:

1. import NativeInAppUpdate.unitypackage via Assets-Import Package
2. clone/download this repository and move the Plugins folder to your Unity project's Assets folder
3. via Package Manager (**Add package from git url**):

    - `https://github.com/onedevapp/UnityNativeInAppUpdate.git`
4. via Package Manager (add the following line to **Packages/manifest.json**):
    - `"com.onedevapp.nativeinappupdate": "https://github.com/onedevapp/UnityNativeInAppUpdate.git",`

   
<br>

### Requirements
* You project should build against Android 5.0 (API level 21) SDK at least.
* This plugin uses a custom tool for dependency management called the [Play Services Resolver](https://github.com/googlesamples/unity-jar-resolver)


**NOTE:** <br>
According to the Unity3D docs [here](https://docs.unity3d.com/Manual/PluginsForAndroid.html?_ga=2.55742827.1931527617.1606199410-1875972592.1543254704):
AndroidManifest.xml file placed in the `Assets->Plugins->Android` folder (placing a custom manifest completely overrides the default Unity Android manifest). 

Or select an existing manifest from `Project Settings->Publishing Settings->Build->Custom Main Manifest`
<br><br>

If your project doesn't have an AndroidManifest, you can copy Unity's default one from `C:\Program Files\Unity\Editor\Data\PlaybackEngines\AndroidPlayer\Apk`. 

Or select from plugins `\Assets\NativeInAppUpdate\Plugins\Android`
<br><br>

### How To
Now android application can be updated wihtout leaving the app, its works on both scenarios:

1. Play Store Update
2. Using External Link (Some app will not be available on playstore but still can be updated)
<br><br>

### Play Store In-App Update
There are two update modes.

- Flexible (default) - Shows the user an upgrade dialog but performs the downloading of the update within the background. This means that the user can continue using our app whilst the update is being downloaded. When the update is downloaded asks the user confirmation to perform the install.

- Immediate - Will trigger a blocking UI until download and installation is finished. Restart is triggered automatically
<br><br>

### External Link In-App Update
Downloads an apk from server and tries install it automatically, but requires storage and install packages permissions.
<br><br>

### API 
-	Add uses-permission to AndroidManifest
	```XML
	<!-- Required for internet access -->
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

	<!-- Required for third party apk install -->
  	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />
  	<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
	```
-	Check for update
	```C#
	//UpdateMode can either play store or third party
	//PLAY_STORE = 0, THIRD_PARTY = 1
	//UpdateType can either FLEXIBLE or Immediate and no use for Third party update
	//FLEXIBLE = 0, IMMEDIATE = 1
	//thirdPartyLink, an external link which can download apk on the fly
	NativeInAppUpdateManager.CheckForUpdate(UpdateMode updateMode, UpdateType updateType, string thirdPartyLink  = "")
	```
-	To start the instalation of the update
	```C#
	//Sholud call only if isUpdateAvailable is true 
	NativeInAppUpdateManager.StartUpdate()
	```
-	To complete the instalation of the update
	```C#
	//Sholud call only if isUpdateAvailable is true 
	//This has no impact while using third party link update
	NativeInAppUpdateManager.CompleteUpdate()
	```
-	To continue update 
	```C#
	//must can called only on app resume to complete the pending update of previous update
	//This has no impact while using third party link update
	NativeInAppUpdateManager.ContinueUpdate()
	```
-	Callbacks
	```C#
	//Register for action
	NativeInAppUpdateManager.OnUpdateAvailable += OnUpdateAvailable;
	NativeInAppUpdateManager.OnUpdateVersionCode += OnUpdateVersionCode;
	NativeInAppUpdateManager.OnUpdateStalenessDays += OnUpdateStalenessDays;
	NativeInAppUpdateManager.OnUpdateInstallState += OnUpdateInstallState;
	NativeInAppUpdateManager.OnUpdateDownloading += OnUpdateDownloading;
	NativeInAppUpdateManager.OnUpdateError += OnUpdateError;

	//Invoked when Google Play Services returns a response 
	private void OnUpdateAvailable(bool isUpdateAvailable) {}

	// Invoked when the update is available with version code
	private void OnUpdateVersionCode(int versionCode) {}

	// Invoked when the update is available with staleness days
	private void OnUpdateStalenessDays(int days) {}

	// Invoked when install status of the update
	private void OnUpdateInstallState(int state) {}

	// Invoked during downloading
	private void onUpdateDownloading(long bytesDownloaded, long bytesTotal) {}

	// Invoked when the update encounter error
	private void onUpdateError(int code, string error) {}	
	```	
-	Error Codes
	```C#
	//Error code returns during the update process	
	//Playstore update error code 
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
	//External apk udpate error code 
	ERROR_STORAGE_PERMISSION = -101,
	ERROR_NETWORK = -102
	```


#### Debug
-	Toggle library logs
	```C#
	//By default puglin console log will be diabled, but can be enabled
	NativeInAppUpdateManager.Instance.PluginDebug(bool showLog);
	```
<br>


**Note:** 
1. You can decide which update should be forced by using either Firebase Remote Config or a Configuration file hosted on your server
2. When using external links, OnUpdateAvailable returns true when storage and network available, so you must update apk from link only when version validation is done by using either Firebase Remote Config or a Configuration file hosted on your server
3. Its mandatory to be called `NativeInAppUpdateManager.Instance.CompleteUpdate()` when download is completed
<br><br>

### Troubleshoot
1. In-app updates works only with devices running Android 5.0 (API level 21) or higher.
2. Testing this won’t work on a debug build. You would need a release build signed with the same key you use to sign your app before uploading to the Play Store. It would be a good time to use the internal testing track.
3. In-app updates are available only to user accounts that own the app. So, make sure the account you’re using has downloaded your app from Google Play at least once before using the account to test in-app updates.
4. Because Google Play can only update an app to a higher version code, make sure the app you are testing as a lower version code than the update version code.
5. Make sure the account is eligible and the Google Play cache is up to date. To do so, while logged into the Google Play Store account on the test device, proceed as follows:
   1. Make sure you completely [close the Google Play Store App](https://support.google.com/android/answer/9079646#close_apps).
   2. Open the Google Play Store app and go to the My Apps & Games tab.
   3. If the app you are testing doesn’t appear with an available update, check that you’ve properly [set up your testing tracks](https://support.google.com/googleplay/android-developer/answer/3131213?hl=en)


## Libraries
- #### [UnityMainThreadDispatcher](https://github.com/PimDeWitte/UnityMainThreadDispatcher)
<br>

## :open_hands: Contributions
Any contributions are welcome!

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create New Pull Request

<br><br>
