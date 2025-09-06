# Railway Deployment Guide

## Environment Variables to Set in Railway:

1. **NODE_ENV** = `production`
2. **PORT** = `4000` (Railway will override this)
3. **JWT_SECRET** = `your-super-secret-jwt-key-change-this-in-production`
4. **MONGODB_URI** = `mongodb+srv://shiv-admin:ShivConstruction2024!@shiv-construction-clust.nkk7mb0.mongodb.net/?retryWrites=true&w=majority&appName=shiv-construction-cluster`
5. **UPLOAD_DIR** = `uploads`

## Deployment Steps:

1. Push code to GitHub
2. Connect Railway to GitHub repo
3. Set environment variables in Railway dashboard
4. Deploy automatically
5. Get public URL
6. Update mobile app with new URL

## After Deployment:

- Update mobile app baseUrl to Railway URL
- Build new APK
- Share with client
