# About

This is the Factual-supported Java driver for [Factual's public API](http://developer.factual.com) on Android.

# Installation

## Maven users

The driver is in Maven Central, so you can just add this to your Maven <tt>pom.xml</tt>:

    <dependency>
      <groupId>com.factual</groupId>
      <artifactId>factual-java-driver</artifactId>
      <version>1.2.1-android</version>
    </dependency>
    
## Non Maven users

You can download the individual driver jar, and view the pom.xml file, here:
[Driver download folder](http://repo1.maven.org/maven2/com/factual/factual-java-driver/1.2.1-android/)

The pom.xml tells you what dependencies you'll need to plug into your project to get the driver to work.

# Application Development

For a description of all the features available in the driver, please refer to the 
[Non-Android Java driver](https://github.com/Factual/factual-java-driver)

## Tips

1. Ensure your application AndroidManifest.xml has Internet access enabled: <uses-permission android:name="android.permission.INTERNET" />.
2. Perform Factual requests using the Android AsyncTask to prevent errors related to blocking of the main thread.