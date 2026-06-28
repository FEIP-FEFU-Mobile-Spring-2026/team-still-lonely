package com.example.project1.ui.catalog

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.project1.R
import com.example.project1.data.Product
import com.example.project1.utils.PriceFormatter

class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit,
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    inner class ViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.productName)
        private val description: TextView = itemView.findViewById(R.id.productDescription)
        private val price: TextView = itemView.findViewById(R.id.productPrice)
        private val image: ImageView = itemView.findViewById(R.id.productImage)
        private val counterLayout: LinearLayout = itemView.findViewById(R.id.counterLayout)
        private val tagNew: TextView = itemView.findViewById(R.id.tagNew)

        fun bind(product: Product) {
            name.text = product.name
            description.text = product.shortDescription
            price.text = PriceFormatter.formatRublesFromKopecks(product.priceInKopecks)
            tagNew.visibility = if (product.isNew) View.VISIBLE else View.GONE

            Glide
                .with(itemView.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .listener(
                    object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean,
                        ): Boolean {
                            Log.e("GlideError", "Ошибка загрузки изображения: ${product.imageUrl}", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>,
                            dataSource: DataSource,
                            isFirstResource: Boolean,
                        ): Boolean = false
                    },
                ).into(image)

            counterLayout.visibility = View.GONE
            itemView.setOnClickListener { onItemClick(product) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}
