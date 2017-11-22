package netset.com.kotlinbaseproject

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findids()
    }

    private fun MainActivity.findids() {
        val myText = findViewById<TextView>(R.id.hello) as TextView
        val button = findViewById<Button>(R.id.button) as TextView
        val imagePick = findViewById<Button>(R.id.imagePick) as TextView
        val recycalview = findViewById<Button>(R.id.recycalview)
        myText.setOnClickListener(this)
        button.setOnClickListener(this)
        recycalview.setOnClickListener(this)
        imagePick.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.hello -> customAlertDialog()
            R.id.button -> showAlertDialog()
            R.id.recycalview -> goToRecycalView()
            R.id.imagePick -> goImagePicker()
        //  else ->
        }
    }

    private fun goImagePicker() {
        val intent = Intent(applicationContext, ImagePickerActivity::class.java)
        startActivity(intent)
    }

    private fun goToRecycalView() {
        val intent = Intent(applicationContext, RecyclerViewActivity::class.java)
        startActivity(intent)
    }


    private fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();
    }

    private fun showAlertDialog() {
        val simpleAlert = AlertDialog.Builder(this@MainActivity).create()
        simpleAlert.setTitle("Alert")
        simpleAlert.setMessage("Show Simple Alert With Two Button")
        simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", { dialogInterface, i ->
            showToast("clicked on OK")
        })
        simpleAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", { dialogInterface, i ->
            showToast("clicked on Cancel")
        })
        simpleAlert.show()
    }

    private fun customAlertDialog() {
        val view = layoutInflater.inflate(R.layout.dolaogview, null) as View
        val simpleAlert = AlertDialog.Builder(this).create()
        simpleAlert.setTitle("custom Dialog")
        simpleAlert.setView(view)
        simpleAlert.setCancelable(false)
        val switch1 = view.findViewById<Switch>(R.id.switch1) as Switch
        val button = view.findViewById<Button>(R.id.cancelBT) as Button
        button.setOnClickListener {
            simpleAlert.dismiss()
        }
        switch1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            showToast("ischecked=" + isChecked)
        })
        simpleAlert.show()
    }
}

