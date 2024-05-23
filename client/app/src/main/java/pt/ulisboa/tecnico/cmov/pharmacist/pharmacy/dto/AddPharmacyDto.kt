package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto

import kotlinx.serialization.Serializable
import pt.ulisboa.tecnico.cmov.pharmacist.exception.AddPharmacyMissingPropertiesException

@Serializable
class AddPharmacyDto(
    private val name: String,
    private val latitude: Double,
    private val longitude: Double,
    private val picturePath : String?,
    private val pictureExtension : String?
){

    fun getName() : String {
        return name
    }

    fun getLatitude() : Double {
        return latitude
    }

    fun getLongitude() : Double {
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
    private var latitude : Double? = null
    private var longitude : Double? = null
    private var picturePath : String? = null
    private var pictureExtension : String? = null

    fun setName(name: String) : AddPharmacyDtoBuilder {
        this.name = name
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
        if (latitude == null) missingProperties.add("latitude")
        if (longitude == null) missingProperties.add("longitude")

        if(missingProperties.isNotEmpty()){
            throw AddPharmacyMissingPropertiesException(missingProperties)
        }else{
            return AddPharmacyDto(name, latitude!!, longitude!!, picturePath, pictureExtension)
        }

    }
}

