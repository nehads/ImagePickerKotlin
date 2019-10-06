package com.example.myapplication


import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.click
import kotlinx.android.synthetic.main.fragment_blank.*
import java.io.File

class BlankFragment : Fragment(), ImagePickerHelper.ImagePickerDialogListener  {
    private var outputPath: String? = null
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)


    private var imageFile: File? = null

    lateinit var context : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        click.setOnClickListener(View.OnClickListener {
            val ft = context.supportFragmentManager.beginTransaction()
            val prev = context.supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val newFragment = ImagePickerDialog.newInstance()
            newFragment.setTargetFragment(this, 300)
            newFragment.show(ft, "dialog")
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            this.context = context
        }
    }

    override fun saveOutputPath(outputPath: String?) {
        this.outputPath = outputPath
    }

    override fun OnImagePick(destFilePath: String, destFileUri: Uri) {
        this.imageFile = File(destFilePath)
        Glide.with(this)
                .load(destFilePath)
                //.apply(RequestOptions.circleCropTransform())
                //  .error(R.drawable.placeholder_round)
                // .placeholder(R.drawable.placeholder_round)
                .into(image)
    }

    override fun OnImageError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }
}
