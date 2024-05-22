import { Body, Controller, HttpCode, Post } from '@nestjs/common';
import { PharmacyDto } from './dto/pharmacy.dto';
import { PharmacyService } from './pharmacy.service';

@Controller('pharmacy')
export class PharmacyController {

    constructor(private pharmacyService: PharmacyService) { }

    @Post('add')
    @HttpCode(201)
    async addNewPharmacy(@Body() pharmacyDto: PharmacyDto) {

        return await this.pharmacyService.addNewPharmacy(pharmacyDto);
    }

}
