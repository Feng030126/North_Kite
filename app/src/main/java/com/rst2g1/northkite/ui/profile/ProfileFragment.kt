package com.rst2g1.northkite.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.ChangePasswordPageBinding
import com.rst2g1.northkite.databinding.FragmentProfileBinding
import com.rst2g1.northkite.databinding.ManageProfilePageBinding
import com.rst2g1.northkite.databinding.ProfilePageBinding
import com.rst2g1.northkite.ui.Encryptor
import com.rst2g1.northkite.ui.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    private lateinit var _bindingProfile: ProfilePageBinding

    private val bindingProfile get() = _bindingProfile

    private lateinit var _bindingManage: ManageProfilePageBinding

    private val bindingManage get() = _bindingManage

    private lateinit var _bindingPassword: ChangePasswordPageBinding

    private val bindingPassword get() = _bindingPassword

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        _bindingProfile = ProfilePageBinding.inflate(inflater)
        _bindingManage = ManageProfilePageBinding.inflate(inflater)
        _bindingPassword = ChangePasswordPageBinding.inflate(inflater)

        sharedPreferences = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)

        database =
            FirebaseDatabase.getInstance("https://northkite-1120-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = database.reference.child("users")

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<FrameLayout>(R.id.fragment_container_profile)
        val currentUserID = sharedPreferences.getString("current_user", null)

        getUserFromDatabase(currentUserID) { user ->
            // Handle the retrieved user here
            user?.let {
                // User retrieved successfully, do something with the user
                initializeEditProfile(it) // Pass the retrieved user to initializeEditProfile function
            }
        }

        container.removeAllViews()
        container.addView(bindingProfile.root)

        bindingProfile.linearLayoutLogout.setOnClickListener {
            sharedPreferences.edit().putInt("login_status", -1).apply()
            sharedPreferences.edit().putString("current_user", null).apply()
            findNavController().navigate(R.id.action_navigation_profile_to_loginFragment)
        }

        val isLoggedIn = sharedPreferences.getInt("login_status", -1)

        if (isLoggedIn == 1) {
            bindingProfile.loginToContinueLabel.visibility = View.VISIBLE // guest login
            bindingProfile.username.setText(R.string.guest)
        }

        if (isLoggedIn == 0) {
            getUserFromDatabase(currentUserID) { user ->
                // Handle the retrieved user here
                user?.let {
                    // User retrieved successfully, do something with the user
                    val username = user.username
                    bindingProfile.username.text = username


                    val registerDate = user.date
                    val joinedTime = calculateDaysSinceRegister(registerDate)
                    bindingProfile.joinedTime.text =
                        getString(R.string.joined_time_format, joinedTime)
                }
            }

            bindingProfile.linearLayoutProfile.setOnClickListener {
                container.removeAllViews()
                container.addView(bindingManage.root)
            }

            bindingProfile.linearLayoutPrivacyAndSecurity.setOnClickListener {
                container.removeAllViews()
                container.addView(bindingPassword.root)
            }

        }

        bindingManage.buttonConfirm.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Enter password for confirmation")

            val input = EditText(requireContext())
            input.hint = "Password" // Set a hint for the EditText
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)

            builder.setPositiveButton("Confirm") { dialog, _ ->
                // Get the text from the EditText when the positive button is clicked
                val password = Encryptor.encrypt(input.text.toString().trim())
                // Handle the entered text as needed

                getUserFromDatabase(currentUserID) { user ->
                    // Handle the retrieved user here
                    user?.let {
                        // User retrieved successfully, check the password
                        val isPasswordCorrect = checkPassword(user, password)
                        if (isPasswordCorrect) {
                            // If the password is correct, proceed with confirming changes
                            confirmChanges(it)
                            // Dismiss the dialog
                            dialog.dismiss()
                        } else {
                            // If the password is incorrect, show an error message
                            input.error = "Incorrect password"
                        }
                    }
                }
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                // Cancel button clicked, dismiss the dialog
                dialog.dismiss()
            }

            // Show the dialog
            val dialog = builder.create()
            dialog.show()
        }

        bindingManage.buttonCancel.setOnClickListener {
            container.removeAllViews()
            container.addView(bindingProfile.root)
        }


        bindingPassword.buttonConfirm.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("Last confirmation")
                .setMessage("Are you sure to change your password?")
                .setPositiveButton("Yes") { _, _ ->
                    if (checkIfPassEqConf()) {
                        getUserFromDatabase(currentUserID) { user ->
                            // Handle the retrieved user here
                            user?.let {
                                // User retrieved successfully, do something with the user
                                if (validate(it)) {
                                    updatePassword(it)
                                } else {
                                    displayError()
                                }

                                // Pass the retrieved user to initializeEditProfile function
                            }
                        }
                    } else {
                        displayError()
                    }
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        bindingPassword.buttonCancel.setOnClickListener {
            container.removeAllViews()
            container.addView(bindingProfile.root)
            bindingPassword.editTextOldPassword.text.clear()
            bindingPassword.editTextNewPassword.text.clear()
            bindingPassword.editTextConfirmOldPassword.text.clear()
            bindingPassword.editTextConfirmNewPassword.text.clear()
        }


    }

    private fun displayError() {
        AlertDialog.Builder(requireContext())
            .setTitle("Change fail!")
            .setMessage("Error! Incorrect password!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun validate(user: User): Boolean {

        return Encryptor.encrypt(
            bindingPassword.editTextOldPassword.text.toString().trim()
        ) == user.password

    }

    private fun updatePassword(user: User) {
        val newPassword =
            Encryptor.encrypt(bindingPassword.editTextNewPassword.text.toString().trim())
        val currentUserID = user.email.replace(".", ",")
        val userWithNewPass = user

        userWithNewPass.password = newPassword
        databaseReference.child(currentUserID).setValue(userWithNewPass).addOnSuccessListener {

            AlertDialog.Builder(requireContext())
                .setTitle("Change success")
                .setMessage("Successfully changed password!")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()

        }
            .addOnFailureListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Change failed")
                    .setMessage(it.message)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }

        bindingPassword.editTextOldPassword.text.clear()
        bindingPassword.editTextNewPassword.text.clear()
        bindingPassword.editTextConfirmOldPassword.text.clear()
        bindingPassword.editTextConfirmNewPassword.text.clear()

    }

    private fun checkIfPassEqConf(): Boolean {

        lateinit var pass: String
        lateinit var cpass: String
        lateinit var newpass: String
        lateinit var cnewpass: String

        with(bindingPassword) {
            pass = editTextOldPassword.text.toString()
            cpass = editTextConfirmOldPassword.text.toString()
            newpass = editTextNewPassword.text.toString()
            cnewpass = editTextConfirmNewPassword.text.toString()
        }

        return ((pass == cpass) && (newpass == cnewpass))
    }

    private fun checkPassword(user: User, password: String): Boolean {
        return user.password == password
    }

    private fun confirmChanges(user: User) {

        val newUserData: User = user
        val currentUserID = user.email.replace(".", ",")

        if (bindingManage.editTextFirstName.text.isNotEmpty()) {
            newUserData.firstName = bindingManage.editTextFirstName.text.toString().trim()
        }

        if (bindingManage.editTextLastName.text.isNotEmpty()) {
            newUserData.lastName = bindingManage.editTextLastName.text.toString().trim()
        }

        if (bindingManage.editTextUsername.text.isNotEmpty()) {
            newUserData.username = bindingManage.editTextUsername.text.toString().trim()
        }

        if (bindingManage.editTextEmail.text.isEmpty()) {
            databaseReference.child(currentUserID).setValue(newUserData).addOnSuccessListener {
                userEditSuccess()
            }
                .addOnFailureListener { exception ->
                    showErrorDialog(exception.message ?: "Unknown error occurred")
                }
        } else {
            newUserData.email = bindingManage.editTextEmail.text.toString().trim()
            //need to recreate the node and assign the userData to it
            databaseReference.child(currentUserID).removeValue().addOnSuccessListener {
                databaseReference.child(newUserData.email.replace(".", ",")).setValue(newUserData)
                    .addOnSuccessListener {
                        userEditSuccess()
                    }
                    .addOnFailureListener { exception ->
                        showErrorDialog(exception.message ?: "Unknown error occurred")
                    }
            }
                .addOnFailureListener { exception ->
                    showErrorDialog(exception.message ?: "Unknown error occurred")
                }

        }
    }

    private fun getUserFromDatabase(userID: String?, callback: (User?) -> Unit) {
        if (userID != null) {
            databaseReference.child(userID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        callback(user) // Pass the retrieved user to the callback function
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null) // Pass null in case of error or no data found
                    }
                })
        } else {
            callback(null) // Pass null if userID is null
        }
    }


    private fun initializeEditProfile(user: User?) {
        user?.let {
            // Use the retrieved user to initialize edit profile fields
            bindingManage.editTextFirstName.hint = it.firstName
            bindingManage.editTextLastName.hint = it.lastName
            bindingManage.editTextUsername.hint = it.username
            bindingManage.editTextEmail.hint = it.email
        }
    }

    private fun userEditSuccess() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Process successful")
            setMessage("Successfully edit users detail")
            setPositiveButton("Confirm") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("Confirm") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDaysSinceRegister(registerDate: String?): Long {
        return if (registerDate != null) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val registrationDate = LocalDate.parse(registerDate, formatter)
            val currentDate = LocalDate.now()
            ChronoUnit.DAYS.between(registrationDate, currentDate)
        } else {
            0
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val container = binding.root.findViewById<FrameLayout>(R.id.fragment_container_profile)
        container.removeAllViews()
    }
}