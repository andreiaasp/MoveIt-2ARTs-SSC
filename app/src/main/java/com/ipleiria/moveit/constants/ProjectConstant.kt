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
                    "RESTAURANT"
                ),
                TypePlace(2, R.drawable.atm, "ATM", "ATM"),
                TypePlace(
                    3,
                    R.drawable.gas_station,
                    "Gas",
                    "GAS_STATION"
                ),
                TypePlace(
                    4,
                    R.drawable.shopping,
                    "Groceries",
                    "GROCERY_OR_SUPERMARKET"
                ),
                TypePlace(5, R.drawable.hotel, "Hotels", "hotel"),
                TypePlace(
                    6,
                    R.drawable.pharmacy,
                    "Pharmacies",
                    "PHARMACY"
                ),
                TypePlace(
                    7,
                    R.drawable.hospital,
                    "Hospitals & Clinics",
                    "HOSPITAL"
                )
            )
    }
}