package naveen.com.kotlinbaseproject


import android.os.Bundle
import android.support.v4.app.Fragment
import naveen.com.kotlinbaseproject.fragment.HomeFragment


/**
 * A simple [Fragment] subclass.
 */
class FragmentActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        supportFragmentManager.beginTransaction().replace(
                R.id.frameLayout,
                HomeFragment()
        ).commit()
    }


}// Required empty public constructor
