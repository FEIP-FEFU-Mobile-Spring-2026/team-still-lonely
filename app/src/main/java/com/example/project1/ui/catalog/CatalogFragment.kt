package com.example.project1.ui.catalog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.R
import com.example.project1.data.Product
import com.example.project1.data.Resource  // ВАЖНО!
import com.example.project1.ui.productdetail.ProductDetailActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CatalogFragment : Fragment() {

    private lateinit var viewModel: CatalogViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var chipGroup: ChipGroup
    private lateinit var resultsCountText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_catalog, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        chipGroup = view.findViewById(R.id.categoryChipGroup)
        resultsCountText = view.findViewById(R.id.resultsCountText)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(this)[CatalogViewModel::class.java]

        observeViewModel()

        return view
    }

    private fun observeViewModel() {
        // Наблюдаем за продуктами
        viewModel.products.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    updateAdapter(resource.data)
                    updateResultsCount(resource.data.size)
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ошибка: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Наблюдаем за категориями
        viewModel.categories.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    updateCategoryChips(resource.data)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun updateAdapter(products: List<Product>) {
        adapter = ProductAdapter(
            products = products,
            onItemClick = { product ->
                val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
                    putExtra("product_id", product.id)
                    putExtra("product_name", product.name)
                    putExtra("product_description", product.description)
                    putExtra("product_price", product.price)
                    putExtra("product_image", product.imageUrl)
                    putExtra("product_category", product.category)  // Добавьте эту строку
                }
                startActivity(intent)
            },
            onQuantityChange = { product, quantity ->
                // Обработка изменения количества
            }
        )
        recyclerView.adapter = adapter
    }

    private fun updateCategoryChips(categories: List<String>) {
        chipGroup.removeAllViews()

        categories.forEachIndexed { index, category ->
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = category
            chip.id = View.generateViewId()

            if (index == 0) {
                chip.isChecked = true
            }

            chip.setOnClickListener {
                viewModel.filterByCategory(category)
            }

            chip.setOnClickListener {
                viewModel.filterByCategory(category)  // category уже на русском
            }

            chipGroup.addView(chip)
        }
    }

    private fun updateResultsCount(count: Int) {
        val word = when {
            count % 10 == 1 && count % 100 != 11 -> "товар"
            count % 10 in 2..4 && (count % 100 !in 12..14) -> "товара"
            else -> "товаров"
        }
        resultsCountText.text = "$count $word"
    }

}