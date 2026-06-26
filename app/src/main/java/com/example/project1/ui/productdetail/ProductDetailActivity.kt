package com.example.project1.ui.productdetail

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.project1.R
import com.example.project1.data.CartManager
import com.example.project1.data.Product
import com.example.project1.data.Size
import com.example.project1.utils.PriceFormatter
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private var currentProduct: Product? = null
    private var selectedSize: String? = null
    private lateinit var selectedSizeText: TextView
    private lateinit var sizesContainer: LinearLayout
    private lateinit var addToCartButton: Button
    private lateinit var sizeSectionTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        CartManager.init(applicationContext)

        // Получаем данные из Intent
        val productId = intent.getStringExtra("product_id") ?: ""
        val productName = intent.getStringExtra("product_name") ?: "Название не указано"
        val productDescription = intent.getStringExtra("product_description") ?: "Описание отсутствует"
        val productPrice = intent.getDoubleExtra("product_price", 0.0)
        val productImage = intent.getStringExtra("product_image") ?: ""
        val productCategory = intent.getStringExtra("product_category") ?: ""
        val productSizes = intent.getStringArrayExtra("product_sizes") ?: arrayOf("XS", "S", "M", "L", "XL")
        val productMaterial = intent.getStringExtra("product_material") ?: ""
        val productCountry = intent.getStringExtra("product_country") ?: ""

        // Создаем объект товара
        val sizes = productSizes.map { Size(id = it, name = it) }
        currentProduct = Product(
            id = productId,
            name = productName,
            shortDescription = productDescription,
            longDescription = productDescription,
            priceInKopecks = (productPrice * 100).toInt(),
            imageUrl = productImage,
            tags = emptyList(),
            sizes = sizes,
            categoryId = productCategory,
            material = productMaterial,
            countryOfOrigin = productCountry
        )

        // Находим View элементы
        val nameTextView = findViewById<TextView>(R.id.productName)
        val descriptionTextView = findViewById<TextView>(R.id.productDescription)
        val imageView = findViewById<ImageView>(R.id.productImage)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val priceTextView = findViewById<TextView>(R.id.productPrice)

        // Находим элементы для размеров
        sizesContainer = findViewById(R.id.sizesContainer)
        selectedSizeText = findViewById(R.id.selectedSizeText)
        addToCartButton = findViewById(R.id.addToCartButton)
        sizeSectionTitle = findViewById(R.id.sizeSectionTitle)

        // Заполняем данными
        nameTextView.text = productName
        descriptionTextView.text = productDescription
        priceTextView.text = PriceFormatter.formatRubles(productPrice)

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

            addToCartButton.isEnabled = true
            selectedSize = "one_size"
            selectedSizeText.text = "Размер: стандартный"
        } else {
            // Для одежды показываем выбор размера
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
                lifecycleScope.launch {
                    CartManager.addToCart(product.id, size, 1)
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Товар добавлен в корзину",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun createSizeButtons() {
        val product = currentProduct ?: return
        val sizes = product.sizes.map { it.name }

        for (size in sizes) {
            val button = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
            button.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            button.text = size
            button.setPadding(8, 8, 8, 8)

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