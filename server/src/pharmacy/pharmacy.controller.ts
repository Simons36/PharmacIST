import {
  Body,
  Controller,
  Get,
  HttpCode,
  Param,
  Post,
  Req,
  UploadedFile,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { PharmacyDto } from './dto/pharmacy.dto';
import { PharmacyService } from './pharmacy.service';
import { Express } from 'express'; // Use Express namespace for types
import { AuthGuard } from '@nestjs/passport';

interface AuthenticatedRequest extends Request {
  user: { username: string };
}


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

  @Get('all')
  async getAllPharmacies() {
    return await this.pharmacyService.getAllPharmacies();
  }

  @Get('get-update')
  async getUpdatedPharmaciesInfo(@Body() pharmaciesDto: PharmacyDto[]) {
    return await this.pharmacyService.getUpdatedPharmaciesInfo(pharmaciesDto);
  }

  @UseGuards(AuthGuard('jwt'))
  @Get('sync/version/:version')
  async getPharmaciesByVersion(@Param('version') version: number, @Req() req : AuthenticatedRequest) {
    let val =  await this.pharmacyService.getPharmacySyncByVersion(version, req.user.username);
    console.log(val);
    return val;
  }

  @Get('photo/:pharmacyName')
  async getPharmacyPhoto(@Param('pharmacyName') pharmacyName: string) {
    return await this.pharmacyService.getPharmacyPhoto(pharmacyName);
  }
}
