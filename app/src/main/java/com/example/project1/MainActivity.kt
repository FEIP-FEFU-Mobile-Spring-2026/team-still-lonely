package com.example.project1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.project1.ui.catalog.CatalogFragment
import com.example.project1.ui.productdetail.CartFragment  // ЭТОТ ИМПОРТ НУЖЕН

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_menu)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.navigation_catalog -> {
                    showCatalogFragment()
                    true
                }
                R.id.navigation_cart -> {
                    showCartFragment()
                    true
                }
                else -> false
            }
        }

        showCatalogFragment()
    }

    private fun showCatalogFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CatalogFragment())
            .commit()
    }

    private fun showCartFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CartFragment())  // Строка 41
            .commit()
    }
}