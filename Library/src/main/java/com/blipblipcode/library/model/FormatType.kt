package com.blipblipcode.library.model

sealed class FormatType(delimiter: String) {
        data class Short(val delimiter: String):FormatType(delimiter)
        data class Large(val delimiter: String):FormatType(delimiter)
    }