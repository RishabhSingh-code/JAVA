/*
Library Management System - Console-based
Step-by-step design included as comments.

Features implemented:
1. Book class: id, title, author, totalCopies, availableCopies
2. Member class: id, name
3. Library class: manages books and members, supports add/remove/search/list, borrow, return
4. Simple persistence via saving/loading to a text file (optional) -- NOT implemented to keep example simple and focused on core logic
5. Main class: interactive menu-driven CLI; sample data added at startup

How to run:
1. Save this file as LibraryManagementSystem.java
2. Compile: javac LibraryManagementSystem.java
3. Run: java LibraryManagementSystem

Notes on extension:
- Replace in-memory lists with a database or file persistence.
- Add GUI (Swing/JavaFX) or web API (Spring Boot).
- Add more validation, logging, and exception handling for production.
*/

import java.util.*;

class Book {
    private final String id;
    private final String title;
    private final String author;
    private int totalCopies;
    private int availableCopies;

    public Book(String id, String title, String author, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.totalCopies = Math.max(0, copies);
        this.availableCopies = this.totalCopies;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public boolean borrow() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    public boolean returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            return true;
        }
        return false;
    }

    public void addCopies(int n) {
        if (n > 0) {
            totalCopies += n;
            availableCopies += n;
        }
    }

    public void removeCopies(int n) {
        if (n <= 0) return;
        int remove = Math.min(n, totalCopies);
        // Reduce available copies first if needed
        int reduceAvailable = Math.min(remove, availableCopies);
        availableCopies -= reduceAvailable;
        totalCopies -= remove;
        if (totalCopies < 0) totalCopies = 0;
        if (availableCopies < 0) availableCopies = 0;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s (Available: %d/%d)", id, title, author, availableCopies, totalCopies);
    }
}

class Member {
    private final String id;
    private final String name;
    private final List<String> borrowedBookIds = new ArrayList<>();

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public void borrowBook(String bookId) {
        borrowedBookIds.add(bookId);
    }

    public boolean returnBook(String bookId) {
        return borrowedBookIds.remove(bookId);
    }

    public List<String> getBorrowedBooks() {
        return Collections.unmodifiableList(borrowedBookIds);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Borrowed: %d", name, id, borrowedBookIds.size());
    }
}

class Library {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();

    // Book operations
    public boolean addBook(Book book) {
        if (books.containsKey(book.getId())) return false;
        books.put(book.getId(), book);
        return true;
    }

    public boolean removeBook(String bookId) {
        return books.remove(bookId) != null;
    }

    public Book findBookById(String id) {
        return books.get(id);
    }

    public List<Book> searchByTitle(String query) {
        String q = query.toLowerCase();
        List<Book> res = new ArrayList<>();
        for (Book b : books.values()) if (b.getTitle().toLowerCase().contains(q)) res.add(b);
        return res;
    }

    public List<Book> searchByAuthor(String query) {
        String q = query.toLowerCase();
        List<Book> res = new ArrayList<>();
        for (Book b : books.values()) if (b.getAuthor().toLowerCase().contains(q)) res.add(b);
        return res;
    }

    public List<Book> listAllBooks() {
        List<Book> all = new ArrayList<>(books.values());
        all.sort(Comparator.comparing(Book::getTitle));
        return all;
    }

    // Member operations
    public boolean addMember(Member m) {
        if (members.containsKey(m.getId())) return false;
        members.put(m.getId(), m);
        return true;
    }

    public boolean removeMember(String memberId) {
        return members.remove(memberId) != null;
    }

    public Member findMemberById(String id) {
        return members.get(id);
    }

    public List<Member> listAllMembers() {
        List<Member> all = new ArrayList<>(members.values());
        all.sort(Comparator.comparing(Member::getName));
        return all;
    }

    // Borrow/Return
    public String borrowBook(String memberId, String bookId) {
        Member m = members.get(memberId);
        if (m == null) return "Member not found";
        Book b = books.get(bookId);
        if (b == null) return "Book not found";
        if (b.borrow()) {
            m.borrowBook(bookId);
            return "Borrowed successfully";
        }
        return "No copies available";
    }

    public String returnBook(String memberId, String bookId) {
        Member m = members.get(memberId);
        if (m == null) return "Member not found";
        Book b = books.get(bookId);
        if (b == null) return "Book not found";
        boolean had = m.returnBook(bookId);
        if (!had) return "Member did not borrow this book";
        b.returnCopy();
        return "Returned successfully";
    }
}

public class LibraryManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Library library = new Library();

    public static void main(String[] args) {
        seedSampleData();
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addBookFlow(); break;
                case "2": listBooksFlow(); break;
                case "3": searchBooksFlow(); break;
                case "4": addMemberFlow(); break;
                case "5": listMembersFlow(); break;
                case "6": borrowFlow(); break;
                case "7": returnFlow(); break;
                case "8": removeBookFlow(); break;
                case "9": removeMemberFlow(); break;
                case "0": System.out.println("Exiting..."); return;
                default: System.out.println("Invalid choice");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("=== Library Management System ===");
        System.out.println("1. Add Book");
        System.out.println("2. List All Books");
        System.out.println("3. Search Books");
        System.out.println("4. Add Member");
        System.out.println("5. List Members");
        System.out.println("6. Borrow Book");
        System.out.println("7. Return Book");
        System.out.println("8. Remove Book");
        System.out.println("9. Remove Member");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    private static void addBookFlow() {
        System.out.print("Book ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Number of copies: ");
        int copies = readInt(1);
        Book b = new Book(id, title, author, copies);
        if (library.addBook(b)) System.out.println("Book added"); else System.out.println("Book ID already exists");
    }

    private static void listBooksFlow() {
        List<Book> all = library.listAllBooks();
        if (all.isEmpty()) { System.out.println("No books"); return; }
        for (Book b : all) System.out.println(b);
    }

    private static void searchBooksFlow() {
        System.out.print("Search by (1) Title (2) Author: ");
        String c = scanner.nextLine().trim();
        if ("1".equals(c)) {
            System.out.print("Title query: ");
            String q = scanner.nextLine();
            List<Book> res = library.searchByTitle(q);
            if (res.isEmpty()) System.out.println("No results"); else res.forEach(System.out::println);
        } else if ("2".equals(c)) {
            System.out.print("Author query: ");
            String q = scanner.nextLine();
            List<Book> res = library.searchByAuthor(q);
            if (res.isEmpty()) System.out.println("No results"); else res.forEach(System.out::println);
        } else {
            System.out.println("Invalid option");
        }
    }

    private static void addMemberFlow() {
        System.out.print("Member ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        Member m = new Member(id, name);
        if (library.addMember(m)) System.out.println("Member added"); else System.out.println("Member ID already exists");
    }

    private static void listMembersFlow() {
        List<Member> all = library.listAllMembers();
        if (all.isEmpty()) { System.out.println("No members"); return; }
        for (Member m : all) System.out.println(m);
    }

    private static void borrowFlow() {
        System.out.print("Member ID: ");
        String mid = scanner.nextLine().trim();
        System.out.print("Book ID: ");
        String bid = scanner.nextLine().trim();
        System.out.println(library.borrowBook(mid, bid));
    }

    private static void returnFlow() {
        System.out.print("Member ID: ");
        String mid = scanner.nextLine().trim();
        System.out.print("Book ID: ");
        String bid = scanner.nextLine().trim();
        System.out.println(library.returnBook(mid, bid));
    }

    private static void removeBookFlow() {
        System.out.print("Book ID to remove: ");
        String id = scanner.nextLine().trim();
        if (library.removeBook(id)) System.out.println("Removed"); else System.out.println("Not found");
    }

    private static void removeMemberFlow() {
        System.out.print("Member ID to remove: ");
        String id = scanner.nextLine().trim();
        if (library.removeMember(id)) System.out.println("Removed"); else System.out.println("Not found");
    }

    private static int readInt(int min) {
        while (true) {
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v >= min) return v;
            } catch (NumberFormatException e) { }
            System.out.print("Enter a valid number (>= " + min + "): ");
        }
    }

    private static void seedSampleData() {
        library.addBook(new Book("B001", "Introduction to Algorithms", "Cormen", 3));
        library.addBook(new Book("B002", "Clean Code", "Robert C. Martin", 2));
        library.addBook(new Book("B003", "Effective Java", "Joshua Bloch", 1));
        library.addMember(new Member("M001", "Rishi"));
        library.addMember(new Member("M002", "Anjali"));
    }
}
