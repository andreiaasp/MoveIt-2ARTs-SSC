package com.ipleiria.moveit.constants

import com.ipleiria.moveit.R
import com.ipleiria.moveit.models.TypePlace

class ProjectConstant {
    companion object {
        val placesName =
            listOf(
                TypePlace(
                    1,
                    R.drawable.restaurant,
                    "Restaurant",
                    "restaurant"
                ),
                TypePlace(2, R.drawable.atm, "ATM", "atm"),
                TypePlace(
                    3,
                    R.drawable.gas_station,
                    "Gas",
                    "gas_station"
                ),
                TypePlace(
                    4,
                    R.drawable.shopping,
                    "Groceries",
                    "supermarket"
                ),
                TypePlace(5, R.drawable.hotel, "Hotels", "hotel"),
                TypePlace(
                    6,
                    R.drawable.pharmacy,
                    "Pharmacies",
                    "pharmacy"
                ),
                TypePlace(
                    7,
                    R.drawable.hospital,
                    "Hospitals & Clinics",
                    "hospital"
                )
            )
    }
}