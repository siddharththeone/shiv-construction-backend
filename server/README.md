# SHIV Construction Management Backend

A comprehensive Node.js/Express backend for the SHIV Construction Site Management mobile application.

## 🏗️ Features

### Core Functionality
- **User Management**: Multi-role authentication (Owner, Contractor, Supplier)
- **Site Management**: Complete construction site lifecycle management
- **Task Management**: Assign, track, and manage construction tasks
- **Issue Tracking**: Report and resolve site issues
- **Material Requests**: Manage material procurement workflow
- **Payment Tracking**: Comprehensive payment management
- **Document Management**: Upload, organize, and share site documents
- **Notifications**: Real-time push notifications
- **File Uploads**: Secure file handling with validation

### Role-Based Access Control
- **Owners**: Full access to their sites, can manage contractors/suppliers
- **Contractors**: Access to assigned sites, can update progress and request materials
- **Suppliers**: Manage material requests and deliveries

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ 
- MongoDB (local or Atlas)
- npm or yarn

### Installation

1. **Clone and navigate to server directory**
   ```bash
   cd server
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Environment Setup**
   Create a `.env` file in the server directory:
   ```env
   # Server Configuration
   PORT=4000
   NODE_ENV=development

   # Database
   MONGODB_URI=mongodb://localhost:27017/construction_mgmt

   # JWT Configuration
   JWT_SECRET=your-super-secret-jwt-key-change-in-production

   # File Upload
   UPLOAD_DIR=uploads
   MAX_FILE_SIZE=10485760

   # Firebase Configuration (for push notifications)
   FIREBASE_PROJECT_ID=your-firebase-project-id
   FIREBASE_PRIVATE_KEY=your-firebase-private-key
   FIREBASE_CLIENT_EMAIL=your-firebase-client-email
   ```

4. **Start MongoDB**
   ```bash
   # Local MongoDB
   mongod
   
   # Or use MongoDB Atlas (update MONGODB_URI in .env)
   ```

5. **Run the server**
   ```bash
   # Development mode with auto-reload
   npm run dev
   
   # Production mode
   npm run build
   npm start
   ```

## 📚 API Endpoints

### Authentication (`/api/auth`)
- `POST /signup` - User registration
- `POST /login` - User login
- `POST /invite` - Invite new users (Owner only)
- `POST /register-fcm` - Register FCM token for notifications

### Sites (`/api/sites`)
- `GET /` - Get user's sites
- `GET /:id` - Get specific site details
- `POST /` - Create new site (Owner only)
- `PUT /:id` - Update site
- `DELETE /:id` - Delete site (Owner only)
- `POST /:id/photos` - Add photos to site
- `GET /stats/overview` - Get site statistics

### Tasks (`/api/tasks`)
- `GET /` - Get user's tasks
- `GET /:id` - Get specific task
- `POST /` - Create new task
- `PUT /:id` - Update task
- `DELETE /:id` - Delete task (Owner only)
- `POST /:id/photos` - Add photos to task
- `POST /:id/notes` - Add notes to task

### Issues (`/api/issues`)
- `GET /` - Get user's issues
- `GET /:id` - Get specific issue
- `POST /` - Create new issue
- `PUT /:id` - Update issue
- `DELETE /:id` - Delete issue (Owner only)
- `POST /:id/photos` - Add photos to issue
- `GET /stats/overview` - Get issue statistics

### Materials (`/api/materials`)
- `GET /` - Get material requests
- `GET /:id` - Get specific request
- `POST /` - Create material request
- `PUT /:id` - Update request status
- `DELETE /:id` - Cancel request

### Payments (`/api/payments`)
- `GET /` - Get payments
- `GET /:id` - Get specific payment
- `POST /` - Create payment
- `PUT /:id` - Update payment
- `DELETE /:id` - Cancel payment

### Documents (`/api/documents`)
- `GET /` - Get documents
- `GET /:id` - Get specific document
- `POST /` - Upload document
- `PUT /:id` - Update document
- `DELETE /:id` - Delete document
- `POST /:id/download` - Record download
- `GET /stats/overview` - Get document statistics

### Notifications (`/api/notifications`)
- `GET /` - Get user notifications
- `PUT /:id/read` - Mark as read
- `DELETE /:id` - Delete notification

### File Uploads (`/api/uploads`)
- `POST /` - Upload file
- `GET /:filename` - Download file

## 🗄️ Database Schema

### Users
- Basic info (name, email, phone)
- Role-based access (Owner, Contractor, Supplier)
- Contractor types (Electrical, Plumbing, etc.)
- FCM tokens for notifications

### Sites
- Site details (name, location, description)
- Progress tracking (status, completion percentage)
- Budget and cost tracking
- Associated users (owner, contractors, suppliers)
- Photos and documents

### Tasks
- Task details (title, description, priority)
- Assignment (assigned to, assigned by)
- Progress tracking (status, completion)
- Time tracking (estimated vs actual hours)
- Photos and notes

### Issues
- Issue details (title, description, category)
- Priority and status tracking
- Assignment and resolution
- Cost tracking
- Photos and location

### Material Requests
- Request details (items, quantities, priorities)
- Status tracking (pending, approved, delivered)
- Cost tracking
- Delivery scheduling

### Payments
- Payment details (amount, type, method)
- Status tracking (pending, approved, paid)
- Transaction tracking
- Approval workflow

### Documents
- File metadata (name, size, type)
- Categorization (plans, permits, contracts, etc.)
- Access control (public/private)
- Download tracking

### Notifications
- Notification content (title, message, type)
- Recipient targeting
- Read status tracking
- Action URLs for deep linking

## 🔐 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Granular permissions per role
- **Input Validation**: Comprehensive request validation
- **File Upload Security**: File type and size validation
- **CORS Configuration**: Cross-origin request handling
- **Environment Variables**: Secure configuration management

## 📱 Mobile Integration

### Authentication Flow
1. User registers/logs in
2. JWT token returned
3. Token used for subsequent API calls
4. FCM token registered for push notifications

### Real-time Features
- Push notifications for task assignments
- Issue reporting and updates
- Material request status changes
- Payment approvals and updates

### File Handling
- Secure file uploads with validation
- Image compression and optimization
- Document categorization and tagging
- Download tracking and analytics

## 🛠️ Development

### Project Structure
```
server/
├── src/
│   ├── models/          # MongoDB schemas
│   ├── routes/          # API route handlers
│   ├── middleware/      # Custom middleware
│   ├── types/           # TypeScript type definitions
│   └── index.ts         # Main server file
├── uploads/             # File upload directory
├── package.json         # Dependencies and scripts
└── tsconfig.json        # TypeScript configuration
```

### Available Scripts
- `npm run dev` - Start development server with auto-reload
- `npm run build` - Build for production
- `npm start` - Start production server
- `npm test` - Run tests (when implemented)

### Environment Variables
- `PORT` - Server port (default: 4000)
- `MONGODB_URI` - MongoDB connection string
- `JWT_SECRET` - JWT signing secret
- `UPLOAD_DIR` - File upload directory
- `MAX_FILE_SIZE` - Maximum file upload size

## 🚀 Deployment

### Local Development
```bash
npm run dev
```

### Production Deployment
1. Set environment variables
2. Build the application: `npm run build`
3. Start the server: `npm start`

### Docker Deployment
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build
EXPOSE 4000
CMD ["npm", "start"]
```

## 📊 Monitoring and Logging

- **Morgan**: HTTP request logging
- **Error Handling**: Comprehensive error responses
- **Health Checks**: `/health` endpoint for monitoring
- **API Documentation**: `/api` endpoint for endpoint overview

## 🔄 API Versioning

Current version: `v1.0.0`

API endpoints are versioned through URL structure:
- Current: `/api/*`
- Future versions: `/api/v2/*`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is part of the SHIV Construction Management System.

## 🆘 Support

For support and questions:
- Check the API documentation at `/api`
- Review the health endpoint at `/health`
- Check server logs for detailed error information


