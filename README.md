# Android-StateLayout-Kotlin

Simple usage:
---
#### 1. in your layout xml, wrap the main content with StateLayout.
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.github.daryl.library.StateLayout
    android:id="@+id/state"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="daryl.library.MainActivity">

    <LinearLayout
        android:orientation="vertical"
        android:id="@+id/main"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <Button
            android:id="@+id/btnSubmit"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

    </LinearLayout>
</com.github.daryl.library.StateLayout>
```

> important: You should remain only ONE child inside StateLayout. For more children, use other layout container to host (LinearLayout / RelativeLayout / FrameLayout)

> To custom statelayout, i.e, background color, icon drawable, text size, text color, etc, go to [customization](#customization)

#### 2. in your kotlin code : 
To show loading...
```kotlin
fun callApi(){
    state.showLoading()
    // or
    state.showLoading(message = "Loading.. Please wait")
    // start aync task for api result.
    // after result callback
    state.dismissLoading()
}

```

To show empty
```kotlin
// list has empty item.
fun main(args: Array<String>) {
    if (list.isEmpty){
        state.showEmpty()
        
        // or
        state.showEmpty(
                icon= empty_icon_drawable,	
                title = "No email left"
                message = "Enjoy free day!"
        )
        // after items are added
        state.dismissEmpty()
    }
}


```

to show error, use showError() with onclick handling
```kotlin
// here we call api with retrofit and rx pattern.
fun callApi(){
    val userService = retrofit.create<UserService>()
    userService
        .getUserList()
        .retry(3)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe ({
            // response code 200
            state.dismissAllStates()    // safe call to ensure all state views are gone.
            // todo: other codes here
        }, {
            // response code != 200
            state.showError {
            	// retry button onclick handling
                toast("Server error. Please try again")
            }
        })
}
```

<a name="customization"></a>Customization
--
```xml
<?xml version="1.0" encoding="utf-8"?>
 <resources>
     <declare-styleable name="StateViews">
         <attr name="loadingContent" format="string" />
         <attr name="loadingImage" format="reference" />
         <attr name="loadingContentTextSize" format="dimension" />
         <attr name="loadingContentTextColor" format="color" />
         <attr name="loadingProgressBarWidth" format="dimension" />
         <attr name="loadingProgressBarHeight" format="dimension" />
         <attr name="loadingProgressBarColor" format="color" />
         <attr name="loadingBackgroundColor" format="color" />
 
         <attr name="emptyTitle" format="string" />
         <attr name="emptyContent" format="string" />
         <attr name="emptyImage" format="reference" />
         <attr name="emptyImageWidth" format="dimension" />
         <attr name="emptyImageHeight" format="dimension" />
         <attr name="emptyTitleTextSize" format="dimension" />
         <attr name="emptyContentTextSize" format="dimension" />
         <attr name="emptyTitleTextColor" format="color" />
         <attr name="emptyContentTextColor" format="color" />
         <attr name="emptyBackgroundColor" format="color" />
 
         <attr name="errorTitle" format="string" />
         <attr name="errorContent" format="string" />
         <attr name="errorImage" format="reference" />
         <attr name="errorImageWidth" format="dimension" />
         <attr name="errorImageHeight" format="dimension" />
         <attr name="errorTitleTextSize" format="dimension" />
         <attr name="errorContentTextSize" format="dimension" />
         <attr name="errorTitleTextColor" format="color" />
         <attr name="errorContentTextColor" format="color" />
         <attr name="errorStateButtonText" format="string" />
         <attr name="errorButtonTextColor" format="color" />
         <attr name="errorButtonBackgroundColor" format="color" />
         <attr name="errorBackgroundColor" format="color" />
     </declare-styleable>
</resources>
```