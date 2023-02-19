package com.k_bootcamp.furry_friends.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.k_bootcamp.Application
import com.k_bootcamp.furry_friends.R
import com.k_bootcamp.furry_friends.databinding.ActivityMainBinding
import com.k_bootcamp.furry_friends.view.main.writing.TabWritingFragment
import com.k_bootcamp.furry_friends.view.main.checklist.ChecklistFragment
import com.k_bootcamp.furry_friends.view.main.home.HomeFragment
import com.k_bootcamp.furry_friends.view.main.home.submitanimal.SubmitAnimalFragment
import com.k_bootcamp.furry_friends.view.main.routine.RoutineFragment
import com.k_bootcamp.furry_friends.view.main.setting.SettingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
                    true
                }
                R.id.routine -> {
                    showFragment(RoutineFragment.newInstance(), RoutineFragment.TAG)
                    true
                }
                R.id.checklist -> {
                    // 0으로 접근하면 작성
                    showFragment(ChecklistFragment.newInstance().apply {
                        arguments = bundleOf(Pair("flag", 0))
                    }, ChecklistFragment.TAG)
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

    fun showFragment(fragment: Fragment, tag: String) {
        // 기존의 프래그먼트 아이디를 찾아서
//        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right_animation, R.anim.exit_from_right_animation,R.anim.enter_from_right_animation, R.anim.exit_from_right_animation)
            .replace(R.id.fragmentContainerView, fragment, tag)
//            .addToBackStack(null)
            .commit()

    }

    override fun onBackPressed() {
//        super.onBackPressed()
//        supportFragmentManager.popBackStack()
//        updateBottomMenu(binding.bottomNavigationView)
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else if (System.currentTimeMillis() <= backPressedTime + 2000) {
            finish()
        }
    }
    private fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag(HomeFragment.TAG)
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag(RoutineFragment.TAG)
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag(ChecklistFragment.TAG)
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag(TabWritingFragment.TAG)
        val tag5: Fragment? = supportFragmentManager.findFragmentByTag(SettingFragment.TAG)
        if(tag1 != null && tag1.isVisible) navigation.menu.findItem(R.id.home).isChecked =
            true
        else if(tag2 != null && tag2.isVisible) navigation.menu.findItem(R.id.routine).isChecked =
            true
        else if(tag3 != null && tag3.isVisible) navigation.menu.findItem(R.id.checklist).isChecked =
            true
        else if(tag4 != null && tag4.isVisible) navigation.menu.findItem(R.id.writing).isChecked =
            true
        else if(tag5 != null && tag5.isVisible) navigation.menu.findItem(R.id.setting).isChecked =
            true
    }
}