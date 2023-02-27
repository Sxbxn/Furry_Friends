package com.k_bootcamp.furry_friends.util.etc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat

fun getGalleryImage(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
    resourceLauncher: ActivityResultLauncher<Intent>
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED
    ) {
        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    } else {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resourceLauncher.launch(intent)
    }
}

fun getCameraImage(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
    resourceLauncher: ActivityResultLauncher<Intent>
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_DENIED
    ) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    } else {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resourceLauncher.launch(intent)
    }
}