package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {


    private var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId:String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var resendingToken: ForceResendingToken

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please waite")
        progressDialog.setCanceledOnTouchOutside(false)

        val btncontinue = findViewById<Button>(R.id.btncontinue)

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                startActivity(Intent(applicationContext, HomeScreenActivity::class.java))
                finish()

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("TAG", "onCodeSent:$verificationId")
                mVerificationId = verificationId
                progressDialog.dismiss()
                resendingToken = token

                Toast.makeText(this@MainActivity, "Verification code sent...", Toast.LENGTH_SHORT).show()

                var intent = Intent(applicationContext, VerifyActivity::class.java)
                intent.putExtra("mVerificationId", mVerificationId)

                startActivity(intent)

            }

        }

        btncontinue.setOnClickListener{
            val phoneEt = findViewById<EditText>(R.id.phoneEt)

            val phone = phoneEt.text.toString().trim()
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }else{
                startPhoneNumberVerification(phone)
            }
        }

    }

    private fun startPhoneNumberVerification(phone:String){
        progressDialog.setMessage("Verifying Phone number")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)

            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

}