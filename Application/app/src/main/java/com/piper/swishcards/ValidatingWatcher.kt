package com.piper.swishcards

import android.text.Editable
import android.text.TextWatcher

class ValidatingWatcher(callback: ValidateCallback): TextWatcher {

    var bad_words: MutableList<String> = mutableListOf()
    var callback: ValidateCallback = callback

    fun setWords(words: MutableList<String>) {
        bad_words = words
    }


    override fun afterTextChanged(currentText: Editable?) {
        val current: String = currentText.toString()
        val currentBadWords = current.findAnyOf(
            bad_words,
            0,
            ignoreCase = true
        ) //check if current has any bad words in it
        if (current.length > 10) {
            callback.setText(currentText?.substring(0, 10) ?: "") //inputTitle.setSelection(10)
            callback.showToast("Title cannot exceed 10 Characters")
        }
        if (currentBadWords != null) {
            val newText = current.replace(currentBadWords.second, "", true)
            callback.setText(newText)
            callback.showToast("Innapropriate Content is Not Allowed!")
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}