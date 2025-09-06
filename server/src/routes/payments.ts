import { Router } from 'express';
import { requireAuth } from '../middleware/auth.js';
import { Payment } from '../models/Payment.js';
import { Transaction } from '../models/Transaction.js';

export const paymentsRouter = Router();

// Record payment (Owner paying contractor/supplier)
paymentsRouter.post('/', requireAuth, async (req, res) => {
  const { siteId, toUserId, amount, date, note } = req.body as { siteId: string; toUserId: string; amount: number; date?: string; note?: string };
  const payment = await Payment.create({ site: siteId, fromUser: req.auth!.userId, toUser: toUserId, amount, date: date ? new Date(date) : undefined, note });
  await Transaction.create({ site: siteId, type: 'PAYMENT', actor: req.auth!.userId, details: { paymentId: payment._id } });
  res.json(payment);
});

// List payments by site or user
paymentsRouter.get('/', requireAuth, async (req, res) => {
  const { siteId, userId } = req.query as any;
  const filter: any = {};
  if (siteId) filter.site = siteId;
  if (userId) filter.$or = [{ fromUser: userId }, { toUser: userId }];
  const payments = await Payment.find(filter).sort({ date: -1 });
  res.json(payments);
});

// Simple financial summary by site
paymentsRouter.get('/summary/:siteId', requireAuth, async (req, res) => {
  const siteId = req.params.siteId;
  const payments = await Payment.find({ site: siteId });
  const paidOut = payments.reduce((sum, p) => sum + p.amount, 0);
  res.json({ siteId, paidOut });
});


