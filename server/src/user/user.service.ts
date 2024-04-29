import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { User, UserDocument } from './schemas/user.schema';
import { CreateUserDto } from './dto/create-user.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectModel(User.name) private readonly userModel: Model<UserDocument>,
  ) {}

  async createUser(createUserDto: CreateUserDto): Promise<User> {
    try {
      const newUser = new this.userModel(createUserDto);
      return await newUser.save();
    } catch (error) {
      if (error.code === 11000 || error.code === 11001) {
        // Duplicate key error (MongoDB error code for duplicate key)
        if (error.keyPattern.email) {
          throw new HttpException(
            `'${createUserDto.email}' is already in use.`,
            HttpStatus.BAD_REQUEST,
          );
        } else if (error.keyPattern.username) {
          throw new HttpException(
            `'${createUserDto.username}' is already in use.`,
            HttpStatus.BAD_REQUEST,
          );
        } else {
          throw new Error('Duplicate key error');
        }
      }
      throw error; // Rethrow other errors
    }
  }
}
