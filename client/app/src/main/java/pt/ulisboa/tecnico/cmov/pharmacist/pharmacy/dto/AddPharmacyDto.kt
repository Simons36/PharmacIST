package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto

import kotlinx.serialization.Serializable
import pt.ulisboa.tecnico.cmov.pharmacist.exception.AddPharmacyMissingPropertiesException

@Serializable
class AddPharmacyDto(
    private val name: String,
    private val address : String?,
    private val latitude: Double?,
    private val longitude: Double?,
    private val picturePath : String?,
    private val pictureExtension : String?
){

    fun getName() : String {
        return name
    }

    fun getAddress() : String? {
        return address
    }

    fun getLatitude() : Double? {
        return latitude
    }

    fun getLongitude() : Double? {
        return longitude
    }

    fun getPicturePath() : String? {
        return picturePath
    }

    fun getPictureExtension() : String? {
        return pictureExtension
    }
}

class AddPharmacyDtoBuilder(){
    private lateinit var name: String
    private var address : String? = null
    private var latitude : Double? = null
    private var longitude : Double? = null
    private var picturePath : String? = null
    private var pictureExtension : String? = null

    fun setName(name: String) : AddPharmacyDtoBuilder {
        this.name = name
        return this
    }

    fun setAddress(address: String) : AddPharmacyDtoBuilder {
        this.address = address
        return this
    }

    fun setLatitude(latitude: Double) : AddPharmacyDtoBuilder {
        this.latitude = latitude
        return this
    }

    fun setLongitude(longitude: Double) : AddPharmacyDtoBuilder {
        this.longitude = longitude
        return this
    }

    fun setPicturePath(picturePath: String) : AddPharmacyDtoBuilder {
        this.picturePath = picturePath
        return this
    }

    fun setPictureExtension(pictureExtension: String) : AddPharmacyDtoBuilder {
        this.pictureExtension = pictureExtension
        return this
    }

    //getters
    fun getName() : String {
        return name
    }

    fun getAddress() : String? {
        return address
    }

    fun getLatitude() : Double? {
        return latitude
    }

    fun getLongitude() : Double? {
        return longitude
    }

    fun getPicturePath() : String? {
        return picturePath
    }

    fun getPictureExtension() : String? {
        return pictureExtension
    }


    fun build() : AddPharmacyDto {

        val missingProperties = ArrayList<String>()

        if (!this::name.isInitialized) missingProperties.add("name")

        // Either address or latitude and longitude must be present
        if (this.address == null && (this.latitude == null || this.longitude == null)) {
            missingProperties.add("address")
            missingProperties.add("latitude")
            missingProperties.add("longitude")
        }

        if(missingProperties.isNotEmpty()){
            throw AddPharmacyMissingPropertiesException(missingProperties)
        }else{
            return AddPharmacyDto(name, address, latitude, longitude, picturePath, pictureExtension)
        }

    }
}

