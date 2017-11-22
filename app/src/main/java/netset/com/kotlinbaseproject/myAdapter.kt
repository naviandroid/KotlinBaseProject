package netset.com.kotlinbaseproject

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import netset.com.kotlinbaseproject.MyInterFace.RecyclerClick
import netset.com.kotlinbaseproject.myAdapter.MyViewHolder

/**
 * Created by netset on 21/11/17.
 */
class myAdapter(val hello: Int, val listener: RecyclerClick) : RecyclerView.Adapter<MyViewHolder>() {

    override fun getItemCount(): Int {
        return hello
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(position, listener)
        //holder.
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(position: Int, listener: RecyclerClick) {
            val textView = itemView.findViewById<TextView>(R.id.text) as TextView;
            textView.text = "Item" + position;
            textView.setOnClickListener {
                listener.onclickItem(position)
            }
        }
    }
}