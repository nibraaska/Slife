package com.slife.slife.BottomNavigation.Messages.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.slife.slife.BottomNavigation.Messages.Model.UserItem
import com.slife.slife.R

class UserAutoCompleteAdapter(context: Context, userList: ArrayList<UserItem>) : ArrayAdapter<UserItem>(context, 0, userList) {
    private var userListFull = ArrayList<UserItem>(userList)

    override fun getFilter(): Filter {
        return userFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.layout_messages_user_item, parent, false)
        }

        val textView = view?.findViewById<TextView>(R.id.messagesUserItemUserName)
        val imageView = view?.findViewById<ImageView>(R.id.messagesUserItemProfilePicture)

        val userItem = getItem(position)
        if (userItem != null) {
            textView?.text = userItem.username
            Glide.with(context).load(userItem.profilePicture).into(imageView)
        }

        return view!!
    }
    private var userFilter: Filter = object : Filter(){
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()
            val suggestions = ArrayList<UserItem>()

            if (p0 == null || p0.isEmpty()){
                suggestions.addAll(userListFull)
            } else {
                val filterPatter = p0.toString().toLowerCase().trim()
                userListFull.forEach {
                    if (it.username.toLowerCase().contains(filterPatter)){
                        suggestions.add(it)
                    }
                }
            }
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            clear()
            addAll(p1?.values as ArrayList<UserItem>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as UserItem).username
        }

    }
}