package com.github.nukc.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.nukc.demo.picker.ImagePickerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_image_picker.setOnClickListener {
            ImagePickerActivity.start(this)
        }
    }
}