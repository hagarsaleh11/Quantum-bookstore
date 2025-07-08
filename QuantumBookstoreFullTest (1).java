import java.util.*;

// Quantum Bookstore System

// --- Interfaces for Behavior ---
interface Shippable {
    void ship(String address);
}

interface Emailable {
    void email(String email);
}

// --- User Model ---
class User {
    private String name;
    private String email;
    private String address;

    public User(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
}

// --- Abstract Book Base Class ---
abstract class Book {
    protected String bookId;
    protected String title;
    protected int year;
    protected double price;
    protected String author;

    public Book(String bookId, String title, int year, double price, String author) {
        this.bookId = bookId;
        this.title = title;
        this.year = year;
        this.price = price;
        this.author = author;
    }

    public String getBookId() { return bookId; }
    public int getYear() { return year; }
    public double getPrice() { return price; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
}

// --- Book Types ---

// PaperBook: physical with stock, shippable
class PaperBook extends Book implements Shippable {
    private int stockLeft;

    public PaperBook(String bookId, String title, int year, double price, String author, int stockLeft) {
        super(bookId, title, year, price, author);
        this.stockLeft = stockLeft;
    }

    public int getStockLeft() { return stockLeft; }

    public void reduceStock(int quantity) {
        if (stockLeft < quantity)
            throw new IllegalArgumentException("Quantum book store: Not enough stock.");
        stockLeft -= quantity;
    }

    @Override
    public void ship(String address) {
        System.out.println("Quantum book store: Shipping paper book to " + address);
    }
}

// EBook: digital with file type, emailable
class EBook extends Book implements Emailable {
    private String fileType;

    public EBook(String bookId, String title, int year, double price, String author, String fileType) {
        super(bookId, title, year, price, author);
        this.fileType = fileType;
    }

    public String getFileType() { return fileType; }

    @Override
    public void email(String email) {
        System.out.println("Quantum book store: Sending ebook to " + email);
    }
}

// DemoBook: not for sale
class DemoBook extends Book {
    public DemoBook(String bookId, String title, int year, String author) {
        super(bookId, title, year, 0.0, author);
    }
}

// --- Inventory Management ---
class Inventory {
    private Map<String, Book> bookInventory = new HashMap<>();

    public void addBookToInventory(Book b) {
        bookInventory.put(b.getBookId(), b);
        System.out.println("Quantum book store: Book added - " + b.getTitle());
    }

    // Remove books older than a given number of years
    public List<Book> removeOldBooks(int yearsOld, int yearNow) {
        List<Book> removed = new ArrayList<>();
        Iterator<Map.Entry<String, Book>> iterator = bookInventory.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Book> entry = iterator.next();
            Book b = entry.getValue();
            if (yearNow - b.getYear() > yearsOld) {
                removed.add(b);
                iterator.remove();
                System.out.println("Quantum book store: Removed outdated book - " + b.getTitle());
            }
        }
        return removed;
    }

    // Handles book purchasing logic
    public double buyBook(String bookId, int quantity, User user) {
        Book selectedBook = bookInventory.get(bookId);
        if (selectedBook == null)
            throw new IllegalArgumentException("Quantum book store: Book not found.");

        double amountPaid = 0;

        if (selectedBook instanceof PaperBook) {
            PaperBook pb = (PaperBook) selectedBook;
            pb.reduceStock(quantity);
            pb.ship(user.getAddress());
            amountPaid = pb.getPrice() * quantity;
        } else if (selectedBook instanceof EBook) {
            if (quantity != 1)
                throw new IllegalArgumentException("Quantum book store: Only one copy of ebook can be bought at a time.");
            EBook eb = (EBook) selectedBook;
            eb.email(user.getEmail());
            amountPaid = eb.getPrice();
        } else {
            throw new UnsupportedOperationException("Quantum book store: Book is not for sale.");
        }

        System.out.println("Quantum book store: Purchase successful. Paid: " + amountPaid);
        return amountPaid;
    }
}

// --- Test Class ---
public class QuantumBookstoreFullTest {

    public static void main(String[] args) {
        System.out.println("Quantum book store: Please make sure to enter your name, email, and address to proceed with your order.");
        Inventory inv = new Inventory();

        // Initialize inventory
        inv.addBookToInventory(new PaperBook("P001", "Java Basics", 2018, 45.0, "John Smith", 10));
        inv.addBookToInventory(new EBook("E001", "Python for All", 2020, 30.0, "Alice Doe", "PDF"));
        inv.addBookToInventory(new DemoBook("D001", "Data Structures Demo", 2010, "James Bond"));

        // Remove outdated books
        inv.removeOldBooks(10, 2025);

        // Get user info
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your address: ");
        String address = scanner.nextLine();
        User student = new User(name, email, address);

        // Display available books
        System.out.println("\nQuantum book store: Available Books:");
        System.out.println("P001 - Java Basics (Paper)");
        System.out.println("E001 - Python for All (EBook)");
        System.out.println("D001 - Data Structures Demo (Demo Book)");

        // Ask for type before quantity
        System.out.print("Do you want to buy a PaperBook or EBook? (Enter 'paper' or 'ebook'): ");
        String type = scanner.nextLine().trim().toLowerCase();

        System.out.print("Enter the book ID you want to buy: ");
        String bookIdToBuy = scanner.nextLine().trim();

        int quantity = 1;
        if (type.equals("paper")) {
            System.out.print("Enter the quantity: ");
            quantity = Integer.parseInt(scanner.nextLine());
        }

        // Try purchasing
        try {
            inv.buyBook(bookIdToBuy, quantity, student);
        } catch (Exception e) {
            System.out.println("Quantum book store: Error - " + e.getMessage());
        }

        // Attempt demo book (should fail)
        try {
            System.out.println("\nTrying to buy Demo Book (should fail):");
            inv.buyBook("D001", 1, student);
        } catch (Exception err) {
            System.out.println("Quantum book store: Error - " + err.getMessage());
        }

        System.out.println("Quantum book store: All test operations completed.");
    }
}
