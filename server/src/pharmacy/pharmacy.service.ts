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
import { PharmacyVersion } from './schemas/pharmacy-version.schema';
import { log } from 'console';
import { UserService } from 'src/user/user.service';

@Injectable()
export class PharmacyService {

  constructor(
    private configService: AppConfigService,
    private userService: UserService,
    @InjectModel(Pharmacy.name)
    private readonly pharmacyModel: Model<PharmacyDocument>,
    @InjectModel(PharmacyVersion.name)
    private readonly pharmacyVersionModel: Model<PharmacyVersion>,
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

    // Now that pharmacy was added, also increase the version counter

    try {
      //get the current version (find greatest version number and increment it by 1)
      let currentVersion = (
        await this.pharmacyVersionModel.findOne().sort({ version: -1 }).exec()
      )
      let newVersion: number;

      if (!currentVersion) {
        newVersion = 1;
      } else {
        newVersion = currentVersion.version + 1;
      }

      //create new version
      const newPharmacyVersion = new this.pharmacyVersionModel({
        version: newVersion,
        wasAdded: true,
        pharmacyName: pharmacyDto.name,
      });
      await newPharmacyVersion.save();
    } catch (error) {
      this.logger.log('Error while adding new pharmacy version: ' + error.message);
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

  async getUpdatedPharmaciesInfo(receivedPharmaciesDto: PharmacyDto[]) {
    this.logger.log('Received request to get updated pharmacies info');

    let pharmaciesToAdd: PharmacyDto[] = [];
    let pharmaciesToRemove: string[] = [];

    // ReceivedPharmaciesDto represents the current state of pharmacies in the client
    // We need to check if the pharmacies in the database have been updated
    // We will return a json object with two lists: pharmaciesToAdd and pharmaciesToRemove

    for (let pharmacyDto of receivedPharmaciesDto) {
      // Check if the pharmacy exists in the database
      const pharmacy = await this.pharmacyModel
        .findOne({ name: pharmacyDto.name })
        .exec();
      if (!pharmacy) {
        // Pharmacy does not exist in the database
        // Add it to the pharmaciesToRemove list
        pharmaciesToRemove.push(pharmacyDto.name);
      } else {
        // If it exists but has different coordinates, remove and add
        if (
          pharmacy.latitude !== pharmacyDto.latitude ||
          pharmacy.longitude !== pharmacyDto.longitude
        ) {
          pharmaciesToRemove.push(pharmacyDto.name);
          //create new pharmacy dto from pharmacy
          let newPharmacyDto = {
            name: pharmacy.name,
            address: pharmacy.address,
            latitude: pharmacy.latitude,
            longitude: pharmacy.longitude,
          } as PharmacyDto;

          pharmaciesToAdd.push(newPharmacyDto);
        }
      }
    }

    return {
      remove: pharmaciesToRemove,
      add: pharmaciesToAdd,
    };
  }


  async getPharmacySyncByVersion(knownVersionByClient: number, username : string) {
    // received number is the version number that the client has
    // need to check all the more recent versions and return the changes (additions and deletions)
    
    this.logger.log('Received request to get pharmacy sync by version, version number received: ' + knownVersionByClient.toString() + '.');
    console.log('Username ' + username)
    
    try {

      // first of all find the user's favorite pharmacies
      const user = await this.userService.findUser(username);
      let favoritePharmacies = user.favoritePharmacies;


      //get the current version (find greatest version number)
      let currentVersionObject = (
        await this.pharmacyVersionModel.findOne().sort({ version: -1 }).exec()
      );

      if(!currentVersionObject){
        // There are no pharmacies currently, return nothing
        return {
          version: 0,
          add: [],
          remove: [],
          favoritePharmacies : favoritePharmacies,
        };
      }

      let currentVersion = currentVersionObject.version;

      if (!currentVersion) {
        return {
          version: 0,
          add: [],
          remove: [],
          favoritePharmacies : favoritePharmacies,
        };
      }

      //if the version is the same as the client's version, return empty lists
      if (currentVersion === knownVersionByClient) {
        return {
          version: currentVersion,
          add: [],
          remove: [],
          favorite: favoritePharmacies,
        };
      }

      //if the client's version is greater than the current version, return error
      if (knownVersionByClient > currentVersion) {
        throw new HttpException(
          'Client version is greater than the current version',
          HttpStatus.BAD_REQUEST,
        );
      }

      //get all versions greater than the client's version
      const versions = await this.pharmacyVersionModel
        .find({ version: { $gt: knownVersionByClient } })
        .exec();

      //create lists of pharmacies to add and remove
      let pharmaciesToAdd: PharmacyDto[] = [];
      let pharmaciesToRemove: string[] = [];

      for (let version of versions) {
        if (version.wasAdded) {
          //pharmacy was added
          //get the pharmacy from the database
          const pharmacy = await this.pharmacyModel
            .findOne({ name: version.pharmacyName })
            .exec();

          if (pharmacy) {
            //create new pharmacy dto from pharmacy
            let newPharmacyDto = {
              name: pharmacy.name,
              address: pharmacy.address,
              latitude: pharmacy.latitude,
              longitude: pharmacy.longitude,
            } as PharmacyDto;

            pharmaciesToAdd.push(newPharmacyDto);
          }
        } else {
          //pharmacy was removed
          pharmaciesToRemove.push(version.pharmacyName);
        }
      }


      return {
        version: currentVersion,
        add: pharmaciesToAdd,
        remove: pharmaciesToRemove,
        favorite: favoritePharmacies,
      };
    } catch (error) {
      this.logger.log('Error while getting pharmacy sync by version: ' + error.message);
      
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  async getPharmacyPhoto(pharmacyName: string) {
    this.logger.log('Received request to get pharmacy photo for ' + pharmacyName);

    try {
      const pharmacy = await this.pharmacyModel
        .findOne({ name: pharmacyName })
        .exec();

      if (!pharmacy) {
        throw new HttpException(
          'Pharmacy not found',
          HttpStatus.NOT_FOUND,
        );
      }

      if (!pharmacy.photoPath) {
        throw new HttpException(
          'Photo not found',
          HttpStatus.NOT_FOUND,
        );
      }

      // Read the photo file
      const fs = require('fs');
      const photoData = fs.readFileSync(pharmacy.photoPath);

      return photoData;
    } catch (error) {
      this.logger.log('Error while getting pharmacy photo: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  async addQuantityOfMedicineToPharmacy(pharmacyName : string, medicineName : string, quantity : number){
    this.logger.log('Received request to add quantity of medicine ' + medicineName + ' to pharmacy ' + pharmacyName + '.');

    try {
      const pharmacy = await this.pharmacyModel
        .findOne({ name: pharmacyName })
        .exec();

      if (!pharmacy) {
        throw new HttpException(
          'Pharmacy not found',
          HttpStatus.NOT_FOUND,
        );
      }

      //check if the medicine already exists in the pharmacy
      let medicineAmount = pharmacy.medicines.find(medicineAmount => medicineAmount.medicineName === medicineName);

      if(medicineAmount){
        //medicine already exists, add the quantity
        medicineAmount.quantity += quantity;
      } else {
        //medicine does not exist, create new medicine amount
        pharmacy.medicines.push({
          medicineName: medicineName,
          quantity: quantity
        });
      }

      await pharmacy.save();
    } catch (error) {
      this.logger.log('Error while adding quantity of medicine to pharmacy: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  async getPharmacyByName(pharmacyName: string) {
    this.logger.log('Received request to get pharmacy by name: ' + pharmacyName);

    try {
      const pharmacy = await this.pharmacyModel
        .findOne({ name: pharmacyName })
        .exec();

      if (!pharmacy) {
        throw new HttpException(
          'Pharmacy not found',
          HttpStatus.NOT_FOUND,
        );
      }

      return {
        name : pharmacy.name,
        address : pharmacy.address,
        latitude : pharmacy.latitude,
        longitude : pharmacy.longitude,
        isFavorite : true,
      };
    } catch (error) {
      this.logger.log('Error while getting pharmacy by name: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
