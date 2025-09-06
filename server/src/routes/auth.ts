import { Router } from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { User } from '../models/User.js';
import { Company } from '../models/Company.js';
import { requireAuth } from '../middleware/auth.js';
import { UserRole } from '../types/roles.js';

export const authRouter = Router();

// Regular signup (for contractors/suppliers)
authRouter.post('/signup', async (req, res) => {
  try {
    const { name, email, phone, password, role } = req.body as {
      name: string;
      email?: string;
      phone?: string;
      password?: string;
      role: UserRole;
    };

    if (!name || !role) return res.status(400).json({ error: 'Missing fields' });
    const passwordHash = password ? await bcrypt.hash(password, 10) : undefined;
    const user = await User.create({ name, email, phone, passwordHash, role });
    const token = signToken(user._id.toString(), user.role);
    res.json({ success: true, token, user: serializeUser(user) });
  } catch (e: any) {
    res.status(400).json({ error: e.message });
  }
});

// Owner signup with company creation
authRouter.post('/signup-owner', async (req, res) => {
  try {
    const { name, email, phone, password, companyName, companyDescription, companyAddress, companyPhone, companyEmail, companyWebsite } = req.body as {
      name: string;
      email?: string;
      phone?: string;
      password?: string;
      companyName: string;
      companyDescription?: string;
      companyAddress?: string;
      companyPhone?: string;
      companyEmail?: string;
      companyWebsite?: string;
    };

    if (!name || !companyName) return res.status(400).json({ error: 'Name and company name are required' });
    
    const passwordHash = password ? await bcrypt.hash(password, 10) : undefined;
    
    // Create user first
    const user = await User.create({ 
      name, 
      email, 
      phone, 
      passwordHash, 
      role: 'OWNER' 
    });
    
    // Create company
    const company = await Company.create({
      name: companyName,
      description: companyDescription,
      address: companyAddress,
      phone: companyPhone,
      email: companyEmail,
      website: companyWebsite,
      owner: user._id
    });
    
    // Update user with company reference
    await User.findByIdAndUpdate(user._id, { company: company._id });
    
    const token = signToken(user._id.toString(), user.role);
    res.json({ 
      success: true, 
      token, 
      user: serializeUser(user),
      company: {
        _id: company._id,
        name: company.name,
        description: company.description,
        address: company.address,
        phone: company.phone,
        email: company.email,
        website: company.website
      }
    });
  } catch (e: any) {
    res.status(400).json({ error: e.message });
  }
});

authRouter.post('/login', async (req, res) => {
  const { email, phone, password } = req.body as { email?: string; phone?: string; password?: string };
  if (!password) return res.status(400).json({ error: 'Password required for this flow' });
  const user = await User.findOne(
    email ? { email } : phone ? { phone } : { _id: null }
  );
  if (!user || !user.passwordHash) return res.status(401).json({ error: 'Invalid credentials' });
  const ok = await bcrypt.compare(password, user.passwordHash);
  if (!ok) return res.status(401).json({ error: 'Invalid credentials' });
  const token = signToken(user._id, user.role);
  res.json({ success: true, token, user: serializeUser(user) });
});

authRouter.post('/invite', requireAuth, async (req, res) => {
  try {
    const inviterId = req.auth!.userId;
    const { name, email, phone, role } = req.body as { name: string; email?: string; phone?: string; role: UserRole };
    if (!['CONTRACTOR', 'SUPPLIER'].includes(role)) return res.status(400).json({ error: 'Invalid role' });
    const invited = await User.create({ name, email, phone, role, invitedBy: inviterId });
    res.json({ user: serializeUser(invited) });
  } catch (e: any) {
    res.status(400).json({ error: e.message });
  }
});

authRouter.post('/register-fcm', requireAuth, async (req, res) => {
  const { token } = req.body as { token: string };
  if (!token) return res.status(400).json({ error: 'token required' });
  await User.findByIdAndUpdate(req.auth!.userId, { $addToSet: { fcmTokens: token } });
  res.json({ ok: true });
});

function signToken(userId: string, role: UserRole): string {
  const secret = process.env.JWT_SECRET || 'dev_secret';
  return jwt.sign({ userId, role }, secret, { expiresIn: '7d' });
}

function serializeUser(user: any) {
  return { 
    _id: user._id, 
    id: user._id, 
    name: user.name, 
    email: user.email, 
    phone: user.phone, 
    role: user.role 
  };
}


