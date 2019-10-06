package com.example.myapplication

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(){
    private var outputPath: String? = null
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)

    private var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createPermissionListeners()

           /* Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(allPermissionsListener)
                    .withErrorListener(errorListener)
                    .check()*/


        click.setOnClickListener(View.OnClickListener {

            var transaction: FragmentTransaction
            var fragment: Fragment? = BlankFragment()
            transaction = supportFragmentManager.beginTransaction()
            if (fragment != null) {
                transaction.replace(R.id.frame, fragment)
            }
            transaction.commit()
        })
    }

    private fun createPermissionListeners() {
       /* val feedbackViewMultiplePermissionListener = SampleMultiplePermissionListener(this)

        allPermissionsListener = CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(contentView,
                        R.string.all_permissions_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build())

        errorListener = SampleErrorListener()*/
    }
}
