import mongoose, { Schema, Document, Model } from 'mongoose';
import { SiteStatus } from '../types/roles.js';

export interface ISite extends Document {
  name: string;
  location?: string;
  address?: string;
  startDate?: Date;
  expectedEndDate?: Date;
  actualEndDate?: Date;
  status: SiteStatus;
  progressPercent: number;
  description?: string;
  budget?: number;
  actualCost?: number;
  company: mongoose.Types.ObjectId;
  owner: mongoose.Types.ObjectId;
  contractors: mongoose.Types.ObjectId[];
  suppliers: mongoose.Types.ObjectId[];
  photos: string[];
  documents: string[];
  latitude?: number;
  longitude?: number;
  siteArea?: number;
  buildingType?: string;
  floors?: number;
}

const SiteSchema = new Schema<ISite>(
  {
    name: { type: String, required: true },
    location: { type: String },
    address: { type: String },
    startDate: { type: Date },
    expectedEndDate: { type: Date },
    actualEndDate: { type: Date },
    status: { type: String, enum: ['NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD'], default: 'NOT_STARTED' },
    progressPercent: { type: Number, default: 0, min: 0, max: 100 },
    description: { type: String },
    budget: { type: Number },
    actualCost: { type: Number },
    company: { type: Schema.Types.ObjectId, ref: 'Company', required: true },
    owner: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    contractors: [{ type: Schema.Types.ObjectId, ref: 'User' }],
    suppliers: [{ type: Schema.Types.ObjectId, ref: 'User' }],
    photos: { type: [String], default: [] },
    documents: { type: [String], default: [] },
    latitude: { type: Number },
    longitude: { type: Number },
    siteArea: { type: Number },
    buildingType: { type: String },
    floors: { type: Number }
  },
  { timestamps: true }
);

export const Site: Model<ISite> = mongoose.model<ISite>('Site', SiteSchema);


