package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_image_picker_dialog.*


class ImagePickerDialog : DialogFragment(), ImagePickerHelper.ImagePickerDialogListener {

    private val TAG = ImagePickerHelper::class.java.canonicalName
    internal var style = DialogFragment.STYLE_NO_TITLE
    internal var theme = R.style.CustomDialog

    var outputPath: String? = null
    internal lateinit var imagePickerHelper: ImagePickerHelper
    private var listener: ImagePickerHelper.ImagePickerDialogListener? = null
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(style, theme)
        imagePickerHelper = ImagePickerHelper()
        imagePickerHelper.initImageHelper(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val window = dialog?.getWindow()
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.fragment_image_picker_dialog, container, false)
        dialog?.setCanceledOnTouchOutside(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        camera.setOnClickListener(View.OnClickListener {
            imagePickerHelper.captureImage()
        })

        gallery.setOnClickListener(View.OnClickListener {
            imagePickerHelper.pickImage()
        })

        cancel.setOnClickListener(View.OnClickListener {
            closeDialog()
        })
    }

    fun closeDialog() {
        mContext = null
        if (dialog?.isShowing()!!) {
            dialog!!.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            imagePickerHelper.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
        if (context is ImagePickerHelper.ImagePickerDialogListener) {
            listener = context
        } else {
            listener = targetFragment as ImagePickerHelper.ImagePickerDialogListener
        }
    }

    override fun saveOutputPath(outputPath: String?) {
        this.outputPath = outputPath
        Log.e(TAG, "saveOutputPath: outputPath=>$outputPath")
        listener?.saveOutputPath(outputPath)
    }

    override fun OnImagePick(destFilePath: String, destFileUri: Uri) {
        listener?.OnImagePick(destFilePath, destFileUri)
        closeDialog()
    }

    override fun OnImageError(error: String) {
        Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                ImagePickerDialog().apply {
                    return ImagePickerDialog()
                }
    }
}
