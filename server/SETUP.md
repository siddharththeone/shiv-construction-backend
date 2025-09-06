# SHIV Construction Management Backend - Setup Guide

## 🚀 Quick Setup

### Option 1: Using MongoDB Atlas (Recommended)

1. **Create MongoDB Atlas Account**
   - Go to [MongoDB Atlas](https://www.mongodb.com/atlas)
   - Sign up for a free account
   - Create a new cluster (free tier is sufficient)

2. **Get Connection String**
   - In your Atlas dashboard, click "Connect"
   - Choose "Connect your application"
   - Copy the connection string

3. **Update Environment Variables**
   ```bash
   # Create .env file
   cp env.example .env
   ```
   
   Edit `.env` file:
   ```env
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/construction_mgmt?retryWrites=true&w=majority
   JWT_SECRET=your-super-secret-jwt-key-change-in-production
   ```

4. **Install Dependencies**
   ```bash
   npm install
   ```

5. **Start the Server**
   ```bash
   npm run dev
   ```

### Option 2: Using Local MongoDB

1. **Install MongoDB**
   - Download from [MongoDB Download Center](https://www.mongodb.com/try/download/community)
   - Or use Docker: `docker run -d -p 27017:27017 --name mongodb mongo:latest`

2. **Start MongoDB**
   ```bash
   # Windows
   "C:\Program Files\MongoDB\Server\6.0\bin\mongod.exe"
   
   # macOS/Linux
   mongod
   
   # Docker
   docker start mongodb
   ```

3. **Create .env file**
   ```bash
   cp env.example .env
   ```

4. **Install Dependencies**
   ```bash
   npm install
   ```

5. **Start the Server**
   ```bash
   npm run dev
   ```

## 📋 Prerequisites

- **Node.js 18+**: [Download here](https://nodejs.org/)
- **npm or yarn**: Comes with Node.js
- **MongoDB**: Local installation or Atlas account

## 🔧 Environment Configuration

Create a `.env` file in the server directory:

```env
# Server Configuration
PORT=4000
NODE_ENV=development

# Database (Choose one)
# For MongoDB Atlas:
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/construction_mgmt?retryWrites=true&w=majority

# For Local MongoDB:
# MONGODB_URI=mongodb://localhost:27017/construction_mgmt

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-in-production

# File Upload
UPLOAD_DIR=uploads
MAX_FILE_SIZE=10485760

# Firebase Configuration (Optional - for push notifications)
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_PRIVATE_KEY=your-firebase-private-key
FIREBASE_CLIENT_EMAIL=your-firebase-client-email
```

## 🏃‍♂️ Running the Application

### Development Mode
```bash
npm run dev
```

### Production Mode
```bash
npm run build
npm start
```

## 🧪 Testing the API

Once the server is running, test these endpoints:

### Health Check
```bash
curl http://localhost:4000/health
```

### API Documentation
```bash
curl http://localhost:4000/api
```

### Create a Test User
```bash
curl -X POST http://localhost:4000/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Owner",
    "email": "owner@test.com",
    "password": "password123",
    "role": "OWNER"
  }'
```

## 📱 Mobile App Integration

The backend is designed to work with the SHIV Construction Management mobile app. The API provides:

- **Authentication**: JWT-based auth with role-based access
- **Site Management**: CRUD operations for construction sites
- **Task Management**: Assign and track construction tasks
- **Issue Tracking**: Report and resolve site issues
- **Material Requests**: Manage procurement workflow
- **Payment Tracking**: Handle payments and approvals
- **Document Management**: Upload and organize site documents
- **Notifications**: Real-time push notifications

## 🔐 Security Features

- JWT Authentication
- Role-based access control
- Input validation
- File upload security
- CORS configuration

## 📊 Database Schema

The application uses MongoDB with the following collections:

- **users**: User accounts and roles
- **sites**: Construction sites
- **tasks**: Construction tasks
- **issues**: Site issues and problems
- **material_requests**: Material procurement
- **payments**: Payment tracking
- **documents**: File management
- **notifications**: Push notifications

## 🛠️ Troubleshooting

### Common Issues

1. **MongoDB Connection Failed**
   - Check if MongoDB is running
   - Verify connection string in .env
   - Check network connectivity

2. **Port Already in Use**
   - Change PORT in .env file
   - Kill process using port 4000

3. **TypeScript Compilation Errors**
   - Run `npm install` to ensure all dependencies
   - Check for missing type definitions

4. **File Upload Issues**
   - Ensure uploads directory exists
   - Check file size limits
   - Verify file type restrictions

### Logs and Debugging

- Check console output for error messages
- Use `npm run dev` for detailed logging
- Check MongoDB logs if using local installation

## 🚀 Deployment

### Local Development
```bash
npm run dev
```

### Production
1. Set production environment variables
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

## 📞 Support

For issues and questions:
- Check the API documentation at `/api`
- Review server logs for error details
- Ensure all prerequisites are installed
- Verify environment configuration

## 🔄 Next Steps

1. **Set up Firebase** (optional): For push notifications
2. **Configure AWS S3** (optional): For file storage
3. **Set up monitoring**: Add logging and monitoring
4. **Add tests**: Implement unit and integration tests
5. **Deploy to production**: Use cloud hosting services


