-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute Source

#### ApkSigner
-keep,allowobfuscation,allowshrinking class com.android.apksig.internal.asn1.* { *; }
-keep,allowobfuscation,allowshrinking @com.android.apksig.internal.asn1.Asn1Class class *
-keepclassmembers,allowobfuscation,allowshrinking class * {
    @com.android.apksig.internal.asn1.Asn1Field *;
}

#### Miscellaneous
-keep class ru.solrudev.okkeipatcher.app.model.Work