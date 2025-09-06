import mongoose, { Schema, Document, Model } from 'mongoose';
import { PaymentStatus } from '../types/roles.js';

export interface IPayment extends Document {
  site: mongoose.Types.ObjectId;
  amount: number;
  date: Date;
  fromUser: mongoose.Types.ObjectId; // payer
  toUser: mongoose.Types.ObjectId; // recipient
  status: PaymentStatus;
  type: 'ADVANCE' | 'PROGRESS' | 'FINAL' | 'MATERIAL' | 'OTHER';
  reference?: string;
  note?: string;
  approvedBy?: mongoose.Types.ObjectId;
  approvedAt?: Date;
  paymentMethod?: 'CASH' | 'BANK_TRANSFER' | 'CHEQUE' | 'CARD' | 'UPI' | 'OTHER';
  transactionId?: string;
  invoiceNumber?: string;
  dueDate?: Date;
  paidAt?: Date;
  attachments: string[];
}

const PaymentSchema = new Schema<IPayment>(
  {
    site: { type: Schema.Types.ObjectId, ref: 'Site', required: true },
    amount: { type: Number, required: true },
    date: { type: Date, default: () => new Date() },
    fromUser: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    toUser: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    status: { type: String, enum: ['PENDING', 'APPROVED', 'PAID', 'CANCELLED'], default: 'PENDING' },
    type: { type: String, enum: ['ADVANCE', 'PROGRESS', 'FINAL', 'MATERIAL', 'OTHER'], required: true },
    reference: { type: String },
    note: { type: String },
    approvedBy: { type: Schema.Types.ObjectId, ref: 'User' },
    approvedAt: { type: Date },
    paymentMethod: { type: String, enum: ['CASH', 'BANK_TRANSFER', 'CHEQUE', 'CARD', 'UPI', 'OTHER'] },
    transactionId: { type: String },
    invoiceNumber: { type: String },
    dueDate: { type: Date },
    paidAt: { type: Date },
    attachments: { type: [String], default: [] }
  },
  { timestamps: true }
);

export const Payment: Model<IPayment> = mongoose.model<IPayment>('Payment', PaymentSchema);


