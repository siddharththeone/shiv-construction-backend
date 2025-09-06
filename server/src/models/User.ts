import mongoose, { Schema, Document, Model } from 'mongoose';
import { UserRole, ContractorType } from '../types/roles.js';

export interface IUser extends Document {
  name: string;
  email?: string;
  phone?: string;
  passwordHash?: string;
  role: UserRole;
  contractorType?: ContractorType;
  invitedBy?: mongoose.Types.ObjectId;
  company?: mongoose.Types.ObjectId;
  fcmTokens: string[];
  profileImage?: string;
  address?: string;
  companyName?: string;
  isActive: boolean;
  lastLoginAt?: Date;
}

const UserSchema = new Schema<IUser>(
  {
    name: { type: String, required: true },
    email: { type: String, index: true, unique: true, sparse: true },
    phone: { type: String, index: true, unique: true, sparse: true },
    passwordHash: { type: String },
    role: { type: String, enum: ['OWNER', 'CONTRACTOR', 'SUPPLIER'], required: true },
    contractorType: { type: String, enum: ['ELECTRICAL', 'PLUMBING', 'CARPENTRY', 'MASONRY', 'PAINTING', 'ROOFING', 'HVAC', 'LANDSCAPING', 'GENERAL'] },
    invitedBy: { type: Schema.Types.ObjectId, ref: 'User' },
    company: { type: Schema.Types.ObjectId, ref: 'Company' },
    fcmTokens: { type: [String], default: [] },
    profileImage: { type: String },
    address: { type: String },
    companyName: { type: String },
    isActive: { type: Boolean, default: true },
    lastLoginAt: { type: Date }
  },
  { timestamps: true }
);

export const User: Model<IUser> = mongoose.model<IUser>('User', UserSchema);


