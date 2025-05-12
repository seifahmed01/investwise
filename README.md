# InvestWise App - README

## Overview
This Java application implements the core functionality of the InvestWise investment management system based on the Software Requirements Specifications (SRS) document. It covers the following user stories marked with "Implement this" in the SRS:

1. User Sign-up (US #1)
2. User Login (US #2)
3. Add Assets (US #3)
4. Edit/Remove Assets (US #4)
8. Zakat Calculation (US #8)
10. Connect Bank Account (US #10)

## Features

### Authentication
- User registration with validation for:
  - Unique username
  - Valid email format
  - Secure password (minimum 8 characters)
- User login with credential verification

### Portfolio Management
- Add assets of different types (Stocks, Real Estate, Crypto, Gold)
- View portfolio with calculated asset values
- Edit asset quantities
- Remove assets from portfolio

### Financial Tools
- Zakat calculation (2.5% of total portfolio value)
- Simulated PDF report generation

### Bank Integration
- Connect bank accounts with:
  - Bank name validation
  - Card number validation (16 digits)
  - OTP verification (simulated)

## Data Persistence
- All data is saved to binary files:
  - `users.dat` - User accounts
  - `portfolios.dat` - Investment portfolios
  - `bank_accounts.dat` - Connected bank accounts
- Data is automatically loaded when the app starts and saved when changes are made

## How to Run
1. Ensure you have Java installed (JDK 8 or later)
2. Compile the program:
   ```
   javac InvestWiseApp.java
   ```
3. Run the program:
   ```
   java InvestWiseApp
   ```

## Usage Instructions

### Main Menu (Not Logged In)
1. **Sign Up** - Create a new account
2. **Login** - Access your existing account
3. **Exit** - Quit the application

### Main Menu (Logged In)
1. **View Portfolio** - See your current assets and total value
2. **Add Asset** - Add a new asset to your portfolio
3. **Edit/Remove Asset** - Modify or delete existing assets
4. **Calculate Zakat** - Calculate your zakat obligation
5. **Connect Bank Account** - Link your bank account
6. **Logout** - Return to the unauthenticated menu

## Technical Details

### Classes
- **User**: Represents a user account with authentication
- **Asset**: Represents an investment asset with type and quantity
- **BankAccount**: Represents a connected bank account
- **InvestWiseApp**: Main application class with all business logic

### Data Validation
- Username: Unique, not empty
- Email: Valid format
- Password: Minimum 8 characters
- Asset quantity: Positive number
- Card number: Exactly 16 digits
- OTP: Exactly 6 digits

### Security (Simplified for Demo)
- Passwords are hashed (simple hash for demonstration)
- Card numbers are "encrypted" (reversed for demonstration)

## Limitations
This is a console-based demonstration version with:
- Simulated financial calculations (uses fixed prices)
- Simplified security measures
- Basic data validation
- No real bank API integration

For a production system, you would need to:
1. Implement proper password hashing (BCrypt, etc.)
2. Add real financial data APIs
3. Implement actual bank integrations
4. Add more robust error handling
5. Create a proper GUI (Android/iOS/Web)
