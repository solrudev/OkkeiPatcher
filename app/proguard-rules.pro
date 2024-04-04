-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute Source

#### Moshi

-keep @com.squareup.moshi.JsonQualifier interface *

# Enum field names are used by the integrated EnumJsonAdapter.
# values() is synthesized by the Kotlin compiler and is used by EnumJsonAdapter indirectly
# Annotate enums with @JsonClass(generateAdapter = false) to use them with Moshi.
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
    **[] values();
}

# Keep helper method to avoid R8 optimisation that would keep all Kotlin Metadata when unwanted
-keepclassmembers class com.squareup.moshi.internal.Util {
    private static java.lang.String getKotlinMetadataClassName();
}

-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

#### ApkSigner
-keep,allowobfuscation,allowshrinking class com.android.apksig.internal.asn1.* { *; }
-keep,allowobfuscation,allowshrinking @com.android.apksig.internal.asn1.Asn1Class class *
-keepclassmembers,allowobfuscation,allowshrinking class * {
    @com.android.apksig.internal.asn1.Asn1Field *;
}

#### Miscellaneous
-keep class ru.solrudev.okkeipatcher.app.model.Work