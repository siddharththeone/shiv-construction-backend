import { Router } from 'express';
import { requireAuth, requireRole } from '../middleware/auth.js';
import { Company } from '../models/Company.js';
import { User } from '../models/User.js';

export const companyRouter = Router();

// Create company (Owner only, first time)
companyRouter.post('/', requireAuth, requireRole(['OWNER']), async (req, res) => {
  try {
    const userId = req.auth!.userId;
    
    // Check if user already has a company
    const existingCompany = await Company.findOne({ owner: userId });
    if (existingCompany) {
      return res.status(400).json({ error: 'User already has a company' });
    }
    
    const { name, description, address, phone, email, website } = req.body;
    
    if (!name) {
      return res.status(400).json({ error: 'Company name is required' });
    }
    
    // Create company
    const company = await Company.create({
      name,
      description,
      address,
      phone,
      email,
      website,
      owner: userId
    });
    
    // Update user with company reference
    await User.findByIdAndUpdate(userId, { company: company._id });
    
    res.status(201).json({ success: true, company });
  } catch (error: any) {
    res.status(400).json({ error: error.message });
  }
});

// Get company details
companyRouter.get('/', requireAuth, async (req, res) => {
  try {
    const userId = req.auth!.userId;
    const user = await User.findById(userId).populate('company');
    
    if (!user || !user.company) {
      return res.status(404).json({ error: 'Company not found' });
    }
    
    res.json({ success: true, company: user.company });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Update company (Owner only)
companyRouter.put('/', requireAuth, requireRole(['OWNER']), async (req, res) => {
  try {
    const userId = req.auth!.userId;
    const company = await Company.findOneAndUpdate(
      { owner: userId },
      req.body,
      { new: true }
    );
    
    if (!company) {
      return res.status(404).json({ error: 'Company not found' });
    }
    
    res.json({ success: true, company });
  } catch (error: any) {
    res.status(400).json({ error: error.message });
  }
});

// Get company users (Owner only)
companyRouter.get('/users', requireAuth, requireRole(['OWNER']), async (req, res) => {
  try {
    const userId = req.auth!.userId;
    const company = await Company.findOne({ owner: userId });
    
    if (!company) {
      return res.status(404).json({ error: 'Company not found' });
    }
    
    const users = await User.find({ company: company._id })
      .select('-passwordHash -fcmTokens')
      .sort({ createdAt: -1 });
    
    res.json({ success: true, users });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});
