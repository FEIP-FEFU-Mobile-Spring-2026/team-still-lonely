package com.example.project1

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.project1.data.CartManager
import com.example.project1.ui.catalog.CatalogFragment
import com.example.project1.ui.productdetail.CartFragment

class MainActivity : AppCompatActivity() {

    private lateinit var cartBadge: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CartManager.init(applicationContext)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_menu)

        // Создаём бейдж вручную
        val menuView = bottomNav.findViewById<View>(R.id.navigation_cart) as? android.view.ViewGroup
        cartBadge = TextView(this).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textSize = 10f
            setTextColor(android.graphics.Color.WHITE)
            setBackgroundResource(R.drawable.cart_badge_background)
            visibility = View.GONE
            setPadding(6, 2, 6, 2)
            gravity = android.view.Gravity.CENTER
        }
        menuView?.addView(cartBadge)

        // Подписываемся на LiveData
        CartManager.totalQuantity.observe(this) { count ->
            updateBadge(count)
        }

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

        if (savedInstanceState == null) {
            showCatalogFragment()
        }
    }

    private fun updateBadge(count: Int) {
        cartBadge.apply {
            if (count > 0) {
                text = if (count > 99) "99+" else count.toString()
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }
    }

    fun openCatalog() {
        findViewById<BottomNavigationView>(R.id.bottom_nav_menu).selectedItemId =
            R.id.navigation_catalog
    }

    private fun showCatalogFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CatalogFragment())
            .commit()
    }

    private fun showCartFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CartFragment())
            .commit()
    }
}