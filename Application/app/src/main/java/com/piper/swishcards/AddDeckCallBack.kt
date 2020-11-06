package com.piper.swishcards

//Call back between AddCardViewModel and AddCard Activity
interface AddDeckCallBack {

    fun setTitleFromModel(message: String) {
    }

    fun setDateFromModel(message: String) {
    }

    fun setSnackBar(message: String) {

    }

}