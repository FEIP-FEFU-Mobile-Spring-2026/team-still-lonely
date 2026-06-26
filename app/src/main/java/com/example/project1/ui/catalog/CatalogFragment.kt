package com.example.project1.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.R
import com.example.project1.data.Product
import com.example.project1.data.Resource
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar

class CatalogFragment : Fragment() {

    private lateinit var viewModel: CatalogViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var chipGroup: ChipGroup
    private lateinit var resultsCountText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: View
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private var networkSnackbar: Snackbar? = null

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
        errorLayout = view.findViewById(R.id.errorLayout)
        errorText = view.findViewById(R.id.errorText)
        retryButton = view.findViewById(R.id.retryButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(
            this,
            CatalogViewModelFactory(requireActivity().application)
        )[CatalogViewModel::class.java]

        savedInstanceState?.let {
            val savedFilter = it.getString("current_filter", "Новинки")
            viewModel.setFilter(savedFilter ?: "Новинки")
        }

        observeViewModel()
        observeNetworkState()

        retryButton.setOnClickListener {
            viewModel.refresh()
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("current_filter", viewModel.getCurrentFilter())
    }

    private fun observeViewModel() {
        showLoading(true)

        viewModel.products.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    hideError()
                    updateAdapter(resource.data)
                    updateResultsCount(resource.data.size)
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(resource.message)
                }
                is Resource.Loading -> {}
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    updateCategoryChips(resource.data)
                }
                is Resource.Error -> {
                    // Не показываем ошибку категорий отдельно
                }
                else -> {}
            }
        }
    }

    private fun observeNetworkState() {
        viewModel.isNetworkAvailable.observe(viewLifecycleOwner) { connected ->
            if (!connected) {
                showNetworkSnackbar("Нет подключения к интернету")
            } else {
                hideNetworkSnackbar()
            }
        }
    }

    private fun showNetworkSnackbar(message: String) {
        networkSnackbar?.dismiss()
        networkSnackbar = Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Повторить") {
            viewModel.refresh()
        }
        networkSnackbar?.show()
    }

    private fun hideNetworkSnackbar() {
        networkSnackbar?.dismiss()
        networkSnackbar = null
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            errorLayout.visibility = View.GONE
            resultsCountText.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            resultsCountText.visibility = View.VISIBLE
        }
    }

    private fun showError(message: String) {
        errorLayout.visibility = View.VISIBLE
        errorText.text = if (message.isNotEmpty()) message else "Не удалось загрузить данные"
        recyclerView.visibility = View.GONE
        resultsCountText.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun hideError() {
        errorLayout.visibility = View.GONE
        resultsCountText.visibility = View.VISIBLE
    }

    private fun updateAdapter(products: List<Product>) {
        adapter = ProductAdapter(
            products = products,
            onItemClick = { product ->
                val bottomSheet = ProductBottomSheet.newInstance(product)
                bottomSheet.show(parentFragmentManager, "ProductBottomSheet")
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

            if (category == viewModel.getCurrentFilter()) {
                chip.isChecked = true
            } else if (index == 0 && viewModel.getCurrentFilter().isEmpty()) {
                chip.isChecked = true
            }

            chip.setOnClickListener {
                viewModel.filterByCategory(category)
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
