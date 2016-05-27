#
# Proguard config for the playservice demo project.
#

# This file only contains the proguard options required by the Google Maps
# Android API v2. It should be used in addition to the one provided by the
# Android SDK (<sdk>/tools/proguard/proguard-android-optimize.txt).
#
# For more details on the use of proguard in Android, please read:
# http://proguard.sourceforge.net/manual/examples.html#androidapplication

-optimizations !code/simplification/variable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment

# The Maps API uses custom Parcelables.
# Use this rule (which is slightly broader than the standard recommended one)
# to avoid obfuscating them.
-keepclassmembers class * implements android.os.Parcelable {
    static *** CREATOR;
}

# The Maps API uses serialization.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#from playservice sample
-dontwarn okio.**
-dontwarn com.github.nkzawa.engineio.client.transports.**

#-dontwarn org.apache.http.**
#-dontwarn android.net.http.AndroidHttpClient
#-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.instabug.**
-dontwarn com.squareup.picasso.**
-keepclasseswithmembernames class * {
    native <methods>;
}
#
## Google Map
#-keep class com.google.android.gms.maps.* { ; }
#-keep interface com.google.android.gms.maps.* { ; }