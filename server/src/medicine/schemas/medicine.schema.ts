import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';

export type MedicineDocument = Medicine & Document;

@Schema()
export class Medicine {
  @Prop({ required: true, unique: true })
  name: string;

  @Prop({ unique: true })
  barcode: string;

  @Prop({ required: true })
  purpose: string;

  @Prop()
  photoPath: string;
}

export const MedicineSchema = SchemaFactory.createForClass(Medicine);

// Ensure unique indexes for name
MedicineSchema.index({ name: 1 }, { unique: true });
