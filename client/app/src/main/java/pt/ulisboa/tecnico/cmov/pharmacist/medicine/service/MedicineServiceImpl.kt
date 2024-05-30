package pt.ulisboa.tecnico.cmov.pharmacist.medicine.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import


class MedicineServiceImpl : MedicineService {

    private val httpClient = HttpClient(Android) {
        install(Logging)

    }
}