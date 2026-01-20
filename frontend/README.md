## Features

### 🎬 VHS Catalog Management
- **Browse Catalog**: View all available VHS titles with filtering by your favorite genres
- **Add New VHS**: Admin functionality to add new VHS titles with image upload, pricing, stock level, and status management
- **VHS Details**: View detailed information about each VHS including release date, genre, rental price, and user reviews

### 👤 User Management
- **Authentication**: Secure login and registration with automatic token refresh
- **Access Token Auto-Refresh**: Seamless token refresh using refresh tokens stored in secure cookies - users stay logged in without interruption
- **User Profile**: Manage your profile information and select favorite genres to personalize your catalog view

### 🎯 Favorite Genres
- **Genre Selection**: Choose your favorite genres from a comprehensive list
- **Smart Filtering**: VHS catalog automatically filters based on your selected favorite genres
- **Genre Browser**: Browse all available genres in the system

### 🏆 Rental Management
- **Rent VHS**: Rent titles with flexible due dates (minimum tomorrow)
- **My Rentals**: View all your active and returned rentals with detailed information:
  - Rental date and due date
  - Actual return date (when applicable)
  - Status indicators (Active, Returned, DELAYED)
  - Rental price per day
- **Delayed Detection**: Automatic detection of late returns with clear visual indicators

### ⭐ Review System
- **Write Reviews**: Submit ratings and comments for VHS titles you've rented
- **My Reviews**: Comprehensive review management dashboard with two sections:
  - **Waiting to Review**: Shows returned rentals ready for review
  - **My Submitted Reviews**: View, edit, and delete your submitted reviews
- **Read Reviews**: View reviews from other users on each VHS detail page

### 🔐 Security Features
- **JWT Authentication**: Secure token-based authentication
- **Automatic Token Refresh**: Access tokens automatically refresh when expired using refresh tokens
- **Protected Routes**: Restricted access to authenticated features
- **Secure Cookie Storage**: Refresh tokens stored in secure, httpOnly cookies

### 🎨 User Experience
- **Responsive Design**: Mobile-friendly interface that works on all devices
- **Error Handling**: Clear, user-friendly error messages from backend
- **Form Validation**: Client-side validation with server-side backup validation

### 📱 Application Routes
- `/` - Home page
- `/login` - Login
- `/register` - Registration
- `/vhs` - VHS Catalog
- `/vhs/add` - Add new VHS
- `/vhs/:id` - VHS Details
- `/rentals` - My Rentals
- `/reviews` - My Reviews
- `/genres` - Browse Genres
- `/profile` - User Profile
