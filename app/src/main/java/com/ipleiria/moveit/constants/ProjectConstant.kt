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
                    "Restaurantes",
                    "RESTAURANT"
                ),
                TypePlace(2, R.drawable.atm, "Banco", "ATM"),
                TypePlace(
                    3,
                    R.drawable.gas_station,
                    "Postos de Gasolina",
                    "GAS_STATION"
                ),
                TypePlace(
                    4,
                    R.drawable.shopping,
                    "Supermercados",
                    "GROCERY_OR_SUPERMARKET"
                ),
                TypePlace(5, R.drawable.hotel, "Hotéis", "hotel"),
                TypePlace(
                    6,
                    R.drawable.pharmacy,
                    "Farmácias",
                    "PHARMACY"
                ),
                TypePlace(
                    7,
                    R.drawable.hospital,
                    "Saúde",
                    "HOSPITAL"
                )
            )
    }
}