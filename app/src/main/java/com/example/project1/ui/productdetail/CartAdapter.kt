package com.example.project1.ui.productdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project1.R
import com.example.project1.data.CartItem
import com.example.project1.utils.PriceFormatter

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemove: (CartItem) -> Unit,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    inner class CartViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
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
            onQuantityChange: (CartItem, Int) -> Unit,
            onRemove: (CartItem) -> Unit,
        ) {
            val product = item.product

            name.text = product.name
            description.text = product.shortDescription
            sizeText.text = "Размер: ${item.sizeName}"
            quantityText.text = item.quantity.toString()
            price.text = PriceFormatter.formatRubles(item.totalPrice)

            Glide
                .with(itemView.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(image)

            minusButton.setOnClickListener {
                val newQuantity = item.quantity - 1
                if (newQuantity > 0) {
                    onQuantityChange(item, newQuantity)
                } else {
                    onRemove(item)
                }
            }

            plusButton.setOnClickListener {
                onQuantityChange(item, item.quantity + 1)
            }

            removeButton.setOnClickListener {
                onRemove(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(cartItems[position], onQuantityChange, onRemove)
    }

    override fun getItemCount() = cartItems.size
}
