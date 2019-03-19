# SampleARCoreCloud
Learn how to use ARCore Cloud Anchors with Sceneform. In this example we save ID of the hosted anchor to the Shared Preferences and use it to restore the anchore after the scene has been cleared or after app restart.

## Requirements
* Android Studio 3.5 Canary 7
* Kotlin 1.3.30-eap-45
* Android Gradle Plugin 3.5.0-alpha07
* Gradle wrapper 5.2.1
* AAPT 2

## Notes
* Devices must be in the same physical environment as the original hosting device
* Cloud Anchors expire after 24 hours
* Project must have Google Cloud API
* ARCore Cloud Anchor API must be enabled
* Hosting can take 30 seconds or more
* Anchors are stored in ARCore Cloud Anchor API, NOT in Firebase
* Firebase can be used to share IDs of the hosted Cloud Anchors

## 3D Models used
Halloween Pumpkin by Neil Realubit:

https://poly.google.com/view/2Z1UzUc0No4

## References
https://developers.google.com/ar/develop/java/cloud-anchors/overview-android

https://medium.com/@ardeploy/build-shared-augmented-reality-experience-for-android-using-sceneform-and-arcore-cloud-anchors-29ae1c55bea7

https://codelabs.developers.google.com/codelabs/arcore-cloud-anchors/#0