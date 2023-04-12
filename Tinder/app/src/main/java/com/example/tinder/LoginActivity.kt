package com.example.tinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        initLoginButton()
        initSignupButton()
        initEmailAndPasswordEditText()
        initFacebookloginButton()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun initLoginButton() {
        val loginButton = findViewById<Button>(R.id.loginbutton)
        loginButton.setOnClickListener {
            val Email = getInputEmail()
            val password = getInputpassword()

            auth.signInWithEmailAndPassword(
                Email,
                password
            ) // firebase에 있는 이메일과 패스워드가 일치하는지 확인하는 함수
                .addOnCompleteListener(this) { task -> //일이 잘 완료가 됬는지  알수있는 함수
                    if (task.isSuccessful) {
                        handleSuccessLogin()
                    } else {
                        Toast.makeText(
                            this,
                            "로그인에 실패 하였습니다. 이메일 또는 비밀번호를 다시 입력해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun initSignupButton() {
        val signupButton = findViewById<Button>(R.id.signupbutton)
        signupButton.setOnClickListener {
            val Email = getInputEmail()
            val password = getInputpassword()

            auth.createUserWithEmailAndPassword(Email, password) // 이메일 패스워드 firebase에 넣은 함수
                .addOnCompleteListener(this) { task -> //일이 잘 완료가 됬는지  알수있는 함수
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "회원가입에 완료되었습니다. 로그인 버튼을 눌러 로그인 해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "이미 가입한 메일이거나, 회원가입에 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }

    private fun initEmailAndPasswordEditText() {
        val Emailedittext = findViewById<EditText>(R.id.EmailText)
        val passwordedittext = findViewById<EditText>(R.id.passwordText)
        val loginButton = findViewById<Button>(R.id.loginbutton)
        val signupButton = findViewById<Button>(R.id.signupbutton)

        Emailedittext.addTextChangedListener {
            val enable = Emailedittext.text.isNotEmpty() && passwordedittext.text.isNotEmpty()
            loginButton.isEnabled = enable
            signupButton.isEnabled = enable
        }

        passwordedittext.addTextChangedListener {
            val enable = Emailedittext.text.isNotEmpty() && passwordedittext.text.isNotEmpty()
            loginButton.isEnabled = enable
            signupButton.isEnabled = enable
        }
    }


    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.EmailText).text.toString()
    }

    private fun getInputpassword(): String {
        return findViewById<EditText>(R.id.passwordText).text.toString()
    }


    private fun initFacebookloginButton() {
        val facebookboutton = findViewById<LoginButton>(R.id.facebookloginButton)
        facebookboutton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // 로그인에 성공 하였을 떄
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                auth.signInWithCredential(credential) // 페이스북에 로그인한 액세스 토큰을 넘겨 줌
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            handleSuccessLogin()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "페이스북 로그인에 실패 하셨습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@LoginActivity, "페이스북 로그인에 실패 하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun handleSuccessLogin(){
        if(auth.currentUser == null){
            Toast.makeText(this, " 로그인에 실패 하셨습니다.", Toast.LENGTH_SHORT).show()
        }
        val userId = auth.currentUser?.uid.orEmpty()
        val CurrentUserDB = Firebase.database.reference.child("Users").child(userId)
        val user = mutableMapOf<String, Any>()
        user["UserId"] = userId
        CurrentUserDB.updateChildren(user)
        finish()
    }

}