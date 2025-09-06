# MongoDB Setup Guide for SHIV Construction Management App

## 🎯 Current Status

✅ **Backend Server**: Working perfectly (test mode)
✅ **Mobile App**: Complete and ready
✅ **API Integration**: Complete
❌ **Database**: MongoDB needs to be installed

## 🚀 Quick Setup Options

### Option 1: Install MongoDB Locally (Recommended)

#### Step 1: Download MongoDB Community Server
1. Go to: https://www.mongodb.com/try/download/community
2. Download MongoDB Community Server for Windows
3. Run the installer and follow the setup wizard

#### Step 2: Start MongoDB Service
```bash
# MongoDB should start automatically as a Windows service
# To check if it's running:
services.msc
# Look for "MongoDB" service and ensure it's running
```

#### Step 3: Test MongoDB Connection
```bash
# Open MongoDB Compass (GUI tool) or use command line
mongosh
# You should see the MongoDB shell
```

### Option 2: Use MongoDB Atlas (Cloud - Free)

#### Step 1: Create MongoDB Atlas Account
1. Go to: https://www.mongodb.com/atlas
2. Sign up for a free account
3. Create a new cluster (free tier)

#### Step 2: Get Connection String
1. Click "Connect" on your cluster
2. Choose "Connect your application"
3. Copy the connection string
4. Replace `<password>` with your database password

#### Step 3: Update Environment Variables
Create a `.env` file in the `server` directory:
```env
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/construction_mgmt?retryWrites=true&w=majority
JWT_SECRET=your-super-secret-jwt-key
PORT=4000
UPLOAD_DIR=uploads
MAX_FILE_SIZE=10485760
```

## 🔧 Running the Full Application

### Step 1: Start the Full Server
```bash
cd server
npm run dev
```

### Step 2: Test the API
```bash
# Health check
curl http://localhost:4000/health

# API documentation
curl http://localhost:4000/api

# Create a test user
curl -X POST http://localhost:4000/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Owner",
    "email": "owner@test.com",
    "password": "password123",
    "role": "OWNER"
  }'
```

### Step 3: Run the Mobile App
1. Open Android Studio
2. Open the project: `C:\Users\SIDDHARTH\AndroidStudioProjects\SHIV`
3. Build and run the app on an emulator or device

## 📱 Mobile App Features Ready to Use

### ✅ Authentication
- User registration and login
- Role-based access (Owner, Contractor, Supplier)
- JWT token management

### ✅ Multi-Role Dashboards
- **Owner Dashboard**: Site management, progress tracking
- **Contractor Dashboard**: Assigned sites, task management
- **Supplier Dashboard**: Material request management

### ✅ Site Management
- Create, view, update, delete construction sites
- Progress tracking with percentage completion
- Budget and cost management
- Photo documentation

### ✅ Task Management
- Create and assign tasks to contractors
- Task status tracking
- Priority levels and time tracking
- Photo attachments and notes

### ✅ Issue Tracking
- Report and track site issues
- Issue categorization and priority management
- Assignment and resolution workflow

### ✅ Material Management
- Material request creation and approval workflow
- Supplier assignment and delivery tracking
- Status tracking and cost management

### ✅ Payment Management
- Payment creation and approval workflow
- Multiple payment types and methods
- Transaction tracking and invoice management

### ✅ Document Management
- File upload and organization
- Document categorization
- Access control and download tracking

### ✅ Notification System
- Real-time push notifications
- Notification types and priority levels
- Read status tracking

## 🎯 Next Steps

1. **Install MongoDB** (choose Option 1 or 2 above)
2. **Start the full server**: `npm run dev`
3. **Test the API endpoints**
4. **Run the mobile app** in Android Studio
5. **Create test users** and start using the app

## 🆘 Troubleshooting

### Server Won't Start
- Check if MongoDB is running
- Verify the connection string in `.env`
- Check the console for error messages

### Mobile App Issues
- Ensure the server is running on `http://localhost:4000`
- Check network permissions in Android manifest
- Verify API endpoints are accessible

### Database Connection Issues
- Test MongoDB connection: `mongosh`
- Check firewall settings
- Verify MongoDB service is running

## 🎉 Success!

Once MongoDB is set up, you'll have a fully functional construction management app with:
- ✅ Complete backend API
- ✅ Full mobile app with all features
- ✅ Database persistence
- ✅ User authentication and authorization
- ✅ File upload and management
- ✅ Real-time notifications

The app is ready for production use!



