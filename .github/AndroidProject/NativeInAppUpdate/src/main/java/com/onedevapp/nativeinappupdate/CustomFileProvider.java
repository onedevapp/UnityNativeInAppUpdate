package com.onedevapp.nativeinappupdate;

import androidx.core.content.FileProvider;

/**
 * ContentProviders are identified by the authority, so it needs to be unique.
 */
//http://myhexaville.com/2018/04/06/android-provider-authority-conflict/
public class CustomFileProvider extends FileProvider {
}