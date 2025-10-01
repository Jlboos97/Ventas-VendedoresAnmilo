package ProcesadorVentas;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class info_estadisticas {
    static Map<String, Integer> ventasPorVendedor = new HashMap<>();
    static Map<String, Integer> productosVendidos = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Opciones:");
        System.out.println("1. Procesar archivos existentes");
        System.out.println("2. Registrar nuevo vendedor y sus productos");
        System.out.print("Selecciona una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // limpiar buffer

        if (opcion == 1) {
            String carpeta = "archivos_vendedores";
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(carpeta), "*.txt")) {
                for (Path archivo : stream) {
                    procesarArchivo(archivo.toFile());
                }
                mostrarResumenVentas();
            } catch (IOException e) {
                System.out.println("Error al leer archivos: " + e.getMessage());
            }
        } else if (opcion == 2) {
            registrarVendedor(scanner);
        } else {
            System.out.println("Opción no válida.");
        }

        scanner.close();
    }

    public static void registrarVendedor(Scanner scanner) {
        System.out.print("Tipo de documento del vendedor: ");
        String tipoDoc = scanner.nextLine();

        System.out.print("Número de documento: ");
        String numDoc = scanner.nextLine();

        System.out.print("Nombre del vendedor: ");
        String tipoName = scanner.nextLine();

        System.out.print("¿Cuántos productos deseas ingresar? ");
        int cantidadProductos = scanner.nextInt();
        scanner.nextLine(); // limpiar buffer

        StringBuilder contenido = new StringBuilder();
        contenido.append(tipoDoc).append(";").append(numDoc).append(";").append(tipoName).append("\n");

        for (int i = 0; i < cantidadProductos; i++) {
            System.out.println("Producto #" + (i + 1));
            System.out.print("ID del producto: ");
            String idProducto = scanner.nextLine();

            System.out.print("Información del producto: ");
            String infProducto = scanner.nextLine();

            System.out.print("Cantidad: ");
            String cantidad = scanner.nextLine();

            System.out.print("Precio unitario: ");
            String precio = scanner.nextLine();

            contenido.append(idProducto).append(" ;").append(infProducto).append(";")
                     .append(cantidad).append(";").append(precio).append("\n");
        }

        // Guardar en archivo
        String carpeta = "archivos_vendedores";
        File directorio = new File(carpeta);
        if (!directorio.exists()) {
            directorio.mkdir();
        }

        File archivo = new File(directorio, numDoc + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write(contenido.toString());
            System.out.println("Datos guardados en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }

    public static void procesarArchivo(File archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String tipoDoc = "", numDoc = "", tipoName = "";
            int contador = 0;
            int totalProductos = 0;
            int totalVentas = 0;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (contador == 0) {
                    tipoDoc = partes[0];
                    numDoc = partes[1];
                    tipoName = partes[2];
                    System.out.println("Vendedor: " + tipoDoc + " " + numDoc + " " + tipoName);
                } else {
                    if (partes.length >= 4) {
                        String idProducto = partes[0];
                        String infProducto = partes[1];
                        int cantidad = Integer.parseInt(partes[2]);
                        int precio = Integer.parseInt(partes[3]);
                        int subtotal = cantidad * precio;
                        totalVentas += subtotal;
                        productosVendidos.put(infProducto, productosVendidos.getOrDefault(infProducto, 0) + cantidad);
                        System.out.println("Producto: " + idProducto + " - información producto: " + infProducto + " - Cantidad: " + cantidad + " - Valor Unitario: $" + precio);
                        totalProductos++;
                    }
                }
                contador++;
            }
            ventasPorVendedor.put(tipoName, totalVentas);
            System.out.println("Total de productos: " + totalProductos);
            System.out.println("Total ventas: $" + totalVentas);
            System.out.println("-----");
        } catch (IOException e) {
            System.out.println("Error al procesar archivo " + archivo.getName() + ": " + e.getMessage());
        }
    }

    public static void mostrarResumenVentas() {
        System.out.println("Resumen de ventas:");

        String vendedorMax = Collections.max(ventasPorVendedor.entrySet(), Map.Entry.comparingByValue()).getKey();
        String vendedorMin = Collections.min(ventasPorVendedor.entrySet(), Map.Entry.comparingByValue()).getKey();
        String productoTop = Collections.max(productosVendidos.entrySet(), Map.Entry.comparingByValue()).getKey();

        System.out.println("Producto más vendido: " + productoTop);
        System.out.println("Vendedor con más ventas: " + vendedorMax);
        System.out.println("Vendedor con menos ventas: " + vendedorMin);
    }
}
