import dotenv from 'dotenv';
dotenv.config();

import express from 'express';
import cors from 'cors';
import morgan from 'morgan';
import mongoose from 'mongoose';
import path from 'path';

import { authRouter } from './routes/auth.js';
import { siteRouter } from './routes/sites.js';
import { companyRouter } from './routes/companies.js';
import { materialsRouter } from './routes/materials.js';
import { paymentsRouter } from './routes/payments.js';
import { notificationsRouter } from './routes/notifications.js';
import { uploadsRouter } from './routes/uploads.js';
import { tasksRouter } from './routes/tasks.js';
import { issuesRouter } from './routes/issues.js';
import { documentsRouter } from './routes/documents.js';

const app = express();

app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(morgan('dev'));

const uploadDir = process.env.UPLOAD_DIR || 'uploads';
app.use('/uploads', express.static(path.resolve(uploadDir)));

app.get('/health', (_req, res) => {
  res.json({ ok: true });
});

app.get('/api', (_req, res) => {
  res.json({
    message: 'SHIV Construction Management API',
    version: '1.0.0',
    endpoints: {
      auth: '/api/auth',
      companies: '/api/companies',
      sites: '/api/sites',
      materials: '/api/materials',
      payments: '/api/payments',
      notifications: '/api/notifications',
      uploads: '/api/uploads',
      tasks: '/api/tasks',
      issues: '/api/issues',
      documents: '/api/documents'
    }
  });
});

app.use('/api/auth', authRouter);
app.use('/api/companies', companyRouter);
app.use('/api/sites', siteRouter);
app.use('/api/materials', materialsRouter);
app.use('/api/payments', paymentsRouter);
app.use('/api/notifications', notificationsRouter);
app.use('/api/uploads', uploadsRouter);
app.use('/api/tasks', tasksRouter);
app.use('/api/issues', issuesRouter);
app.use('/api/documents', documentsRouter);

const port = Number(process.env.PORT || 4000);
const mongoUri = process.env.MONGODB_URI || 'mongodb+srv://shiv-admin:ShivConstruction2024!@shiv-construction-clust.nkk7mb0.mongodb.net/?retryWrites=true&w=majority&appName=shiv-construction-cluster';

mongoose
  .connect(mongoUri)
  .then(() => {
    app.listen(port, () => {
      console.log(`🚀 SHIV Construction Management API running on http://localhost:${port}`);
      console.log(`📊 Health check: http://localhost:${port}/health`);
      console.log(`📚 API docs: http://localhost:${port}/api`);
      console.log(`🗄️  Database: ${mongoUri}`);
    });
  })
  .catch((err) => {
    console.error('❌ Failed to connect to MongoDB', err);
    process.exit(1);
  });


