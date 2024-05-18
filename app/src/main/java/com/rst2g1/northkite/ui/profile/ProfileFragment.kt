package com.rst2g1.northkite.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.FragmentProfileBinding
import com.rst2g1.northkite.databinding.ManageProfilePageBinding
import com.rst2g1.northkite.databinding.ProfilePageBinding
import com.rst2g1.northkite.ui.Encryptor
import com.rst2g1.northkite.ui.User

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    private lateinit var _bindingProfile: ProfilePageBinding

    private val bindingProfile get() = _bindingProfile

    private lateinit var _bindingManage: ManageProfilePageBinding

    private val bindingManage get() = _bindingManage

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        _bindingProfile = ProfilePageBinding.inflate(inflater)
        _bindingManage = ManageProfilePageBinding.inflate(inflater)

        sharedPreferences = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)

        database =
            FirebaseDatabase.getInstance("https://northkite-1120-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = database.reference.child("users")

        return binding.root
    }

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
            findNavController().navigate(R.id.action_navigation_profile_to_loginFragment)
        }

        val isLoggedIn = sharedPreferences.getInt("login_status", -1)

        if (isLoggedIn == 1) {
            bindingProfile.loginToContinueLabel.visibility = View.VISIBLE // guest login
            bindingProfile.displayUsername.setText(R.string.guest)
        }

        if (isLoggedIn == 0){
            getUserFromDatabase(currentUserID) { user ->
                // Handle the retrieved user here
                user?.let {
                    // User retrieved successfully, do something with the user
                    val username = user.username
                    bindingProfile.displayUsername.text = username
                }
            }

            //normal login, all profile button function here
            bindingProfile.linearLayoutProfile.setOnClickListener {
                container.removeAllViews()
                container.addView(bindingManage.root)
            }
        }

        bindingManage.buttonConfirm.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Enter password for confirmation")

            val input = EditText(requireContext())
            input.hint = "Password" // Set a hint for the EditText
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)

            builder.setPositiveButton("Confirm") { dialog, which ->
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

            builder.setNegativeButton("Cancel") { dialog, which ->
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

    }

    private fun checkPassword(user: User, password: String): Boolean {
        return user.password == password
    }

    private fun confirmChanges(user: User) {

        var newUserData : User = user
        val currentUserID = user.email.replace(".", ",")

        if(!bindingManage.editTextFirstName.text.isEmpty()){
            newUserData.firstName = bindingManage.editTextFirstName.text.toString().trim()
        }

        if(!bindingManage.editTextLastName.text.isEmpty()){
            newUserData.lastName = bindingManage.editTextLastName.text.toString().trim()
        }

        if(!bindingManage.editTextUsername.text.isEmpty()){
            newUserData.username = bindingManage.editTextUsername.text.toString().trim()
        }

        if(bindingManage.editTextEmail.text.isEmpty()){
            databaseReference.child(currentUserID).setValue(newUserData).addOnSuccessListener {
                userEditSuccess()
            }
                .addOnFailureListener { exception ->
                    showErrorDialog(exception.message ?: "Unknown error occurred")
                }
        }
        else{
            newUserData.email = bindingManage.editTextEmail.text.toString().trim()
            //need to recreate the node and assign the userData to it
            databaseReference.child(currentUserID).removeValue().addOnSuccessListener {
                databaseReference.child(newUserData.email.replace(".",",")).setValue(newUserData).addOnSuccessListener {
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
            databaseReference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
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

    private fun userEditSuccess(){
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Process successful")
            setMessage("Successfully edit users detail")
            setPositiveButton("Confirm") { dialog, which ->
                dialog.dismiss()
            }
        }.create().show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("Confirm") { dialog, which ->
                dialog.dismiss()
            }
        }.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val container = binding.root.findViewById<FrameLayout>(R.id.fragment_container_profile)
        container.removeAllViews()
    }
}