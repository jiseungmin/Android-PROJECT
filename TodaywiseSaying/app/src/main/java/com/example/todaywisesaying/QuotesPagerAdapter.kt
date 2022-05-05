package com.example.todaywisesaying

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuotesPagerAdapter(
   private val quote: List<Quote>, //  // adapter에 들어갈 list 입니다.
   private val isNameRevealed: Boolean
) : RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = //// LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        QuoteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quote, parent, false)
        )

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) { // // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        val actualyposition = position % quote.size
        holder.bind(quote[actualyposition],isNameRevealed)

    }

    override fun getItemCount() = Int.MAX_VALUE //  // RecyclerView의 총 개수 입니다.

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val qouteTextView: TextView = itemView.findViewById(R.id.quoteTextview)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

        @SuppressLint("SetTextI18n")
        fun bind(qoute: Quote, isNameRevealed: Boolean) {
            qouteTextView.text = "\"${qoute.quote}\""

            if(isNameRevealed){
                nameTextView.text = "- ${qoute.name}"
                nameTextView.visibility = View.VISIBLE // 이름을 보여준다
            }else{
                nameTextView.visibility = View.GONE // 안보여줌
            }
        }

    }

}