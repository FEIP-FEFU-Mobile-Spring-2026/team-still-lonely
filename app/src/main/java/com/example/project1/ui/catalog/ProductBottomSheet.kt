package com.example.project1.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.example.project1.R
import com.example.project1.data.CartManager
import com.example.project1.data.Product
import com.example.project1.utils.PriceFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProductBottomSheet : BottomSheetDialogFragment() {

    private lateinit var product: Product
    private var selectedSize: String? = null

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): ProductBottomSheet {
            return ProductBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PRODUCT, product)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getSerializable(ARG_PRODUCT) as? Product
            ?: throw IllegalStateException("Product is required")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view.findViewById<ImageView>(R.id.productImage)
        val nameTextView = view.findViewById<TextView>(R.id.productName)
        val descriptionTextView = view.findViewById<TextView>(R.id.productDescription)
        val priceTextView = view.findViewById<TextView>(R.id.productPrice)
        val tagsChipGroup = view.findViewById<ChipGroup>(R.id.tagsChipGroup)
        val sizesContainer = view.findViewById<LinearLayout>(R.id.sizesContainer)
        val selectedSizeText = view.findViewById<TextView>(R.id.selectedSizeText)
        val addToCartButton = view.findViewById<Button>(R.id.addToCartButton)
        val infoButton = view.findViewById<ImageView>(R.id.infoButton)
        val closeButton = view.findViewById<ImageView>(R.id.closeButton)

        // Заполняем данными
        nameTextView.text = product.name
        descriptionTextView.text = product.longDescription
        priceTextView.text = PriceFormatter.formatRublesFromKopecks(product.priceInKopecks)

        // Загружаем изображение
        Glide.with(this)
            .load(product.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(imageView)

        // Теги (чипы поверх картинки)
        setupTags(tagsChipGroup)

        // Размеры
        setupSizes(sizesContainer, selectedSizeText, addToCartButton)

        // Кнопка "В корзину"
        addToCartButton.setOnClickListener {
            val size = selectedSize ?: "one_size"
            CartManager.addToCart(product, size)
            Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // Кнопка информации
        infoButton.setOnClickListener {
            showInfoDialog()
        }

        // Кнопка закрытия
        closeButton.setOnClickListener {
            dismiss()
        }

        // По умолчанию кнопка неактивна, пока не выбран размер
        addToCartButton.isEnabled = false
    }

    private fun setupTags(tagsChipGroup: ChipGroup) {
        val tags = product.tags
        if (tags.isEmpty()) {
            tagsChipGroup.visibility = View.GONE
            return
        }

        tagsChipGroup.removeAllViews()
        tags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag
                isClickable = false
                isFocusable = false
                setChipBackgroundColorResource(android.R.color.transparent)
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                chipStrokeColor = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
                chipStrokeWidth = 1f
                setEnsureMinTouchTargetSize(false)
            }
            tagsChipGroup.addView(chip)
        }
    }

    private fun setupSizes(
        sizesContainer: LinearLayout,
        selectedSizeText: TextView,
        addToCartButton: Button
    ) {
        val sizes = product.sizes.map { it.name }

        if (sizes.isEmpty()) {
            selectedSizeText.text = "Размер: стандартный"
            selectedSizeText.visibility = View.VISIBLE
            addToCartButton.isEnabled = true
            selectedSize = "one_size"
            return
        }

        sizesContainer.removeAllViews()

        sizes.forEach { sizeName ->
            val button = MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
            button.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            button.text = sizeName
            button.setPadding(8, 8, 8, 8)
            button.isAllCaps = false

            if (CartManager.isInCart(product.id, sizeName)) {
                button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                button.strokeColor = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_green_dark)
            }

            button.setOnClickListener {
                sizesContainer.children.forEach { child ->
                    if (child is MaterialButton) {
                        child.isChecked = false
                    }
                }
                button.isChecked = true
                selectedSize = sizeName
                selectedSizeText.text = "Размер: $sizeName"
                selectedSizeText.visibility = View.VISIBLE
                addToCartButton.isEnabled = true
            }

            sizesContainer.addView(button)
        }
    }

    private fun showInfoDialog() {
        val infoText = buildString {
            append("Характеристики товара:\n\n")
            append("Материал: ${product.material.ifEmpty { "не указан" }}\n")
            append("Вес: ${product.weight.ifEmpty { "не указан" }}\n")
            append("Сезон: ${product.season.ifEmpty { "не указан" }}\n")
            append("Страна производства: ${product.countryOfOrigin.ifEmpty { "не указана" }}")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Дополнительная информация")
            .setMessage(infoText)
            .setPositiveButton("Закрыть", null)
            .show()
    }
}