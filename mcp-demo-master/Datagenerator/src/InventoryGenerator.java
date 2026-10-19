import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Random;

public class InventoryGenerator {

    public static void main(String[] args) {
        // Update this path to match your exact machine setup
        String filePath = "C:\\Users\\adity\\projects\\mcp-demo-master\\mcp-demo-master\\src\\main\\resources\\inventory.csv";
        int totalAdditionalRecords = 2500; // Generate 2,500 extra items

        String[] photoStatuses = {"COMPLETED", "COMPLETED", "COMPLETED", "PENDING"};
        String[] booleanValues = {"TRUE", "FALSE", "FALSE", "FALSE", "FALSE"};
        String[] brands = {"NK", "ADI", "NB", "ASICS", "VANS", "CON", "PUMA", "SNP"};
        String[] categories = {"SHOE", "TEE", "HD", "PNT", "SOCKS", "CAP"};

        Random random = new Random();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // 1. Write the header exactly as your updated SnipesRepository expects
            writer.println("sku,rotterdam_central_stock,leipzig_eshop_stock,photo_shoot_status,is_release_product");

            // 2. Hardcode the 11 core test SKUs so your specific AI prompts don't break
            writer.println("NK-AF1-WHT,1200,145,COMPLETED,FALSE");
            writer.println("JD-4-BRED,500,2,PENDING,TRUE");
            writer.println("SNP-HD-BLK,300,38,COMPLETED,FALSE");
            writer.println("ADI-SAM-BLK,400,62,COMPLETED,FALSE");
            writer.println("NK-DNK-PND,100,4,COMPLETED,FALSE");
            writer.println("NB-550-WHT,250,28,COMPLETED,FALSE");
            writer.println("JD-1-TRV,800,0,COMPLETED,TRUE");
            writer.println("SNP-CG-KHK,150,19,COMPLETED,FALSE");
            writer.println("ADI-GAZ-BLU,100,15,COMPLETED,FALSE");
            writer.println("ASICS-GEL-1130,200,3,COMPLETED,FALSE");
            writer.println("SNP-TEE-WHT,500,110,COMPLETED,FALSE");

            // 3. Generate massive volume for the rest of the catalog
            for (int i = 0; i < totalAdditionalRecords; i++) {
                String brand = brands[random.nextInt(brands.length)];
                String cat = categories[random.nextInt(categories.length)];
                // Create unique SKUs like ADI-SHOE-1005
                String sku = brand + "-" + cat + "-" + (1000 + i);

                // Rotterdam usually holds more bulk stock
                int rotterdamStock = random.nextInt(2000);

                // Leipzig eShop stock is usually lower, occasionally sold out (0)
                int leipzigStock = random.nextInt(100) > 10 ? random.nextInt(300) : 0;

                String photoStatus = photoStatuses[random.nextInt(photoStatuses.length)];
                String isRelease = booleanValues[random.nextInt(booleanValues.length)];

                // Add realistic business logic: if it's a hype release, it might not be in Leipzig yet
                if (isRelease.equals("TRUE") && random.nextBoolean()) {
                    leipzigStock = 0;
                    photoStatus = "PENDING";
                }

                writer.printf("%s,%d,%d,%s,%s%n", sku, rotterdamStock, leipzigStock, photoStatus, isRelease);
            }

            System.out.println("✅ Successfully generated baseline + " + totalAdditionalRecords + " mock inventory records in " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Error writing inventory CSV file: " + e.getMessage());
        }
    }
}