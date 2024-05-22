import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';

export type PharmacyDocument = Pharmacy & Document;

@Schema()
export class Pharmacy {

    @Prop({required: true, unique: true})
    name: string;

    @Prop()
    address: string;

    @Prop({required: true})
    latitude: number;

    @Prop({required: true})
    longitude: number;

    @Prop()
    photo: Buffer;

    @Prop()
    photoExtension: string;
}


export const PharmacySchema = SchemaFactory.createForClass(Pharmacy);

// Ensure unique indexes for name
PharmacySchema.index({ name: 1 }, { unique: true });