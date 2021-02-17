package com.eit.brnnda.dataclass

data class SubCategoryDataItem(
    val category_id: String,
    val id: String,
    val name: String,
    val slug: String,
    val status: String,
    val childcat_status: String
)