package com.syedmurtaza.css.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.transition.MaterialElevationScale
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.FragmentHomeBinding
import com.syedmurtaza.css.models.Note
import com.syedmurtaza.css.ui.notes.NotesAdapter
import com.syedmurtaza.css.ui.subject.SubjectBottomSheet
import com.syedmurtaza.css.ui.subject.SubjectsFragment
import com.syedmurtaza.css.ui.vocabulary.VocabularyBottomSheet
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exitTransition = MaterialElevationScale(false).apply {
            duration = 300
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300
        }

        val tabAdapter = TabAdapter(this, 2)
        binding.pager.adapter = tabAdapter

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomNavigationView.selectedItemId = R.id.page_1
                    1 -> binding.bottomNavigationView.selectedItemId = R.id.page_2
                }
            }
        })

        binding.fabIcon.setOnClickListener {
            when (binding.bottomNavigationView.selectedItemId) {
                R.id.page_1 -> {
                    val subjectBottomSheet = SubjectBottomSheet()
                    subjectBottomSheet.arguments =
                        bundleOf(SubjectBottomSheet.KEY_ARG_BUNDLE to null)
                    subjectBottomSheet.show(childFragmentManager, "")
                }
                R.id.page_2 -> {
                    val vocabularyBottomSheet = VocabularyBottomSheet()
                    vocabularyBottomSheet.arguments =
                        bundleOf(VocabularyBottomSheet.KEY_ARG_BUNDLE to null)
                    vocabularyBottomSheet.show(childFragmentManager, "")
                }
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> binding.pager.currentItem = 0
                R.id.page_2 -> binding.pager.currentItem = 1
            }
            return@setOnNavigationItemSelectedListener true
        }

    }
}
