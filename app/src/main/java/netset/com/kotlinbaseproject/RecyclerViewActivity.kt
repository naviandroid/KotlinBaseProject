package netset.com.kotlinbaseproject

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.Toast
import netset.com.kotlinbaseproject.MyInterFace.RecyclerClick

class RecyclerViewActivity : AppCompatActivity(), RecyclerClick {
    override fun onclickItem(i: Int) {
        Toast.makeText(applicationContext, "" + i, Toast.LENGTH_SHORT).show();
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycal_view_acitvity)
        val recyclerview = findViewById<RecyclerView>(R.id.recycalview) as RecyclerView
        recyclerview.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val myAdapter = myAdapter(20, this)
        recyclerview.adapter = myAdapter
    }
}
