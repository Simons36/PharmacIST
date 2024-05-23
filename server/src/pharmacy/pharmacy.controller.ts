import { Body, Controller, HttpCode, Post, UploadedFile, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { PharmacyDto } from './dto/pharmacy.dto';
import { PharmacyService } from './pharmacy.service';
import { Express } from 'express'; // Use Express namespace for types

@Controller('pharmacy')
export class PharmacyController {
  constructor(private pharmacyService: PharmacyService) {}

  @Post('add')
  @HttpCode(201)
  @UseInterceptors(FileInterceptor('photo')) // This handles single photo upload
  async addNewPharmacy(
    @UploadedFile() photo: Express.Multer.File | undefined, // File can be undefined
    @Body() pharmacyDto: PharmacyDto,
  ) {
    console.log(pharmacyDto);
    // Process the file and the other form data here
    return await this.pharmacyService.addNewPharmacy(pharmacyDto, photo);
  }
}
