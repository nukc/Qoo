## Qoo

```groovy
implementation 'com.github.nukc.permissionrer:permissionrer:1.0'

```

> Scanner

文件扫描，可通过 ``Path`` 或 ``Uri`` 。

主要是通过实现 ``BasePathMatcher`` 或 ``BaseUriMatcher``。

Image Picker Demo:

<img src="https://raw.githubusercontent.com/nukc/Qoo/master/art/image_picker.png" width="300px">

> PermissionRer

权限请求

```kotlin
PermissionRer.with(activity)
    .request(Manifest.permission.READ_EXTERNAL_STORAGE)
    .subscribe { granted, permissions ->
        if (granted) {
            activity.startActivityForResult(
                Intent(activity, ImagePickerActivity::class.java),
                REQUEST_CODE
            )
        } else if (permissions[0].shouldShowRequestPermissionRationale) {
            Toast.makeText(activity, R.string.tils_select_image_need_read_image_permission, Toast.LENGTH_SHORT).show()
        }
    }
```


#### 用法示例：

```kotlin
viewModelScope.launch {
	val fileBox = FileScanner.build(context)
    	.scan(ImageMatcher())
    	.scan(ApkMatcher())
    	.scan(InvalidImageMatcher(), InvalidVideoMatcher())
    	.setScanListener(..)
        .start()
}
```

Scan Listener: 

```kotlin
interface OnScanListener {
    fun onScanStart()

    fun onScanning(item: BaseItem)

    fun onScanEnd()
}
```

Matcher eg:

```kotlin
/**
 * image matcher, eg: jpg, gif, png
 * @author Nukc.
 */
open class ImageMatcher : BaseUriMatcher<ImageItem>() {

    override fun provideUri(): Uri {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    override fun provideProjection(): Array<String> {
        return getProjection()
    }

    override fun provideSelection(): String? {
        return null
    }

    override fun provideSelectionArgs(): Array<String>? {
        return null
    }

    override fun provideSortOrder(): String? {
        return "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
    }

    override fun fill(cur: Cursor): ImageItem {
        val id = cur.getLong(cacheColumn[MediaStore.Images.Media._ID]!!)
        val contentUri: Uri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )
        return ImageItem(
            id = id,
            name = cur.getString(cacheColumn[MediaStore.Images.Media.DISPLAY_NAME]!!),
            path = cur.getString(cacheColumn[MediaStore.Images.Media.DATA]!!),
            size = cur.getLong(cacheColumn[MediaStore.Images.Media.SIZE]!!),
            dateAdded = cur.getLong(cacheColumn[MediaStore.Images.Media.DATE_ADDED]!!),
            dateModified = cur.getLong(cacheColumn[MediaStore.Images.Media.DATE_MODIFIED]!!),
            mimeType = cur.getString(cacheColumn[MediaStore.Images.Media.MIME_TYPE]!!),
            contentUri = contentUri,
            bucketName = cur.getString(cacheColumn[MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME]!!)
        )
    }


    private fun getProjection(): Array<String> {
        return arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
        )
    }
}
```



### TODO:

* [x] Demo
* [x] Image Picker

