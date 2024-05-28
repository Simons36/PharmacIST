import { Controller, Get, Post, Param, HttpCode, UploadedFile, Body, UseInterceptors } from '@nestjs/common';
import { MedicineService } from './medicine.service';
import { MedicineDto } from './dto/medicine.dto';
import { FileInterceptor } from '@nestjs/platform-express';

@Controller('medicine')
export class MedicineController {

    constructor(private medicineService: MedicineService) {}

    @Post('add')
    @HttpCode(201)
    @UseInterceptors(FileInterceptor('photo'))
    addMedicine(@Body() medicineDto : MedicineDto, @UploadedFile() photo : Express.Multer.File | undefined) {
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

}