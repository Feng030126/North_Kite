package com.rst2g1.northkite

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rst2g1.northkite.databinding.LoginPageBinding
import com.rst2g1.northkite.databinding.RegisterPageBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginPageBinding
    private lateinit var bindingRegister: RegisterPageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var isRegister = false

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var recoveryEmailEditText: EditText

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        sharedPreferences.edit().putInt("login_status", -1).apply()

        binding = LoginPageBinding.inflate(layoutInflater)
        bindingRegister = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database =
            FirebaseDatabase.getInstance("https://northkite-1120-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = database.reference.child("users")

        setupListeners()
        setupBackPressedHandler()
        initializeRegistrationFields()
    }

    private fun setupListeners() {
        binding.apply {
            buttonGuest.setOnClickListener {
                sharedPreferences.edit().putInt("login_status", 1).apply()
                finish()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            buttonRegister.setOnClickListener {
                setContentView(bindingRegister.root)
                isRegister = true
            }
        }

        bindingRegister.buttonCancel.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this) {
            if (isRegister) {
                showCancelConfirmationDialog()
            } else {
                finishAffinity()
            }
        }
    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirm to cancel register")
            setMessage("Are you sure you want to cancel registration?")
            setPositiveButton("Confirm") { _, _ ->
                isRegister = false
                finish()
                startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
            }
            setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }.create().show()
    }

    private fun initializeRegistrationFields() {
        with(bindingRegister) {
            firstNameEditText = editTextFirstName
            lastNameEditText = editTextLastName
            usernameEditText = editTextUsername
            passwordEditText = editTextPassword
            confirmPasswordEditText = editTextConfirmPassword
            recoveryEmailEditText = editTextRecoveryEmail
        }

        addTextWatchers()
        bindingRegister.buttonRegister.setOnClickListener {
            if (areAnyFieldsEmpty()) {
                highlightEmptyFields()
            } else {
                if (!isAccountEmailExistent()) {
                    registerAccount()
                }
            }
        }
    }

    private fun addTextWatchers() {
        listOf(
            firstNameEditText,
            lastNameEditText,
            usernameEditText,
            recoveryEmailEditText
        ).forEach {
            it.addTextChangedListener(createTextWatcher(it))
        }

        listOf(passwordEditText, confirmPasswordEditText).forEach {
            it.addTextChangedListener(createPasswordTextWatcher(it))
        }
    }

    private fun areAnyFieldsEmpty(): Boolean {
        return listOf(
            firstNameEditText,
            lastNameEditText,
            usernameEditText,
            passwordEditText,
            confirmPasswordEditText,
            recoveryEmailEditText
        ).any { it.text.isNullOrEmpty() }
    }

    private fun highlightEmptyFields() {
        listOf(
            firstNameEditText to "Cannot leave the field blank",
            lastNameEditText to "Cannot leave the field blank",
            usernameEditText to "Cannot leave the field blank",
            passwordEditText to "Cannot leave the field blank",
            confirmPasswordEditText to "Cannot leave the field blank",
            recoveryEmailEditText to "Cannot leave the field blank"
        ).forEach { (editText, message) ->
            if (editText.text.isNullOrEmpty()) {
                editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
                editText.error = message
            }
        }
    }

    private fun createTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateField(editText, s)
            }
        }
    }

    private fun createPasswordTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validatePasswordFields(editText, s)
            }
        }
    }

    private fun validateField(editText: EditText, s: Editable?) {
        if (s.isNullOrEmpty()) {
            editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
            editText.error = "Cannot leave the field blank"
        } else {
            editText.setBackgroundResource(R.drawable.rounded_edittext_background)
        }

        if (editText == recoveryEmailEditText && !android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString())
                .matches()
        ) {
            editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
            editText.error = "Invalid email format"
        }
    }

    private fun validatePasswordFields(editText: EditText, s: Editable?) {
        if (editText == passwordEditText) {
            if (s.isNullOrEmpty() || s.length < 8) {
                editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
                editText.error = "Password must be at least 8 characters"
            } else {
                editText.setBackgroundResource(R.drawable.rounded_edittext_background)
            }
        }

        if (editText == confirmPasswordEditText) {
            if (s.isNullOrEmpty() || passwordEditText.text.toString() != s.toString()) {
                editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
                editText.error = "Passwords do not match"
            } else {
                editText.setBackgroundResource(R.drawable.rounded_edittext_background)
            }
        }
    }

    private fun isAccountEmailExistent(): Boolean {
        // TODO: Implement email existence check
        return false
    }

    private fun registerAccount() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val recoveryEmail = recoveryEmailEditText.text.toString().trim()

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return
        }

        // Using email as ID
        val userId = recoveryEmail.replace(".", ",")

        val user = User(firstName, lastName, username, password, recoveryEmail, null, null)

        databaseReference.child(userId).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    sharedPreferences.edit().putInt("login_status", 0).apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Registration failed
                    AlertDialog.Builder(this)
                        .setTitle("Registration Failed")
                        .setMessage(task.exception?.message)
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
    }

    data class User(
        val firstName: String,
        val lastName: String,
        val username: String,
        val password: String,
        val recoveryEmail: String,
        val userGoal: String?,
        val userType: String?
    )
}