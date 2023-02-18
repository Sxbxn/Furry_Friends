package com.k_bootcamp.furry_friends.view.main.setting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fc.baeminclone.screen.base.BaseFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.DialogUpdateAnimalBinding
import com.k_bootcamp.furry_friends.databinding.FragmentSettingBinding
import com.k_bootcamp.furry_friends.extension.load
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.animal.Animal
import com.k_bootcamp.furry_friends.model.writing.Daily
import com.k_bootcamp.furry_friends.util.dialog.setAiFancyDialog
import com.k_bootcamp.furry_friends.util.dialog.setFancyDialog
import com.k_bootcamp.furry_friends.util.etc.*
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.home.submitanimal.SubmitAnimalFragment
import com.k_bootcamp.furry_friends.view.main.login.LoginActivity
import com.k_bootcamp.furry_friends.view.main.writing.TabWritingFragment
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


    private fun dummies() {
        imageUrl =
            "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c"
        updateName = "123"
        updateWeight = "1.2"
        updateBirthday = "2020-1-2"
        updateSex = "남"
        updateNeutered = false
    }
    ///////////////////////////////////////////////////////////////// 런쳐 변수는 추후 모듈화 해야할 듯 중복이 많음
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

    /////////////////////////////////////////////////////////////////
    override fun getViewBinding(): FragmentSettingBinding =
        FragmentSettingBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun observeData() {
        // 동물 정보 가져와서 바인딩딩
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
                    binding.textView2.text = it.userId
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
        // 더미데이터
//        dummies()
        initDialog()
        initSettings()
        initUpdateButton()
        initAiProfileImage()
    }

    private fun initAiProfileImage() = with(binding) {
        // 세션이 있고 동물이 있을 때만 작동
        Log.e("user_id",session.toString())
        Log.e("animal_id",animalId.toString())
        if (session != null && animalId != -999) {
            ivMember.setOnClickListener {
                Log.e("pushed","pushed!")
                flag = "ai"
                setAiFancyDialog(requireContext()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        imageUrl = "https://fastly.picsum.photos/id/544/200/200.jpg?hmac=iIsE7MkJ1i0DzyQjD7hXFjiVpz8uukzJTk9XCNuWS8c"
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
                    }, 1000)
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
                    requireContext().toast("AI 프로필 이미지 생성에 성공공습니다.")
                    binding.ivMember.load(it.response)
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
                            requireContext().toast("로그인 창으로 돌아갑니다.")
                            mainActivity.startActivity(LoginActivity.newIntent(requireContext()).apply{
                                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            })
                        }
                        is SettingState.SuccessGetInfo -> {}
                    }
                }
            }
            withdrawUser.setOnClickListener {
                viewModel.withdrawUser()
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

    private fun initDialog() {
        // 요청 취소
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
        }
        // 요청 다시시도
        loading.retryButton().setOnClickListener {
            // ai 프로필일 때 다시요청
            if(flag == "ai") profileScoping(body)
            else observeData()
        }
    }

    private fun onLicenseDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Open Source License")
            .setView(R.layout.view_opensource)
            .setPositiveButton("확인") { _, _ -> }
            .show()
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
                sendFile = viewModel.getFile(imageUrl)!!
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
//                        observeData()
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
            Log.e("name", updateName)
        }
        animalAge.doOnTextChanged { text, _, _, _ ->
            initValidate(ageInputLayout)
            updateBirthday = text.toString()
            Log.e("age", updateBirthday)
        }
        animalWeight.doOnTextChanged { text, _, _, _ ->
            initValidate(weightInputLayout)
            updateWeight = text.toString()
            Log.e("weight", updateWeight)
        }
        animalSex.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonMale -> updateSex = getString(R.string.male)
                R.id.radioButtonFemale -> updateSex = getString(R.string.female)
            }
            Log.e("sex", updateSex)
        }
        animalNeutered.setOnCheckedChangeListener { _, isChecked ->
            updateNeutered = when (isChecked) {
                true -> true
                false -> false
            }
            Log.e("ne", updateNeutered.toString())
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