import { Prop, Schema, SchemaFactory } from "@nestjs/mongoose";

export type PharmacyVersionDocument = PharmacyVersion & Document;

@Schema()
export class PharmacyVersion{
    @Prop({unique : true, required: true })
    version: number;

    @Prop({required: true})
    pharmacyName : string;

    @Prop({required: true})
    wasAdded : boolean;
}

export const PharmacyVersionSchema = SchemaFactory.createForClass(PharmacyVersion);

// Ensure unique indexes for version
PharmacyVersionSchema.index({ version: 1 }, { unique: true });