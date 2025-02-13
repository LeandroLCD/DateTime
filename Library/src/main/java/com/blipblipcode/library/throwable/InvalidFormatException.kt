package com.blipblipcode.library.throwable

class InvalidFormatException(val dateString: String, override val message: String = "Invalid Format: ", override val cause:Throwable? = null):Throwable(message.plus(dateString), cause) {
    override fun toString(): String {
        return "InvalidFormatException(message=${message.plus(dateString)}, cause=${cause ?: "No cause"})"
    }
}