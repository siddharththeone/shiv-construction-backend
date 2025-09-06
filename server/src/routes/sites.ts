import { Router } from 'express';
import { requireAuth, requireRole } from '../middleware/auth.js';
import { Site } from '../models/Site.js';
import { User } from '../models/User.js';
import { Company } from '../models/Company.js';
import { Transaction } from '../models/Transaction.js';

export const siteRouter = Router();

// Create site (Owner only)
siteRouter.post('/', requireAuth, requireRole(['OWNER']), async (req, res) => {
  try {
    const owner = req.auth!.userId;
    const { 
      name, 
      location, 
      address,
      startDate, 
      expectedEndDate,
      description,
      budget,
      latitude,
      longitude,
      siteArea,
      buildingType,
      floors
    } = req.body;

    if (!name) {
      return res.status(400).json({ error: 'Site name is required' });
    }

    // Get user's company
    const user = await User.findById(owner).populate('company');
    console.log('User found:', user ? 'Yes' : 'No');
    console.log('User company:', user?.company ? 'Yes' : 'No');
    console.log('User ID:', owner);
    
    if (!user) {
      return res.status(400).json({ error: 'User not found' });
    }
    
    if (!user.company) {
      // Try to find company by owner
      const company = await Company.findOne({ owner: user._id });
      if (company) {
        // Update user with company reference
        await User.findByIdAndUpdate(user._id, { company: company._id });
        user.company = company._id;
      } else {
        return res.status(400).json({ error: 'User must belong to a company' });
      }
    }

    const site = await Site.create({ 
      name, 
      location, 
      address,
      startDate: startDate ? new Date(startDate) : undefined,
      expectedEndDate: expectedEndDate ? new Date(expectedEndDate) : undefined,
      description,
      budget: budget ? parseFloat(budget) : undefined,
      latitude: latitude ? parseFloat(latitude) : undefined,
      longitude: longitude ? parseFloat(longitude) : undefined,
      siteArea: siteArea ? parseFloat(siteArea) : undefined,
      buildingType,
      floors: floors ? parseInt(floors) : undefined,
      company: user.company!._id,
      owner,
      status: 'NOT_STARTED',
      progressPercent: 0
    });

    res.status(201).json({ success: true, site });
  } catch (error: any) {
    res.status(400).json({ error: error.message });
  }
});

// List sites (role-based)
siteRouter.get('/', requireAuth, async (req, res) => {
  try {
    const { role, userId } = req.auth!;
    
    // Get user's company
    const user = await User.findById(userId).populate('company');
    if (!user || !user.company) {
      return res.status(400).json({ error: 'User must belong to a company' });
    }
    
    let filter: any = { company: user.company._id };
    if (role === 'OWNER') filter.owner = userId;
    if (role === 'CONTRACTOR') filter.contractors = userId;
    if (role === 'SUPPLIER') filter.suppliers = userId;
    
    const sites = await Site.find(filter).sort({ createdAt: -1 });
    res.json(sites);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Update site
siteRouter.put('/:id', requireAuth, requireRole(['OWNER']), async (req, res) => {
  const site = await Site.findOneAndUpdate({ _id: req.params.id, owner: req.auth!.userId }, req.body, { new: true });
  if (!site) return res.status(404).json({ error: 'Not found' });
  res.json(site);
});

// Delete site
siteRouter.delete('/:id', requireAuth, requireRole(['OWNER']), async (req, res) => {
  const result = await Site.findOneAndDelete({ _id: req.params.id, owner: req.auth!.userId });
  if (!result) return res.status(404).json({ error: 'Not found' });
  res.json({ ok: true });
});

// Assign contractors/suppliers
siteRouter.post('/:id/assign', requireAuth, requireRole(['OWNER']), async (req, res) => {
  const { contractors, suppliers } = req.body as { contractors?: string[]; suppliers?: string[] };
  const site = await Site.findOneAndUpdate(
    { _id: req.params.id, owner: req.auth!.userId },
    {
      ...(contractors ? { contractors } : {}),
      ...(suppliers ? { suppliers } : {})
    },
    { new: true }
  );
  if (!site) return res.status(404).json({ error: 'Not found' });
  res.json(site);
});

// Update progress and status (Contractor)
siteRouter.post('/:id/progress', requireAuth, requireRole(['CONTRACTOR']), async (req, res) => {
  const { progressPercent, status } = req.body as { progressPercent?: number; status?: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' };
  const site = await Site.findOne({ _id: req.params.id, contractors: req.auth!.userId });
  if (!site) return res.status(404).json({ error: 'Not found' });
  if (typeof progressPercent === 'number') site.progressPercent = Math.max(0, Math.min(100, progressPercent));
  if (status) site.status = status;
  await site.save();
  await Transaction.create({ site: site._id, type: 'STATUS_UPDATE', actor: req.auth!.userId, details: { progressPercent: site.progressPercent, status: site.status } });
  res.json(site);
});


