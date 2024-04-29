import { Injectable } from "@nestjs/common";
import { User } from "./schemas/user.schema";
import { Model } from "mongoose";
import { InjectModel } from "@nestjs/mongoose";
import { CreateUserDto } from "./dto/create-user.dto";


@Injectable()
export class UserService {

    constructor(@InjectModel(User.name) private userModel: Model<User>) {}
    

    async getUserByEmail(email: string): Promise<User | null> {
        return this.userModel.findOne({email}).exec();
    }

    createUser(createUserDto : CreateUserDto): Promise<User> {
        const newUser = new this.userModel(createUserDto);
        return newUser.save();
    }
}