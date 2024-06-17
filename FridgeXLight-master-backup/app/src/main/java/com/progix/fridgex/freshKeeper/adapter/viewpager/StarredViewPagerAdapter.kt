package com.progix.fridgex.freshKeeper.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.progix.fridgex.freshKeeper.fragment.starred.StarProductsFragment
import com.progix.fridgex.freshKeeper.fragment.starred.StarRecipesFragment


class StarredViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StarRecipesFragment()
            else -> StarProductsFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}