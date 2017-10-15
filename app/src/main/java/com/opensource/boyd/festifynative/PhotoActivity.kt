package com.opensource.boyd.festifynative

import android.app.Activity
import android.os.Bundle
import com.opensource.boyd.festifynative.Model.User
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import android.os.Environment.DIRECTORY_PICTURES
import android.support.v4.content.FileProvider
import android.view.View
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.opensource.boyd.festifynative.REST.Services.Retrofit.FestifyApi
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Boyd on 10/14/2017.
 *
 *  This Activity will handle the camera interaction while taking photo to send off to server
 *
 */
class PhotoActivity : Activity() {

    lateinit var user : User
    lateinit var currentPhotoPath: String
    val REQUEST_IMAGE_CAPTURE = 1811


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        user = intent.extras.getParcelable("current-user")

        takePictureBtn.setOnClickListener(View.OnClickListener {
            dispatchTakePictureIntent()
        })



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            setPicture()
        }
    }

    private fun setPicture() {
        val targetW = photoView.width
        val targetH = photoView.height

        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true

        BitmapFactory.decodeFile(currentPhotoPath, bitmapOptions)
        val photoW = bitmapOptions.outWidth
        val photoH = bitmapOptions.outHeight

        val scale = Math.min(photoW/targetW, photoH/targetH)

        bitmapOptions.inJustDecodeBounds = false
        bitmapOptions.inSampleSize = scale

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bitmapOptions)
        photoView.setImageBitmap(bitmap)

        dispatchUploadImageService()

    }


    private fun dispatchUploadImageService() {
        val takenPhoto : File = File(currentPhotoPath)

        val reqFile : RequestBody = RequestBody.create(MediaType.parse("image/*"), takenPhoto)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("upload", takenPhoto.name, reqFile)
        val name : RequestBody = RequestBody.create(MediaType.parse("text/plain"), "upload_test")

        val request : Flowable<ResponseBody> = FestifyApi.instance.uploadImage(body, name)
                request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it
                    Log.i("tag", response.string())
                }, {
                    val exception = it
                    Log.e("tag", exception.localizedMessage)
                })
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photo : File? = null
            try {
                photo = createImageFile()
            } catch(exception : IOException) {
                Log.e("Photo Capture", exception.localizedMessage)
            }
            photo?.let {
                val photoURI = FileProvider.getUriForFile(
                        this,
                        resources.getString(R.string.photo_provider),
                        it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }


}