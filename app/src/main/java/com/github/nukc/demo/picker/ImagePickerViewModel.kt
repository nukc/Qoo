package com.github.nukc.demo.picker

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.nukc.demo.picker.matcher.ImageMatcher
import com.github.nukc.demo.picker.model.Album
import com.github.nukc.demo.picker.model.BaseMediaItem
import com.github.nukc.demo.picker.model.ImageItem
import com.github.nukc.scanner.FileScanner
import com.github.nukc.scanner.OnScanListener
import com.github.nukc.scanner.model.BaseItem
import kotlinx.coroutines.launch

class ImagePickerViewModel : ViewModel() {

    private val allString = "All"
    private val buckets = LinkedHashMap<Album, java.util.ArrayList<BaseItem>>()

    val images = MutableLiveData<List<BaseItem>>()
    val album = MutableLiveData<List<Album>>()

    init {
        val allAlbum =
            Album(name = allString, count = 0, uri = Uri.EMPTY).apply {
                selected = true
            }
        buckets[allAlbum] = arrayListOf()
    }

    fun scan(context: Context) {
        viewModelScope.launch {
            val fileBox = FileScanner.build(context)
                .scan(ImageMatcher())
                .setScanListener(object : OnScanListener {
                    override fun onScanStart() {

                    }

                    override fun onScanning(item: BaseItem) {
                        if (item is BaseMediaItem) {
                            val a = findAlbum(item.bucketName)
                            if (a == null) {
                                buckets[Album(
                                    name = item.bucketName,
                                    count = 1,
                                    uri = item.contentUri
                                )] = java.util.ArrayList<BaseItem>().apply {
                                    add(item)
                                }
                            } else {
                                a.count += 1
                                buckets[a]?.add(item)
                            }
                        }
                    }

                    override fun onScanEnd() {
                    }
                })
                .start()

            if (fileBox.array?.values.isNullOrEmpty()) {
                images.value = emptyList()
            } else {
                val data =
                    fileBox.array?.get(ImageItem::class.java as Class<BaseItem>) as List<ImageItem>
                images.value = data

                findAlbum(allString)?.let {
                    it.uri = data[0].contentUri
                    it.count = data.size
                    buckets[it]?.addAll(data)
                }

                album.postValue(buckets.keys.toList())
            }
        }
    }

    fun selectAlbum(album: Album) {
        images.value = buckets[album]
    }

    /**
     * 是否已经存在
     */
    private fun findAlbum(name: String): Album? {
        buckets.keys.forEach {
            if (it.name == name) {
                return it
            }
        }
        return null
    }
}