package com.k_bootcamp.furry_friends.view.main.writing.diagnosis

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fc.baeminclone.screen.base.BaseFragment
import com.google.gson.Gson
import com.k_bootcamp.furry_friends.databinding.FragmentDiagnosisWritingBinding
import com.k_bootcamp.furry_friends.extension.load
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.writing.Daily
import com.k_bootcamp.furry_friends.model.writing.Diagnosis
import com.k_bootcamp.furry_friends.util.dialog.CustomAlertDialog
import com.k_bootcamp.furry_friends.util.etc.*
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.home.submitanimal.SubmitAnimalFragment
import com.k_bootcamp.furry_friends.view.main.writing.TabWritingFragment
import com.k_bootcamp.furry_friends.view.main.writing.daily.DailyState
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DiagnosisWritingFragment: BaseFragment<DiagnosisWritingViewModel, FragmentDiagnosisWritingBinding>() {
    override val viewModel: DiagnosisWritingViewModel by viewModels()
    private var args: Bundle? = null
    private lateinit var loading: LoadingDialog
    private lateinit var dialog: CustomAlertDialog
    private lateinit var mainActivity: MainActivity
    private lateinit var sendFile: File
    private lateinit var body: MultipartBody.Part
    private lateinit var jsonDiagnosisWriting: RequestBody
    private lateinit var diagnosisWriting: Diagnosis
    private var description = ""
    private var date = ""
    private var kind = ""
    private var affectedArea = ""

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
                    .into(binding.imageButtonImageSelect)

                sendFile = File(absoluteUri!!)
                Log.e("gallery", sendFile.name)
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
                Log.e("camera", sendFile.path)
            }
        }
    override fun getViewBinding(): FragmentDiagnosisWritingBinding = FragmentDiagnosisWritingBinding.inflate(layoutInflater)

    override fun observeData() {

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        args = arguments
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun initViews() {
        initShimmer()
        Log.e("flag", args.toString())
        if (args?.get("flag") == 0) {
            initReadOnlyView()
        } else {
            initWritableView()
        }
    }
    private fun initReadOnlyView() = with(binding) {
        val content = args?.get("content")
        val date = args?.get("date")
        val imageUrl = args?.get("url")
        val kind = args?.get("kind")
        val affectedArea = args?.get("affectedArea")
        saveWriting.toGone()
        // read only에선 스피너 없애고 텍스트만 보여주기
        animalKind.toGone()
        animalAffectedArea.toGone()
        writeOnlyTint.toGone()
        animalKindTextView.toVisible()
        animalAffectedAreaTextView.toVisible()
        readOnlyTint.toVisible()

        diagnosisTextView.apply {
            isEnabled = false
            text = date.toString()
        }
        editTextDescription.apply {
            isEnabled = false
            setText(content.toString())
        }
        animalKindTextView.text = kind.toString()+", "
        animalAffectedAreaTextView.text = affectedArea.toString()
        imageButtonImageSelect.load(imageUrl.toString())

    }

    private fun initWritableView() = with(binding) {
        loading = LoadingDialog(requireContext())
        dialog = CustomAlertDialog(requireContext())
        initDialog()
        initTextState()
        initButton()
        initSpinner()
        getToday()
    }
    private fun initTextState() = with(binding) {
        editTextDescription.doOnTextChanged{ text, _, _, _ ->
            initValidate(descriptionInputLayout)
//            viewModel.descLiveData = text.toString()
            description = text.toString()
        }
    }
    private fun initButton() = with(binding) {
        imageButtonImageSelect.setOnClickListener {
            setOnImageButtonClickListener()
        }
        saveWriting.setOnClickListener {
            if(checkValidation()) {
                diagnosisWriting = Diagnosis(description, date, "", kind, affectedArea)
                if(!::sendFile.isInitialized) {
                    requireContext().toast("이미지를 불러와 주십시오")
                } else {
                    val fileName = sendFile.name
                    val requestFile = sendFile.asRequestBody("image/*".toMediaTypeOrNull())
                    jsonDiagnosisWriting = Gson().toJson(diagnosisWriting).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    body = MultipartBody.Part.createFormData("file", fileName, requestFile)
                    submitDiagnosisWriting(body, jsonDiagnosisWriting)
                }
            }
        }
    }
    private fun initSpinner() = with(binding) {
        animalKind.toVisible()
        animalAffectedArea.toVisible()
        animalAffectedAreaTextView.toGone()
        animalKindTextView.toGone()
        val spinnerKind = animalKind
        val spinnerAffectedArea = animalAffectedArea
        spinnerKind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                kind = adapterView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                kind = "강아지"
            }
        }
        spinnerAffectedArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                affectedArea = adapterView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                affectedArea = "안구"
            }
        }

    }
    // 진단 기록 등록 -> 등록하면 서버에서 받고 진단 후 진단 피드백을 반환해야함
    private fun submitDiagnosisWriting(body: MultipartBody.Part, jsonDiagnosisWriting: RequestBody) {
        Log.e("daily", diagnosisWriting.toString())
        viewModel.submitDiagnosisWriting(body, jsonDiagnosisWriting)
        viewModel.isSuccess.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DiagnosisState.Success -> {
                    loading.dismiss()
                    requireContext().toast("진단 기록 등록이 완료되었습니다. 진단을 시작합니다.")
                    mainActivity.showFragment(TabWritingFragment.newInstance(), TabWritingFragment.TAG)
                }
                is DiagnosisState.Error -> {
                    // error code
                    loading.setError()
                }
                is DiagnosisState.Loading -> {
                    // loading code
                    loading.setVisible()
                }
            }
        }

    }
    private fun setOnImageButtonClickListener() {
        dialog.init("사진 가져올 곳을 선택해주세요", "카메라", "갤러리")
        dialog.getPositive().setOnClickListener {
            dialog.exit()
            getCameraImage(mainActivity, permissionLauncher, getCameraImageLauncher)
        }
        dialog.getNegative().setOnClickListener {
            dialog.exit()
            getGalleryImage(mainActivity, permissionLauncher, getGalleryImageLauncher)
        }

    }
    private fun checkValidation(): Boolean {
        var check = true
        with(binding) {
            if (!validateEmpty(descriptionInputLayout, description)) {
                shake(editTextDescription, requireContext())
                check = false
            }
            return check
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
            submitDiagnosisWriting(body, jsonDiagnosisWriting)
        }
    }
    private fun initShimmer() = with(binding) {
        shimmerLayout.hideShimmer()
    }
    @SuppressLint("SimpleDateFormat")
    private fun getToday() {
        val sdf = SimpleDateFormat("yyyy-MM-dd E요일")
        val cal = Calendar.getInstance().time
        date = sdf.format(cal).toString()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    companion object {
        fun newInstance() = DiagnosisWritingFragment().apply {

        }
        const val TAG = "DIAGNOSIS_WRITING_FRAGMENT"
    }

}