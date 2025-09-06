import { Router } from 'express';
import { Task } from '../models/Task.js';
import { Site } from '../models/Site.js';
import { User } from '../models/User.js';
import { Notification } from '../models/Notification.js';
import { requireAuth, requireRole } from '../middleware/auth.js';
import { UserRole } from '../types/roles.js';

export const tasksRouter = Router();

// Get all tasks for a user
tasksRouter.get('/', requireAuth, async (req, res) => {
  try {
    const { siteId, status, priority, assignedTo } = req.query;
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

    // Filter by assigned user if provided
    if (assignedTo) {
      query.assignedTo = assignedTo;
    }

    // Role-based filtering
    if (userRole === 'CONTRACTOR') {
      query.assignedTo = userId;
    } else if (userRole === 'OWNER') {
      // Owners can see all tasks for their sites
      const userSites = await Site.find({ owner: userId }).select('_id');
      query.site = { $in: userSites.map(site => site._id) };
    }

    const tasks = await Task.find(query)
      .populate('site', 'name location')
      .populate('assignedTo', 'name role contractorType')
      .populate('assignedBy', 'name')
      .sort({ createdAt: -1 });

    res.json(tasks);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Get a specific task
tasksRouter.get('/:id', requireAuth, async (req, res) => {
  try {
    const task = await Task.findById(req.params.id)
      .populate('site', 'name location')
      .populate('assignedTo', 'name role contractorType')
      .populate('assignedBy', 'name')
      .populate('dependencies', 'title status');

    if (!task) {
      return res.status(404).json({ error: 'Task not found' });
    }

    // Check if user has access to this task
    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    if (userRole === 'CONTRACTOR' && task.assignedTo.toString() !== userId) {
      return res.status(403).json({ error: 'Access denied' });
    }

    if (userRole === 'OWNER') {
      const site = await Site.findById(task.site);
      if (!site || site.owner.toString() !== userId) {
        return res.status(403).json({ error: 'Access denied' });
      }
    }

    res.json(task);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Create a new task
tasksRouter.post('/', requireAuth, requireRole(['OWNER', 'CONTRACTOR']), async (req, res) => {
  try {
    const {
      title,
      description,
      siteId,
      assignedTo,
      priority,
      dueDate,
      estimatedHours,
      tags
    } = req.body;

    if (!title || !siteId || !assignedTo) {
      return res.status(400).json({ error: 'Missing required fields' });
    }

    // Verify site access
    const site = await Site.findById(siteId);
    if (!site) {
      return res.status(404).json({ error: 'Site not found' });
    }

    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    if (userRole === 'OWNER' && site.owner.toString() !== userId) {
      return res.status(403).json({ error: 'Access denied' });
    }

    if (userRole === 'CONTRACTOR' && !site.contractors.map(id => id.toString()).includes(userId)) {
      return res.status(403).json({ error: 'Access denied' });
    }

    const task = await Task.create({
      title,
      description,
      site: siteId,
      assignedTo,
      assignedBy: userId,
      priority: priority || 'MEDIUM',
      dueDate,
      estimatedHours,
      tags: tags || []
    });

    // Create notification for assigned user
    await Notification.create({
      title: 'New Task Assigned',
      message: `You have been assigned a new task: ${title}`,
      recipient: assignedTo,
      sender: userId,
      type: 'TASK_ASSIGNMENT',
      priority: priority === 'HIGH' || priority === 'URGENT' ? 'HIGH' : 'MEDIUM',
      site: siteId,
      actionUrl: `/tasks/${task._id}`,
      data: { taskId: task._id, siteId }
    });

    const populatedTask = await Task.findById(task._id)
      .populate('site', 'name location')
      .populate('assignedTo', 'name role contractorType')
      .populate('assignedBy', 'name');

    res.status(201).json(populatedTask);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Update a task
tasksRouter.put('/:id', requireAuth, async (req, res) => {
  try {
    const task = await Task.findById(req.params.id);
    if (!task) {
      return res.status(404).json({ error: 'Task not found' });
    }

    const userId = req.auth!.userId;
    const userRole = req.auth!.role;

    // Check access permissions
    if (userRole === 'CONTRACTOR' && task.assignedTo.toString() !== userId) {
      return res.status(403).json({ error: 'Access denied' });
    }

    if (userRole === 'OWNER') {
      const site = await Site.findById(task.site);
      if (!site || site.owner.toString() !== userId) {
        return res.status(403).json({ error: 'Access denied' });
      }
    }

    const updateData = { ...req.body };
    
    // If status is being updated to completed, set completedAt
    if (updateData.status === 'COMPLETED' && task.status !== 'COMPLETED') {
      updateData.completedAt = new Date();
    }

    const updatedTask = await Task.findByIdAndUpdate(
      req.params.id,
      updateData,
      { new: true }
    )
      .populate('site', 'name location')
      .populate('assignedTo', 'name role contractorType')
      .populate('assignedBy', 'name');

    res.json(updatedTask);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Delete a task
tasksRouter.delete('/:id', requireAuth, requireRole(['OWNER']), async (req, res) => {
  try {
    const task = await Task.findById(req.params.id);
    if (!task) {
      return res.status(404).json({ error: 'Task not found' });
    }

    // Verify site ownership
    const site = await Site.findById(task.site);
    if (!site || site.owner.toString() !== req.auth!.userId) {
      return res.status(403).json({ error: 'Access denied' });
    }

    await Task.findByIdAndDelete(req.params.id);
    res.json({ message: 'Task deleted successfully' });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Add photos to a task
tasksRouter.post('/:id/photos', requireAuth, async (req, res) => {
  try {
    const { photos } = req.body;
    const task = await Task.findById(req.params.id);
    
    if (!task) {
      return res.status(404).json({ error: 'Task not found' });
    }

    // Check access permissions
    const userId = req.auth!.userId;
    if (task.assignedTo.toString() !== userId && req.auth!.role !== 'OWNER') {
      return res.status(403).json({ error: 'Access denied' });
    }

    task.photos.push(...photos);
    await task.save();

    res.json(task);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Add notes to a task
tasksRouter.post('/:id/notes', requireAuth, async (req, res) => {
  try {
    const { note } = req.body;
    const task = await Task.findById(req.params.id);
    
    if (!task) {
      return res.status(404).json({ error: 'Task not found' });
    }

    // Check access permissions
    const userId = req.auth!.userId;
    if (task.assignedTo.toString() !== userId && req.auth!.role !== 'OWNER') {
      return res.status(403).json({ error: 'Access denied' });
    }

    task.notes.push(note);
    await task.save();

    res.json(task);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});
