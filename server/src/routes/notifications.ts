import { Router } from 'express';
import { requireAuth } from '../middleware/auth.js';

// Stub for server-side FCM send integration (to be wired with admin SDK or HTTP v1)
export const notificationsRouter = Router();

notificationsRouter.post('/send', requireAuth, async (req, res) => {
  // Accept { toUserIds: string[], title: string, body: string, data?: object }
  // In production, fetch users' fcmTokens and call FCM send
  res.json({ queued: true });
});


