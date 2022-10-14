package com.ace1ofspades.recyclerview.providers


interface SpanSizeProvider {
    fun getSpanSize(spanCount: Int, position: Int): Int
}