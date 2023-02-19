package com.k_bootcamp.furry_friends.view.main.setting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.fc.baeminclone.screen.base.BaseFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.FragmentSettingBinding
import com.k_bootcamp.furry_friends.extension.*
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.animal.Animal
import com.k_bootcamp.furry_friends.util.dialog.setAiFancyDialog
import com.k_bootcamp.furry_friends.util.dialog.setFancyDialog
import com.k_bootcamp.furry_friends.util.dialog.setWithDrawUser
import com.k_bootcamp.furry_friends.util.etc.*
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.home.submitanimal.SubmitAnimalFragment
import com.k_bootcamp.furry_friends.view.main.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

@AndroidEntryPoint
class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {
    override val viewModel: SettingViewModel by viewModels()
    private lateinit var loading: LoadingDialog
    private val session = Application.prefs.session
    private val animalId = Application.prefs.animalId
    // 이미지 링크를 파일로 바꿔줌
    private lateinit var sendFile: File
    // 보여줄거
    private lateinit var dialogViewGroup: ViewGroup
    private lateinit var dialogView: View
    private lateinit var profileImage: ImageButton
    private lateinit var animalNames: EditText
    private lateinit var animalWeight: EditText
    private lateinit var animalAge: EditText
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var weightInputLayout: TextInputLayout
    private lateinit var ageInputLayout: TextInputLayout
    private lateinit var dateButton: Button
    private lateinit var animalSex: RadioGroup
    private lateinit var animalNeutered: CheckBox

    // 바꾼거
    private lateinit var imageUrl: String
    private lateinit var updateName: String
    private lateinit var updateWeight: String
    private lateinit var updateBirthday: String
    private lateinit var updateSex: String
    private var updateNeutered: Boolean = false

    private lateinit var mainActivity: MainActivity
    private lateinit var body: MultipartBody.Part
    private lateinit var jsonUpdateProfile: RequestBody
    private lateinit var updateProfile: Animal
    // ai 프로필을 클릭했다는 플래그
    private lateinit var flag: String

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                ActivityCompat.requestPermissions(
                    mainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    SubmitAnimalFragment.REQ_STORAGE_PERMISSION
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
                     // findViewById
                    .into(profileImage)

                sendFile = File(absoluteUri!!)
            }
        }
    private val getCameraImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as Bitmap
                val file =
                    bitmapToFile(bitmap, mainActivity.applicationContext.filesDir.path.toString())

                Glide.with(requireContext())
                    .load(bitmap)
                    .transform(CenterCrop(), RoundedCorners(15), Rotate(90))
                    // findViewById
                    .into(profileImage)
                sendFile = file
            }
        }

    override fun getViewBinding(): FragmentSettingBinding =
        FragmentSettingBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun observeData() {
        // 동물 정보 가져와서 바인딩
        viewModel.getAnimalInfo()
        viewModel.animalInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is SettingState.Loading -> {
                    loading.setVisible()
                }
                is SettingState.Error -> {
                    loading.setError()
                    binding.update.toGone()   //   중요!!!!!!! 테스트시 주석해제
                    // 정보가져오지 못했을 때  1. 로그인 x
                    if (session == null) {
                        binding.textView2.text = "로그인 필요"
                    } else {
                        // 2. 로그인은 됐는데 동물 정보가 없음
                        if (animalId == -999) {
                            binding.textView2.text = "동물 정보가 없음"
                        } else {
                            binding.textView2.text = "오류 발생"
                        }
                    }
                }
                is SettingState.SuccessGetInfo -> {
                    loading.dismiss()
                    binding.update.toVisible()    //중요!!!!!!! 테스트시 주석해제
                    binding.ivMember.load(it.imageUrl)
                    binding.textView2.text = it.userId+"님"
                    binding.animalName.text = it.name
                    binding.birthDay.text = it.birthDay
                    binding.sex.text = it.sex
                    binding.idNeutered.isChecked = it.isNeutered
                    binding.weight.text = it.weight.toString() + getString(R.string.kg)

                    updateName = it.name
                    updateBirthday = it.birthDay
                    updateSex = it.sex
                    updateNeutered = it.isNeutered
                    updateWeight = it.weight.toString()
                    imageUrl = it.imageUrl
                }
                is SettingState.Success -> {
                    when (it.response) {
                        "successfully removed" -> requireContext().toast("프로필 삭제 성공")
                        "removal failed" -> requireContext().toast("프로필 삭제 실패")
                        "successfully updated" -> requireContext().toast("프로필 업데이트 성공")
                    }
                }
            }
        }
    }

    override fun initViews() {
        loading = LoadingDialog(requireContext())
        initDialog()
        initSettings()
        initUpdateButton()
        initAiProfileImage()
    }

    private fun initAiProfileImage() = with(binding) {
        // 세션이 있고 동물이 있을 때만 작동
        if (session != null && animalId != -999) {
            ivMember.setOnClickListener {
                Log.e("pushed","pushed!")
                flag = "ai"
                setAiFancyDialog(requireContext()) {
                    loading.setVisible()
                    CoroutineScope(Dispatchers.IO).launch {
                        // 더미
//                        imageUrl = "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c"
                        val file = viewModel.getFile(imageUrl)
                        val fileName = file.name
                        Log.e("ai file",fileName)
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        body = MultipartBody.Part.createFormData("file", fileName, requestFile)
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        // url ?? image file ??
                        Log.e("ai body", body.toString())
                        profileScoping(body)
                    }, 2000)
                }.show()
            }
        }
    }

    private fun profileScoping(body: MultipartBody.Part) {
        viewModel.runAiProfile(body)
        viewModel.isSuccess.observe(viewLifecycleOwner) {
            when(it) {
                is SettingState.Error -> {
                    loading.setError()
                    requireContext().toast("AI 프로필 이미지 생성에 실패했습니다.")
                }
                is SettingState.Loading -> { loading.setVisible() }
                is SettingState.Success -> {
                    loading.dismiss()
                    requireContext().toast("AI 프로필 이미지 생성에 성공했습니다.")
                    binding.ivMember.load(it.response)
                    // 다운로드 실패 시 재시도를 위한 url 초기화
                    imageUrl = it.response
                    // 갤러리에 저장?
                    AlertDialog.Builder(requireContext())
                        .setMessage("저장하시겠습니까?")
                        .setPositiveButton("예"){_,_ -> downloadPhoto(it.response) }
                        .setNegativeButton("아니오"){_,_ ->}
                        .create()
                        .show()
                }
                is SettingState.SuccessGetInfo -> { }
            }
        }
    }

    private fun initSettings() = with(binding) {
        if(session == null) {
            logout.text = "로그인"
            logout.setOnClickListener {
                startActivity(LoginActivity.newIntent(requireContext()))
            }
            withdrawUser.isClickable = false
            withdrawUser.isFocusable = false
            deleteProfile.isClickable = false
            deleteProfile.isFocusable = false
        } else {
            logout.setOnClickListener {
                viewModel.logout()
                viewModel.isSuccess.observe(viewLifecycleOwner) {
                    when(it) {
                        is SettingState.Error -> {
                            loading.setError()
                            requireContext().toast("로그아웃 실패")
                        }
                        is SettingState.Loading -> {
                            loading.setVisible()
                        }
                        is SettingState.Success -> {
                            loading.dismiss()
                            backToLoginActivity()
                        }
                        is SettingState.SuccessGetInfo -> {}
                    }
                }
            }
            withdrawUser.setOnClickListener {
                reallyWithDrawUser()
            }
            deleteProfile.setOnClickListener {
                viewModel.deleteProfile()
                viewModel.isSuccess.observe(viewLifecycleOwner) {
                    when(it) {
                        is SettingState.Error -> {
                            loading.setError()
                            requireContext().toast("프로필 삭제 실패")
                        }
                        is SettingState.Loading -> {
                            loading.setVisible()
                        }
                        is SettingState.Success -> {
                            loading.dismiss()
                            Application.prefs.animalId = it.response.toInt()
                            requireContext().toast("프로필 삭제에 성공하였습니다.")
                            observeData()
                        }
                        is SettingState.SuccessGetInfo -> {}
                    }
                }
            }
        }
        openSourceLicense.setOnClickListener {
            onLicenseDialog()
        }
    }

    // 회원탈퇴 로직
    private fun reallyWithDrawUser() {
        setWithDrawUser(requireContext()) {
            viewModel.withdrawUser()
            viewModel.isSuccess.observe(viewLifecycleOwner) {
                when(it) {
                    is SettingState.Error -> {
                        loading.setError()
                        requireContext().toast("회원탈퇴 실패")
                    }
                    is SettingState.Loading -> {
                        loading.setVisible()
                    }
                    is SettingState.Success -> {
                        loading.dismiss()
                        backToLoginActivity()
                    }
                    is SettingState.SuccessGetInfo -> {}
                }
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
            // ai 프로필일 때 다시요청
            when (flag) {
                "ai" -> profileScoping(body)
                "retry" -> downloadPhoto(imageUrl)
                else -> observeData()
            }
        }
    }



    private fun initUpdateButton() = with(binding) {
        update.setOnClickListener {
            // 기본 바인딩 설정
            dialogView = layoutInflater.inflate(R.layout.dialog_update_animal, null, false)
            dialogViewGroup = (dialogView as ViewGroup)
            ageInputLayout = dialogView.findViewById(R.id.ageInputLayout)
            nameInputLayout = dialogView.findViewById(R.id.nameInputLayout)
            weightInputLayout = dialogView.findViewById(R.id.weightInputLayout)
            profileImage = dialogView.findViewById(R.id.imageButtonImageSelect)
            dateButton = dialogView.findViewById(R.id.datePickerButton)
            animalNames = dialogView.findViewById(R.id.editText_animal_name)
            animalAge = dialogView.findViewById(R.id.editText_animal_age)
            animalWeight = dialogView.findViewById(R.id.editText_animal_weight)
            animalSex = dialogView.findViewById(R.id.radioGroup)
            animalNeutered = dialogView.findViewById(R.id.idNeutered)
            dateButton.setOnClickListener {
                getBirthDay()
            }
            profileImage.setOnClickListener {
                setOnImageButtonClickListener()
            }
            CoroutineScope(Dispatchers.Main).launch {
                sendFile = viewModel.getFile(imageUrl)
            }
            profileImage.load(imageUrl)
            animalAge.setText(updateBirthday)
            animalNames.setText(updateName)
            animalWeight.setText(updateWeight)
            when (updateSex) {
                "남" -> animalSex.check(R.id.radioButtonMale)
                "여" -> animalSex.check(R.id.radioButtonFemale)
            }
            animalNeutered.isChecked = updateNeutered
            // textinputlayout 설정
            initTextState()
            // 다이얼로그
            val d = AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.update_animal))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.update)) { _, _ -> }
                .create()
            d.show()
            d.getButton(AlertDialog.BUTTON_POSITIVE).text = "수정"
            d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (checkValidation()) {
                    if(!::sendFile.isInitialized) {
                         requireContext().toast("이미지를 선택해주세요")

                    } else {
                        //수정 등록
                        updateProfile = Animal(updateName, updateBirthday, updateWeight.toFloat(), updateSex, updateNeutered)
                        Log.e("updateProfile",updateProfile.toString())
                        val fileName = sendFile.name
                        val requestFile = sendFile.asRequestBody("image/*".toMediaTypeOrNull())
                        jsonUpdateProfile = Gson().toJson(updateProfile).toString()
                            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                        body = MultipartBody.Part.createFormData("file", fileName, requestFile)
                        updateAnimalProfile(body, jsonUpdateProfile)
                        d.dismiss()
                        // 둘 중 하나 - 다시 프래그먼트 띄우던지, 데이터만 다시 보던지
                        mainActivity.showFragment(
                            newInstance(),
                            TAG
                        )
                    }
                }
                // 중요 !! 다이얼로그 뷰를 지워주지 않으면 뷰가 중첩되어 에러가 발생함  -> 어쩔 수 없이 findViewById 써야함
                dialogViewGroup.removeView(dialogView)
            }
        }
    }

    private fun updateAnimalProfile(body: MultipartBody.Part, jsonUpdateProfile: RequestBody) {
        viewModel.updateAnimalProfile(body, jsonUpdateProfile)
        viewModel.isSuccess.observe(viewLifecycleOwner) {
            when(it) {
                is SettingState.Error -> {
                    loading.setError()
                }
                is SettingState.Loading -> {
                    loading.setVisible()
                }
                is SettingState.Success -> {
                    loading.dismiss()
                    requireContext().toast("수정이 완료되었습니다.")
                }
                is SettingState.SuccessGetInfo -> {}
            }
        }
    }


    private fun initTextState(){
        animalNames.doOnTextChanged { text, _, _, _ ->
            initValidate(nameInputLayout)
            updateName = text.toString()
        }
        animalAge.doOnTextChanged { text, _, _, _ ->
            initValidate(ageInputLayout)
            updateBirthday = text.toString()
        }
        animalWeight.doOnTextChanged { text, _, _, _ ->
            initValidate(weightInputLayout)
            updateWeight = text.toString()
        }
        animalSex.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonMale -> updateSex = getString(R.string.male)
                R.id.radioButtonFemale -> updateSex = getString(R.string.female)
            }
        }
        animalNeutered.setOnCheckedChangeListener { _, isChecked ->
            updateNeutered = when (isChecked) {
                true -> true
                false -> false
            }
        }
    }

    private fun checkValidation(): Boolean {
        var check = true

            if (!validateEmpty(nameInputLayout, updateName)) {
                shake(animalNames, requireContext())
                check = false
            }
            if (!validateEmpty(ageInputLayout, updateBirthday)) {
                shake(animalAge, requireContext())
                check = false
            }
            if (!validateEmpty(weightInputLayout, updateWeight) || updateWeight.last() == '.') {
                shake(animalWeight, requireContext())
                check = false
            }

            return check

    }
    private fun getBirthDay()   {
        val curCalendar = Calendar.getInstance()
        val datePicker =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val text = "$year.${month + 1}.$dayOfMonth"
                animalAge.setText(text)
                updateBirthday = text
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
    private fun setOnImageButtonClickListener() =
        setFancyDialog(requireContext(), mainActivity, permissionLauncher, getCameraImageLauncher, getGalleryImageLauncher).show()

    private fun downloadPhoto(photoUrl: String?) {
        photoUrl ?: return
        // 글라이드로 로딩 후 로딩 성공시 다운로드 로직 실행
        Glide.with(this)
            .asBitmap()
            .load(photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(
                object: CustomTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        saveBitmapToMediaStore(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        binding.root.appearSnackBar(requireContext(), "다운로드 중...")
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        flag = "retry"
                        binding.root.appearSnackBar(requireContext(), "다운로드 실패..!")
                        loading.setError()
                    }
                }
            )
    }
    // scope storage로 인한 MediaStore 분기처리가 요구됨 (안드로이드 10 기준)
    private fun saveBitmapToMediaStore(bitmap: Bitmap) {
        // 컨텐츠 리졸버를 통하여 미디어스토어의 설정
        val fileName = "${System.currentTimeMillis()}.jpg"
        val resolver = requireContext().contentResolver
        val imageCollectionUrl =
            //10 이상일 경우 정해진 Volume들이 있음 VOLUME_EXTERNAL_PRIMARY- 읽고 쓰기 가능
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                //10이상이 아닐 경우 그냥 외부 URI 갖고 오면 됨
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            // (10이상일때) 이미지 저장시 긴 시간이 걸릴 수 있는데, 이 사이에 그 이미지 파일에 접근할 수도 있다.
            // 이것을 IS_PENDING 값을 1로 두면 막을 수 있고, 0이 되면 그때부터 접근할 수 있다.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollectionUrl, imageDetails)
        imageUri ?: return
        // 실제 저장 과정
        resolver.openOutputStream(imageUri).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)

        }
        // 다시 이미지의 접근 권한을 바꾸고 리졸버의 설정을 업데이트한다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }

        binding.root.appearSnackBar(requireContext(), "다운로드 완료")
    }

    private fun onLicenseDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Open Source License")
            .setView(R.layout.view_opensource)
            .setPositiveButton("확인") { _, _ -> }
            .show()
    }

    private fun backToLoginActivity() {
        requireContext().toast("로그인 창으로 돌아갑니다.")
        mainActivity.startActivity(LoginActivity.newIntent(requireContext()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    companion object {
        fun newInstance() = SettingFragment().apply {

        }
        const val TAG = "SETTING_FRAGMENT"
    }
}