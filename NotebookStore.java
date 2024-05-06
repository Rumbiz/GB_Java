import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
class Notebook {
    private String brand;
    private String model;
    private int ram;
    private int storage;
    private String os;
    private String color;
    private double price;

    public Notebook(String brand, String model, int ram, int storage, String os, String color, double price) {
        this.brand = brand;
        this.model = model;
        this.ram = ram;
        this.storage = storage;
        this.os = os;
        this.color = color;
        this.price = price;
    }
//Функции-геттеры. 
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getRam() { return ram; }
    public int getStorage() { return storage; }
    public String getOs() { return os; }
    public String getColor() { return color; }
    public double getPrice() { return price; }
//Шаблон вывода информации о ноутбуке
    public void displayInfo() {
        System.out.println("Бренд: " + brand);
        System.out.println("Модель: " + model);
        System.out.println("ОЗУ: " + ram + " GB");
        System.out.println("Накопитель: " + storage + " GB");
        System.out.println("ОС: " + os);
        System.out.println("Цвет: " + color);
        System.out.println("Цена: $" + price);
        System.out.println();
    }
}

public class NotebookStore {
    //Имя .CSV-файла по-умолчанию.
    private static final String DATA_FILE = "notebooks.csv";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
//Подгружаем уже имеющуюся информацию из файла
        Set<Notebook> notebooks = loadNotebooksFromFile();
//Цикл ввода информации о ноутбуках
        boolean addAnotherNotebook = true;
        while (addAnotherNotebook) {
            System.out.println("Добавить ещё один ноутбук? (y/n)");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                Notebook newNotebook = createNotebook(scanner);
                notebooks.add(newNotebook);
                saveNotebooksToFile(notebooks);
            } else {
                addAnotherNotebook = false;
            }
        }

        filterNotebooks(notebooks);
    }
//Ввод информации о ноутбуке
    private static Notebook createNotebook(Scanner scanner) {
        System.out.print("Бренд: ");
//Ниже по коду -убираем запятые из входных данных для формирования корректного CSV-совместимого ввода.
        String brand = scanner.nextLine().replace(",", "");
        System.out.print("Модель: ");
        String model = scanner.nextLine().replace(",", "");
        System.out.print("ОЗУ (GB): ");
        int ram = scanner.nextInt();
        System.out.print("Накопитель (GB): ");
        int storage = scanner.nextInt();
        scanner.nextLine(); // Очищаем буфер
        System.out.print("ОС: ");
        String os = scanner.nextLine().replace(",", "");
        System.out.print("Цвет: ");
        String color = scanner.nextLine().replace(",", "");
        System.out.print("Цена: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Очищаем буфер
//Формируем объект из введённых данных.
        return new Notebook(brand, model, ram, storage, os, color, price);
    }

    private static Set<Notebook> loadNotebooksFromFile() {
        Set<Notebook> notebooks = new HashSet<>();
//Парсинг CSV-файла.
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
//Разделение файла по запятой
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    String brand = parts[0];
                    String model = parts[1];
                    int ram = Integer.parseInt(parts[2]);
                    int storage = Integer.parseInt(parts[3]);
                    String os = parts[4];
                    String color = parts[5];
                    double price = Double.parseDouble(parts[6]);

                    Notebook notebook = new Notebook(brand, model, ram, storage, os, color, price);
                    notebooks.add(notebook);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки данных из файла: " + e.getMessage());
        }

        return notebooks;
    }

    private static void saveNotebooksToFile(Set<Notebook> notebooks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Notebook notebook : notebooks) {
                String line = String.join(",",
                        notebook.getBrand(),
                        notebook.getModel(),
                        Integer.toString(notebook.getRam()),
                        Integer.toString(notebook.getStorage()),
                        notebook.getOs(),
                        notebook.getColor(),
                        Double.toString(notebook.getPrice()));
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения данных в файл: " + e.getMessage());
        }
    }

    private static void filterNotebooks(Set<Notebook> notebooks) {
        Scanner scanner = new Scanner(System.in);
        Map<Integer, String> filterCriteria = new HashMap<>();
        filterCriteria.put(1, "ОЗУ");
        filterCriteria.put(2, "Накопитель");
        filterCriteria.put(3, "ОС");
        filterCriteria.put(4, "Цвет");

        System.out.println("Введите номер, соответствующий критерию фильтрации:");
        for (Map.Entry<Integer, String> entry : filterCriteria.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        int choice = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера

        if (filterCriteria.containsKey(choice)) {
            String selectedCriteria = filterCriteria.get(choice);
            System.out.print("Введите минимальное значение для критерия поиска " + selectedCriteria + ": ");
            String minValue = scanner.nextLine();

            Set<Notebook> filteredNotebooks = filterNotebooksByCriteria(notebooks, selectedCriteria, minValue);
            System.out.println("Ноутбуки, попадающие под критерии поиска:");
            for (Notebook notebook : filteredNotebooks) {
                notebook.displayInfo();
            }
        } else {
            System.out.println("Некорректный выбор критерия.");
        }
    }

    private static Set<Notebook> filterNotebooksByCriteria(Set<Notebook> notebooks, String criteria, String minValue) {
        switch (criteria) {
            case "ОЗУ":
                int minRam = Integer.parseInt(minValue);
                return notebooks.stream()
                        .filter(notebook -> notebook.getRam() >= minRam)
                        .collect(Collectors.toSet());
            case "Накопитель":
                int minStorage = Integer.parseInt(minValue);
                return notebooks.stream()
                        .filter(notebook -> notebook.getStorage() >= minStorage)
                        .collect(Collectors.toSet());
            case "ОС":
                return notebooks.stream()
                        .filter(notebook -> notebook.getOs().equalsIgnoreCase(minValue))
                        .collect(Collectors.toSet());
            case "Цвет":
                return notebooks.stream()
                        .filter(notebook -> notebook.getColor().equalsIgnoreCase(minValue))
                        .collect(Collectors.toSet());
            default:
                return notebooks;
        }
    }
}