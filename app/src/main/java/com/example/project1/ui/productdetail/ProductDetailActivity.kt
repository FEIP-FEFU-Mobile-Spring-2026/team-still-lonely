package com.example.project1.ui.productdetail

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.project1.R
import com.example.project1.data.CartManager
import com.example.project1.data.Product
import com.google.android.material.button.MaterialButton
import android.view.View

class ProductDetailActivity : AppCompatActivity() {

    private var currentProduct: Product? = null
    private var selectedSize: String? = null
    private lateinit var selectedSizeText: TextView
    private lateinit var sizesContainer: LinearLayout
    private lateinit var addToCartButton: Button
    private lateinit var sizeSectionTitle: TextView  // Заголовок "Выберите размер"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Получаем данные из Intent
        val productId = intent.getIntExtra("product_id", -1)
        val productName = intent.getStringExtra("product_name") ?: "Название не указано"
        val productDescription = intent.getStringExtra("product_description") ?: "Описание отсутствует"
        val productPrice = intent.getDoubleExtra("product_price", 0.0)
        val productImage = intent.getStringExtra("product_image") ?: ""
        val productCategory = intent.getStringExtra("product_category") ?: ""

        // Создаем объект товара с размерами
        currentProduct = Product(
            id = productId,
            name = productName,
            description = productDescription,
            price = productPrice,
            imageUrl = productImage,
            category = productCategory,
            availableSizes = listOf("XS", "S", "M", "L", "XL")
        )

        // Находим View элементы
        val nameTextView = findViewById<TextView>(R.id.productName)
        val descriptionTextView = findViewById<TextView>(R.id.productDescription)
        val imageView = findViewById<ImageView>(R.id.productImage)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val priceTextView = findViewById<TextView?>(R.id.productPrice)

        // Находим элементы для размеров
        sizesContainer = findViewById(R.id.sizesContainer)
        selectedSizeText = findViewById(R.id.selectedSizeText)
        addToCartButton = findViewById(R.id.addToCartButton)
        sizeSectionTitle = findViewById(R.id.sizeSectionTitle)  // Заголовок секции размеров

        // Заполняем данными
        nameTextView.text = productName
        descriptionTextView.text = productDescription
        priceTextView?.text = String.format("$%,.2f", productPrice)

        // Загружаем изображение
        Glide.with(this)
            .load(productImage)
            .placeholder(android.R.color.darker_gray)
            .error(android.R.color.holo_red_light)
            .into(imageView)

        // Проверяем категорию - нужно ли показывать размеры
        if (productCategory == "electronics") {
            // Для электроники скрываем выбор размера
            sizesContainer.visibility = View.GONE
            selectedSizeText.visibility = View.GONE
            sizeSectionTitle.visibility = View.GONE

            // Кнопка сразу активна (размер не нужен)
            addToCartButton.isEnabled = true
            selectedSize = "one_size"  // Условный размер
            selectedSizeText.text = "Размер: стандартный"
        } else {
            // Для одежды и драгоценностей показываем выбор размера
            sizesContainer.visibility = View.VISIBLE
            selectedSizeText.visibility = View.VISIBLE
            sizeSectionTitle.visibility = View.VISIBLE
            createSizeButtons()
        }

        // Обработка нажатия на кнопку "Назад"
        backButton.setOnClickListener {
            finish()
        }

        // Обработка нажатия на кнопку добавления в корзину
        addToCartButton.setOnClickListener {
            val size = selectedSize
            val product = currentProduct

            if (size != null && product != null) {
                CartManager.addToCart(product, size)
                Toast.makeText(
                    this,
                    "Товар добавлен в корзину",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun createSizeButtons() {
        val sizes = currentProduct?.availableSizes ?: return

        for (size in sizes) {
            val button = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
            button.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            button.text = size
            button.setPadding(8, 8, 8, 8)

            if (CartManager.isInCart(currentProduct!!.id, size)) {
                button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                button.strokeColor = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
            }

            button.setOnClickListener {
                for (i in 0 until sizesContainer.childCount) {
                    val child = sizesContainer.getChildAt(i) as MaterialButton
                    child.isChecked = false
                }

                button.isChecked = true
                selectedSize = size
                selectedSizeText.text = "Размер: $size"
                addToCartButton.isEnabled = true
            }

            sizesContainer.addView(button)
        }
    }
}