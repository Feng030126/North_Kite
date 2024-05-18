package com.rst2g1.northkite.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rst2g1.northkite.MainActivity
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.FragmentLoginBinding
import com.rst2g1.northkite.databinding.LoginPageBinding
import com.rst2g1.northkite.databinding.RegisterPageBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var bindingLogin: LoginPageBinding
    private lateinit var bindingRegister: RegisterPageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var isRegister = false

    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var emailEditText: EditText

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPreferences =
            requireActivity().getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
        sharedPreferences.edit().putInt("login_status", -1).apply()

        binding = FragmentLoginBinding.inflate(inflater,container,false)
        bindingLogin = LoginPageBinding.inflate(inflater)
        bindingRegister = RegisterPageBinding.inflate(inflater)

        database =
            FirebaseDatabase.getInstance("https://northkite-1120-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = database.reference.child("users")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        initializeLoginFields()

        val container = view.findViewById<FrameLayout>(R.id.fragment_container_login)

        container.removeAllViews()
        container.addView(bindingLogin.root)

        bindingLogin.apply {
            buttonGuest.setOnClickListener {
                sharedPreferences.edit().putInt("login_status", 1).apply()
                findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
            }

            buttonRegister.setOnClickListener {
                initializeRegistrationFields()
                isRegister = true
                container.removeAllViews()
                container.addView(bindingRegister.root)

            }

            buttonLogin.setOnClickListener {
                loginAccount()
            }
        }

        bindingRegister.buttonCancel.setOnClickListener {
            showCancelConfirmationDialog(container)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isRegister) {
                showCancelConfirmationDialog(container)
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun showCancelConfirmationDialog(container: FrameLayout) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirm to cancel register")
            setMessage("Are you sure you want to cancel registration?")
            setPositiveButton("Confirm") { _, _ ->
                isRegister = false
                container.removeAllViews()
                container.addView(bindingLogin.root)
            }
            setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }.create().show()
    }

    private fun initializeLoginFields() {
        with(bindingLogin) {
            loginEmail = editTextEmail
            loginPassword = editTextPassword
        }

        addLoginTextWatchers()
    }

    private fun addLoginTextWatchers() {
        listOf(loginEmail, loginPassword).forEach {
            it.addTextChangedListener(createLoginTextWatcher(it))
        }
    }

    private fun createLoginTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                resetLoginErrorState(editText)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun resetErrorState(editText: EditText) {
        editText.setBackgroundResource(R.drawable.rounded_edittext_background)
        editText.error = null
    }

    private fun resetLoginErrorState(editText: EditText) {
        editText.error = null
        if (editText == loginEmail) {
            editText.setBackgroundResource(R.drawable.login_user)
        } else {
            editText.setBackgroundResource(R.drawable.login_password)
        }
    }

    private fun loginAccount() {
        val email = loginEmail.text.toString().trim()
        val password = loginPassword.text.toString().trim()

        // Check if email or password is empty
        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                setLoginErrorState(loginEmail, "Email cannot be empty")
            }
            if (password.isEmpty()) {
                setLoginErrorState(loginPassword, "Password cannot be empty")
            }
            return
        }

        // Check if email and password match
        val userId = email.replace(".", ",")
        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null && user.password == password) {
                        // Login successful
                        sharedPreferences.edit().putInt("login_status", 0).apply()
                        sharedPreferences.edit().putString("current_user", userId).apply()
                        findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
                    } else {
                        // Incorrect email or password
                        setLoginErrorState(loginEmail, "Incorrect email or password")
                        setLoginErrorState(loginPassword, "Incorrect email or password")
                    }
                } else {
                    // Email not found or incorrect password
                    setLoginErrorState(loginEmail, "Incorrect email or password")
                    setLoginErrorState(loginPassword, "Incorrect email or password")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage(databaseError.message)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        })
    }


    private fun setErrorState(editText: EditText, errorMessage: String) {
        editText.setBackgroundResource(R.drawable.redborder_rounded_edittext_background)
        editText.error = errorMessage
    }

    private fun setLoginErrorState(editText: EditText, errorMessage: String) {
        if (editText == loginEmail) {
            editText.setBackgroundResource(R.drawable.login_user_error)
        } else {
            editText.setBackgroundResource(R.drawable.login_password_error)
        }
        editText.error = errorMessage
    }

    private fun initializeRegistrationFields() {
        with(bindingRegister) {
            firstNameEditText = editTextFirstName
            lastNameEditText = editTextLastName
            usernameEditText = editTextUsername
            passwordEditText = editTextPassword
            confirmPasswordEditText = editTextConfirmPassword
            emailEditText = editTextEmail
        }

        addTextWatchers()
        bindingRegister.buttonRegister.setOnClickListener {
            if (areAnyFieldsEmpty()) {
                highlightEmptyFields()
            } else {
                val email = emailEditText.text.toString().trim()
                isEmailExistent(email)
            }
        }
    }

    private fun addTextWatchers() {
        listOf(firstNameEditText, lastNameEditText, usernameEditText, emailEditText).forEach {
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
            emailEditText
        ).any { it.text.isNullOrEmpty() }
    }

    private fun highlightEmptyFields() {
        listOf(
            firstNameEditText to "Cannot leave the field blank",
            lastNameEditText to "Cannot leave the field blank",
            usernameEditText to "Cannot leave the field blank",
            passwordEditText to "Cannot leave the field blank",
            confirmPasswordEditText to "Cannot leave the field blank",
            emailEditText to "Cannot leave the field blank"
        ).forEach { (editText, message) ->
            if (editText.text.isNullOrEmpty()) {
                setErrorState(editText, message)
            }
        }
    }

    private fun createTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                resetErrorState(editText)
            }

            override fun afterTextChanged(s: Editable?) {
                validateField(editText, s)
            }
        }
    }

    private fun createPasswordTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                resetErrorState(editText)
            }

            override fun afterTextChanged(s: Editable?) {
                validatePasswordFields(editText, s)
            }
        }
    }

    private fun validateField(editText: EditText, s: Editable?) {
        if (s.isNullOrEmpty()) {
            setErrorState(editText, "Cannot leave the field blank")
        } else {
            resetErrorState(editText)
        }

        if (editText == emailEditText && !Patterns.EMAIL_ADDRESS.matcher(s.toString())
                .matches()
        ) {
            setErrorState(editText, "Invalid email format")
        }
    }

    private fun validatePasswordFields(editText: EditText, s: Editable?) {
        if (editText == passwordEditText) {
            if (s.isNullOrEmpty() || s.length < 8) {
                setErrorState(editText, "Password must be at least 8 characters")
            } else {
                resetErrorState(editText)
            }
        }

        if (editText == confirmPasswordEditText) {
            if (s.isNullOrEmpty() || passwordEditText.text.toString() != s.toString()) {
                setErrorState(editText, "Passwords do not match")
            } else {
                resetErrorState(editText)
            }
        }
    }

    private fun isEmailExistent(email: String) {
        val userId = email.replace(".", ",")

        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    setErrorState(emailEditText, "Email already exists")
                } else {
                    registerAccount()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage(databaseError.message)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        })
    }

    private fun registerAccount() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()

        if (password != confirmPassword) {
            setErrorState(confirmPasswordEditText, "Passwords do not match")
            return
        }

        val userId = email.replace(".", ",")
        val user = User(firstName, lastName, username, password, email, null, null)

        databaseReference.child(userId).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sharedPreferences.edit().putInt("login_status", 0).apply()
                    sharedPreferences.edit().putString("current_user", userId).apply()
                    findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
                    requireActivity().finish()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Registration Failed")
                        .setMessage(task.exception?.message)
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}

public data class User(
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    val password: String = "",
    var email: String = "",
    val userGoal: String? = null,
    val userType: String? = null
)
