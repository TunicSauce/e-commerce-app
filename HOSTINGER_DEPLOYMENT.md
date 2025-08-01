w# Hostinger VPS Deployment Guide

## Prerequisites
- Hostinger VPS account
- Domain name (optional but recommended)
- Production JAR file: `build/libs/e-commerce-app-0.0.1-SNAPSHOT.jar`

## Step 1: Set Up Hostinger VPS

1. **Purchase Hostinger VPS Plan**
   - Go to hostinger.com
   - Choose a VPS plan (minimum 2GB RAM recommended)
   - Select Ubuntu 20.04 or 22.04 LTS

2. **Access Your VPS**
   ```bash
   ssh root@148.230.88.118
   ```

## Step 2: Install Java 17

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Verify installation
java -version
```

## Step 3: Install PostgreSQL

```bash
# Install PostgreSQL
sudo apt install postgresql postgresql-contrib -y

# Start and enable PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database and user
sudo -u postgres psql
```

In PostgreSQL shell:
```sql
CREATE DATABASE ecommerce;
CREATE USER ecommerceuser WITH ENCRYPTED PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE ecommerce TO ecommerceuser;
\q
```

## Step 4: Install Nginx

```bash
# Install Nginx
sudo apt install nginx -y

# Start and enable Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

## Step 5: Upload Application

```bash
# Create application directory
sudo mkdir -p /opt/ecommerce
sudo chown $USER:$USER /opt/ecommerce

# Upload JAR file (use SCP from local machine)
scp build/libs/e-commerce-app-0.0.1-SNAPSHOT.jar root@your-vps-ip:/opt/ecommerce/
```

## Step 6: Create Environment File

```bash
# Create environment file
sudo nano /opt/ecommerce/.env
```

Add environment variables:
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecommerce
DB_USER=ecommerceuser
DB_PASSWORD=your_secure_password
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
STRIPE_SECRET_KEY=your_stripe_secret_key
STRIPE_PUBLISHABLE_KEY=your_stripe_publishable_key
```

## Step 7: Create Systemd Service

```bash
sudo nano /etc/systemd/system/ecommerce.service
```

Add service configuration:
```ini
[Unit]
Description=E-Commerce Spring Boot Application
After=syslog.target

[Service]
User=root
ExecStart=/usr/bin/java -jar /opt/ecommerce/e-commerce-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
SuccessExitStatus=143
EnvironmentFile=/opt/ecommerce/.env

[Install]
WantedBy=multi-user.target
```

## Step 8: Configure Nginx

```bash
sudo nano /etc/nginx/sites-available/ecommerce
```

Add Nginx configuration:
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable the site:
```bash
sudo ln -s /etc/nginx/sites-available/ecommerce /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## Step 9: Start Application

```bash
# Reload systemd and start service
sudo systemctl daemon-reload
sudo systemctl enable ecommerce
sudo systemctl start ecommerce

# Check status
sudo systemctl status ecommerce

```

## Step 10: Set Up SSL (Optional but Recommended)

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Get SSL certificate
sudo certbot --nginx -d your-domain.com
```

## Useful Commands

```bash
# View application logs
sudo journalctl -u ecommerce -f

# Restart application
sudo systemctl restart ecommerce

# Check application status
sudo systemctl status ecommerce

# Test database connection
sudo -u postgres psql -d ecommerce -c "SELECT version();"
```

## Firewall Configuration

```bash
# Configure UFW firewall
sudo ufw allow ssh
sudo ufw allow 'Nginx Full'
sudo ufw enable
```

## Environment Variables Required

- `DB_HOST`: Database host (localhost)
- `DB_PORT`: Database port (5432)
- `DB_NAME`: Database name (ecommerce)
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password
- `MAIL_USERNAME`: Gmail address
- `MAIL_PASSWORD`: Gmail app password
- `STRIPE_SECRET_KEY`: Stripe secret key
- `STRIPE_PUBLISHABLE_KEY`: Stripe publishable key

## Troubleshooting

1. **Application won't start**: Check logs with `sudo journalctl -u ecommerce -f`
2. **Database connection issues**: Verify PostgreSQL is running and credentials are correct
3. **502 Bad Gateway**: Check if application is running on port 8080
4. **Email not working**: Verify Gmail app password and SMTP settings