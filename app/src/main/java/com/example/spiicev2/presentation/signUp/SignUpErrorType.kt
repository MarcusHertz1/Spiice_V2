package com.example.spiicev2.presentation.signUp

import com.example.spiicev2.presentation.appBase.ErrorType

object SignUpErrorType {
    object EmailError : ErrorType
    object PasswordError : ErrorType
    object PasswordAndConfirmPasswordError : ErrorType
    object OtherError : ErrorType
}