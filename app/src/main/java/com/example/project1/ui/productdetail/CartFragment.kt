package com.example.project1.ui.productdetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.R
import com.example.project1.data.CartItem
import com.example.project1.data.CartManager
import com.example.project1.utils.PriceFormatter
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var clearCartButton: Button
    private lateinit var bottomPanel: LinearLayout
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.cartRecyclerView)
        emptyTextView = view.findViewById(R.id.emptyCartText)
        totalPriceTextView = view.findViewById(R.id.totalPriceText)
        checkoutButton = view.findViewById(R.id.checkoutButton)
        clearCartButton = view.findViewById(R.id.clearCartButton)
        bottomPanel = view.findViewById(R.id.bottomPanel)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        clearCartButton.setOnClickListener {
            showClearCartConfirmation()
        }

        checkoutButton.setOnClickListener {
            showCheckoutDialog()
        }

        loadCartItems()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadCartItems()
    }

    private fun loadCartItems() {
        lifecycleScope.launch {
            CartManager.getCartItems().collect { items ->
                updateUI(items)
            }
        }
    }

    private fun updateUI(items: List<CartItem>) {
        if (items.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
            bottomPanel.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
            bottomPanel.visibility = View.VISIBLE

            adapter = CartAdapter(
                cartItems = items,
                onQuantityChange = { item, newQuantity ->
                    lifecycleScope.launch {
                        CartManager.updateQuantity(item.product.id, item.sizeName, newQuantity)
                        loadCartItems()
                    }
                },
                onRemove = { item ->
                    lifecycleScope.launch {
                        CartManager.removeFromCart(item.product.id, item.sizeName)
                        loadCartItems()
                    }
                }
            )
            recyclerView.adapter = adapter

            val total = items.sumOf { it.totalPrice }
            totalPriceTextView.text = "Итого: ${PriceFormatter.formatRubles(total)}"
        }
    }

    private fun showClearCartConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Очистка корзины")
            .setMessage("Вы уверены, что хотите удалить все товары из корзины?")
            .setPositiveButton("Очистить") { _, _ ->
                lifecycleScope.launch {
                    CartManager.clearCart()
                    loadCartItems()
                    Toast.makeText(requireContext(), "Корзина очищена", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showCheckoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_checkout, null)
        val nameInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.nameInput)
        val emailInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.emailInput)
        val commentInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.commentInput)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Оформление заказа")
            .setView(dialogView)
            .setNegativeButton("Отмена", null)
            .create()

        fun updateSubmitButtonState() {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val isValid = name.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            submitButton.isEnabled = isValid
        }

        submitButton.isEnabled = false
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) = updateSubmitButtonState()
        }
        nameInput.addTextChangedListener(watcher)
        emailInput.addTextChangedListener(watcher)

        submitButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val comment = commentInput.text.toString().trim()

            if (name.isEmpty()) {
                nameInput.error = "Введите имя"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Введите корректный email"
                return@setOnClickListener
            }

            dialog.dismiss()
            showSuccessDialog()
        }

        dialog.show()
    }

    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_order_success, null)
        val backToMainButton = dialogView.findViewById<Button>(R.id.backToMainButton)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        backToMainButton.setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                CartManager.clearCart()
                loadCartItems()
            }
            // Возврат на главную (если нужно раскомментировать)
            // (activity as? MainActivity)?.showCatalogFragment()
        }

        dialog.show()
    }
}