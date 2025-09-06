import mongoose, { Schema, Document, Model } from 'mongoose';

export type TransactionType = 'PAYMENT' | 'MATERIAL_REQUEST' | 'DELIVERY_UPDATE' | 'STATUS_UPDATE';

export interface ITransaction extends Document {
  site: mongoose.Types.ObjectId;
  type: TransactionType;
  actor: mongoose.Types.ObjectId; // user who performed action
  details: Record<string, unknown>;
}

const TransactionSchema = new Schema<ITransaction>(
  {
    site: { type: Schema.Types.ObjectId, ref: 'Site', required: true },
    type: { type: String, enum: ['PAYMENT', 'MATERIAL_REQUEST', 'DELIVERY_UPDATE', 'STATUS_UPDATE'], required: true },
    actor: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    details: { type: Schema.Types.Mixed, default: {} }
  },
  { timestamps: true }
);

export const Transaction: Model<ITransaction> = mongoose.model<ITransaction>('Transaction', TransactionSchema);


