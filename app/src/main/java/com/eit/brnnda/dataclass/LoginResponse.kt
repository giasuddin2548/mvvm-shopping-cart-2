package com.eit.brnnda.dataclass

data class LoginResponse(
    val message: String,
    val token: String,
    val user_id: String
)