# DxWaveRefresh
Pull down to refreshing with wave animation

[![](https://jitpack.io/v/StevenDXC/DxWaveRefresh.svg)](https://jitpack.io/#StevenDXC/DxWaveRefresh)


Demo
---

![](https://github.com/StevenDXC/DxWaveRefresh/blob/master/image/waverefresh.gif)


Usage
---

layout：

```xml
<scrollView
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:background="@drawable/image"
        android:id="@+id/scrollView"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <com.dx.waverefresh.lib.WaveRefreshLayout
            android:id="@+id/contentLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            app:wr_topImageHeight="@dimen/defaultTopImageHeight"
            app:wr_waveAmplitude="10dp"
            app:wr_angle="10"
            app:wr_bgColor="@color/colorWhite"
            app:wr_speed="6"
            app:wr_gravity="right">
	    
	    ...
	    
    </com.dx.waverefresh.lib.WaveRefreshLayout>
</scrollView>    
```

then process pull down to refresh gesture in scrollView.

pulling down:

```java
waveRefreshLayout.setBackgroundOffset(p);
```

touch up:

```java
//start refreshing
waveRefreshLayout.startLoadingAnimation();
//not trigger refreshing
waveRefreshLayout.restoreBackground();
```

stop wave Animation:

```java
waveRefreshLayout.stopLoading();
```

more usage and information see demo.


Dependency
---


Add it in your root build.gradle at the end of repositories:

```java
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency:

```java
dependencies {
    compile 'com.github.StevenDXC:DxWaveRefresh:1.0'
}
```
  
  
