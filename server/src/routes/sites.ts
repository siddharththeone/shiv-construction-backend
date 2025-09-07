import { Router } from 'express';
import { Site } from '../models/Site.js';
import { User } from '../models/User.js';
import { Company } from '../models/Company.js';
import { requireAuth } from '../middleware/auth.js';
import { UserRole } from '../types/roles.js';

const sitesRouter = Router();

// Get all sites (for owner) or assigned sites (for contractor/supplier)
sitesRouter.get('/', requireAuth, async (req, res) => {
  try {
    const user = await User.findById(req.auth!.userId);
    if (!user) return res.status(401).json({ error: 'User not found' });
    
    let sites;
    if (user.role === 'OWNER') {
      // Owner sees all sites from their company
      sites = await Site.find({ company: user.company })
        .populate('contractors', 'name email phone')
        .populate('suppliers', 'name email phone')
        .sort({ createdAt: -1 });
    } else if (user.role === 'CONTRACTOR') {
      // Contractor sees only assigned sites
      sites = await Site.find({ contractors: user._id })
        .populate('owner', 'name email phone')
        .populate('suppliers', 'name email phone')
        .sort({ createdAt: -1 });
    } else if (user.role === 'SUPPLIER') {
      // Supplier sees only assigned sites
      sites = await Site.find({ suppliers: user._id })
        .populate('owner', 'name email phone')
        .populate('contractors', 'name email phone')
        .sort({ createdAt: -1 });
    } else {
      return res.status(403).json({ error: 'Access denied' });
    }

    res.json({ success: true, sites });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Get single site details
sitesRouter.get('/:id', requireAuth, async (req, res) => {
  try {
    const { id } = req.params;
    const user = await User.findById(req.auth!.userId);
    if (!user) return res.status(401).json({ error: 'User not found' });

    const site = await Site.findById(id)
      .populate('owner', 'name email phone')
      .populate('contractors', 'name email phone')
      .populate('suppliers', 'name email phone')
      .populate('company', 'name description');

    if (!site) {
      return res.status(404).json({ error: 'Site not found' });
    }

    // Check if user has access to this site
    const hasAccess = user.role === 'OWNER' && site.company.toString() === user.company?.toString() ||
                     user.role === 'CONTRACTOR' && site.contractors.some(c => c._id.toString() === user._id.toString()) ||
                     user.role === 'SUPPLIER' && site.suppliers.some(s => s._id.toString() === user._id.toString());

    if (!hasAccess) {
      return res.status(403).json({ error: 'Access denied' });
    }

    res.json({ success: true, site });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Create new site (only owner)
sitesRouter.post('/', requireAuth, async (req, res) => {
  try {
    const user = await User.findById(req.auth!.userId);
    if (!user) return res.status(401).json({ error: 'User not found' });

    if (user.role !== 'OWNER') {
      return res.status(403).json({ error: 'Only owners can create sites' });
    }

    const {
      name,
      location,
      address,
      startDate,
      expectedEndDate,
      description,
      budget,
      siteArea,
      buildingType,
      floors
    } = req.body;

    if (!name) {
      return res.status(400).json({ error: 'Site name is required' });
    }

    const site = await Site.create({
      name,
      location,
      address,
      startDate: startDate ? new Date(startDate) : undefined,
      expectedEndDate: expectedEndDate ? new Date(expectedEndDate) : undefined,
      description,
      budget,
      siteArea,
      buildingType,
      floors,
      company: user.company,
      owner: user._id,
      status: 'NOT_STARTED',
      progressPercent: 0
    });

    const populatedSite = await Site.findById(site._id)
      .populate('owner', 'name email phone')
      .populate('contractors', 'name email phone')
      .populate('suppliers', 'name email phone');

    res.status(201).json({ success: true, site: populatedSite });
  } catch (error: any) {
    res.status(400).json({ error: error.message });
  }
});

// Update site (owner can update everything, contractors can update progress)
sitesRouter.put('/:id', requireAuth, async (req, res) => {
  try {
    const { id } = req.params;
    const user = await User.findById(req.auth!.userId);
    if (!user) return res.status(401).json({ error: 'User not found' });

    const site = await Site.findById(id);
    if (!site) {
      return res.status(404).json({ error: 'Site not found' });
    }

    // Check access
    const isOwner = user.role === 'OWNER' && site.company.toString() === user.company?.toString();
    const isContractor = user.role === 'CONTRACTOR' && site.contractors.some(c => c._id.toString() === user._id.toString());

    if (!isOwner && !isContractor) {
      return res.status(403).json({ error: 'Access denied' });
    }

    // Contractors can only update progress and status
    if (isContractor) {
      const { status, progressPercent } = req.body;
      const updateData: any = {};
      
      if (status !== undefined) updateData.status = status;
      if (progressPercent !== undefined) updateData.progressPercent = progressPercent;

      const updatedSite = await Site.findByIdAndUpdate(id, updateData, { new: true })
        .populate('owner', 'name email phone')
        .populate('contractors', 'name email phone')
        .populate('suppliers', 'name email phone');

      return res.json({ success: true, site: updatedSite });
    }

    // Owner can update everything
    const updateData = { ...req.body };
    if (updateData.startDate) updateData.startDate = new Date(updateData.startDate);
    if (updateData.expectedEndDate) updateData.expectedEndDate = new Date(updateData.expectedEndDate);
    if (updateData.actualEndDate) updateData.actualEndDate = new Date(updateData.actualEndDate);

    const updatedSite = await Site.findByIdAndUpdate(id, updateData, { new: true })
      .populate('owner', 'name email phone')
      .populate('contractors', 'name email phone')
      .populate('suppliers', 'name email phone');

    res.json({ success: true, site: updatedSite });
  } catch (error: any) {
    res.status(400).json({ error: error.message });
  }
});

// Assign contractor to site (only owner)
sitesRouter.post('/:id/assign-contractor', requireAuth, async (req, res) => {
  try {
    const { id } = req.params;
    const { contractorId } = req.body;
    const user = await User.findById(req.auth!.userId);
    if (!user) return res.status(401).json({ error: 'User not found' });

    if (user.role !== 'OWNER') {
      return res.status(403).json({ error: 'Only owners can assign contractors' });
    }

    const site = await Site.findById(id);
    if (!site) {
      return res.status(404).json({ error: 'Site not found' });
    }

    if (site.company.toString() !== user.company?.toString()) {
      return res.status(403).json({ error: 'Access denied' });
    }

    const contractor = await User.findById(contractorId);
    if (!contractor || contractor.role !== 'CONTRACTOR') {
      return res.status(400).json({ error: 'Invalid contractor' });
    }

    if (!site.contractors.includes(contractorId)) {
      site.contractors.push(contractorId);
      await site.save();
    }

    const updatedSite = await Site.findById(id)
      .populate('owner', 'name email phone')
      .populate('contractors', 'name email phone')
      .populate('suppliers', 'name email phone');

    res.json({ success: true, site: updatedSite });
  } catch (error: any) {
    res.status(400).json({ error: error.message });
  }
});

// Assign supplier to site (only owner)
sitesRouter.post('/:id/assign-supplier', requireAuth, async (req, res) => {
  try {
    const { id } = req.params;
    const { supplierId } = req.body;
    const user = await User.findById(req.auth!.userId);
    if (!user) return res.status(401).json({ error: 'User not found' });

    if (user.role !== 'OWNER') {
      return res.status(403).json({ error: 'Only owners can assign suppliers' });
    }

    const site = await Site.findById(id);
    if (!site) {
      return res.status(404).json({ error: 'Site not found' });
    }

    if (site.company.toString() !== user.company?.toString()) {
      return res.status(403).json({ error: 'Access denied' });
    }

    const supplier = await User.findById(supplierId);
    if (!supplier || supplier.role !== 'SUPPLIER') {
      return res.status(400).json({ error: 'Invalid supplier' });
    }

    if (!site.suppliers.includes(supplierId)) {
      site.suppliers.push(supplierId);
      await site.save();
    }

    const updatedSite = await Site.findById(id)
      .populate('owner', 'name email phone')
      .populate('contractors', 'name email phone')
      .populate('suppliers', 'name email phone');

    res.json({ success: true, site: updatedSite });
  } catch (error: any) {
    res.status(400).json({ error: error.message });
  }
});

// Get available contractors and suppliers for assignment
sitesRouter.get('/available/:role', requireAuth, async (req, res) => {
  try {
    const { role } = req.params;
    const user = await User.findById(req.auth!.userId);
    if (!user) return res.status(401).json({ error: 'User not found' });

    if (user.role !== 'OWNER') {
      return res.status(403).json({ error: 'Only owners can view available users' });
    }

    if (role !== 'CONTRACTOR' && role !== 'SUPPLIER') {
      return res.status(400).json({ error: 'Invalid role' });
    }

    const users = await User.find({ 
      role: role.toUpperCase(),
      company: user.company 
    }).select('name email phone');

    res.json({ success: true, users });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

export { sitesRouter };