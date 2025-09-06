import { Router } from 'express';
import { Document } from '../models/Document.js';
import { Site } from '../models/Site.js';
import { User } from '../models/User.js';
import { requireAuth, requireRole } from '../middleware/auth.js';
import { UserRole } from '../types/roles.js';

export const documentsRouter = Router();

// Get all documents
documentsRouter.get('/', requireAuth, async (req, res) => {
  try {
    const { siteId, category, uploadedBy, isPublic } = req.query;
    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    let query: any = {};

    // Filter by site if provided
    if (siteId) {
      query.site = siteId;
    }

    // Filter by category if provided
    if (category) {
      query.category = category;
    }

    // Filter by uploaded by if provided
    if (uploadedBy) {
      query.uploadedBy = uploadedBy;
    }

    // Filter by public status if provided
    if (isPublic !== undefined) {
      query.isPublic = isPublic === 'true';
    }

    // Role-based filtering
    if (userRole === 'CONTRACTOR') {
      // Contractors can see documents from sites they work on or documents they uploaded
      const userSites = await Site.find({ contractors: userId }).select('_id');
      query.$or = [
        { site: { $in: userSites.map(site => site._id) } },
        { uploadedBy: userId },
        { isPublic: true }
      ];
    } else if (userRole === 'OWNER') {
      // Owners can see all documents for their sites
      const userSites = await Site.find({ owner: userId }).select('_id');
      query.$or = [
        { site: { $in: userSites.map(site => site._id) } },
        { uploadedBy: userId },
        { isPublic: true }
      ];
    } else if (userRole === 'SUPPLIER') {
      // Suppliers can see documents they uploaded or public documents
      query.$or = [
        { uploadedBy: userId },
        { isPublic: true }
      ];
    }

    const documents = await Document.find(query)
      .populate('site', 'name location')
      .populate('uploadedBy', 'name role')
      .sort({ createdAt: -1 });

    res.json(documents);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Get a specific document
documentsRouter.get('/:id', requireAuth, async (req, res) => {
  try {
    const document = await Document.findById(req.params.id)
      .populate('site', 'name location')
      .populate('uploadedBy', 'name role');

    if (!document) {
      return res.status(404).json({ error: 'Document not found' });
    }

    // Check if user has access to this document
    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    if (document.uploadedBy.toString() === userId) {
      // User uploaded this document
      res.json(document);
      return;
    }

    if (document.isPublic) {
      // Public document
      res.json(document);
      return;
    }

    if (document.site) {
      const site = await Site.findById(document.site);
      if (!site) {
        return res.status(404).json({ error: 'Site not found' });
      }

      if (userRole === 'OWNER' && site.owner.toString() === userId) {
        res.json(document);
        return;
      }

      if (userRole === 'CONTRACTOR' && site.contractors.map(id => id.toString()).includes(userId)) {
        res.json(document);
        return;
      }
    }

    return res.status(403).json({ error: 'Access denied' });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Create a new document
documentsRouter.post('/', requireAuth, async (req, res) => {
  try {
    const {
      title,
      description,
      fileName,
      filePath,
      fileSize,
      mimeType,
      siteId,
      category,
      tags,
      isPublic
    } = req.body;

    if (!title || !fileName || !filePath || !fileSize || !mimeType) {
      return res.status(400).json({ error: 'Missing required fields' });
    }

    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    // Verify site access if siteId is provided
    if (siteId) {
      const site = await Site.findById(siteId);
      if (!site) {
        return res.status(404).json({ error: 'Site not found' });
      }

      if (userRole === 'CONTRACTOR' && !site.contractors.map(id => id.toString()).includes(userId)) {
        return res.status(403).json({ error: 'Access denied' });
      }

      if (userRole === 'OWNER' && site.owner.toString() !== userId) {
        return res.status(403).json({ error: 'Access denied' });
      }
    }

    const document = await Document.create({
      title,
      description,
      fileName,
      filePath,
      fileSize,
      mimeType,
      site: siteId,
      uploadedBy: userId,
      category: category || 'OTHER',
      tags: tags || [],
      isPublic: isPublic || false
    });

    const populatedDocument = await Document.findById(document._id)
      .populate('site', 'name location')
      .populate('uploadedBy', 'name role');

    res.status(201).json(populatedDocument);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Update a document
documentsRouter.put('/:id', requireAuth, async (req, res) => {
  try {
    const document = await Document.findById(req.params.id);
    if (!document) {
      return res.status(404).json({ error: 'Document not found' });
    }

    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    // Check access permissions
    if (document.uploadedBy.toString() !== userId && userRole !== 'OWNER') {
      return res.status(403).json({ error: 'Access denied' });
    }

    // If site is being changed, verify access to new site
    if (req.body.siteId && req.body.siteId !== document.site?.toString()) {
      const site = await Site.findById(req.body.siteId);
      if (!site) {
        return res.status(404).json({ error: 'Site not found' });
      }

      if (userRole === 'CONTRACTOR' && !site.contractors.map(id => id.toString()).includes(userId)) {
        return res.status(403).json({ error: 'Access denied' });
      }

      if (userRole === 'OWNER' && site.owner.toString() !== userId) {
        return res.status(403).json({ error: 'Access denied' });
      }
    }

    const updatedDocument = await Document.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true }
    )
      .populate('site', 'name location')
      .populate('uploadedBy', 'name role');

    res.json(updatedDocument);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Delete a document
documentsRouter.delete('/:id', requireAuth, async (req, res) => {
  try {
    const document = await Document.findById(req.params.id);
    if (!document) {
      return res.status(404).json({ error: 'Document not found' });
    }

    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    // Check access permissions
    if (document.uploadedBy.toString() !== userId && userRole !== 'OWNER') {
      return res.status(403).json({ error: 'Access denied' });
    }

    await Document.findByIdAndDelete(req.params.id);
    res.json({ message: 'Document deleted successfully' });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Download a document (increment download count)
documentsRouter.post('/:id/download', requireAuth, async (req, res) => {
  try {
    const document = await Document.findById(req.params.id);
    if (!document) {
      return res.status(404).json({ error: 'Document not found' });
    }

    // Check access permissions (same logic as GET /:id)
    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    let hasAccess = false;

    if (document.uploadedBy.toString() === userId) {
      hasAccess = true;
    } else if (document.isPublic) {
      hasAccess = true;
    } else if (document.site) {
      const site = await Site.findById(document.site);
      if (site) {
        if (userRole === 'OWNER' && site.owner.toString() === userId) {
          hasAccess = true;
        } else if (userRole === 'CONTRACTOR' && site.contractors.map(id => id.toString()).includes(userId)) {
          hasAccess = true;
        }
      }
    }

    if (!hasAccess) {
      return res.status(403).json({ error: 'Access denied' });
    }

    // Increment download count and update last downloaded time
    document.downloadCount += 1;
    document.lastDownloadedAt = new Date();
    await document.save();

    res.json({ 
      message: 'Download recorded',
      downloadCount: document.downloadCount,
      filePath: document.filePath
    });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Get document statistics
documentsRouter.get('/stats/overview', requireAuth, async (req, res) => {
  try {
    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    let siteFilter: any = {};

    if (userRole === 'OWNER') {
      const userSites = await Site.find({ owner: userId }).select('_id');
      siteFilter = { $in: userSites.map(site => site._id) };
    } else if (userRole === 'CONTRACTOR') {
      const userSites = await Site.find({ contractors: userId }).select('_id');
      siteFilter = { $in: userSites.map(site => site._id) };
    }

    const stats = await Document.aggregate([
      { $match: { site: siteFilter } },
      {
        $group: {
          _id: null,
          total: { $sum: 1 },
          totalSize: { $sum: '$fileSize' },
          totalDownloads: { $sum: '$downloadCount' },
          plans: { $sum: { $cond: [{ $eq: ['$category', 'PLAN'] }, 1, 0] } },
          permits: { $sum: { $cond: [{ $eq: ['$category', 'PERMIT'] }, 1, 0] } },
          contracts: { $sum: { $cond: [{ $eq: ['$category', 'CONTRACT'] }, 1, 0] } },
          invoices: { $sum: { $cond: [{ $eq: ['$category', 'INVOICE'] }, 1, 0] } },
          reports: { $sum: { $cond: [{ $eq: ['$category', 'REPORT'] }, 1, 0] } },
          photos: { $sum: { $cond: [{ $eq: ['$category', 'PHOTO'] }, 1, 0] } }
        }
      }
    ]);

    res.json(stats[0] || {
      total: 0,
      totalSize: 0,
      totalDownloads: 0,
      plans: 0,
      permits: 0,
      contracts: 0,
      invoices: 0,
      reports: 0,
      photos: 0
    });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});
