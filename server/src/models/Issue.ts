import mongoose, { Schema, Document, Model } from 'mongoose';
import { IssueStatus, IssuePriority } from '../types/roles.js';

export interface IIssue extends Document {
  title: string;
  description: string;
  site: mongoose.Types.ObjectId;
  reportedBy: mongoose.Types.ObjectId;
  assignedTo?: mongoose.Types.ObjectId;
  status: IssueStatus;
  priority: IssuePriority;
  category: 'SAFETY' | 'QUALITY' | 'SCHEDULE' | 'COST' | 'MATERIAL' | 'EQUIPMENT' | 'OTHER';
  location?: string;
  photos: string[];
  resolvedAt?: Date;
  resolution?: string;
  estimatedCost?: number;
  actualCost?: number;
  tags: string[];
}

const IssueSchema = new Schema<IIssue>(
  {
    title: { type: String, required: true },
    description: { type: String, required: true },
    site: { type: Schema.Types.ObjectId, ref: 'Site', required: true },
    reportedBy: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    assignedTo: { type: Schema.Types.ObjectId, ref: 'User' },
    status: { type: String, enum: ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'], default: 'OPEN' },
    priority: { type: String, enum: ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'], default: 'MEDIUM' },
    category: { type: String, enum: ['SAFETY', 'QUALITY', 'SCHEDULE', 'COST', 'MATERIAL', 'EQUIPMENT', 'OTHER'], required: true },
    location: { type: String },
    photos: { type: [String], default: [] },
    resolvedAt: { type: Date },
    resolution: { type: String },
    estimatedCost: { type: Number },
    actualCost: { type: Number },
    tags: { type: [String], default: [] }
  },
  { timestamps: true }
);

export const Issue: Model<IIssue> = mongoose.model<IIssue>('Issue', IssueSchema);

