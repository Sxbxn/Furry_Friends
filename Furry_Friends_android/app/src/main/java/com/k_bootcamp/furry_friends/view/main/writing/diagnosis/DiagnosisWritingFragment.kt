package com.k_bootcamp.furry_friends.view.main.writing.diagnosis

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fc.baeminclone.screen.base.BaseFragment
import com.google.gson.Gson
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.FragmentDiagnosisWritingBinding
import com.k_bootcamp.furry_friends.extension.load
import com.k_bootcamp.furry_friends.extension.toGone
import com.k_bootcamp.furry_friends.extension.toVisible
import com.k_bootcamp.furry_friends.extension.toast
import com.k_bootcamp.furry_friends.model.writing.Diagnosis
import com.k_bootcamp.furry_friends.util.dialog.CustomAlertDialog
import com.k_bootcamp.furry_friends.util.dialog.setFancyDialog
import com.k_bootcamp.furry_friends.util.etc.*
import com.k_bootcamp.furry_friends.view.MainActivity
import com.k_bootcamp.furry_friends.view.main.home.submitanimal.SubmitAnimalFragment
import com.k_bootcamp.furry_friends.view.main.writing.TabWritingFragment
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
        if (args?.get("flag") == 0) {
            initReadOnlyView()
        } else if(args?.get("flag") == 1){
            initWritableView()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun initReadOnlyView() = with(binding) {
        val content = args?.get("content")
        val date = args?.get("date")
        val imageUrl = args?.get("url")
        val kind = args?.get("kind")
        val affectedArea = args?.get("affectedArea")
        val comment = args?.get("comment") as String
        dialog = CustomAlertDialog(requireContext())
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
        if(comment.isEmpty()) {
            shimmerLayout2.toGone()
            shimmerLayout2.hideShimmer()
            feedbackTextView.toGone()
        } else {
            shimmerLayout2.toVisible()
            shimmerLayout2.showShimmer(true)
            feedbackTextView.toVisible()
            shimmerLayout2.hideShimmer()
            // 정상 비정상 별 분기처리
            when(comment) {
                // 비정상
                "abnormal" -> {
                    // 동물 분류
                    when(kind) {
                        "강아지" -> {
                            diagnosisListDog.root.toVisible()
                            feedbackTextView.text = getString(R.string.abnormal_dog)
                            diagnosisListDog.abnormalList.toVisible()
                            setDogTextViewClickListener()
                        }
                        "고양이" -> {
                            diagnosisListCat.root.toVisible()
                            feedbackTextView.text = getString(R.string.abnormal_cat)
                            diagnosisListCat.abnormalList.toVisible()
                            setCatTextViewClickListener()
                        }
                    }
                }
                // 정상
                "normal" -> {
                    // 동물 분류
                    when(kind) {
                        "강아지" -> {
                            diagnosisListDog.root.toVisible()
                            feedbackTextView.text = getString(R.string.normal_dog)
                            diagnosisListDog.abnormalList.toGone()
                            setDogTextViewClickListener()
                        }
                        "고양이" -> {
                            diagnosisListCat.root.toVisible()
                            feedbackTextView.text = getString(R.string.normal_cat)
                            diagnosisListCat.abnormalList.toGone()
                            setCatTextViewClickListener()
                        }
                    }
                }
            }

            shimmerLayout2.hideShimmer()
        }

    }

    private fun initWritableView() = with(binding) {
        loading = LoadingDialog(requireContext())
        initDialog()
        initTextState()
        initButton()
        initSpinner()
        getToday()
    }
    private fun initTextState() = with(binding) {
        editTextDescription.doOnTextChanged{ text, _, _, _ ->
            initValidate(descriptionInputLayout)
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
        viewModel.submitDiagnosisWriting(body, jsonDiagnosisWriting)
        viewModel.isSuccess.observe(viewLifecycleOwner) { response ->
            when (response) {
                is DiagnosisState.Success -> {
                    loading.dismiss()
                    requireContext().toast("진단 기록 등록이 완료되었습니다. 잠시 후에 결과를 확인해주세요.")
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
    private fun setOnImageButtonClickListener() =
        setFancyDialog(requireContext(), mainActivity, permissionLauncher, getCameraImageLauncher, getGalleryImageLauncher).show()

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
    private fun setDogTextViewClickListener() = with(binding) {
        diagnosisListDog.cornealUlcer.setOnClickListener {
            setDialogView(diagnosisListDog.cornealUlcer.text.toString(), R.drawable.dog_cornealulcer, R.string.cornealUlcerd)
        }
        diagnosisListDog.blepharitis.setOnClickListener {
            setDialogView(diagnosisListDog.blepharitis.text.toString(), R.drawable.dog_blepharitis, R.string.blepharitisd)
        }
        diagnosisListDog.Cataract.setOnClickListener {
            setDialogView(diagnosisListDog.Cataract.text.toString(), R.drawable.dog_cataract, R.string.Cataract)
        }
        diagnosisListDog.conjunctivitis.setOnClickListener {
            setDialogView(diagnosisListDog.conjunctivitis.text.toString(), R.drawable.dog_conjunctivitis, R.string.conjunctivitisd)
        }
        diagnosisListDog.eyelidTumor.setOnClickListener {
            setDialogView(diagnosisListDog.eyelidTumor.text.toString(), R.drawable.dog_eyelidtumor, R.string.eyelidTumor)
        }
        diagnosisListDog.nuclearSclerosis.setOnClickListener {
            setDialogView(diagnosisListDog.nuclearSclerosis.text.toString(), R.drawable.dog_nuclearsclerosis, R.string.nuclearSclerosis)
        }
        diagnosisListDog.entropionOfTheEyelid.setOnClickListener {
            setDialogView(diagnosisListDog.entropionOfTheEyelid.text.toString(), R.drawable.dog_entropionoftheeyelid, R.string.entropionOfTheEyelid)
        }
        diagnosisListDog.vitreousDuctility.setOnClickListener {
            setDialogView(diagnosisListDog.vitreousDuctility.text.toString(), R.drawable.dog_vitreousductility, R.string.vitreousDuctility)
        }
        diagnosisListDog.mastopathy.setOnClickListener {
            setDialogView(diagnosisListDog.mastopathy.text.toString(), R.drawable.dog_mastopathy, R.string.mastopathy)
        }
        diagnosisListDog.pigmentedKeratitis.setOnClickListener {
            setDialogView(diagnosisListDog.pigmentedKeratitis.text.toString(), R.drawable.dog_pigmentedkeratitis, R.string.pigmentedKeratitis)
        }
    }

    private fun setCatTextViewClickListener() = with(binding) {
        diagnosisListCat.cornealUlcer.setOnClickListener {
            setDialogView(diagnosisListCat.cornealUlcer.text.toString(), R.drawable.cat_cornealulcer, R.string.cornealUlcerc)
        }
        diagnosisListCat.blepharitis.setOnClickListener {
            setDialogView(diagnosisListCat.blepharitis.text.toString(), R.drawable.cat_blepharitis, R.string.blepharitisc)
        }
        diagnosisListCat.conjunctivitis.setOnClickListener {
            setDialogView(diagnosisListCat.conjunctivitis.text.toString(), R.drawable.cat_conjunctivitis, R.string.conjunctivitisc)
        }
        diagnosisListCat.felineCornealSequesration.setOnClickListener {
            setDialogView(diagnosisListCat.felineCornealSequesration.text.toString(), R.drawable.cat_felinecornealsequesration, R.string.felineCornealSequesration)
        }
    }


    private fun setDialogView(message: String, @DrawableRes id: Int, @StringRes sId: Int) = with(binding){
        dialog.initWithView(message, R.layout.dialog_diagnosis_info)
        dialog.findViewById<ImageView>(R.id.diagnosisImageView).setImageResource(id)
        dialog.findViewById<TextView>(R.id.diagnosisTextView).text = getString(sId)
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