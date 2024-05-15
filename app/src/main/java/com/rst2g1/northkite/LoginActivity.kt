package com.rst2g1.northkite

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.rst2g1.northkite.databinding.LoginPageBinding
import com.rst2g1.northkite.databinding.RegisterPageBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginPageBinding
    private lateinit var bindingRegister: RegisterPageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var isRegister = false
    private lateinit var cancelDialog: AlertDialog

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var recoveryEmailEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        sharedPreferences.edit().putInt("login_status", -1).apply()
        //login status: -1 for no login, 0 for logged in, 1 for guest

        binding = LoginPageBinding.inflate(layoutInflater)
        bindingRegister = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupBackPressedHandler()
        register()
    }

    private fun setupListeners() {
        binding.buttonGuest.setOnClickListener {
            sharedPreferences.edit().putInt("login_status", 1).apply()
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.buttonRegister.setOnClickListener {
            setContentView(bindingRegister.root)
            isRegister = true
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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm to cancel register")
            .setMessage("Are you sure you want to cancel registration?")
            .setPositiveButton("Confirm") { _, _ ->
                isRegister = false
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }.setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        cancelDialog = builder.create()
        cancelDialog.show()
    }

    private fun register() {
        firstNameEditText = bindingRegister.editTextFirstName
        lastNameEditText = bindingRegister.editTextLastName
        usernameEditText = bindingRegister.editTextUsername
        passwordEditText = bindingRegister.editTextPassword
        confirmPasswordEditText = bindingRegister.editTextConfirmPassword
        recoveryEmailEditText = bindingRegister.editTextRecoveryEmail

        firstNameEditText.addTextChangedListener(createTextWatcher(firstNameEditText))
        lastNameEditText.addTextChangedListener(createTextWatcher(lastNameEditText))
        usernameEditText.addTextChangedListener(createTextWatcher(usernameEditText))
        passwordEditText.addTextChangedListener(createPasswordTextWatcher(passwordEditText))
        confirmPasswordEditText.addTextChangedListener(
            createPasswordTextWatcher(confirmPasswordEditText)
        )
        recoveryEmailEditText.addTextChangedListener(createTextWatcher(recoveryEmailEditText))

        bindingRegister.buttonRegister.setOnClickListener {
            if (isAnyFieldEmpty()) {
                highlightEmptyFields()
            } else {
                if (!accountEmailExisted()) {
                    registerAccount()
                }
            }
        }
    }

    private fun isAnyFieldEmpty(): Boolean {
        return listOf(
            firstNameEditText,
            lastNameEditText,
            usernameEditText,
            passwordEditText,
            confirmPasswordEditText,
            recoveryEmailEditText
        ).any { TextUtils.isEmpty(it.text) }
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
            if (TextUtils.isEmpty(editText.text)) {
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
                if (s.isNullOrEmpty()) {
                    editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
                    editText.error = "Cannot leave the field blank"
                } else {
                    editText.setBackgroundResource(R.drawable.rounded_edittext_background)
                }

                if (editText == recoveryEmailEditText && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        s.toString()
                    ).matches()
                ) {
                    editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
                    editText.error = "Invalid email format"
                }
            }
        }
    }

    private fun createPasswordTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (editText == passwordEditText) {
                    handlePasswordTextChanged(s, editText)
                } else if (editText == confirmPasswordEditText) {
                    handleConfirmPasswordTextChanged(s)
                }
            }
        }
    }

    private fun handlePasswordTextChanged(s: Editable?, editText: EditText) {
        if (s.isNullOrEmpty() || s.length < 8) {
            editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
            editText.error = "Password must be at least 8 characters"
        } else {
            editText.setBackgroundResource(R.drawable.rounded_edittext_background)
        }
    }

    private fun handleConfirmPasswordTextChanged(s: Editable?) {
        if (s.isNullOrEmpty() || passwordEditText.text.toString() != s.toString()) {
            confirmPasswordEditText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
            confirmPasswordEditText.error = "Passwords do not match"
        } else {
            confirmPasswordEditText.setBackgroundResource(R.drawable.rounded_edittext_background)
        }
    }

    private fun accountEmailExisted(): Boolean {
        //TODO:check email
        return false
    }

    private fun registerAccount() {
        //TODO:register account
    }
}