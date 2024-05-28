import { HttpException, HttpStatus, Injectable, Logger } from '@nestjs/common';
import { PharmacyDto } from './dto/pharmacy.dto';
import {
  getAddressFromCoordinates,
  getCoordinatesFromAddress,
} from './util/pharmacy.util';
import { AppConfigService } from 'src/config/app-config.service';
import { InjectModel } from '@nestjs/mongoose';
import { Pharmacy, PharmacyDocument } from './schemas/pharmacy.schema';
import { Model } from 'mongoose';

@Injectable()
export class PharmacyService {
  constructor(
    private configService: AppConfigService,
    @InjectModel(Pharmacy.name)
    private readonly pharmacyModel: Model<PharmacyDocument>,
  ) {}

  private readonly logger = new Logger(PharmacyService.name);

  /**
   * Adds new pharmacy to the database
   * @param pharmacyDto
   */
  async addNewPharmacy(
    pharmacyDto: PharmacyDto,
    photo: Express.Multer.File | undefined,
  ) {
    console.log('Received photo:', photo);

    // Log the request
    this.logger.log(
      'Received request to add new pharmacy with name ' +
        pharmacyDto.name +
        '.',
    );

    // If address is not provided, get the address from the coordinates
    if (!pharmacyDto.address) {
      try {
        const address = await await getAddressFromCoordinates(
          pharmacyDto.latitude,
          pharmacyDto.longitude,
          this.configService.googleMapsApiKey,
        );
        pharmacyDto = { ...pharmacyDto, address };
      } catch (error) {
        // Unable to get address from coordinates, leave it empty
      }
    } else if (!pharmacyDto.latitude || !pharmacyDto.longitude) {
      // If address is provided but coordinates are not
      try {
        const coordinates = await getCoordinatesFromAddress(
          pharmacyDto.address,
          this.configService.googleMapsApiKey,
        );
        pharmacyDto = {
          ...pharmacyDto,
          latitude: coordinates.latitude,
          longitude: coordinates.longitude,
        };
      } catch (error) {
        // if unable to get coordinates from address, it means that address is invalid
        throw new HttpException(
          'Invalid address provided.',
          HttpStatus.BAD_REQUEST,
        );
      }
    }

    //if photo is not undefined
    if (photo) {
      const photoPath = this.configService.photosPath;

      //Create path if it does not exist
      const fs = require('fs');
      if (!fs.existsSync(photoPath)) {
        fs.mkdirSync(photoPath);
      }

      try {
        const photoFilename =
          pharmacyDto.name + '.' + photo.mimetype.split('/')[1];

        // Write the photo data to a file
        fs.writeFileSync(`${photoPath}/${photoFilename}`, photo.buffer);

        pharmacyDto = {
          ...pharmacyDto,
          photoPath: `${photoPath}/${photoFilename}`,
        };
      } catch (error) {
        this.logger.log('Error while saving photo: ' + error.message);
        throw new HttpException(
          'Error while saving photo: ' + error.message,
          HttpStatus.INTERNAL_SERVER_ERROR,
        );
      }
    }

    // Now save the pharmacy to the database
    try {
      const newPharmacy = new this.pharmacyModel(pharmacyDto);
      await newPharmacy.save();
    } catch (error) {
      // Before throwing the error, erase the photo file if it was saved
      if (pharmacyDto.photoPath) {
        try {
          require('fs').unlinkSync(pharmacyDto.photoPath);
        } catch (error) {
          // Ignore the error
        }
      }

      // Now check if the error is due to duplicate key

      if (error.code === 11000 || error.code === 11001) {
        throw new HttpException(
          `Pharmacy name '${pharmacyDto.name}' is already in use.`,
          HttpStatus.CONFLICT,
        );
      }

      this.logger.log('Error while adding new pharmacy: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    this.logger.log('New pharmacy added successfully');
  }

  /**
   * Get all pharmacies from the database
   */
  async getAllPharmacies() {
    this.logger.log('Received request to get all pharmacies');

    try {
      const pharmacies = await this.pharmacyModel
        .find()
        .select('name address latitude longitude -_id')
        .exec();
      return pharmacies;
    } catch (error) {
      this.logger.log('Error while getting all pharmacies: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
