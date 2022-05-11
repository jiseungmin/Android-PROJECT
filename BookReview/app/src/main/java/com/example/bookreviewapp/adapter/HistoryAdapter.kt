import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookreviewapp.databinding.ItemHistoryBinding
import com.example.bookreviewapp.model.History

class HistoryAdapter(val historyDeleteClickListener: (String) -> (Unit)) : ListAdapter<History, HistoryAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(historyModel: History) {
            binding.historykeywordTextview.text = historyModel.keyword
            binding.historykeywordDeleteButton.setOnClickListener {
                historyDeleteClickListener(historyModel.keyword.orEmpty())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>() {
            override fun areContentsTheSame(oldItem: History, newItem: History) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: History, newItem: History) =
                oldItem.uid == newItem.uid
        }
    }

}