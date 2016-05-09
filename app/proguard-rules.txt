-dontwarn okio.**
-dontwarn com.github.nkzawa.engineio.client.transports.**

-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.toolbox.**
#-dontwarn com.instabug.*

# Google Map
-keep class com.google.android.gms.maps.* { ; }
-keep interface com.google.android.gms.maps.* { ; }