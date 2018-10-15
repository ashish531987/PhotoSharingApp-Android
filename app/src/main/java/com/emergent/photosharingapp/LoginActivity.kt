package com.emergent.photosharingapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.emergent.photosharingapp.api.MediaSharingApi
import com.emergent.photosharingapp.api.dto.requestDTO.SignInRequestDTO
import com.emergent.photosharingapp.domain.ExceptionResponseDTO
import com.emergent.photosharingapp.domain.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private val TAG = "LoginActivity"
        private val RC_SIGN_IN: Int = 123
    }
    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Google Sign in
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if(account != null) {
//            if(account.idToken == null) {
//                mGoogleSignInClient?.signOut()?.addOnCompleteListener {
//                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
//                }
//            } else{
//                sendIdToClient(account.idToken!!)
//            }
//        }
//
        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.getId()) {
            R.id.sign_in_button -> signIn()
        }
    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken
            sendIdToClient(idToken!!)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendIdToClient(idToken : String){
        val request = MediaSharingApi.create(idToken).login(SignInRequestDTO(idToken))
        request.enqueue(object : Callback<User>{
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MediaMasterActivity::class.java)
                    intent.putExtra("key", response.body() as User)
                    intent.putExtra("idToken", idToken)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                } else{
                    val exceptionResponseDTO = response.errorBody() as ExceptionResponseDTO
                    Toast.makeText(this@LoginActivity, exceptionResponseDTO.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
