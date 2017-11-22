package netset.com.kotlinbaseproject.MyInterFace

/**
 * Created by netset on 21/11/17.
 */
public interface PermCallBack {
    fun permGranted(reqcode: Int)
    fun permDenied(reqcode: Int)
}