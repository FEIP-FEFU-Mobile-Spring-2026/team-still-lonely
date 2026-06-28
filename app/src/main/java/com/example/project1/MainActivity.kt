package com.example.project1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.project1.data.CartManager
import com.example.project1.ui.catalog.CatalogFragment
import com.example.project1.ui.productdetail.CartFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CartManager.init(applicationContext)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_menu)

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.navigation_catalog
            showFragment(TAG_CATALOG) { CatalogFragment() }
        } else {
            bottomNav.selectedItemId = savedInstanceState.getInt(
                KEY_SELECTED_NAV,
                R.id.navigation_catalog
            )
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_catalog -> {
                    showFragment(TAG_CATALOG) { CatalogFragment() }
                    true
                }
                R.id.navigation_cart -> {
                    showFragment(TAG_CART) { CartFragment() }
                    true
                }
                else -> false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            KEY_SELECTED_NAV,
            findViewById<BottomNavigationView>(R.id.bottom_nav_menu).selectedItemId
        )
    }

    private fun showFragment(tag: String, factory: () -> Fragment) {
        val fragmentManager = supportFragmentManager
        val current = fragmentManager.findFragmentById(R.id.fragment_container)
        if (current != null && current.tag == tag) return

        val fragment = fragmentManager.findFragmentByTag(tag) ?: factory()
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

    companion object {
        private const val KEY_SELECTED_NAV = "selected_nav_item"
        private const val TAG_CATALOG = "catalog"
        private const val TAG_CART = "cart"
    }
}
