# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

####################################################################################################
#  ____    _    _____ _____ ____   ___   ___  __  __
# / ___|  / \  |  ___| ____|  _ \ / _ \ / _ \|  \/  |
# \___ \ / _ \ | |_  |  _| | |_) | | | | | | | |\/| |
#  ___) / ___ \|  _| | |___|  _ <| |_| | |_| | |  | |
# |____/_/   \_\_|   |_____|_| \_\\___/ \___/|_|  |_|
#
####################################################################################################
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }

####################################################################################################
#    _____
#   /  _  \ __________ _________   ____
#  /  /_\  \\___   /  |  \_  __ \_/ __ \
# /    |    \/    /|  |  /|  | \/\  ___/
# \____|__  /_____ \____/ |__|    \___  >
#
####################################################################################################

-keep class com.azure.** { *; }
-keep class com.fasterxml.jackson.** { *; }
-keep class org.codehaus.stax2.** { *; }
-keep class com.bea.xml.** { *; }
-keep class javax.xml.stream.** { *; }
-keep class io.netty.** { *; }
-keep class org.apache.logging.log4j.** { *; }

####################################################################################################
#     _    _   _ ____  ____   ___ ___ ____       _    ____   ____ _   _
#    / \  | \ | |  _ \|  _ \ / _ \_ _|  _ \     / \  |  _ \ / ___| | | |
#   / _ \ |  \| | | | | |_) | | | | || | | |   / _ \ | |_) | |   | |_| |
#  / ___ \| |\  | |_| |  _ <| |_| | || |_| |  / ___ \|  _ <| |___|  _  |
# /_/   \_\_| \_|____/|_| \_\\___/___|____/  /_/   \_\_| \_\\____|_| |_|
#
####################################################################################################
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

####################################################################################################
#  __  __  ___  ____  _   _ ___
# |  \/  |/ _ \/ ___|| | | |_ _|
# | |\/| | | | \___ \| |_| || |
# | |  | | |_| |___) |  _  || |
# |_|  |_|\___/|____/|_| |_|___|
#
####################################################################################################
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}

-keep @com.squareup.moshi.JsonQualifier interface *

# Enum field names are used by the integrated EnumJsonAdapter.
# values() is synthesized by the Kotlin compiler and is used by EnumJsonAdapter indirectly
# Annotate enums with @JsonClass(generateAdapter = false) to use them with Moshi.
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
    **[] values();
}

-keep class kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoaderImpl

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Keep helper method to avoid R8 optimisation that would keep all Kotlin Metadata when unwanted
-keepclassmembers class com.squareup.moshi.internal.Util {
    private static java.lang.String getKotlinMetadataClassName();
}

####################################################################################################
#  ____  _____ _____ ____   ___  _____ ___ _____
# |  _ \| ____|_   _|  _ \ / _ \|  ___|_ _|_   _|
# | |_) |  _|   | | | |_) | | | | |_   | |  | |
# |  _ <| |___  | | |  _ <| |_| |  _|  | |  | |
# |_| \_\_____| |_| |_| \_\\___/|_|   |___| |_|
#
####################################################################################################

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn org.codehaus.stax2.validation.XMLValidationSchemaFactory.**
-dontwarn reactor.blockhound.integration.BlockHoundIntegration
-dontwarn io.netty.channel.epoll.Epoll

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepattributes InnerClasses

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# Keep the generated R class after compiliation
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class **.R$*

#VaxJobs
-keepnames @kotlin.Metadata class com.vaxcare.unifiedhub.jobs.**

#SafeArgs/NavArgs must also be kept
-keepnames @kotlin.Metadata class com.vaxcare.unifiedhub.worker.args.**
-keepnames @kotlin.Metadata class com.vaxcare.unifiedhub.core.data.model.**
-keep class com.vaxcare.unifiedhub.core.data.model.** { *; }
-keep class com.vaxcare.unifiedhub.core.network.model.** { *; }

####################################################################################################
#  ____
# |  _ \
# | |_)
# |  _ <
# |_| \_
#
####################################################################################################

-keep class **.R$* {
        <fields>;
    }

####################################################################################################
#  ____
# |  _ \
# | |_)       eport
# |  _ <
# |_| \_
#
####################################################################################################

-keep public class com.vaxcare.unifiedhub.library.analytics.** {
    public protected *;
    public <methods>;
}

-keep public interface com.vaxcare.unifiedhub.library.analytics.** {
    public protected *;
    public <methods>;
}

-keep public class com.vaxcare.unifiedhub.library.analytics.di.RepositoryModule {
    public <fields>;
}

#-keepclassmembers class com.vaxcare.vaxhub.testdata.TestProducts { ##not implemented yet
#    <fields>;
#    <methods>;
#    **[] values();
#}

# Missing class ignore - need these for higher env building
-dontwarn com.aayushatharva.brotli4j.**
-dontwarn com.beust.jcommander.**
-dontwarn com.conversantmedia.util.concurrent.DisruptorBlockingQueue
-dontwarn com.conversantmedia.util.concurrent.SpinPolicy
-dontwarn com.fasterxml.jackson.dataformat.yaml.YAMLFactory
-dontwarn com.fasterxml.jackson.dataformat.yaml.YAMLMapper
-dontwarn com.github.luben.zstd.Zstd
-dontwarn com.google.protobuf.**
-dontwarn com.jcraft.jzlib.**
-dontwarn com.lmax.disruptor.**
-dontwarn com.ning.compress.**
-dontwarn com.oracle.svm.core.annotate.**
-dontwarn com.sun.activation.registries.LogSupport
-dontwarn com.sun.activation.registries.MailcapFile
-dontwarn io.micrometer.core.instrument.**
-dontwarn io.micrometer.core.instrument.composite.CompositeMeterRegistry
-dontwarn io.netty.incubator.channel.uring.**
-dontwarn java.awt.datatransfer.DataFlavor
-dontwarn java.awt.datatransfer.Transferable
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn java.lang.management.**
-dontwarn java.rmi.MarshalledObject
-dontwarn javax.jms.**
-dontwarn javax.lang.**
-dontwarn javax.mail.**
-dontwarn javax.management.**
-dontwarn javax.naming.**
-dontwarn javax.persistence.**
-dontwarn javax.script.**
-dontwarn javax.tools.**
-dontwarn lzma.sdk.ICodeProgress
-dontwarn lzma.sdk.lzma.Encoder
-dontwarn net.jpountz.**
-dontwarn org.apache.**
-dontwarn org.eclipse.jetty.**
-dontwarn org.fusesource.jansi.**
-dontwarn org.jboss.marshalling.**
-dontwarn org.osgi.framework.**
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn org.zeromq.**
-dontwarn reactor.blockhound.BlockHound$Builder
-dontwarn sun.**

# Retrofit
# copied from: https://github.com/square/retrofit/blob/trunk/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

##---------------End: proguard configuration for Retrofit  ----------

# Okhttp
# copied from: https://raw.githubusercontent.com/square/okhttp/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
##---------------End: proguard configuration for Okhttp  ----------
-keep class retrofit.** { *; }
-keep class * implements java.io.Serializable { *; }
-keep class kotlin.Metadata

# Moshi
# copied from https://github.com/square/moshi/blob/master/moshi/src/main/resources/META-INF/proguard/moshi.pro

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}

-keep @com.squareup.moshi.JsonQualifier @interface *

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

# Keep ToJson/FromJson-annotated methods
-keepclassmembers class * {
  @com.squareup.moshi.FromJson <methods>;
  @com.squareup.moshi.ToJson <methods>;
}

# Timber properly obfuscated in release
-keep class timber.log.** { *; }
-keepclassmembers class * {
   @dagger.hilt.* *;
}
