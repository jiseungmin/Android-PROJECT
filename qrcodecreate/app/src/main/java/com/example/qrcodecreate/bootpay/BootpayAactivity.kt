package com.example.qrcodecreate.bootpay

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodecreate.R
import kr.co.bootpay.android.events.BootpayEventListener
import kr.co.bootpay.android.models.BootExtra
import kr.co.bootpay.android.models.BootItem
import kr.co.bootpay.android.models.BootUser
import kr.co.bootpay.android.models.Payload
import kr.co.bootpay.android.Bootpay
import kr.co.bootpay.android.constants.BootpayBuildConfig
import kr.co.bootpay.android.webview.BootpayWebView

class BootpayAactivity : AppCompatActivity () {

    var application_id = "63227b31cf9f6d001b6ce5be"
    private val Supportview : View by lazy {
        findViewById(R.id.supportview)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supportview)
        if (BootpayBuildConfig.DEBUG) {
            application_id = "63227b31cf9f6d001b6ce5be"
        }
        goRequest(Supportview)
    }

    fun goRequest(v: View?) {
        val user = BootUser().setPhone("010-1234-5678") // 구매자 정보
        val extra = BootExtra()
            .setCardQuota("0,2,3")

        var price = 5000.0

//        val pg: String = "토스"
//        val method: String = "계좌이체"

        val items : MutableList<BootItem> = ArrayList()
        val item = BootItem().setName("아동급식 후원").setId("Support Monney").setQty(1).setPrice(5000.0)
        items.add(item)

        val payload = Payload()
        payload.setApplicationId(application_id)
            .setOrderName("아동급식후원")
//            .setPg(pg)
            .setOrderId("1234")
//            .setMethod(method)
            .setPrice(price)
            .setUser(user)
            .setExtra(extra).items = items

        val map: MutableMap<String, Any> = HashMap()
        map["1"] = "abcdef"
        map["2"] = "abcdef55"
        map["3"] = 1234
        payload.metadata = map

        Bootpay.init(supportFragmentManager, applicationContext)
            .setPayload(payload)
            .setEventListener(object : BootpayEventListener {
                override fun onCancel(data: String) {
                    Log.d("bootpay", "cancel: $data")
                }

                override fun onError(data: String) {
                    Log.d("bootpay", "error: $data")
                }

                override fun onClose() {
                    Bootpay.removePaymentWindow()
                }

                override fun onIssued(data: String) {
                    Log.d("bootpay", "issued: $data")
                }

                override fun onConfirm(data: String): Boolean {
                    Log.d("bootpay", "confirm: $data")
                    return true
                }

                override fun onDone(data: String) {
                    Log.d("done", data)
                }

            }).requestPayment()
    }
}
