package netset.com.kotlinbaseproject

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.squareup.picasso.Picasso
import netset.com.kotlinbaseproject.MyInterFace.PermCallBack
import netset.com.kotlinbaseproject.Utils.ImageUtils
import java.io.File

class ImagePickerActivity : BaseActivity(), PermCallBack, ImageUtils.ImageSelectCallback {
    var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)
        val pickImage = findViewById<Button>(R.id.button2) as Button
        imageView = findViewById<Button>(R.id.imageView) as ImageView
        pickImage.setOnClickListener {
            if (checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA), 123, this)) {
                selectImage()
            }
        }
    }

    private fun selectImage() {
        val builder = ImageUtils.ImageSelect.Builder(this, this, 123).aspectRatio(1, 1)
        builder.crop().start()
    }

    override fun permDenied(resultCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun permGranted(resultCode: Int) {
        selectImage()
    }

    override fun onImageSelected(imagePath: String?, resultCode: Int) {
        val file = File(imagePath)
        Picasso.with(applicationContext).load(file).into(imageView);

    }
}
