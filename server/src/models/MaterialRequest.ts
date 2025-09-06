import mongoose, { Schema, Document, Model } from 'mongoose';
import { MaterialStatus } from '../types/roles.js';

export interface IMaterialRequest extends Document {
  site: mongoose.Types.ObjectId;
  requestedBy: mongoose.Types.ObjectId; // contractor
  supplier: mongoose.Types.ObjectId;
  items: Array<{ 
    name: string; 
    quantity: number; 
    unit?: string;
    price?: number;
    deliveredQuantity?: number;
    notes?: string;
  }>;
  status: MaterialStatus;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  requestedDate: Date;
  expectedDeliveryDate?: Date;
  actualDeliveryDate?: Date;
  totalAmount?: number;
  notes?: string;
  attachments: string[];
  approvedBy?: mongoose.Types.ObjectId;
  approvedAt?: Date;
  rejectionReason?: string;
}

const MaterialRequestSchema = new Schema<IMaterialRequest>(
  {
    site: { type: Schema.Types.ObjectId, ref: 'Site', required: true },
    requestedBy: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    supplier: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    items: [
      {
        name: { type: String, required: true },
        quantity: { type: Number, required: true },
        unit: { type: String },
        price: { type: Number },
        deliveredQuantity: { type: Number, default: 0 },
        notes: { type: String }
      }
    ],
    status: { type: String, enum: ['PENDING', 'APPROVED', 'REJECTED', 'DELIVERED', 'CANCELLED'], default: 'PENDING' },
    priority: { type: String, enum: ['LOW', 'MEDIUM', 'HIGH', 'URGENT'], default: 'MEDIUM' },
    requestedDate: { type: Date, default: Date.now },
    expectedDeliveryDate: { type: Date },
    actualDeliveryDate: { type: Date },
    totalAmount: { type: Number },
    notes: { type: String },
    attachments: { type: [String], default: [] },
    approvedBy: { type: Schema.Types.ObjectId, ref: 'User' },
    approvedAt: { type: Date },
    rejectionReason: { type: String }
  },
  { timestamps: true }
);

export const MaterialRequest: Model<IMaterialRequest> = mongoose.model<IMaterialRequest>('MaterialRequest', MaterialRequestSchema);


