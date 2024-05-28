import { Module } from '@nestjs/common';
import { MedicineController } from './medicine.controller';
import { MedicineService } from './medicine.service';
import { AppConfigModule } from 'src/config/app-config.module';
import { DatabaseModule } from 'src/database/database.module';
import { MongooseModule } from '@nestjs/mongoose';
import { Medicine, MedicineSchema } from './schemas/medicine.schema';

@Module({
  imports: [AppConfigModule, DatabaseModule, MongooseModule.forFeature([{ name: Medicine.name, schema: MedicineSchema }])],
  controllers: [MedicineController],
  providers: [MedicineService]
})
export class MedicineModule {}
