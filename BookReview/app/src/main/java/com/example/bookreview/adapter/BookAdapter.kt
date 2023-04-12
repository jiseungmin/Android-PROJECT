package com.example.bookreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookreview.databinding.ItemBookBinding
import com.example.bookreview.model.Book

class BookAdapter(private val itemClickedListener : (Book)->Unit) : ListAdapter<Book,BookAdapter.BookItemViewHolder>(diffUtil) {

    inner class BookItemViewHolder(private val binding: ItemBookBinding) :  RecyclerView.ViewHolder(binding.root){

        fun bind(bookModel: Book){
            binding.titleTextview.text = bookModel.title
            binding.descriptionTextView.text = bookModel.description
            binding.root.setOnClickListener{
                itemClickedListener(bookModel)
            }
            Glide
                .with(binding.converImageView.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.converImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        // 미리 만들어진 뷰 홀더가 없을 경우 생성하는 함수
        return BookItemViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        //뷰 홀더에 데이터를 바인드 하게 해주는 함수
        holder.bind(currentList[position])
    }
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Book>(){
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                // old item 과 new item의 content이 같냐
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                // old item 과 new item의 content이 같냐
                return oldItem.id == newItem.id
            }

        }
    }

}