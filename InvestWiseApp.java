import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.text.DecimalFormat;

/**
 * InvestWiseApp - A personal investment tracking application with features for
 * managing assets, calculating zakat, and connecting bank accounts.
 *
 * <p>The application provides a console-based interface for users to:
 * <ul>
 *   <li>Create accounts and authenticate</li>
 *   <li>Manage investment portfolios</li>
 *   <li>Calculate zakat obligations</li>
 *   <li>Connect bank accounts</li>
 * </ul>
 *
 * <p>All data is persisted using Java serialization to local files.
 */
public class InvestWiseApp {
    /** File path for storing user data */
    private static final String USERS_FILE = "users.dat";
    /** File path for storing portfolio data */
    private static final String PORTFOLIOS_FILE = "portfolios.dat";
    /** File path for storing bank account data */
    private static final String BANK_ACCOUNTS_FILE = "bank_accounts.dat";

    /** In-memory database of registered users */
    private static Map<String, User> users;
    /** In-memory database of user portfolios */
    private static Map<String, List<Asset>> portfolios;
    /** In-memory database of linked bank accounts */
    private static Map<String, BankAccount> bankAccounts;

    /** Currently authenticated username */
    private static String currentUser = null;

    /** Formatter for currency values */
    private static final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    /**
     * Main entry point for the application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        loadData();

        System.out.println("=== InvestWise App ===");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                showUnauthenticatedMenu(scanner);
            } else {
                showAuthenticatedMenu(scanner);
            }
        }

        scanner.close();
    }

    // ========== Menu Methods ==========

    /**
     * Displays the menu for unauthenticated users (login/signup).
     *
     * @param scanner The Scanner instance for user input
     */
    private static void showUnauthenticatedMenu(Scanner scanner) {
        System.out.println("\nMain Menu (Not Logged In)");
        System.out.println("1. Sign Up");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    userStory1_SignUp(scanner);
                    break;
                case 2:
                    userStory2_Login(scanner);
                    break;
                case 3:
                    saveData();
                    System.out.println("Thank you for using InvestWise!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * Displays the menu for authenticated users (portfolio management).
     *
     * @param scanner The Scanner instance for user input
     */
    private static void showAuthenticatedMenu(Scanner scanner) {
        System.out.println("\nMain Menu (Logged in as " + currentUser + ")");
        System.out.println("1. View Portfolio");
        System.out.println("2. Add Asset");
        System.out.println("3. Edit/Remove Asset");
        System.out.println("4. Calculate Zakat");
        System.out.println("5. Connect Bank Account");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    viewPortfolio(scanner);
                    break;
                case 2:
                    userStory3_AddAsset(scanner);
                    break;
                case 3:
                    userStory4_EditRemoveAsset(scanner);
                    break;
                case 4:
                    userStory8_ZakatCalculation(scanner);
                    break;
                case 5:
                    userStory10_ConnectBank(scanner);
                    break;
                case 6:
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // ========== Data Persistence Methods ==========

    /**
     * Loads application data from serialized files.
     * Initializes empty collections if files don't exist or can't be read.
     */
    @SuppressWarnings("unchecked")
    private static void loadData() {
        try {
            // Load users
            if (Files.exists(Paths.get(USERS_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
                    users = (Map<String, User>) ois.readObject();
                }
            } else {
                users = new HashMap<>();
            }

            // Load portfolios
            if (Files.exists(Paths.get(PORTFOLIOS_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PORTFOLIOS_FILE))) {
                    portfolios = (Map<String, List<Asset>>) ois.readObject();
                }
            } else {
                portfolios = new HashMap<>();
            }

            // Load bank accounts
            if (Files.exists(Paths.get(BANK_ACCOUNTS_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BANK_ACCOUNTS_FILE))) {
                    bankAccounts = (Map<String, BankAccount>) ois.readObject();
                }
            } else {
                bankAccounts = new HashMap<>();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
            // Initialize empty collections if loading fails
            users = new HashMap<>();
            portfolios = new HashMap<>();
            bankAccounts = new HashMap<>();
        }
    }

    /**
     * Saves application data to serialized files.
     * Silently handles IO errors.
     */
    private static void saveData() {
        try {
            // Save users
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
                oos.writeObject(users);
            }

            // Save portfolios
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PORTFOLIOS_FILE))) {
                oos.writeObject(portfolios);
            }

            // Save bank accounts
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BANK_ACCOUNTS_FILE))) {
                oos.writeObject(bankAccounts);
            }

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // ========== User Story Implementations ==========

    /**
     * Implements User Story 1: Allows new users to sign up.
     *
     * @param scanner The Scanner instance for user input
     */
    public static void userStory1_SignUp(Scanner scanner) {
        System.out.println("\n--- Sign Up ---");

        String username = getValidUsername(scanner);
        String name = getNonEmptyInput(scanner, "Enter name: ");
        String email = getValidEmail(scanner);
        String password = getValidPassword(scanner);

        users.put(username, new User(name, email, username, password));
        saveData();
        System.out.println(" Sign-up successful! You can now login.");
    }

    /**
     * Implements User Story 2: Allows existing users to login.
     *
     * @param scanner The Scanner instance for user input
     */
    public static void userStory2_Login(Scanner scanner) {
        System.out.println("\n--- Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user != null && user.authenticate(password)) {
            currentUser = username;
            System.out.println(" Login successful! Welcome, " + user.getName() + ".");
        } else {
            System.out.println(" Invalid username or password.");
        }
    }

    /**
     * Displays the current user's portfolio with asset values.
     *
     * @param scanner The Scanner instance for user input
     */
    private static void viewPortfolio(Scanner scanner) {
        System.out.println("\n--- Your Portfolio ---");

        List<Asset> assets = portfolios.getOrDefault(currentUser, new ArrayList<>());
        if (assets.isEmpty()) {
            System.out.println("You don't have any assets yet.");
            return;
        }

        double totalValue = 0;
        System.out.println("Your assets:");
        for (int i = 0; i < assets.size(); i++) {
            Asset asset = assets.get(i);
            double value = asset.calculateValue();
            totalValue += value;
            System.out.printf("%d: %s - Quantity: %.2f, Value: %s\n",
                    i, asset.getType(), asset.getQuantity(), currencyFormat.format(value));
        }

        System.out.printf("\nTotal Portfolio Value: %s\n", currencyFormat.format(totalValue));
    }

    /**
     * Implements User Story 3: Allows users to add assets to their portfolio.
     *
     * @param scanner The Scanner instance for user input
     */
    public static void userStory3_AddAsset(Scanner scanner) {
        System.out.println("\n--- Add Asset ---");

        AssetType type = getValidAssetType(scanner);
        double quantity = getPositiveDouble(scanner, "Enter quantity: ");

        Asset asset = new Asset(type, quantity);
        portfolios.computeIfAbsent(currentUser, k -> new ArrayList<>()).add(asset);
        saveData();
        System.out.println(" Asset added to portfolio!");
    }

    /**
     * Implements User Story 4: Allows users to edit or remove assets.
     *
     * @param scanner The Scanner instance for user input
     */
    public static void userStory4_EditRemoveAsset(Scanner scanner) {
        System.out.println("\n--- Edit/Remove Asset ---");
        List<Asset> assets = portfolios.getOrDefault(currentUser, new ArrayList<>());

        if (assets.isEmpty()) {
            System.out.println("You don't have any assets to edit.");
            return;
        }

        viewPortfolio(scanner);

        int index = getValidAssetIndex(scanner, assets.size());
        if (index == -1) return;

        System.out.print("Edit (E) or Remove (R)? ");
        String choice = scanner.nextLine().toUpperCase();

        if (choice.equals("E")) {
            double newQty = getPositiveDouble(scanner, "Enter new quantity: ");
            assets.get(index).setQuantity(newQty);
            saveData();
            System.out.println("✅ Asset updated!");
        } else if (choice.equals("R")) {
            assets.remove(index);
            saveData();
            System.out.println("✅ Asset removed!");
        } else {
            System.out.println("Invalid choice. Operation cancelled.");
        }
    }

    /**
     * Implements User Story 8: Calculates zakat obligation based on portfolio value.
     *
     * @param scanner The Scanner instance for user input
     */
    public static void userStory8_ZakatCalculation(Scanner scanner) {
        System.out.println("\n--- Zakat Calculation ---");
        List<Asset> assets = portfolios.getOrDefault(currentUser, new ArrayList<>());

        if (assets.isEmpty()) {
            System.out.println("You don't have any assets to calculate zakat for.");
            return;
        }

        double totalValue = assets.stream().mapToDouble(Asset::calculateValue).sum();
        double zakat = totalValue * 0.025; // 2.5%

        System.out.println("Asset Summary:");
        assets.forEach(asset -> System.out.printf("- %s: %s\n",
                asset.getType(), currencyFormat.format(asset.calculateValue())));

        System.out.printf("\nTotal Value: %s\n", currencyFormat.format(totalValue));
        System.out.printf("Zakat Due (2.5%%): %s\n", currencyFormat.format(zakat));

        System.out.print("Generate PDF report? (Y/N): ");
        if (scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("📄 PDF report generated (simulated)!");
        }
    }

    /**
     * Implements User Story 10: Allows users to connect bank accounts.
     *
     * @param scanner The Scanner instance for user input
     */
    public static void userStory10_ConnectBank(Scanner scanner) {
        System.out.println("\n--- Connect Bank Account ---");

        String bankName = getNonEmptyInput(scanner, "Enter bank name: ");
        String cardNumber = getValidCardNumber(scanner);
        String otp = getValidOTP(scanner);

        // Simulate OTP verification
        System.out.println("Verifying OTP with bank... (simulated)");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        bankAccounts.put(currentUser, new BankAccount(bankName, cardNumber));
        saveData();
        System.out.println("✅ Bank account linked successfully!");
    }

    // ========== Helper Methods ==========

    /**
     * Prompts user for a valid username (unique and non-empty).
     *
     * @param scanner The Scanner instance for user input
     * @return Valid username
     */
    private static String getValidUsername(Scanner scanner) {
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
                continue;
            }

            if (users.containsKey(username)) {
                System.out.println("Username already exists. Please choose another.");
            } else {
                break;
            }
        }
        return username;
    }

    /**
     * Prompts user for a valid email address.
     *
     * @param scanner The Scanner instance for user input
     * @return Valid email address
     */
    private static String getValidEmail(Scanner scanner) {
        String email;
        while (true) {
            System.out.print("Enter email: ");
            email = scanner.nextLine().trim();

            if (email.isEmpty() || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                System.out.println("Invalid email format. Please try again.");
            } else {
                break;
            }
        }
        return email;
    }

    /**
     * Prompts user for a valid password (minimum length 8 characters).
     *
     * @param scanner The Scanner instance for user input
     * @return Valid password
     */
    private static String getValidPassword(Scanner scanner) {
        String password;
        while (true) {
            System.out.print("Enter password (min 8 chars): ");
            password = scanner.nextLine().trim();

            if (password.length() < 8) {
                System.out.println("Password must be at least 8 characters.");
            } else {
                break;
            }
        }
        return password;
    }

    /**
     * Prompts user for a valid asset type.
     *
     * @param scanner The Scanner instance for user input
     * @return Valid AssetType enum value
     */
    private static AssetType getValidAssetType(Scanner scanner) {
        AssetType type = null;
        while (type == null) {
            System.out.print("Enter asset type (STOCK/REAL_ESTATE/CRYPTO/GOLD): ");
            try {
                type = AssetType.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid asset type. Please choose from: " + Arrays.toString(AssetType.values()));
            }
        }
        return type;
    }

    /**
     * Prompts user for a positive double value.
     *
     * @param scanner The Scanner instance for user input
     * @param prompt The prompt message to display
     * @return Positive double value
     */
    private static double getPositiveDouble(Scanner scanner, String prompt) {
        double value = 0;
        while (value <= 0) {
            System.out.print(prompt);
            try {
                value = Double.parseDouble(scanner.nextLine());
                if (value <= 0) {
                    System.out.println("Value must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        return value;
    }

    /**
     * Prompts user for a valid asset index or cancel (-1).
     *
     * @param scanner The Scanner instance for user input
     * @param maxIndex The maximum valid index (exclusive)
     * @return Valid index or -1 to cancel
     */
    private static int getValidAssetIndex(Scanner scanner, int maxIndex) {
        int index = -1;
        while (true) {
            System.out.print("Enter asset index to edit/remove (or -1 to cancel): ");
            try {
                index = Integer.parseInt(scanner.nextLine());
                if (index == -1) return -1;
                if (index < 0 || index >= maxIndex) {
                    System.out.println("Invalid index. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        return index;
    }

    /**
     * Prompts user for non-empty input.
     *
     * @param scanner The Scanner instance for user input
     * @param prompt The prompt message to display
     * @return Non-empty string input
     */
    private static String getNonEmptyInput(Scanner scanner, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) break;
            System.out.println("Input cannot be empty.");
        }
        return input;
    }

    /**
     * Prompts user for a valid 16-digit card number.
     *
     * @param scanner The Scanner instance for user input
     * @return Valid card number
     */
    private static String getValidCardNumber(Scanner scanner) {
        String cardNumber;
        while (true) {
            System.out.print("Enter card number (16 digits): ");
            cardNumber = scanner.nextLine().trim().replaceAll("\\s+", "");
            if (cardNumber.matches("\\d{16}")) break;
            System.out.println("Invalid card number. Must be 16 digits.");
        }
        return cardNumber;
    }

    /**
     * Prompts user for a valid 6-digit OTP.
     *
     * @param scanner The Scanner instance for user input
     * @return Valid OTP
     */
    private static String getValidOTP(Scanner scanner) {
        String otp;
        while (true) {
            System.out.print("Enter OTP (6 digits): ");
            otp = scanner.nextLine().trim();
            if (otp.matches("\\d{6}")) break;
            System.out.println("Invalid OTP. Must be 6 digits.");
        }
        return otp;
    }

    // ========== Inner Classes ==========

    /**
     * Enumeration of supported asset types.
     */
    enum AssetType {
        /** Stocks and equities */
        STOCK,
        /** Real estate properties */
        REAL_ESTATE,
        /** Cryptocurrencies */
        CRYPTO,
        /** Gold and precious metals */
        GOLD
    }

    /**
     * Represents a user of the InvestWise application.
     */
    static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final String email;
        private final String username;
        private final String passwordHash;

        /**
         * Creates a new User instance.
         *
         * @param name The user's full name
         * @param email The user's email address
         * @param username The user's unique username
         * @param password The user's password (will be hashed)
         */
        public User(String name, String email, String username, String password) {
            this.name = name;
            this.email = email;
            this.username = username;
            this.passwordHash = hashPassword(password);
        }

        /**
         * Hashes a password for secure storage.
         *
         * @param password The plaintext password
         * @return Hashed password (simplified for demo)
         */
        private String hashPassword(String password) {
            // In a real app, use BCrypt or similar
            return Integer.toString(password.hashCode());
        }

        /**
         * Authenticates a user with the given password.
         *
         * @param password The password to check
         * @return true if authentication succeeds, false otherwise
         */
        public boolean authenticate(String password) {
            return passwordHash.equals(hashPassword(password));
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
    }

    /**
     * Represents an investment asset in a user's portfolio.
     */
    static class Asset implements Serializable {
        private static final long serialVersionUID = 1L;
        private final AssetType type;
        private double quantity;

        /**
         * Creates a new Asset instance.
         *
         * @param type The type of asset
         * @param quantity The quantity/amount of the asset
         */
        public Asset(AssetType type, double quantity) {
            this.type = type;
            this.quantity = quantity;
        }

        public void setQuantity(double quantity) { this.quantity = quantity; }
        public double getQuantity() { return quantity; }
        public AssetType getType() { return type; }

        /**
         * Calculates the estimated value of this asset.
         *
         * @return The calculated value in USD
         */
        public double calculateValue() {
            // Simplified valuation - in a real app, fetch current prices
            switch (type) {
                case STOCK: return quantity * 150; // Average stock price
                case REAL_ESTATE: return quantity * 250000; // Average property value
                case CRYPTO: return quantity * 50000; // Average crypto price
                case GOLD: return quantity * 1800; // Gold per ounce
                default: return 0;
            }
        }
    }

    /**
     * Represents a linked bank account for a user.
     */
    static class BankAccount implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String bankName;
        private final String encryptedCardNumber;

        /**
         * Creates a new BankAccount instance.
         *
         * @param bankName The name of the bank
         * @param cardNumber The card number (will be encrypted)
         */
        public BankAccount(String bankName, String cardNumber) {
            this.bankName = bankName;
            this.encryptedCardNumber = encryptCardNumber(cardNumber);
        }

        /**
         * Encrypts a card number for secure storage.
         *
         * @param cardNumber The plaintext card number
         * @return Encrypted card number (simplified for demo)
         */
        private String encryptCardNumber(String cardNumber) {
            // Simple encryption for demonstration
            // In a real app, use proper encryption like AES
            return "ENC-" + new StringBuilder(cardNumber).reverse().toString();
        }

        /**
         * Gets a masked version of the card number for display.
         *
         * @return Masked card number (showing only last 4 digits)
         */
        public String getMaskedCardNumber() {
            if (encryptedCardNumber == null || encryptedCardNumber.length() < 8) return "";
            return "---" + encryptedCardNumber.substring(encryptedCardNumber.length() - 4);
        }

        public String getBankName() { return bankName; }
    }
}