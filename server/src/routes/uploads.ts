import { Router } from 'express';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import { requireAuth, requireRole } from '../middleware/auth.js';
import { Site } from '../models/Site.js';

const uploadDir = process.env.UPLOAD_DIR || 'uploads';
fs.mkdirSync(uploadDir, { recursive: true });

const storage = multer.diskStorage({
  destination: (_req, _file, cb) => cb(null, uploadDir),
  filename: (_req, file, cb) => {
    const ext = path.extname(file.originalname);
    const base = path.basename(file.originalname, ext).replace(/[^a-zA-Z0-9_-]/g, '_');
    cb(null, `${Date.now()}_${base}${ext}`);
  }
});

const upload = multer({ storage });

export const uploadsRouter = Router();

// Contractor uploads site progress photo
uploadsRouter.post('/site/:id/photo', requireAuth, requireRole(['CONTRACTOR']), upload.single('photo'), async (req, res) => {
  const site = await Site.findOne({ _id: req.params.id, contractors: req.auth!.userId });
  if (!site) return res.status(404).json({ error: 'Site not found' });
  const relPath = `/uploads/${req.file!.filename}`;
  site.photos.push(relPath);
  await site.save();
  res.json({ url: relPath });
});


