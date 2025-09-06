import mongoose, { Schema, Document, Model } from 'mongoose';
import { TaskStatus } from '../types/roles.js';

export interface ITask extends Document {
  title: string;
  description?: string;
  site: mongoose.Types.ObjectId;
  assignedTo: mongoose.Types.ObjectId;
  assignedBy: mongoose.Types.ObjectId;
  status: TaskStatus;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  dueDate?: Date;
  completedAt?: Date;
  estimatedHours?: number;
  actualHours?: number;
  photos: string[];
  notes: string[];
  dependencies: mongoose.Types.ObjectId[];
  tags: string[];
}

const TaskSchema = new Schema<ITask>(
  {
    title: { type: String, required: true },
    description: { type: String },
    site: { type: Schema.Types.ObjectId, ref: 'Site', required: true },
    assignedTo: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    assignedBy: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    status: { type: String, enum: ['TODO', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'], default: 'TODO' },
    priority: { type: String, enum: ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'], default: 'MEDIUM' },
    dueDate: { type: Date },
    completedAt: { type: Date },
    estimatedHours: { type: Number },
    actualHours: { type: Number },
    photos: { type: [String], default: [] },
    notes: { type: [String], default: [] },
    dependencies: [{ type: Schema.Types.ObjectId, ref: 'Task' }],
    tags: { type: [String], default: [] }
  },
  { timestamps: true }
);

export const Task: Model<ITask> = mongoose.model<ITask>('Task', TaskSchema);

