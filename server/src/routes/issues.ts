import { Router } from 'express';
import { Issue } from '../models/Issue.js';
import { Site } from '../models/Site.js';
import { User } from '../models/User.js';
import { Notification } from '../models/Notification.js';
import { requireAuth, requireRole } from '../middleware/auth.js';
import { UserRole } from '../types/roles.js';

export const issuesRouter = Router();

// Get all issues
issuesRouter.get('/', requireAuth, async (req, res) => {
  try {
    const { siteId, status, priority, category, assignedTo } = req.query;
    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    let query: any = {};

    // Filter by site if provided
    if (siteId) {
      query.site = siteId;
    }

    // Filter by status if provided
    if (status) {
      query.status = status;
    }

    // Filter by priority if provided
    if (priority) {
      query.priority = priority;
    }

    // Filter by category if provided
    if (category) {
      query.category = category;
    }

    // Filter by assigned user if provided
    if (assignedTo) {
      query.assignedTo = assignedTo;
    }

    // Role-based filtering
    if (userRole === 'CONTRACTOR') {
      // Contractors can see issues they reported or are assigned to
      query.$or = [
        { reportedBy: userId },
        { assignedTo: userId }
      ];
    } else if (userRole === 'OWNER') {
      // Owners can see all issues for their sites
      const userSites = await Site.find({ owner: userId }).select('_id');
      query.site = { $in: userSites.map(site => site._id) };
    }

    const issues = await Issue.find(query)
      .populate('site', 'name location')
      .populate('reportedBy', 'name role')
      .populate('assignedTo', 'name role contractorType')
      .sort({ priority: -1, createdAt: -1 });

    res.json(issues);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Get a specific issue
issuesRouter.get('/:id', requireAuth, async (req, res) => {
  try {
    const issue = await Issue.findById(req.params.id)
      .populate('site', 'name location')
      .populate('reportedBy', 'name role')
      .populate('assignedTo', 'name role contractorType');

    if (!issue) {
      return res.status(404).json({ error: 'Issue not found' });
    }

    // Check if user has access to this issue
    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    if (userRole === 'CONTRACTOR') {
      if (issue.reportedBy.toString() !== userId && 
          (!issue.assignedTo || issue.assignedTo.toString() !== userId)) {
        return res.status(403).json({ error: 'Access denied' });
      }
    }

    if (userRole === 'OWNER') {
      const site = await Site.findById(issue.site);
      if (!site || site.owner.toString() !== userId) {
        return res.status(403).json({ error: 'Access denied' });
      }
    }

    res.json(issue);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Create a new issue
issuesRouter.post('/', requireAuth, async (req, res) => {
  try {
    const {
      title,
      description,
      siteId,
      priority,
      category,
      location,
      estimatedCost,
      photos
    } = req.body;

    if (!title || !description || !siteId || !category) {
      return res.status(400).json({ error: 'Missing required fields' });
    }

    // Verify site access
    const site = await Site.findById(siteId);
    if (!site) {
      return res.status(404).json({ error: 'Site not found' });
    }

    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    // Check if user has access to this site
    if (userRole === 'CONTRACTOR' && !site.contractors.map(id => id.toString()).includes(userId)) {
      return res.status(403).json({ error: 'Access denied' });
    }

    if (userRole === 'OWNER' && site.owner.toString() !== userId) {
      return res.status(403).json({ error: 'Access denied' });
    }

    const issue = await Issue.create({
      title,
      description,
      site: siteId,
      reportedBy: userId,
      priority: priority || 'MEDIUM',
      category,
      location,
      estimatedCost,
      photos: photos || []
    });

    // Create notification for site owner
    await Notification.create({
      title: 'New Issue Reported',
      message: `A new ${category.toLowerCase()} issue has been reported: ${title}`,
      recipient: site.owner,
      sender: userId,
      type: 'ISSUE_REPORT',
      priority: priority === 'HIGH' || priority === 'CRITICAL' ? 'HIGH' : 'MEDIUM',
      site: siteId,
      actionUrl: `/issues/${issue._id}`,
      data: { issueId: issue._id, siteId }
    });

    const populatedIssue = await Issue.findById(issue._id)
      .populate('site', 'name location')
      .populate('reportedBy', 'name role')
      .populate('assignedTo', 'name role contractorType');

    res.status(201).json(populatedIssue);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Update an issue
issuesRouter.put('/:id', requireAuth, async (req, res) => {
  try {
    const issue = await Issue.findById(req.params.id);
    if (!issue) {
      return res.status(404).json({ error: 'Issue not found' });
    }

    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    // Check access permissions
    if (userRole === 'CONTRACTOR') {
      if (issue.reportedBy.toString() !== userId && 
          (!issue.assignedTo || issue.assignedTo.toString() !== userId)) {
        return res.status(403).json({ error: 'Access denied' });
      }
    }

    if (userRole === 'OWNER') {
      const site = await Site.findById(issue.site);
      if (!site || site.owner.toString() !== userId) {
        return res.status(403).json({ error: 'Access denied' });
      }
    }

    const updateData = { ...req.body };
    
    // If status is being updated to resolved, set resolvedAt
    if (updateData.status === 'RESOLVED' && issue.status !== 'RESOLVED') {
      updateData.resolvedAt = new Date();
    }

    // If assigning to someone, create notification
    if (updateData.assignedTo && updateData.assignedTo !== issue.assignedTo?.toString()) {
      await Notification.create({
        title: 'Issue Assigned to You',
        message: `You have been assigned an issue: ${issue.title}`,
        recipient: updateData.assignedTo,
        sender: userId,
        type: 'ISSUE_REPORT',
        priority: issue.priority === 'HIGH' || issue.priority === 'CRITICAL' ? 'HIGH' : 'MEDIUM',
        site: issue.site,
        actionUrl: `/issues/${issue._id}`,
        data: { issueId: issue._id, siteId: issue.site }
      });
    }

    const updatedIssue = await Issue.findByIdAndUpdate(
      req.params.id,
      updateData,
      { new: true }
    )
      .populate('site', 'name location')
      .populate('reportedBy', 'name role')
      .populate('assignedTo', 'name role contractorType');

    res.json(updatedIssue);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Delete an issue
issuesRouter.delete('/:id', requireAuth, requireRole(['OWNER']), async (req, res) => {
  try {
    const issue = await Issue.findById(req.params.id);
    if (!issue) {
      return res.status(404).json({ error: 'Issue not found' });
    }

    // Verify site ownership
    const site = await Site.findById(issue.site);
    if (!site || site.owner.toString() !== req.auth!.userId) {
      return res.status(403).json({ error: 'Access denied' });
    }

    await Issue.findByIdAndDelete(req.params.id);
    res.json({ message: 'Issue deleted successfully' });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Add photos to an issue
issuesRouter.post('/:id/photos', requireAuth, async (req, res) => {
  try {
    const { photos } = req.body;
    const issue = await Issue.findById(req.params.id);
    
    if (!issue) {
      return res.status(404).json({ error: 'Issue not found' });
    }

    // Check access permissions
    const userId = req.auth!.userId;
    if (issue.reportedBy.toString() !== userId && 
        (!issue.assignedTo || issue.assignedTo.toString() !== userId) &&
        req.auth!.role !== 'OWNER') {
      return res.status(403).json({ error: 'Access denied' });
    }

    issue.photos.push(...photos);
    await issue.save();

    res.json(issue);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Get issue statistics
issuesRouter.get('/stats/overview', requireAuth, async (req, res) => {
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

    const stats = await Issue.aggregate([
      { $match: { site: siteFilter } },
      {
        $group: {
          _id: null,
          total: { $sum: 1 },
          open: { $sum: { $cond: [{ $eq: ['$status', 'OPEN'] }, 1, 0] } },
          inProgress: { $sum: { $cond: [{ $eq: ['$status', 'IN_PROGRESS'] }, 1, 0] } },
          resolved: { $sum: { $cond: [{ $eq: ['$status', 'RESOLVED'] }, 1, 0] } },
          closed: { $sum: { $cond: [{ $eq: ['$status', 'CLOSED'] }, 1, 0] } },
          critical: { $sum: { $cond: [{ $eq: ['$priority', 'CRITICAL'] }, 1, 0] } },
          high: { $sum: { $cond: [{ $eq: ['$priority', 'HIGH'] }, 1, 0] } }
        }
      }
    ]);

    res.json(stats[0] || {
      total: 0,
      open: 0,
      inProgress: 0,
      resolved: 0,
      closed: 0,
      critical: 0,
      high: 0
    });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});
