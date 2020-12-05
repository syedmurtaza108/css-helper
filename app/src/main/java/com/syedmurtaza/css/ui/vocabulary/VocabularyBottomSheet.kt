package com.syedmurtaza.css.ui.vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syedmurtaza.css.databinding.VocabularyBottomSheetBinding
import com.syedmurtaza.css.models.Vocabulary

class VocabularyBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: VocabularyBottomSheetBinding
    private var vocabulary: Vocabulary? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = VocabularyBottomSheetBinding.inflate(inflater, container, false)
        val bundle = requireArguments()
        vocabulary = bundle.getParcelable(KEY_ARG_BUNDLE)
        if (vocabulary != null) {
            binding.materialTextView.text = "Update the vocabulary"
            binding.wordText.setText(vocabulary?.word)
            binding.meaningText.setText(vocabulary?.meaning)
            binding.synonymText.setText(vocabulary?.synonym)
            binding.exampleText.setText(vocabulary?.example)
            binding.addSubjectButton.text = "UPDATE"
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.wordText.doOnTextChanged { _, _, _, _ ->
            addButtonEnableChange()
        }

        binding.meaningText.doOnTextChanged { _, _, _, _ ->
            addButtonEnableChange()
        }

        binding.synonymText.doOnTextChanged { _, _, _, _ ->
            addButtonEnableChange()
        }

        binding.exampleText.doOnTextChanged { _, _, _, _ ->
            addButtonEnableChange()
        }

        binding.addSubjectButton.setOnClickListener {
            if (vocabulary == null) {
                val vocabulary = Vocabulary(
                    id = "",
                    word = binding.wordText.text.toString(),
                    meaning = binding.meaningText.text.toString(),
                    synonym = binding.synonymText.text.toString(),
                    example = binding.exampleText.text.toString()
                )
                setFragmentResult(VocabularyFragment.KEY_REQUEST_STRING,
                    bundleOf(VocabularyFragment.KEY_VOCABULARY_BUNDLE to vocabulary))
            } else {
                val vocabulary = Vocabulary(
                    id = vocabulary?.id!!,
                    word = binding.wordText.text.toString(),
                    meaning = binding.meaningText.text.toString(),
                    synonym = binding.synonymText.text.toString(),
                    example = binding.exampleText.text.toString()
                )
                setFragmentResult(VocabularyFragment.KEY_REQUEST_STRING,
                    bundleOf(VocabularyFragment.KEY_VOCABULARY_BUNDLE to vocabulary))
            }
            dismiss()
        }
    }

    private fun addButtonEnableChange() {
        binding.addSubjectButton.isEnabled = binding.wordText.text.toString().isNotEmpty() &&
                binding.meaningText.text.toString()
                    .isNotEmpty() && binding.synonymText.text.toString().isNotEmpty()
                && binding.exampleText.text.toString().isNotEmpty()
    }

    companion object {
        const val KEY_ARG_BUNDLE = "vocabulary"
    }
}