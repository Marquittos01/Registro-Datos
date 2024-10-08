import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class RegistroGastos {
    private static final String ARCHIVO_GASTOS = "gastos.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static ArrayList<List<String>> gastos = new ArrayList<>();

    public static void main(String[] args) {
        cargarGastos();
        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            mostrarMenu();
            opcion = scanner.nextInt();
            scanner.nextLine();
            switch (opcion) {
                case 1 -> anadirGasto(scanner);
                case 2 -> verGastos();
                case 3 -> calcularTotalGastos();
                case 4 -> verGastosPorCategoria(scanner);
                case 5 -> editarGasto(scanner);
                case 6 -> eliminarGasto(scanner);
                case 7 -> buscarPorRangoFechas(scanner);
                case 8 -> exportarCSV();
                case 9 -> mostrarEstadisticas();
                case 0 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n--- Registro de Gastos Personales ---");
        System.out.println("1. Añadir gasto");
        System.out.println("2. Ver todos los gastos");
        System.out.println("3. Calcular total de gastos");
        System.out.println("4. Ver gastos por categoría");
        System.out.println("5. Editar un gasto");
        System.out.println("6. Eliminar un gasto");
        System.out.println("7. Buscar gastos por rango de fechas");
        System.out.println("8. Exportar a CSV");
        System.out.println("9. Mostrar estadísticas");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    private static void anadirGasto(Scanner scanner) {
        try {
            System.out.print("Introduce la fecha (DD/MM/YYYY): ");
            String fecha = scanner.nextLine();
            LocalDate.parse(fecha, formatter);
            System.out.print("Introduce la categoría: ");
            String categoria = scanner.nextLine();
            System.out.print("Introduce la descripción: ");
            String descripcion = scanner.nextLine();
            System.out.print("Introduce la cantidad: ");
            double cantidad = scanner.nextDouble();
            scanner.nextLine();

            gastos.add(Arrays.asList(fecha, categoria, descripcion, String.valueOf(cantidad)));
            System.out.println("Gasto registrado correctamente.");
            guardarGastos();
        } catch (DateTimeParseException e) {
            System.out.println("Fecha en formato incorrecto. Usa DD/MM/YYYY.");
        } catch (InputMismatchException e) {
            System.out.println("Cantidad inválida. Debe ser un número.");
            scanner.nextLine();
        }
    }

    private static void verGastos() {
        System.out.println("\n--- Todos los Gastos ---");
        gastos.forEach(gasto -> System.out.println("Fecha: " + gasto.get(0) + ", Categoría: " + gasto.get(1) + ", Descripción: " + gasto.get(2) + ", Cantidad: $" + gasto.get(3)));
    }

    private static void calcularTotalGastos() {
        double total = gastos.stream()
                .mapToDouble(g -> Double.parseDouble(g.get(3)))
                .sum();
        System.out.println("Total de gastos: $" + total);
    }

    private static void verGastosPorCategoria(Scanner scanner) {
        System.out.print("Introduce la categoría a buscar: ");
        String categoria = scanner.nextLine().toLowerCase();
        List<List<String>> gastosCategoria = gastos.stream()
                .filter(g -> g.get(1).equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
        if (gastosCategoria.isEmpty()) {
            System.out.println("No se encontraron gastos en esta categoría.");
        } else {
            gastosCategoria.forEach(g -> System.out.println("Fecha: " + g.get(0) + ", Descripción: " + g.get(2) + ", Cantidad: $" + g.get(3)));
        }
    }

    private static void editarGasto(Scanner scanner) {
        System.out.print("Introduce el índice del gasto a editar: ");
        int indice = scanner.nextInt();
        scanner.nextLine();
        if (indice < 0 || indice >= gastos.size()) {
            System.out.println("Índice no válido.");
            return;
        }
        System.out.println("Editando gasto existente.");
        anadirGasto(scanner);
        gastos.set(indice, gastos.get(gastos.size() - 1));
        gastos.remove(gastos.size() - 1);
        guardarGastos();
        System.out.println("Gasto editado correctamente.");
    }

    private static void eliminarGasto(Scanner scanner) {
        System.out.print("Introduce el índice del gasto a eliminar: ");
        int indice = scanner.nextInt();
        scanner.nextLine();
        if (indice < 0 || indice >= gastos.size()) {
            System.out.println("Índice no válido.");
        } else {
            gastos.remove(indice);
            guardarGastos();
            System.out.println("Gasto eliminado.");
        }
    }

    private static void buscarPorRangoFechas(Scanner scanner) {
        try {
            System.out.print("Fecha de inicio (DD/MM/YYYY): ");
            LocalDate inicio = LocalDate.parse(scanner.nextLine(), formatter);
            System.out.print("Fecha de fin (DD/MM/YYYY): ");
            LocalDate fin = LocalDate.parse(scanner.nextLine(), formatter);

            gastos.stream()
                    .filter(g -> {
                        LocalDate fecha = LocalDate.parse(g.get(0), formatter);
                        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
                    })
                    .forEach(g -> System.out.println("Fecha: " + g.get(0) + ", Categoría: " + g.get(1) + ", Descripción: " + g.get(2) + ", Cantidad: $" + g.get(3)));
        } catch (DateTimeParseException e) {
            System.out.println("Error en el formato de fecha. Usa DD/MM/YYYY.");
        }
    }

    private static void exportarCSV() {
        guardarGastos();
    }

    private static void mostrarEstadisticas() {
        double total = gastos.stream().mapToDouble(g -> Double.parseDouble(g.get(3))).sum();
        double promedio = total / (gastos.size() > 0 ? gastos.size() : 1);
        double max = gastos.stream().mapToDouble(g -> Double.parseDouble(g.get(3))).max().orElse(0);
        double min = gastos.stream().mapToDouble(g -> Double.parseDouble(g.get(3))).min().orElse(0);
        System.out.printf("Total: $%.2f, Promedio: $%.2f, Máximo: $%.2f, Mínimo: $%.2f%n", total, promedio, max, min);
    }

    private static void cargarGastos() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_GASTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                gastos.add(Arrays.asList(linea.split(",")));
            }
        } catch (IOException e) {
            System.out.println("Error al cargar gastos.");
        }
    }

    private static void guardarGastos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_GASTOS))) {
            gastos.forEach(g -> writer.println(String.join(",", g)));
        } catch (IOException e) {
            System.out.println("Error al guardar gastos: " + e.getMessage());
        }
    }
}
