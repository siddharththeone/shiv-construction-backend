import mongoose, { Schema, Document, Model } from 'mongoose';

export interface INotification extends Document {
  title: string;
  message: string;
  recipient: mongoose.Types.ObjectId;
  sender?: mongoose.Types.ObjectId;
  type: 'SITE_UPDATE' | 'MATERIAL_REQUEST' | 'PAYMENT' | 'TASK_ASSIGNMENT' | 'ISSUE_REPORT' | 'GENERAL' | 'SYSTEM';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  isRead: boolean;
  readAt?: Date;
  data?: Record<string, any>;
  site?: mongoose.Types.ObjectId;
  actionUrl?: string;
  expiresAt?: Date;
}

const NotificationSchema = new Schema<INotification>(
  {
    title: { type: String, required: true },
    message: { type: String, required: true },
    recipient: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    sender: { type: Schema.Types.ObjectId, ref: 'User' },
    type: { type: String, enum: ['SITE_UPDATE', 'MATERIAL_REQUEST', 'PAYMENT', 'TASK_ASSIGNMENT', 'ISSUE_REPORT', 'GENERAL', 'SYSTEM'], required: true },
    priority: { type: String, enum: ['LOW', 'MEDIUM', 'HIGH', 'URGENT'], default: 'MEDIUM' },
    isRead: { type: Boolean, default: false },
    readAt: { type: Date },
    data: { type: Schema.Types.Mixed },
    site: { type: Schema.Types.ObjectId, ref: 'Site' },
    actionUrl: { type: String },
    expiresAt: { type: Date }
  },
  { timestamps: true }
);

// Index for efficient querying
NotificationSchema.index({ recipient: 1, isRead: 1, createdAt: -1 });
NotificationSchema.index({ expiresAt: 1 }, { expireAfterSeconds: 0 });

export const Notification: Model<INotification> = mongoose.model<INotification>('Notification', NotificationSchema);


