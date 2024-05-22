import { HttpException, HttpStatus, Injectable, Logger } from '@nestjs/common';
import { PharmacyDto } from './dto/pharmacy.dto';
import { getAddressFromCoordinates } from './util/pharmacy.util';
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
  async addNewPharmacy(pharmacyDto: PharmacyDto) {

    // Log the request
    this.logger.log(
      'Received request to add new pharmacy with name ' + pharmacyDto.name + '.',
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
    }

    // Now save the pharmacy to the database
    try {
      const newPharmacy = new this.pharmacyModel(pharmacyDto);
      await newPharmacy.save();
    } catch (error) {
      if (error.code === 11000 || error.code === 11001) {
        throw new HttpException(
          `Pharmacy name '${pharmacyDto.name}' is already in use.`,
          HttpStatus.BAD_REQUEST,
        );
      }

      this.logger.log('Error while adding new pharmacy: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    this.logger.log('New pharmacy added successfully');
  }
}
