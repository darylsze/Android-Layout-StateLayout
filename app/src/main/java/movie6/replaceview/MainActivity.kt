package movie6.replaceview

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import movie6.replaceview.Views.StateLayout
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lblResult
        btnSubmit.clicks().subscribe({
            callApi()
        }, { it.printStackTrace() })

    }

    private fun callApi() {
        llState.updateState(StateLayout.State.LOADING, null)

        Handler().postDelayed({
            try {
                runOnUiThread {
                    lblResult.text = "success! result is back"
                    llState.updateState(StateLayout.State.CONTENT, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                llState.updateState(StateLayout.State.ERROR) {
                    callApi()
                }
            }
        }, 3000)

    }
}
