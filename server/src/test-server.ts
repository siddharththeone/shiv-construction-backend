import express from 'express';
import cors from 'cors';
import morgan from 'morgan';

const app = express();

app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(morgan('dev'));

// Mock user data for testing
const mockUsers = [
  {
    _id: '1',
    name: 'Test Owner',
    email: 'owner@test.com',
    phone: '+1234567890',
    role: 'OWNER',
    contractorType: null,
    profileImage: null,
    address: 'Test Address',
    companyName: 'Test Company'
  },
  {
    _id: '2',
    name: 'Test Contractor',
    email: 'contractor@test.com',
    phone: '+1234567891',
    role: 'CONTRACTOR',
    contractorType: 'GENERAL',
    profileImage: null,
    address: 'Contractor Address',
    companyName: 'Contractor Company'
  },
  {
    _id: '3',
    name: 'Test Supplier',
    email: 'supplier@test.com',
    phone: '+1234567892',
    role: 'SUPPLIER',
    contractorType: null,
    profileImage: null,
    address: 'Supplier Address',
    companyName: 'Supplier Company'
  },
  {
    _id: '4',
    name: 'Siddharth',
    email: 'siddharthshardul27@gmail.com',
    phone: '+1234567893',
    role: 'OWNER',
    contractorType: null,
    profileImage: null,
    address: 'Siddharth Address',
    companyName: 'Siddharth Company'
  }
];

// Mock sites data
const mockSites = [
  {
    _id: '1',
    name: 'Downtown Office Complex',
    location: 'Downtown Area',
    address: '123 Main Street, Downtown',
    startDate: '2024-01-15',
    expectedEndDate: '2024-12-31',
    actualEndDate: null,
    status: 'IN_PROGRESS',
    progressPercent: 65,
    description: 'Modern office complex with 20 floors',
    budget: 5000000,
    actualCost: 3200000,
    owner: mockUsers[0],
    contractors: [mockUsers[1]],
    suppliers: [mockUsers[2]],
    photos: [],
    documents: [],
    latitude: 40.7128,
    longitude: -74.0060,
    siteArea: 5000,
    buildingType: 'Office',
    floors: 20
  },
  {
    _id: '2',
    name: 'Residential Complex',
    location: 'Suburban Area',
    address: '456 Oak Avenue, Suburbs',
    startDate: '2024-03-01',
    expectedEndDate: '2024-11-30',
    actualEndDate: null,
    status: 'IN_PROGRESS',
    progressPercent: 45,
    description: 'Luxury residential complex with 100 units',
    budget: 3000000,
    actualCost: 1350000,
    owner: mockUsers[0],
    contractors: [mockUsers[1]],
    suppliers: [mockUsers[2]],
    photos: [],
    documents: [],
    latitude: 40.7589,
    longitude: -73.9851,
    siteArea: 8000,
    buildingType: 'Residential',
    floors: 15
  },
  {
    _id: '3',
    name: 'SHIV Construction Project',
    location: 'Mumbai, Maharashtra',
    address: 'Andheri West, Mumbai',
    startDate: '2024-08-01',
    expectedEndDate: '2025-06-30',
    actualEndDate: null,
    status: 'IN_PROGRESS',
    progressPercent: 25,
    description: 'High-rise commercial building project by SHIV Construction',
    budget: 15000000,
    actualCost: 3750000,
    owner: mockUsers[3], // Siddharth's project
    contractors: [mockUsers[1]],
    suppliers: [mockUsers[2]],
    photos: [],
    documents: [],
    latitude: 19.0760,
    longitude: 72.8777,
    siteArea: 12000,
    buildingType: 'Commercial',
    floors: 25
  },
  {
    _id: '4',
    name: 'Tech Park Development',
    location: 'Bangalore, Karnataka',
    address: 'Electronic City, Bangalore',
    startDate: '2024-09-15',
    expectedEndDate: '2025-12-31',
    actualEndDate: null,
    status: 'PLANNING',
    progressPercent: 10,
    description: 'Modern tech park with multiple office buildings',
    budget: 25000000,
    actualCost: 2500000,
    owner: mockUsers[3], // Siddharth's project
    contractors: [mockUsers[1]],
    suppliers: [mockUsers[2]],
    photos: [],
    documents: [],
    latitude: 12.9716,
    longitude: 77.5946,
    siteArea: 20000,
    buildingType: 'Tech Park',
    floors: 30
  }
];

// Authentication endpoints
app.post('/api/auth/signup', (req, res) => {
  const { name, email, password, role, contractorType } = req.body;
  
  // Check if user already exists
  const existingUser = mockUsers.find(user => user.email === email);
  if (existingUser) {
    return res.status(400).json({ error: 'User already exists' });
  }
  
  // Create new user
  const newUser = {
    _id: (mockUsers.length + 1).toString(),
    name: name || 'New User',
    email: email || 'user@example.com',
    phone: '+1234567890',
    role: role || 'CONTRACTOR',
    contractorType: contractorType || null,
    profileImage: null,
    address: 'New Address',
    companyName: 'New Company'
  };
  
  mockUsers.push(newUser);
  
  // Return success response
  res.status(201).json({
    token: 'mock-jwt-token-' + Date.now(),
    user: newUser
  });
});

app.post('/api/auth/login', (req, res) => {
  const { email, password } = req.body;
  
  // Find user by email
  const user = mockUsers.find(u => u.email === email);
  
  if (!user) {
    return res.status(401).json({ error: 'Invalid credentials' });
  }
  
  // For test purposes, accept any password
  // In real app, you would verify the password hash
  
  // Return success response
  res.json({
    token: 'mock-jwt-token-' + Date.now(),
    user
  });
});

app.post('/api/auth/register-fcm', (req, res) => {
  // Mock FCM registration
  res.json({ message: 'FCM token registered successfully' });
});

// Sites endpoints
app.get('/api/sites', (req, res) => {
  res.json(mockSites);
});

app.get('/api/sites/:id', (req, res) => {
  const site = mockSites.find(s => s._id === req.params.id);
  if (!site) {
    return res.status(404).json({ error: 'Site not found' });
  }
  res.json(site);
});

app.post('/api/sites', (req, res) => {
  const newSite = {
    _id: (mockSites.length + 1).toString(),
    ...req.body,
    owner: mockUsers[0],
    contractors: [],
    suppliers: [],
    photos: [],
    documents: []
  };
  mockSites.push(newSite);
  res.status(201).json(newSite);
});

// Tasks endpoints
app.get('/api/tasks', (req, res) => {
  res.json([]);
});

// Issues endpoints
app.get('/api/issues', (req, res) => {
  res.json([]);
});

// Materials endpoints
app.get('/api/materials', (req, res) => {
  res.json([]);
});

// Payments endpoints
app.get('/api/payments', (req, res) => {
  res.json([]);
});

// Documents endpoints
app.get('/api/documents', (req, res) => {
  res.json([]);
});

// Notifications endpoints
app.get('/api/notifications', (req, res) => {
  res.json([]);
});

// Basic endpoints
app.get('/health', (_req, res) => {
  res.json({ ok: true, message: 'SHIV Construction Management API is running!' });
});

app.get('/api', (_req, res) => {
  res.json({
    message: 'SHIV Construction Management API',
    version: '1.0.0',
    status: 'running',
    endpoints: {
      health: '/health',
      api: '/api',
      auth: '/api/auth',
      sites: '/api/sites',
      tasks: '/api/tasks',
      issues: '/api/issues',
      materials: '/api/materials',
      payments: '/api/payments',
      documents: '/api/documents',
      notifications: '/api/notifications'
    }
  });
});

app.get('/api/test', (_req, res) => {
  res.json({
    message: 'API is working!',
    timestamp: new Date().toISOString(),
    features: [
      'Authentication',
      'Site Management',
      'Task Management',
      'Issue Tracking',
      'Material Requests',
      'Payment Management',
      'Document Management',
      'Notifications'
    ],
    testUsers: mockUsers.map(u => ({ email: u.email, role: u.role }))
  });
});

const port = Number(process.env.PORT || 4000);

app.listen(port, () => {
  console.log(`🚀 SHIV Construction Management API (Test Mode) running on http://localhost:${port}`);
  console.log(`📊 Health check: http://localhost:${port}/health`);
  console.log(`📚 API docs: http://localhost:${port}/api`);
  console.log(`🧪 Test endpoint: http://localhost:${port}/api/test`);
  console.log(`⚠️  Running in test mode without database connection`);
  console.log(`👥 Test users available:`);
  mockUsers.forEach(user => {
    console.log(`   - ${user.email} (${user.role})`);
  });
});
