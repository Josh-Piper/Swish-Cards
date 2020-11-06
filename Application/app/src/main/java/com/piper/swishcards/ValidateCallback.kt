package com.piper.swishcards

//Validate callback used for AddCardActivity and ValidatingWatcher (TextWatcher)
interface ValidateCallback {

    fun showToast(message: String)
    fun setText(message: String)
    fun setSnackBar(message: String)
}