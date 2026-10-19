import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {

    public static void main(String[] args) {
        // Change this path to where you want the giant CSV saved
        String filePath = "C:\\Users\\adity\\projects\\mcp-demo-master\\mcp-demo-master\\src\\main\\resources\\orders.csv";
        int totalRecords = 50000; // Set to whatever volume you want!

        String[] customers = {
                "Aarav Sharma", "Priya Nair", "Lukas Weber", "Elena Schmidt",
                "Maximilian Koch", "Sophie Becker", "Jonas Fischer", "Fatima Al-Mansoor",
                "Noah Müller", "Mia Wagner", "Leon Bauer", "Amelia Hoffman",
                "Felix Becker", "Laura Schulz"
        };

        String[][] products = {
                {"NK-AF1-WHT", "Nike Air Force 1 '07"},
                {"JD-4-BRED", "Jordan 4 Retro 'Bred Reimagined'"},
                {"SNP-HD-BLK", "Snipes Essential Logo Hoodie"},
                {"ADI-SAM-BLK", "Adidas Samba Classic"},
                {"NK-DNK-PND", "Nike Dunk Low 'Panda'"},
                {"NB-550-WHT", "New Balance 550 White Grey"},
                {"JD-1-TRV", "Travis Scott x Air Jordan 1 Low"},
                {"SNP-CG-KHK", "Snipes Utility Cargo Pants"},
                {"ADI-GAZ-BLU", "Adidas Originals Gazelle"},
                {"ASICS-GEL-1130", "Asics Gel-1130 Silver"},
                {"SNP-TEE-WHT", "Snipes Small Logo Tee"}
        };

        String[] sizes = {"EU 37", "EU 38", "EU 39", "EU 40", "EU 41", "EU 42", "EU 43", "EU 44", "EU 45", "S", "M", "L", "XL"};

        // Weighted statuses (more delivered/shipped than cancelled)
        String[] statuses = {
                "DELIVERED", "DELIVERED", "DELIVERED", "DELIVERED",
                "SHIPPED", "SHIPPED", "SHIPPED",
                "PROCESSING", "PROCESSING",
                "CANCELLED"
        };

        Random random = new Random();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write the header
            writer.println("id,customer,sku,item,size,status");

            // Generate thousands of rows
            for (int i = 1042; i < 1042 + totalRecords; i++) {
                String id = "SNP-" + i;
                String customer = customers[random.nextInt(customers.length)];

                String[] product = products[random.nextInt(products.length)];
                String sku = product[0];
                String item = product[1];

                String size = sizes[random.nextInt(sizes.length)];
                String status = statuses[random.nextInt(statuses.length)];

                writer.printf("%s,%s,%s,%s,%s,%s%n", id, customer, sku, item, size, status);
            }

            System.out.println("✅ Successfully generated " + totalRecords + " records in " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Error writing CSV file: " + e.getMessage());
        }
    }
}