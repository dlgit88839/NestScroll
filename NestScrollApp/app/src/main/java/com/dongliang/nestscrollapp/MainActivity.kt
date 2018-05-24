package com.dongliang.nestscrollapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var recycler: RecyclerView
    var list: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init() {
        recycler = findViewById(R.id.recycler)
        for (i in 0..49) {
            list.add("$i")
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = MyAdapter()

    }

    inner class MyAdapter() : RecyclerView.Adapter<MyAdapter.MyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder {
            return MyHolder(LayoutInflater.from(this@MainActivity).inflate(R.layout.item_card, parent, false))
        }

        override fun getItemCount(): Int {
            return 50
        }

        override fun onBindViewHolder(holder: MyHolder?, position: Int) {

            holder!!.text.text = list.get(position)
        }


        inner class MyHolder(item: View) : RecyclerView.ViewHolder(item) {
            var text: TextView

            init {
                text = item.findViewById(R.id.tv_card)
            }
        }
    }

}
