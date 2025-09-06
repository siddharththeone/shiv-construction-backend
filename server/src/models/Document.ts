import mongoose, { Schema, Document as MongoDocument, Model } from 'mongoose';

export interface IDocument extends MongoDocument {
  title: string;
  description?: string;
  fileName: string;
  filePath: string;
  fileSize: number;
  mimeType: string;
  site?: mongoose.Types.ObjectId;
  uploadedBy: mongoose.Types.ObjectId;
  category: 'PLAN' | 'PERMIT' | 'CONTRACT' | 'INVOICE' | 'REPORT' | 'PHOTO' | 'OTHER';
  tags: string[];
  isPublic: boolean;
  downloadCount: number;
  lastDownloadedAt?: Date;
}

const DocumentSchema = new Schema<IDocument>(
  {
    title: { type: String, required: true },
    description: { type: String },
    fileName: { type: String, required: true },
    filePath: { type: String, required: true },
    fileSize: { type: Number, required: true },
    mimeType: { type: String, required: true },
    site: { type: Schema.Types.ObjectId, ref: 'Site' },
    uploadedBy: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    category: { type: String, enum: ['PLAN', 'PERMIT', 'CONTRACT', 'INVOICE', 'REPORT', 'PHOTO', 'OTHER'], default: 'OTHER' },
    tags: { type: [String], default: [] },
    isPublic: { type: Boolean, default: false },
    downloadCount: { type: Number, default: 0 },
    lastDownloadedAt: { type: Date }
  },
  { timestamps: true }
);

export const Document: Model<IDocument> = mongoose.model<IDocument>('Document', DocumentSchema);

