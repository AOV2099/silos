package com.aov2099.silos.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aov2099.silos.R
import com.aov2099.silos.databinding.ActivityAuthActivityBinding
import com.aov2099.silos.model.ProviderType
import com.aov2099.silos.utils.Network
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.io.InputStream


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthActivityBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.sleep(1500)
        setTheme(R.style.Theme_Silos)
        //setTitle("Auth")
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)

        //View Binding
        binding = ActivityAuthActivityBinding.inflate(layoutInflater)

        setUp()
        getSession()

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.authLayout.visibility = View.VISIBLE
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUp(){

        if (Network.conExists(this)){

            FirebaseApp.initializeApp(this )

            binding.btnRegister.setOnClickListener {

               /* if( binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword( binding.etEmail.text.toString(), binding.etPassword.text.toString() ).addOnCompleteListener{

                        if (it.isSuccessful){
                            showHome( it.result?.user?.email?:"", ProviderType.BASIC)
                        }else{
                            showAlert("Ha ocurrido un error al dar de alta la cuenta")
                        }
                    }
                }*/

            }

        }else{

            Toast.makeText(this, "Check Internet connection", Toast.LENGTH_SHORT).show()
        }



       /* binding.btnLogIn.setOnClickListener {

            if(Network.conExists (this)){

                if( binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){

                    FirebaseAuth.getInstance().signInWithEmailAndPassword( binding.etEmail.text.toString(), binding.etPassword.text.toString() ).addOnCompleteListener{

                        if (it.isSuccessful){
                            showHome( it.result?.user?.email?:"", ProviderType.BASIC)
                        }else{
                            showAlert("Ha ocurrido un error al dar de alta la cuenta")
                        }
                    }
                }

            }else{
                Toast.makeText(this, "Check Internet connection", Toast.LENGTH_SHORT).show()
            }

        }*/

        binding.btnGoogle.setOnClickListener {

            if(Network.conExists(this)){
                // auth con google
                val googleConf = GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                    .requestIdToken( getString(R.string.default_web_client_id)  )//it already exists. Therefore it is a bug
                    .requestEmail()
                    .build()

                val googleClient = GoogleSignIn.getClient( this, googleConf )
                googleClient.signOut()

                startActivityForResult( googleClient.signInIntent, getString(R.string.google_sign_in).toInt() )

            }else{
                Toast.makeText(this, "Check Internet connection", Toast.LENGTH_SHORT).show()

            }

        }

    }


    private fun showHome(email: String, provider: ProviderType) {

        val homeIntent = Intent( this, MainActivity::class.java ).apply {
            putExtra( "email", email )
            putExtra( "provider", provider.name )
        }

        startActivity( homeIntent )
    }

    private fun showAlert(text: String){

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Atention")
        dialog.setMessage(text)
        dialog.setPositiveButton("Ok"){ view, _ -> view.dismiss()}
        dialog.create()
        dialog.show()
    }


    private fun getSession(){

        val prefs = getSharedPreferences( getString(R.string.prefs_data), Context.MODE_PRIVATE )
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if ( provider != null && email != null ){
            binding.authLayout.visibility = View.INVISIBLE
            showHome( email, ProviderType.valueOf(provider) )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ( requestCode == getString(R.string.google_sign_in).toInt()){

            val task = GoogleSignIn.getSignedInAccountFromIntent( data )

            try {

                val account = task.getResult( ApiException::class.java )

                if( account != null ){

                    val credential = GoogleAuthProvider.getCredential( account.idToken, null )

                    FirebaseAuth.getInstance().signInWithCredential( credential ).addOnCompleteListener{

                        if (it.isSuccessful){
                            showHome( account.email?:"", ProviderType.GOOGLE)
                        }else{
                            Log.e("ERROR", it.result.toString())
                            showAlert("An error Has occurred trying to access Google")

                        }
                    }

                }

            }catch (e: ApiException){
                Log.e("ERROR", e.toString())
                showAlert("Google services error")
                //showHome( "", ProviderType.GOOGLE)
            }

        }
    }


}