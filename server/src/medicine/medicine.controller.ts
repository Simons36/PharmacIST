import { Controller, Get, Post, Param, HttpCode, UploadedFile, Body, UseInterceptors, Query } from '@nestjs/common';
import { MedicineService } from './medicine.service';
import { MedicineDto } from './dto/medicine.dto';
import { FileInterceptor } from '@nestjs/platform-express';
import { AddMedicineDto } from './dto/add-medicine.dto';

@Controller('medicine')
export class MedicineController {

    constructor(private medicineService: MedicineService) {}

    @Post('add')
    @HttpCode(201)
    @UseInterceptors(FileInterceptor('photo'))
    addMedicine(@Body() medicineDto : AddMedicineDto, @UploadedFile() photo : Express.Multer.File | undefined) {
        return this.medicineService.addMedicine(medicineDto, photo);
    }

    //TODO: Return photo in response
    @Get('get/:name')
    getMedicine(@Param('name') id: string) {
        return this.medicineService.getMedicineByName(id);
    }

    //TODO: Return photo in response
    @Get('all')
    getAllMedicines() {
        return this.medicineService.getAllMedicines();
    }

    @Get('search')
    searchMedicines(@Query('query') query: string) {
        return this.medicineService.searchMedicines(query);
    }

    @Get('photo/:medicineName')
    async getMedicinePhoto(@Param('medicineName') medicineName: string) {
        return await this.medicineService.getMedicinePhoto(medicineName);
    }

}
