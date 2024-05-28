import { HttpException, HttpStatus, Injectable, Logger } from '@nestjs/common';
import { Model } from 'mongoose';
import { AppConfigService } from 'src/config/app-config.service';
import { Medicine, MedicineDocument } from './schemas/medicine.schema';
import { InjectModel } from '@nestjs/mongoose';
import { MedicineDto } from './dto/medicine.dto';

@Injectable()
export class MedicineService {
  constructor(
    private configService: AppConfigService,
    @InjectModel(Medicine.name)
    private readonly pharmacyModel: Model<MedicineDocument>,
  ) {}

  private readonly logger = new Logger(MedicineService.name);

  async addMedicine(
    medicineDto: MedicineDto,
    photo: Express.Multer.File | undefined,
  ): Promise<void> {
    //if photo is not undefined

    this.logger.log('Received request to add new medicine with name ' + medicineDto.name + '.');

    if (photo) {
      const photoPath = this.configService.photosPath;

      //Create path if it does not exist
      const fs = require('fs');
      if (!fs.existsSync(photoPath)) {
        fs.mkdirSync(photoPath);
      }

      try {
        const photoFilename =
          medicineDto.name + '.' + photo.mimetype.split('/')[1];

        // Write the photo data to a file
        fs.writeFileSync(`${photoPath}/${photoFilename}`, photo.buffer);

        medicineDto = {
          ...medicineDto,
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

    // Save the pharmacy to the database
    try {
      const newMedicine = new this.pharmacyModel(medicineDto);
      await newMedicine.save();
      this.logger.log('New medicine with name ' + medicineDto.name + ' added successfully.');
    } catch (error) {
      this.logger.error('Error adding new medicine:' + error.message);

      // Check if the error is due to duplicate key
      if (error.code === 11000 || error.code === 11001) {
        throw new HttpException(
          `Medicine name '${medicineDto.name}' is already in use.`,
          HttpStatus.CONFLICT,
        );
      }

      // Else, throw error
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  async getMedicineByName(name: string) {
    return await this.pharmacyModel.findOne({ name: name }).select("name -_id").exec();
  }

  async getAllMedicines() {
    return await this.pharmacyModel.find().select("name -_id").exec();
  }
}
