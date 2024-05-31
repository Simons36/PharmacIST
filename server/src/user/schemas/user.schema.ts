import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';

export type UserDocument = User & Document;

@Schema()
export class User {
    @Prop({ required: true, unique: true })
    username: string;
    
    @Prop({ required: true, unique: true })
    email: string;
    
    @Prop({ required: true })
    password: string;

    @Prop({ type: [String], default: [] }) // Add this property
    favoritePharmacies: string[];
}

export const UserSchema = SchemaFactory.createForClass(User);

// Ensure unique indexes for email and username
UserSchema.index({ email: 1 }, { unique: true });
UserSchema.index({ username: 1 }, { unique: true });
