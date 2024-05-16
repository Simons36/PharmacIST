package pt.ulisboa.tecnico.cmov.pharmacist.dto

import pt.ulisboa.tecnico.cmov.pharmacist.exception.AddPharmacyMissingPropertiesException
import kotlin.properties.Delegates

class AddPharmacyDto(
    private val name: String,
    private val latitude: Double,
    private val longitude: Double,
    private val picture : ByteArray,
    private val pictureExtension : String
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

    fun getPicture() : ByteArray {
        return picture
    }

    fun getPictureExtension() : String {
        return pictureExtension
    }
}

class AddPharmacyDtoBuilder(){
    private lateinit var name: String
    private var latitude : Double? = null
    private var longitude : Double? = null
    private lateinit var picture : ByteArray
    private lateinit var pictureExtension : String

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

    fun setPicture(picture: ByteArray) : AddPharmacyDtoBuilder {
        this.picture = picture
        return this
    }

    fun setPictureExtension(pictureExtension: String) : AddPharmacyDtoBuilder {
        this.pictureExtension = pictureExtension
        return this
    }

    fun build() : AddPharmacyDto {

        val missingProperties = ArrayList<String>()

        if (!this::name.isInitialized) missingProperties.add("name")
        if (latitude == null) missingProperties.add("latitude")
        if (longitude == null) missingProperties.add("longitude")
        if (!this::picture.isInitialized) missingProperties.add("picture")
        if (!this::pictureExtension.isInitialized) missingProperties.add("pictureExtension")

        if(missingProperties.isNotEmpty()){
            throw AddPharmacyMissingPropertiesException(missingProperties)
        }else{
            return AddPharmacyDto(name, latitude!!, longitude!!, picture, pictureExtension)
        }

    }
}

