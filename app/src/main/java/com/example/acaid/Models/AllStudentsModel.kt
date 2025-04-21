package com.example.acaid.Models

import android.os.Parcel
import android.os.Parcelable


data class AllStudentsModel(
    val studentId: String = "",
    val studentName: String="",
    val studentRoll: String="",
    val studentClass: String=""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(studentId)
        parcel.writeString(studentName)
        parcel.writeString(studentRoll)
        parcel.writeString(studentClass)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllStudentsModel> {
        override fun createFromParcel(parcel: Parcel): AllStudentsModel {
            return AllStudentsModel(parcel)
        }

        override fun newArray(size: Int): Array<AllStudentsModel?> {
            return arrayOfNulls(size)
        }
    }
}
