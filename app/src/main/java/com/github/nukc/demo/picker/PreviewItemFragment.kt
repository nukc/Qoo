package com.github.nukc.demo.picker

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.github.nukc.demo.R
import com.github.nukc.demo.picker.model.BaseMediaItem
import com.github.nukc.demo.picker.model.ImageItem
import com.github.nukc.demo.picker.model.VideoItem
import com.github.nukc.demo.picker.utils.PhotoMetadataUtils
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase
import kotlinx.android.synthetic.main.fragment_preview_item.*

class PreviewItemFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_preview_item, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val item: BaseMediaItem = arguments?.getParcelable(ARGS_ITEM) ?: return

        if (item is VideoItem) {
            video_play_button.visibility = View.VISIBLE
            video_play_button.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(item.contentUri, "video/*")
                try {
                    startActivity(intent);
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        it.context,
                        R.string.error_no_video_activity,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            video_play_button.visibility = View.GONE
        }

        if (item is ImageItem) {
            val size: Point = PhotoMetadataUtils.getBitmapSize(item.contentUri, activity)
            Glide.with(requireContext())
                .load(item.contentUri)
                .apply(
                    RequestOptions()
                        .override(size.x, size.y)
                        .priority(Priority.HIGH)
                        .fitCenter()
                )
                .into(image_view)
        }

        image_view.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN
        image_view.setSingleTapListener {
            view_background.animate()
                .translationY(
                    if (view_background.translationY == 0f) view_background.height.toFloat()
                    else 0f
                )
                .setDuration(200)
                .start()
            tv_back.animate()
                .translationY(
                    if (tv_back.translationY == 0f) tv_back.height.toFloat()
                    else 0f
                )
                .setDuration(200)
                .start()
            listener?.onClick()
        }

        tv_back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun resetView() {
        image_view.resetMatrix()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        private const val ARGS_ITEM = "args_item"
        fun newInstance(item: BaseMediaItem?): PreviewItemFragment {
            val fragment = PreviewItemFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARGS_ITEM, item)
            fragment.arguments = bundle
            return fragment
        }
    }
}