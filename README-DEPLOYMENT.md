# Deployment Guide

## Environment Variables Required

### Database
- `DATABASE_URL` - PostgreSQL connection string
- `DATABASE_USERNAME` - Database username  
- `DATABASE_PASSWORD` - Database password

### Email (Gmail SMTP)
- `MAIL_USERNAME` - Your Gmail address
- `MAIL_PASSWORD` - Gmail App Password (not regular password)

### Stripe Payment
- `STRIPE_SECRET_KEY` - Stripe secret key (sk_test_...)
- `STRIPE_PUBLISHABLE_KEY` - Stripe publishable key (pk_test_...)

### Railway Deployment
- `SPRING_PROFILES_ACTIVE=prod` - Activates production configuration

## Local Development

1. Copy your values to `.env.local` (already created)
2. Set environment variables in your IDE or use the .env.local file
3. Run: `./gradlew bootRun`

## Production Deployment

1. Set all environment variables in Railway dashboard
2. Push to GitHub (connected to Railway)
3. Railway will automatically build and deploy

## Security Notes

- All sensitive data is now in environment variables
- `.env.local` is git-ignored
- Production uses `application-prod.properties`
- Database connections are secured