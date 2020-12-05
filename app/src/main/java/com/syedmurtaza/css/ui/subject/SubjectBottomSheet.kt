package com.syedmurtaza.css.ui.subject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syedmurtaza.css.databinding.SubjectBottomSheetBinding
import com.syedmurtaza.css.models.Subject

class SubjectBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding:SubjectBottomSheetBinding
    private var subject: Subject? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = SubjectBottomSheetBinding.inflate(inflater,container,false)
        val bundle = requireArguments()
        subject = bundle.getParcelable(KEY_ARG_BUNDLE)
        if(subject != null){
            binding.materialTextView.text = "Update the subject"
            binding.subjectName.setText(subject?.name)
            binding.addSubjectButton.text = "UPDATE"
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.subjectName.doOnTextChanged{_,_,_,_ ->
            binding.addSubjectButton.isEnabled = binding.subjectName.text.toString().isNotEmpty()
        }

        binding.addSubjectButton.setOnClickListener{
            if(subject == null){
                val subject = Subject(id = "", name = binding.subjectName.text.toString())
                setFragmentResult(SubjectsFragment.KEY_REQUEST_STRING, bundleOf(SubjectsFragment.KEY_SUBJECT_BUNDLE to subject))
            }
            else{
                val subject = Subject(id = subject?.id!!, name = binding.subjectName.text.toString())
                setFragmentResult(SubjectsFragment.KEY_REQUEST_STRING, bundleOf(SubjectsFragment.KEY_SUBJECT_BUNDLE to subject))
            }
            dismiss()
        }
    }

    companion object{
        const val KEY_ARG_BUNDLE = "subject"
    }
}