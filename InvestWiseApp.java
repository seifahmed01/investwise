import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.text.DecimalFormat;

public class InvestWiseApp {
    // Constants for file paths
    private static final String USERS_FILE = "users.dat";
    private static final String PORTFOLIOS_FILE = "portfolios.dat";
    private static final String BANK_ACCOUNTS_FILE = "bank_accounts.dat";

    // Database simulation with serialization
    private static Map<String, User> users;
    private static Map<String, List<Asset>> portfolios;
    private static Map<String, BankAccount> bankAccounts;

    // Current logged in user
    private static String currentUser = null;

    // Formatters
    private static final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load data from files
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

    // ===== Data Serialization Methods =====

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

    // ===== User Story 1: Sign Up =====
    public static void userStory1_SignUp(Scanner scanner) {
        System.out.println("\n--- Sign Up ---");

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

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();

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

        users.put(username, new User(name, email, username, password));
        saveData();
        System.out.println(" Sign-up successful! You can now login.");
    }

    // ===== User Story 2: Login =====
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

    // ===== Portfolio View =====
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

    // ===== User Story 3: Add Asset =====
    public static void userStory3_AddAsset(Scanner scanner) {
        System.out.println("\n--- Add Asset ---");

        AssetType type = null;
        while (type == null) {
            System.out.print("Enter asset type (STOCK/REAL_ESTATE/CRYPTO/GOLD): ");
            try {
                type = AssetType.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid asset type. Please choose from: " + Arrays.toString(AssetType.values()));
            }
        }

        double quantity = 0;
        while (quantity <= 0) {
            System.out.print("Enter quantity: ");
            try {
                quantity = Double.parseDouble(scanner.nextLine());
                if (quantity <= 0) {
                    System.out.println("Quantity must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        Asset asset = new Asset(type, quantity);
        portfolios.computeIfAbsent(currentUser, k -> new ArrayList<>()).add(asset);
        saveData();
        System.out.println(" Asset added to portfolio!");
    }

    // ===== User Story 4: Edit/Remove Asset =====
    public static void userStory4_EditRemoveAsset(Scanner scanner) {
        System.out.println("\n--- Edit/Remove Asset ---");
        List<Asset> assets = portfolios.getOrDefault(currentUser, new ArrayList<>());

        if (assets.isEmpty()) {
            System.out.println("You don't have any assets to edit.");
            return;
        }

        viewPortfolio(scanner);

        int index = -1;
        while (true) {
            System.out.print("Enter asset index to edit/remove (or -1 to cancel): ");
            try {
                index = Integer.parseInt(scanner.nextLine());
                if (index == -1) return;
                if (index < 0 || index >= assets.size()) {
                    System.out.println("Invalid index. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        System.out.print("Edit (E) or Remove (R)? ");
        String choice = scanner.nextLine().toUpperCase();

        if (choice.equals("E")) {
            double newQty = 0;
            while (newQty <= 0) {
                System.out.print("Enter new quantity: ");
                try {
                    newQty = Double.parseDouble(scanner.nextLine());
                    if (newQty <= 0) {
                        System.out.println("Quantity must be positive.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
            assets.get(index).setQuantity(newQty);
            saveData();
            System.out.println(" Asset updated!");
        } else if (choice.equals("R")) {
            assets.remove(index);
            saveData();
            System.out.println(" Asset removed!");
        } else {
            System.out.println("Invalid choice. Operation cancelled.");
        }
    }

    // ===== User Story 8: Zakat Calculation =====
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
            System.out.println(" PDF report generated (simulated)!");
        }
    }

    // ===== User Story 10: Connect Bank Account =====
    public static void userStory10_ConnectBank(Scanner scanner) {
        System.out.println("\n--- Connect Bank Account ---");

        String bankName;
        while (true) {
            System.out.print("Enter bank name: ");
            bankName = scanner.nextLine().trim();
            if (!bankName.isEmpty()) break;
            System.out.println("Bank name cannot be empty.");
        }

        String cardNumber;
        while (true) {
            System.out.print("Enter card number (16 digits): ");
            cardNumber = scanner.nextLine().trim().replaceAll("\\s+", "");
            if (cardNumber.matches("\\d{16}")) break;
            System.out.println("Invalid card number. Must be 16 digits.");
        }

        String otp;
        while (true) {
            System.out.print("Enter OTP (6 digits): ");
            otp = scanner.nextLine().trim();
            if (otp.matches("\\d{6}")) break;
            System.out.println("Invalid OTP. Must be 6 digits.");
        }

        // In a real app, you would verify the OTP with a bank API
        System.out.println("Verifying OTP with bank... (simulated)");
        try {
            Thread.sleep(2000); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        bankAccounts.put(currentUser, new BankAccount(bankName, cardNumber));
        saveData();
        System.out.println(" Bank account linked successfully!");
    }

    // ===== Helper Classes =====

    enum AssetType {
        STOCK, REAL_ESTATE, CRYPTO, GOLD
    }

    static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name, email, username;
        private String passwordHash; // Store hashed password only

        public User(String name, String email, String username, String password) {
            this.name = name;
            this.email = email;
            this.username = username;
            this.passwordHash = hashPassword(password);
        }

        private String hashPassword(String password) {
            // In a real app, use BCrypt or similar
            // This is a simplified version for demonstration
            return Integer.toString(password.hashCode());
        }

        public boolean authenticate(String password) {
            return passwordHash.equals(hashPassword(password));
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
    }

    static class Asset implements Serializable {
        private static final long serialVersionUID = 1L;
        private AssetType type;
        private double quantity;

        public Asset(AssetType type, double quantity) {
            this.type = type;
            this.quantity = quantity;
        }

        public void setQuantity(double quantity) { this.quantity = quantity; }
        public double getQuantity() { return quantity; }
        public AssetType getType() { return type; }

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

    static class BankAccount implements Serializable {
        private static final long serialVersionUID = 1L;
        private String bankName;
        private String encryptedCardNumber;

        public BankAccount(String bankName, String cardNumber) {
            this.bankName = bankName;
            this.encryptedCardNumber = encryptCardNumber(cardNumber);
        }

        private String encryptCardNumber(String cardNumber) {
            // Simple encryption for demonstration
            // In a real app, use proper encryption like AES
            return "ENC-" + new StringBuilder(cardNumber).reverse().toString();
        }

        public String getMaskedCardNumber() {
            if (encryptedCardNumber == null || encryptedCardNumber.length() < 8) return "";
            return "---" + encryptedCardNumber.substring(encryptedCardNumber.length() - 4);
        }

        public String getBankName() { return bankName; }
    }
}