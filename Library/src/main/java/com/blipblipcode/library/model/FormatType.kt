package com.blipblipcode.library.model

sealed class FormatType(delimiter: Char) {
        data class Short(val delimiter: Char):FormatType(delimiter)
        data class Large(val delimiter: Char):FormatType(delimiter)
    }