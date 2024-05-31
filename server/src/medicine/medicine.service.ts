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
    this.logger.log('Received request to add new medicine with name ' + medicineDto.name + '.');

    if (photo) {
      const photoPath = this.configService.photosPath;

      const fs = require('fs');
      if (!fs.existsSync(photoPath)) {
        fs.mkdirSync(photoPath);
      }

      try {
        const photoFilename =
          medicineDto.name + '.' + photo.mimetype.split('/')[1];

        fs.writeFileSync(`${photoPath}/${photoFilename}`, photo.buffer);

        medicineDto = {
          ...medicineDto,
        };
      } catch (error) {
        this.logger.error('Error while saving photo: ' + error.message);
        throw new HttpException(
          'Error while saving photo: ' + error.message,
          HttpStatus.INTERNAL_SERVER_ERROR,
        );
      }
    }

    try {
      const newMedicine = new this.pharmacyModel(medicineDto);
      await newMedicine.save();
      this.logger.log('New medicine with name ' + medicineDto.name + ' added successfully.'); // Log success
    } catch (error) {
      this.logger.error('Error adding new medicine: ' + error.message);

      if (error.code === 11000 || error.code === 11001) {
        throw new HttpException(
          `Medicine name '${medicineDto.name}' is already in use.`,
          HttpStatus.CONFLICT,
        );
      }

      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  async getMedicineByName(name: string): Promise<MedicineDto | null> {
    try {
      const medicine = await this.pharmacyModel.findOne({ name }).select("name -_id").exec();
      return medicine ? this.mapToDto(medicine) : null;
    } catch (error) {
      this.logger.error('Error getting medicine by name: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  async getAllMedicines(): Promise<MedicineDto[]> {
    try {
      const medicines = await this.pharmacyModel.find().select("name -_id").exec();
      const medicineDtos = medicines.map(medicine => this.mapToDto(medicine));
      this.logger.log(`Retrieved ${medicineDtos.length} medicines successfully.`);
      return medicineDtos;
    } catch (error) {
      this.logger.error('Error getting all medicines: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  async searchMedicines(query: string): Promise<MedicineDto[]> {
    try {
      const regex = new RegExp(query, 'i');
      const medicines = await this.pharmacyModel.find({ name: { $regex: regex } }).select("name -_id").exec();
      const medicineDtos = medicines.map(medicine => this.mapToDto(medicine));
      this.logger.log(`Searched for medicines successfully. Found ${medicineDtos.length} matches.`);
      return medicineDtos;
    } catch (error) {
      this.logger.error('Error searching for medicines: ' + error.message);
      throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    } 
  }

  async getMedicinePhoto(name: string): Promise<Buffer | null> {
    try {
        const medicine = await this.pharmacyModel.findOne({ name }).select("photoPath -_id").exec();

        
        if (!medicine) {
          throw new HttpException(
            'Medicine not found',
            HttpStatus.NOT_FOUND,
          );
        }

        if (!medicine.photoPath) {
          throw new HttpException(
            'Medicine does not have a photo',
            HttpStatus.NOT_FOUND,
          );
        }
        
        // Read the photo file
        const fs = require('fs');
        const photoData = fs.readFileSync(medicine.photoPath);
        
        return photoData;
    } catch (error) {
        this.logger.log('Error while getting medicine photo: ' + error.message);
        throw new HttpException(error.message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  private mapToDto(medicine: MedicineDocument): MedicineDto {
    return {
        name: medicine.name,
        photoPath: medicine.photoPath // Assuming 'photoPath' is a property of your 'Medicine' model
    };
  }
}
