package com.gpetuhov.android.samplearcorecloud

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {
        // Enable cloud anchor mode
        val config = super.getSessionConfiguration(session)
        config.cloudAnchorMode = Config.CloudAnchorMode.ENABLED
        return config
    }
}