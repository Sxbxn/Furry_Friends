package com.k_bootcamp.furry_friends.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.ActivityMainBinding
import com.k_bootcamp.furry_friends.view.main.TabWriting.TabWritingFragment
import com.k_bootcamp.furry_friends.view.main.checklist.ChecklistFragment
import com.k_bootcamp.furry_friends.view.main.home.HomeFragment
import com.k_bootcamp.furry_friends.view.main.routine.RoutineFragment
import com.k_bootcamp.furry_friends.view.main.setting.SettingFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
                    true
                }
                R.id.routine -> {
                    showFragment(RoutineFragment.newInstance(), RoutineFragment.TAG)
                    true
                }
                R.id.checklist -> {
                    showFragment(ChecklistFragment.newInstance(), ChecklistFragment.TAG)
                    true
                }
                R.id.writing -> {
                    showFragment(TabWritingFragment.newInstance(), TabWritingFragment.TAG)
                    true
                }
                R.id.setting -> {
                    showFragment(SettingFragment.newInstance(), SettingFragment.TAG)
                    true
                }
                else -> false
            }
        }
        binding.bottomNavigationView.selectedItemId = R.id.home
    }
    private fun showFragment(fragment: Fragment, tag: String) {
        // 기존의 프래그먼트 아이디를 찾아서
        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction()
                .hide(it).commit()
        }
        // 있으면 기존거 그대로 처리
        findFragment?.let {
            supportFragmentManager.beginTransaction()
                .show(it).commit()
        } ?: kotlin.run { // 없으면 새로 만듦 손실허용하면서
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, fragment, tag).commitAllowingStateLoss()
        }
    }
}