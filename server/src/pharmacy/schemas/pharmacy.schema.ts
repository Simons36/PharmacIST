import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Types } from 'mongoose';

export type PharmacyDocument = Pharmacy & Document;


@Schema()
export class MedicineAmount {
  @Prop({ required: true, unique: true})
  medicineName: string;

  @Prop({ required: true })
  quantity: number;
}

export const MedicineAmountSchema = SchemaFactory.createForClass(MedicineAmount);

@Schema()
export class Pharmacy {
  @Prop({ required: true, unique: true })
  name: string;

  @Prop()
  address: string;

  @Prop({ required: true })
  latitude: number;

  @Prop({ required: true })
  longitude: number;

  @Prop()
  photoPath: string;

  @Prop({ type: [MedicineAmountSchema], default: [] })
  medicines: Types.DocumentArray<MedicineAmount>;
}

export const PharmacySchema = SchemaFactory.createForClass(Pharmacy);

// Ensure unique indexes for name
PharmacySchema.index({ name: 1 }, { unique: true });
