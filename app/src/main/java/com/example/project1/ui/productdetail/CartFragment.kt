package com.example.project1.ui.productdetail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project1.R
import com.example.project1.data.CartManager
import com.example.project1.data.CartManager.CartItem
import com.example.project1.data.Product
import com.example.project1.utils.PriceFormatter
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var selectAllPanel: LinearLayout
    private lateinit var selectAllCheckBox: CheckBox
    private lateinit var selectedCountText: TextView
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
        selectAllPanel = view.findViewById(R.id.selectAllPanel)
        selectAllCheckBox = view.findViewById(R.id.selectAllCheckBox)
        selectedCountText = view.findViewById(R.id.selectedCountText)
        bottomPanel = view.findViewById(R.id.bottomPanel)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        selectAllCheckBox.setOnClickListener {
            val isChecked = selectAllCheckBox.isChecked
            CartManager.selectAll(isChecked)
            updateCartUI()
        }

        checkoutButton.setOnClickListener {
            val selectedItems = CartManager.getSelectedItems()
            val total = CartManager.getTotalPrice()

            if (selectedItems.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Оформление заказа на сумму ${PriceFormatter.formatRubles(total)}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        updateCartUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateCartUI()
    }

    private fun updateCartUI() {
        val cartItems = CartManager.getCartItems()

        if (cartItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
            selectAllPanel.visibility = View.GONE
            bottomPanel.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
            selectAllPanel.visibility = View.VISIBLE
            bottomPanel.visibility = View.VISIBLE

            adapter = CartAdapter(
                cartItems = cartItems,
                onItemSelected = { item ->
                    updateCartUI()
                },
                onQuantityChange = { item, newQuantity ->
                    CartManager.updateQuantity(item.product.id, item.size, newQuantity)
                    updateCartUI()
                },
                onRemove = { item ->
                    CartManager.removeFromCart(item.product.id, item.size)
                    updateCartUI()
                },
                onItemClick = { product ->
                    val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
                        putExtra("product_id", product.id)
                        putExtra("product_name", product.name)
                        putExtra("product_description", product.longDescription)
                        putExtra("product_price", product.price)
                        putExtra("product_image", product.imageUrl)
                        putExtra("product_category", product.categoryId)
                        putExtra("product_sizes", product.sizes.map { it.name }.toTypedArray())
                        putExtra("product_material", product.material)
                        putExtra("product_country", product.countryOfOrigin)
                    }
                    startActivity(intent)
                }
            )
            recyclerView.adapter = adapter

            val allSelected = CartManager.isAllSelected()
            selectAllCheckBox.isChecked = allSelected

            val selectedCount = CartManager.getSelectedCount()
            selectedCountText.text = "$selectedCount/${cartItems.size}"

            val total = CartManager.getTotalPrice()
            totalPriceTextView.text = "Итого: ${PriceFormatter.formatRubles(total)}"
        }
    }
}

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onItemSelected: (CartItem) -> Unit,
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemove: (CartItem) -> Unit,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val selectCheckBox: CheckBox = itemView.findViewById(R.id.selectCheckBox)
        private val name: TextView = itemView.findViewById(R.id.productName)
        private val description: TextView = itemView.findViewById(R.id.productDescription)
        private val price: TextView = itemView.findViewById(R.id.productPrice)
        private val image: ImageView = itemView.findViewById(R.id.productImage)
        private val sizeText: TextView = itemView.findViewById(R.id.sizeText)
        private val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        private val minusButton: TextView = itemView.findViewById(R.id.minusButton)
        private val plusButton: TextView = itemView.findViewById(R.id.plusButton)
        private val removeButton: ImageView = itemView.findViewById(R.id.removeButton)

        fun bind(
            item: CartItem,
            onItemSelected: (CartItem) -> Unit,
            onQuantityChange: (CartItem, Int) -> Unit,
            onRemove: (CartItem) -> Unit,
            onItemClick: (Product) -> Unit
        ) {
            val product = item.product

            name.text = product.name
            description.text = product.shortDescription
            price.text = PriceFormatter.formatRublesFromKopecks(product.priceInKopecks)
            sizeText.text = "Размер: ${item.size}"
            quantityText.text = item.quantity.toString()

            selectCheckBox.isChecked = item.isSelected

            selectCheckBox.setOnClickListener {
                item.isSelected = selectCheckBox.isChecked
                onItemSelected(item)
            }

            Glide.with(itemView.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("GlideError", "Ошибка загрузки: ${product.imageUrl}", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("GlideSuccess", "Успешно загружено: ${product.imageUrl}")
                        return false
                    }
                })
                .into(image)

            minusButton.setOnClickListener {
                val newQuantity = item.quantity - 1
                if (newQuantity > 0) {
                    item.quantity = newQuantity
                    quantityText.text = newQuantity.toString()
                    onQuantityChange(item, newQuantity)
                } else {
                    onRemove(item)
                }
            }

            plusButton.setOnClickListener {
                val newQuantity = item.quantity + 1
                item.quantity = newQuantity
                quantityText.text = newQuantity.toString()
                onQuantityChange(item, newQuantity)
            }

            removeButton.setOnClickListener {
                onRemove(item)
            }

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(
            cartItems[position],
            onItemSelected,
            onQuantityChange,
            onRemove,
            onItemClick
        )
    }

    override fun getItemCount() = cartItems.size
}