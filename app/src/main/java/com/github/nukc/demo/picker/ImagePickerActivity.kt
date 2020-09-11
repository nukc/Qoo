package com.github.nukc.demo.picker

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.nukc.demo.R
import com.github.nukc.demo.picker.model.Album
import com.github.nukc.demo.picker.model.ImageItem
import com.github.nukc.demo.picker.model.Selectable
import com.github.nukc.demo.picker.utils.Utils
import com.github.nukc.permissionrer.PermissionRer
import com.github.nukc.recycleradapter.RecyclerAdapter
import com.github.nukc.recycleradapter.dsl.setup
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_image_picker.*
import kotlinx.android.synthetic.main.item_album.*
import kotlinx.android.synthetic.main.item_image_picker.*
import kotlinx.android.synthetic.main.item_image_picker.iv_cover
import kotlinx.android.synthetic.main.item_image_picker.tv_count
import java.io.File
import java.util.*

/**
 * 图片选择，可拓展成媒体选择，查询什么由 matcher 决定
 */
class ImagePickerActivity : AppCompatActivity() {

    private lateinit var viewModel: ImagePickerViewModel
    private lateinit var adapter: RecyclerAdapter
    private lateinit var adapterAlbum: RecyclerAdapter
    private val mediaSelected = ArrayList<ImageItem>()
    private var maxSelectedCount = 1
    private val resize by lazy(LazyThreadSafetyMode.NONE) { Utils.getScreenWidth(this) / 3 }
    private var preSelectAlbumIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        viewModel = ViewModelProvider(this)[ImagePickerViewModel::class.java]
        viewModel.scan(this)

        adapter = recycler_view.setup(GridLayoutManager(this, 3)) {
            renderItem<ImageItem> {
                res(R.layout.item_image_picker) {
                    iv_cover.setOnClickListener {
                        if (!data.selected && exceeds()) {
                            Toast.makeText(
                                this@ImagePickerActivity,
                                getString(R.string.select_up_to_num, maxSelectedCount),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        data.selected = !data.selected
                        setChecked(check_view, tv_count, data.selected)
                        if (data.selected) {
                            mediaSelected.add(data)
                        } else {
                            mediaSelected.remove(data)
                        }
                    }

                    iv_preview.setOnClickListener {
                        supportFragmentManager.beginTransaction()
                            .addToBackStack("preview")
                            .add(android.R.id.content, PreviewItemFragment.newInstance(data))
                            .commit()
                    }
                }

                bind {
                    Glide.with(this@ImagePickerActivity)
                        .load(data.contentUri)
                        .apply {
                            override(resize, resize)
                            centerCrop()
                        }
                        .into(iv_cover)
                    setChecked(check_view, tv_count, data.selected)
                }

            }
        }

        adapterAlbum = recycler_view_album.setup(LinearLayoutManager(this)) {
            renderItem<Album> {
                res(R.layout.item_album) {
                    itemView.setOnClickListener {
                        if (preSelectAlbumIndex != adapterPosition) {
                            val pre = adapterAlbum.items[preSelectAlbumIndex]
                            if (pre is Selectable) {
                                pre.selected = false
                            }
                            adapterAlbum.notifyItemChanged(preSelectAlbumIndex)
                            preSelectAlbumIndex = adapterPosition
                        }
                        data.selected = !data.selected
                        if (data.selected) {
                            iv_selected.visibility = View.VISIBLE
                            viewModel.selectAlbum(data)
                        } else {
                            iv_selected.visibility = View.GONE
                        }

                        this@ImagePickerActivity.toggle()
                    }
                }
                bind {
                    Glide.with(this@ImagePickerActivity)
                        .load(data.uri)
                        .apply {
                            override(resize, resize)
                            centerCrop()
                        }
                        .into(iv_cover)
                    tv_count.text = "${data.count}"
                    tv_name.text = data.name
                    iv_selected.visibility = if (data.selected) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.images.observe(this, Observer {
            adapter.refresh(it ?: emptyList())
        })
        viewModel.album.observe(this, Observer {
            adapterAlbum.refresh(it)
        })

        tv_done.setOnClickListener {
            if (isSingleMode()) {
                if (mediaSelected.isEmpty()) {
                    finish()
                    return@setOnClickListener
                }
                val image = mediaSelected[0]
                val sourceUri = image.contentUri
                val destinationUri =
                    Uri.fromFile(File(cacheDir, "crop_${image.name}"))
                UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(16f, 9f)
                    .start(this)
            } else {
                setResultAndFinish()
            }
        }
        tv_albums.setOnClickListener {
            toggle()
        }
        view_dismiss.setOnClickListener {
            toggle()
        }
    }

    private fun setResultAndFinish(uris: ArrayList<Uri>? = null) {
        val data: ArrayList<Uri> = uris ?: mediaSelected.map { it.contentUri } as ArrayList<Uri>
        setResult(REQUEST_CODE, Intent().apply {
            putParcelableArrayListExtra(EXTRA_IMAGES, data)
        })
        finish()
    }

    private fun toggle() {
        view_dismiss.visibility =
            if (recycler_view_album.translationX == 0f) View.GONE else View.VISIBLE
        recycler_view_album.animate()
            .translationX(
                if (recycler_view_album.translationX == 0f) -recycler_view_album.width.toFloat()
                else 0f
            )
            .setDuration(200)
            .start()
    }

    private fun setChecked(checkView: ImageView, tvCount: TextView, selected: Boolean) {
        if (selected) {
            checkView.setBackgroundResource(R.drawable.bg_media_check_selected)
            if (isSingleMode()) {
                checkView.setImageResource(R.drawable.ic_media_check)
            } else {
                tvCount.text = "${mediaSelected.size}"
            }
        } else {
            checkView.setBackgroundResource(R.drawable.bg_media_check_unselected)
            if (isSingleMode()) {
                checkView.setImageBitmap(null)
            } else {
                tvCount.text = null
            }
        }
    }

    private fun isSingleMode() = maxSelectedCount == 1

    private fun exceeds() = maxSelectedCount == mediaSelected.size


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!) ?: return
            setResultAndFinish(arrayListOf(resultUri))
        } else if (resultCode == UCrop.RESULT_ERROR) {
            if (mediaSelected.isNotEmpty()) {
                setResultAndFinish()
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 987
        const val EXTRA_IMAGES = "extra_images"

        fun start(activity: FragmentActivity) {
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
        }
    }

}