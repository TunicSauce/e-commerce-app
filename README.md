# Root & Branch E-Commerce Application

A full-featured e-commerce web application built with Spring Boot, specializing in handcrafted wooden products for home and office.

## 🌐 Live Site

**Website**: [https://rootandbranch.site](https://rootandbranch.site)

## 🚀 Features

### Customer Features
- **Product Catalog**: Browse handcrafted wooden products with detailed descriptions
- **Shopping Cart**: Add/remove items, update quantities
- **User Authentication**: Secure registration and login system
- **Wishlist**: Save favorite products for later
- **Checkout**: Complete purchase flow with Stripe payment integration
- **Order History**: View past orders and order details
- **Reviews**: Rate and review products
- **Email Notifications**: Order confirmations and updates

### Admin Features
- **Admin Dashboard**: Comprehensive management interface
- **Product Management**: Add, edit, delete products
- **Order Management**: View and update order statuses
- **User Management**: Manage customer accounts and roles
- **Discount Codes**: Create and manage promotional codes
- **Review Management**: Moderate customer reviews

### Security Features
- **Rate Limiting**: Prevents spam and abuse
- **Input Sanitization**: XSS protection
- **Password Validation**: Strong password requirements
- **Secure Authentication**: BCrypt password hashing
- **HTTPS**: SSL/TLS encryption

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: PostgreSQL
- **Security**: Spring Security 6
- **Template Engine**: Thymeleaf
- **Email**: Spring Mail (Gmail SMTP)
- **Payments**: Stripe API
- **Frontend**: Bootstrap 5, Font Awesome
- **Deployment**: Hostinger VPS, Nginx, Let's Encrypt SSL

## 📦 Installation & Setup

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Gradle 7+

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/TunicSauce/e-commerce-app.git
   cd e-commerce-app
   ```

2. **Configure Database**
   ```sql
   CREATE DATABASE ecommerce;
   CREATE USER ecommerceuser WITH ENCRYPTED PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE ecommerce TO ecommerceuser;
   ```

3. **Environment Variables**
   Create `.env.local` file:
   ```bash
   DATABASE_PASSWORD=your_database_password
   MAIL_USERNAME=your_email@gmail.com
   MAIL_PASSWORD=your_gmail_app_password
   STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
   STRIPE_PUBLISHABLE_KEY=pk_test_your_stripe_publishable_key
   ```

4. **Run the Application**
   ```bash
   ./gradlew bootRun
   ```

5. **Access the Application**
   - Website: http://localhost:8080
   - Admin Panel: http://localhost:8080/admin

## 👤 Default Admin Account

- **Email**: admin@geraldikem.com
- **Password**: password123

## 🏗 Project Structure

```
src/main/java/com/geraldikem/ecommerceapp/
├── config/          # Security and configuration
├── controller/      # Web controllers
├── dto/            # Data transfer objects
├── model/          # Entity models
├── repository/     # Data access layer
├── service/        # Business logic
└── util/           # Utility classes

src/main/resources/
├── templates/      # Thymeleaf templates
├── static/         # CSS, JS, images
├── application.properties
├── application-prod.properties
└── data.sql        # Sample data
```

## 🔧 Key Components

### Models
- **User**: Customer accounts and admin users
- **Product**: Wooden products with details and inventory
- **Order**: Customer orders and order items
- **Review**: Product reviews and ratings
- **DiscountCode**: Promotional discount codes

### Services
- **ProductService**: Product catalog management
- **OrderService**: Order processing and management
- **UserService**: User authentication and management
- **PaymentService**: Stripe payment integration
- **EmailService**: Email notifications
- **ReviewService**: Product review system

## 🌐 Deployment

The application is deployed on Hostinger VPS with:
- **SSL Certificate**: Let's Encrypt (auto-renewal)
- **Reverse Proxy**: Nginx
- **Database**: PostgreSQL
- **Process Management**: Systemd service

## 📝 API Endpoints

### Public Endpoints
- `GET /` - Homepage
- `GET /products` - Product catalog
- `GET /products/{id}` - Product details
- `POST /register` - User registration
- `POST /login` - User login

### Admin Endpoints
- `GET /admin` - Admin dashboard
- `GET /admin/products` - Product management
- `GET /admin/orders` - Order management
- `GET /admin/users` - User management

## 🔒 Security Features

- **CSRF Protection**: Enabled for all forms
- **XSS Protection**: Input sanitization
- **SQL Injection**: JPA/Hibernate protection
- **Rate Limiting**: Review spam prevention
- **Password Security**: BCrypt hashing with strong validation

## 🎨 UI/UX Features

- **Responsive Design**: Mobile-friendly Bootstrap layout
- **Product Images**: Placeholder images with consistent styling
- **Search & Filter**: Product catalog filtering
- **Shopping Cart**: Dynamic cart management
- **User Dashboard**: Order history and profile management

## 📧 Contact

For questions or support, please contact: [geraldikem18@gmail.com](mailto:geraldikem18@gmail.com)
