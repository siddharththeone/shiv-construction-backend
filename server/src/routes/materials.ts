import { Router } from 'express';
import { requireAuth, requireRole } from '../middleware/auth.js';
import { MaterialRequest } from '../models/MaterialRequest.js';
import { Site } from '../models/Site.js';
import { Transaction } from '../models/Transaction.js';

export const materialsRouter = Router();

// Contractor requests materials from a supplier
materialsRouter.post('/request', requireAuth, requireRole(['CONTRACTOR']), async (req, res) => {
  const { siteId, supplierId, items } = req.body as { siteId: string; supplierId: string; items: Array<{ name: string; quantity: number; unit?: string }> };
  const site = await Site.findOne({ _id: siteId, contractors: req.auth!.userId });
  if (!site) return res.status(404).json({ error: 'Site not found' });
  if (!site.suppliers.map(String).includes(supplierId)) return res.status(400).json({ error: 'Supplier not assigned to site' });
  const request = await MaterialRequest.create({ site: site._id, requestedBy: req.auth!.userId, supplier: supplierId, items });
  await Transaction.create({ site: site._id, type: 'MATERIAL_REQUEST', actor: req.auth!.userId, details: { requestId: request._id } });
  res.json(request);
});

// Supplier updates delivery status
materialsRouter.post('/:id/status', requireAuth, requireRole(['SUPPLIER']), async (req, res) => {
  const { status } = req.body as { status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DELIVERED' | 'CANCELLED' };
  const request = await MaterialRequest.findOne({ _id: req.params.id, supplier: req.auth!.userId });
  if (!request) return res.status(404).json({ error: 'Not found' });
  request.status = status;
  await request.save();
  await Transaction.create({ site: request.site, type: 'DELIVERY_UPDATE', actor: req.auth!.userId, details: { requestId: request._id, status } });
  res.json(request);
});

// List material requests per role
materialsRouter.get('/', requireAuth, async (req, res) => {
  const { role, userId } = req.auth!;
  let filter: any = {};
  if (role === 'CONTRACTOR') filter.requestedBy = userId;
  if (role === 'SUPPLIER') filter.supplier = userId;
  if (role === 'OWNER') filter = {}; // owners can see all via admin UI; optionally filter by their sites in future
  const requests = await MaterialRequest.find(filter).sort({ createdAt: -1 });
  res.json(requests);
});


