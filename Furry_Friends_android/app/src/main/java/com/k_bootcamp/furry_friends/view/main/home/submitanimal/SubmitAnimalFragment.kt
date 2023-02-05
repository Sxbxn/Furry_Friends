package com.k_bootcamp.furry_friends.view.main.home.submitanimal

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fc.baeminclone.screen.base.BaseFragment
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.FragmentSubmitAnimalBinding
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.animal.Animal
import com.k_bootcamp.furry_friends.model.user.SignInUser
import com.k_bootcamp.furry_friends.util.dialog.CustomAlertDialog
import com.k_bootcamp.furry_friends.util.etc.*
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.home.HomeFragment
import com.k_bootcamp.furry_friends.view.main.login.LoginActivity
import com.k_bootcamp.furry_friends.view.main.login.LoginState
import com.k_bootcamp.furry_friends.view.main.signin.SignInState
import com.k_bootcamp.furry_friends.view.main.signin.SignInViewModel

import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*

@AndroidEntryPoint
class SubmitAnimalFragment : BaseFragment<SubmitAnimalViewModel, FragmentSubmitAnimalBinding>() {
    override val viewModel: SubmitAnimalViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    private var session = Application.prefs.session
    private lateinit var loading: LoadingDialog
    private lateinit var animal: Animal
    private var name: String = ""
    private var birthDay: String = ""
    private var weight: String = ""
    private var sex: String = "남"
    private var isNeutered: Boolean = false
    private lateinit var sendFile: File
    private lateinit var dialog: CustomAlertDialog
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                ActivityCompat.requestPermissions(
                    mainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQ_STORAGE_PERMISSION
                )
            }
        }
    private val getGalleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data // 선택한 이미지의 주소(상대경로)
                val absoluteUri = getFullPathFromUri(requireContext(), uri!!)

                Glide.with(requireContext())
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(15))
                    .into(binding.imageButtonImageSelect)

                sendFile = File(absoluteUri!!)
                Log.e("gallery", sendFile.absolutePath)
            }
        }
    private val getCameraImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as Bitmap
                val file = bitmapToFile(bitmap, mainActivity.applicationContext.filesDir.path.toString())

                Glide.with(requireContext())
                    .load(bitmap)
                    .transform(CenterCrop(), RoundedCorners(15), Rotate(90))
                    .into(binding.imageButtonImageSelect)

                sendFile = file
                Log.e("camera", sendFile.absolutePath)
            }
        }

    override fun getViewBinding(): FragmentSubmitAnimalBinding =
        FragmentSubmitAnimalBinding.inflate(layoutInflater)

    override fun observeData() {
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initViews() {
        loading = LoadingDialog(requireContext())
        dialog = CustomAlertDialog(requireContext())
        initDialog()
        initTextState()
        initButton()
    }

    private fun submitAnimal(animal: Animal) {
        viewModel.submitAnimal(animal)
        viewModel.isSuccess.observe(this) { response ->
            when (response) {
                is SubmitAnimalState.Success -> {
                    loading.dismiss()
                    requireContext().toast("등록이 완료되었습니다.")
                    mainActivity.showFragment(HomeFragment.newInstance().apply {
                        arguments = bundleOf("session" to session)
                    }, HomeFragment.TAG)
                }
                is SubmitAnimalState.Error -> {
                    // error code
                    loading.setError()
                }
                is SubmitAnimalState.Loading -> {
                    // loading code
                    loading.setVisible()
                }
            }

        }
    }

    private fun initTextState() = with(binding) {
        editTextAnimalName.doOnTextChanged { text, _, _, _ ->
            initValidate(nameInputLayout)
            viewModel.nameLiveData.value = text.toString()
            name = text.toString()
        }

        editTextAnimalAge.doOnTextChanged { text, _, _, _ ->
            initValidate(ageInputLayout)
        }

        editTextAnimalWeight.doOnTextChanged { text, _, _, _ ->
            initValidate(weightInputLayout)
            viewModel.weightLiveData.value = text.toString()
            weight = text.toString()
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonMale -> sex = getString(R.string.male)
                R.id.radioButtonFemale -> sex = getString(R.string.female)
            }
        }
        idNeutered.setOnCheckedChangeListener { _, isChecked ->
            isNeutered = when (isChecked) {
                true -> true
                false -> false
            }
        }
    }

    private fun initDialog() {
        // 요청 취소
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
        }
        // 요청 다시시도
        loading.retryButton().setOnClickListener {
            loading.setInvisible()
            submitAnimal(animal)
        }
    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun initButton() = with(binding) {
        imageButtonImageSelect.setOnClickListener {
            setOnImageButtonClickListener()
        }
        datePickerButton.setOnClickListener {
            getBirthDay()
        }
        buttonSubmit.setOnClickListener {
            if (checkValidation()) {
                animal = Animal(name, birthDay, weight, sex, isNeutered)
                submitAnimal(animal)
            }
        }
    }

    private fun setOnImageButtonClickListener() {
        dialog.init("사진 가져올 곳을 선택해주세요", "카메라", "갤러리")
        dialog.getPositive().setOnClickListener {
            dialog.exit()
            getCameraImage()
        }
        dialog.getNegative().setOnClickListener {
            dialog.exit()
            getGalleryImage()
        }

    }

    private fun getBirthDay() = with(binding) {
        val curCalendar = Calendar.getInstance()
        val datePicker =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                val text = "$year.${month + 1}.$dayOfMonth"
                viewModel.birthDayLiveData.value = text
                editTextAnimalAge.setText(text)
                birthDay = text
            }
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.MySpinnerDatePickerStyle,
            datePicker,
            curCalendar.get(Calendar.YEAR),
            curCalendar.get(Calendar.MONTH),
            curCalendar.get(Calendar.DAY_OF_WEEK)
        )
        datePickerDialog.show()
    }

    private fun getGalleryImage() {
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getGalleryImageLauncher.launch(intent)
        }
    }

    private fun getCameraImage() {
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            intent.type = "image/*"
            getCameraImageLauncher.launch(intent)
        }
    }

    private fun checkValidation(): Boolean {
        var check = true
        with(binding) {
            if (!validateEmpty(nameInputLayout, name)) {
                shake(editTextAnimalName, requireContext())
                check = false
            }
            if (!validateEmpty(ageInputLayout, birthDay)) {
                shake(editTextAnimalAge, requireContext())
                check = false
            }
            if (!validateEmpty(weightInputLayout, weight)) {
                shake(editTextAnimalWeight, requireContext())
                check = false
            }

            return check
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    companion object {
        fun newInstance() = SubmitAnimalFragment().apply {

        }

        const val REQ_STORAGE_PERMISSION = 1
        const val TAG = "SUBMIT_ANIMAL_FRAGMENT"
    }
}