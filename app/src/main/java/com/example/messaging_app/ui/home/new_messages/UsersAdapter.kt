import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messaging_app.R
import com.example.messaging_app.ui.home.new_messages.UsersData


class UsersAdapter (private var users: List<UsersData>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>()
{

    fun updateUsersList(newUsers: List<UsersData>) {
        users = newUsers
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView = itemView.findViewById<TextView>(R.id.user_name_text)
        val emailTextView = itemView.findViewById<TextView>(R.id.user_email_text)
        val imageUrlView = itemView.findViewById<ImageView>(R.id.user_image_view)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.recycler_view_item, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: UsersAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val user: UsersData = users.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.nameTextView
        textView.setText(user.name)
        val emailTextView = viewHolder.emailTextView
        emailTextView.setText(user.email)
        val imageUrl:ImageView = viewHolder.imageUrlView
        Glide.with(imageUrl.context).load(user.imageUrl).into(imageUrl)
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return users.size
    }
}
