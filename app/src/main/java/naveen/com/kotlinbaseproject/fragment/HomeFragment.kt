package naveen.com.kotlinbaseproject.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_home.*
import naveen.com.kotlinbaseproject.R

class HomeFragment : Fragment() {
    var email: EditText? = null
    var password: EditText? = null
    var loginBT: Button? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = getView()!!.findViewById<EditText>(R.id.emailET) as EditText
        password = getView()!!.findViewById<EditText>(R.id.passwordET) as EditText
        loginBT = getView()!!.findViewById<Button>(R.id.loginBT) as Button
        loginBT!!.setOnClickListener {

            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()
            when {
                email.isEmpty() -> Toast.makeText(activity, "Please enter the email", Toast.LENGTH_SHORT).show()
                password.isBlank() -> Toast.makeText(activity, "Please enter the password", Toast.LENGTH_SHORT).show()
                else -> {
                    //hit api

                }
            }
        }
    }
}