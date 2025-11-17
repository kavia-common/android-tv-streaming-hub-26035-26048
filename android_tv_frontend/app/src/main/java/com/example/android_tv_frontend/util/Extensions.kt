package com.example.android_tv_frontend.util

// PUBLIC_INTERFACE
inline fun <T, R> T?.letOr(default: R, block: (T) -> R): R = if (this != null) block(this) else default
