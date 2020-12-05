package com.syedmurtaza.css.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.syedmurtaza.css.ui.HomeFragment
import com.syedmurtaza.css.ui.subject.SubjectsFragment
import com.syedmurtaza.css.ui.vocabulary.VocabularyFragment

class TabAdapter(fragment: HomeFragment, private val itemsCount: Int) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            SubjectsFragment.getInstance()
        else
            VocabularyFragment.getInstance()

    }
}