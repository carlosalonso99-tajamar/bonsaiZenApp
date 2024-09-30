package com.bonsaizen.bonsaizenapp.data.model.bonsais

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Bonsai(
    val id: String = "",
    val name: String = "",
    val dateAdquisition: String = "",
    val dateLastTransplant: String = "",
    val dateNextTransplant: String = "",
    val images: List<String> = listOf()

) : Parcelable
