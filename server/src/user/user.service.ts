import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { User, UserDocument } from './schemas/user.schema';
import { CreateUserDto } from './dto/create-user.dto';
import { UserDto } from './dto/user.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectModel(User.name) private readonly userModel: Model<UserDocument>,
  ) {}

  // called on register
  async createUser(createUserDto: CreateUserDto): Promise<void> {
    try {
      const newUser = new this.userModel(createUserDto);
      const savedUser = await newUser.save();

      const returnedDto: UserDto = {
        username: savedUser.username,
        email: savedUser.email,
        password: savedUser.password,
      };
    } catch (error) {
      if (error.code === 11000 || error.code === 11001) {
        // Duplicate key error (MongoDB error code for duplicate key)
        if (error.keyPattern.email) {
          throw new HttpException(
            {
              message: `'${createUserDto.email}' is already in use.`,
              field: 'email',
            },
            HttpStatus.CONFLICT,
          );
        } else if (error.keyPattern.username) {
          throw new HttpException(
            {
              message: `'${createUserDto.username}' is already in use.`,
              field: 'username',
            },
            HttpStatus.CONFLICT,
          );
        } else {
          throw new Error('Duplicate key error');
        }
      }
      throw error; // Rethrow other errors
    }
  }

  async findUser(email: string): Promise<UserDto> {
    try {
      // Find user by email
      const user = await this.userModel.findOne({ email }).exec();

      if (!user) {
        throw new HttpException('User not found', HttpStatus.NOT_FOUND);
      }

      // Construct and return UserDto
      const userDto: UserDto = {
        username: user.username,
        email: user.email,
        password: user.password,
        // Add other properties from user as needed
      };

      return userDto;
    } catch (error) {
      throw error; // Rethrow any errors
    }
  }
}
