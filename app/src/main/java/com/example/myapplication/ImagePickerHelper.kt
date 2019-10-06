package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kbeanie.multipicker.api.CameraImagePicker
import com.kbeanie.multipicker.api.ImagePicker
import com.kbeanie.multipicker.api.Picker
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback
import com.kbeanie.multipicker.api.entity.ChosenImage
import com.kbeanie.multipicker.utils.FileUtils
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImagePickerHelper {
    private val FILE_NAME_FORMAT = "yyyyMMddHHmmss"
    private val TAG = ImagePickerHelper::class.java.canonicalName
    private var deleteOriginal = false
    private var saveToCache = false
    private var imagePicker: ImagePicker? = null
    private var cameraPicker: CameraImagePicker? = null
    private var mImagePickerCallback: ImagePickerCallback? = null
    private var outputPath: String? = null
    private var mContext: Context? = null
    private var listener: ImagePickerDialogListener? = null
    private var mActivity: Activity? = null
    private var mFragment: Fragment? = null


    fun initImageHelper(activity: Activity) {
        mActivity = activity
        initHelper()
    }

    fun initImageHelper(fragment: Fragment) {
        mFragment = fragment
        initHelper()
    }

    private fun initHelper() {
        try {
            if (this.mActivity != null) {
                listener = mActivity as ImagePickerDialogListener
                mContext = mActivity
            } else if (this.mFragment != null) {
                listener = mFragment as ImagePickerDialogListener
                mContext = mFragment!!.context
            }
        } catch (e: ClassCastException) {
            throw ClassCastException(mContext.toString() + " must implement ImagePickerDialogListener")
        }

        initializeImageCallback()
        initializeCameraPicker()
        initializeGalleryPicker()
    }

    private fun initializeCameraPicker() {
        if (this.mActivity != null) {
            cameraPicker = CameraImagePicker(mActivity)
        } else if (this.mFragment != null) {
            cameraPicker = CameraImagePicker(mFragment)
        }
        cameraPicker?.shouldGenerateMetadata(false)
        cameraPicker?.shouldGenerateThumbnails(false)
        cameraPicker?.setImagePickerCallback(mImagePickerCallback)
    }

    private fun initializeGalleryPicker() {
        if (this.mActivity != null) {
            imagePicker = ImagePicker(mActivity)
        } else if (this.mFragment != null) {
            imagePicker = ImagePicker(mFragment)
        }
        imagePicker?.shouldGenerateMetadata(false)
        imagePicker?.shouldGenerateThumbnails(false)
        imagePicker?.setImagePickerCallback(mImagePickerCallback)
    }

    fun pickImage() {
        imagePicker?.pickImage()
    }

    fun captureImage() {
        outputPath = cameraPicker?.pickImage()
        listener?.saveOutputPath(outputPath)
    }

    fun setDeleteOriginal(deleteOriginal: Boolean) {
        this.deleteOriginal = deleteOriginal
    }

    fun setSaveToCache(saveToCache: Boolean) {
        this.saveToCache = saveToCache
    }

    private fun initializeImageCallback() {
        mImagePickerCallback = object : ImagePickerCallback {

            override fun onError(errorMessage: String) {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onImagesChosen(list: List<ChosenImage>?) {

                if (list != null && list.size > 0) {
                    //create new file name path
                    val fileName = SimpleDateFormat(FILE_NAME_FORMAT, Locale.getDefault()).format(Date())
                    val fileDirectory: String
                    if (saveToCache)
                        fileDirectory = mContext?.getFilesDir().toString()
                    else
                        fileDirectory = (Environment.getExternalStorageDirectory().toString() + "/"
                                + mContext?.getApplicationInfo()?.loadLabel(mContext!!.getPackageManager()).toString())

                    Log.e(TAG, "onImagesChosen: fileDirectory=>$fileDirectory")

                    var fileDest: File? = null
                    val fileSource = File(list[0].getOriginalPath())
                    Log.e(TAG, "onImagesChosen: fileSource=>$fileSource")
                    if (fileSource.exists()) {
                        try {
                            //Try to compress the image and save it
                            Class.forName("id.zelory.compressor.Compressor")
                            fileDest = Compressor(mContext)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(fileDirectory)
                                    .compressToFile(fileSource)
                            Log.e(javaClass.canonicalName, "Saved file through compression library at fileDest=>" + fileDest!!)

                        } catch (e: ClassNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {

                            if (fileDest == null) {
                                //The library is not present so save using file utils
                                fileDest = File(fileDirectory, fileName + "_copy_" + ".jpg")
                                if (fileDest.exists()) {
                                    fileDest.delete()
                                }
                                try {
                                    FileUtils.copyFile(fileSource, fileDest)
                                    Log.e(javaClass.canonicalName, "Saved file through FileUtils")
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                                if (deleteOriginal)
                                    fileSource.delete()


                            }
                        }
                    } else {
                        Log.e(javaClass.canonicalName, mContext.toString() + " must implement ImagePickerDialogListener")
                    }

                    val imageUri = Uri.fromFile(fileDest)
                    Log.e("File size", "Original file size in KB =>>" + fileDest!!.length() / 1024)
                    Log.d("destination uri", imageUri.toString())
                    Log.d("destination uri", fileDest.absolutePath)

                    listener?.OnImagePick(fileDest.absolutePath, imageUri)
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    if (this.mActivity != null) {
                        imagePicker = ImagePicker(mActivity)
                    } else if (this.mFragment != null) {
                        imagePicker = ImagePicker(mFragment)
                    }
                    imagePicker?.shouldGenerateMetadata(false)
                    imagePicker?.shouldGenerateThumbnails(false)
                    imagePicker?.setImagePickerCallback(mImagePickerCallback)
                }
                imagePicker?.submit(data)
                return
            }

            if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    if (this.mActivity != null) {
                        cameraPicker = CameraImagePicker(mActivity)
                    } else if (this.mFragment != null) {
                        cameraPicker = CameraImagePicker(mFragment)
                    }
                    cameraPicker?.reinitialize(outputPath)
                    cameraPicker?.shouldGenerateMetadata(false)
                    cameraPicker?.shouldGenerateThumbnails(false)
                    cameraPicker?.setImagePickerCallback(mImagePickerCallback)
                }
                cameraPicker?.submit(data)
                return
            }
        }
    }

    fun onRestoreInstance(outputPath: String) {
        this.outputPath = outputPath
    }


    interface ImagePickerDialogListener {
        fun saveOutputPath(outputPath: String?)

        fun OnImagePick(destFilePath: String, destFileUri: Uri)

        fun OnImageError(error: String)
    }
}