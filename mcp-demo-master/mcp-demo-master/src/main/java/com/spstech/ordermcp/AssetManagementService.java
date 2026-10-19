package com.spstech.ordermcp;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@Service
public class AssetManagementService {

    private final Cloudinary cloudinary;

    // Securely injects credentials from application.properties
    public AssetManagementService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    // Fixed: Added <String, Object> to the Map definition
    public Map<String, Object> searchCampaignAssets(String campaignTag) throws Exception {
        return cloudinary.search()
                .expression("tags:" + campaignTag)
                .execute();
    }

    // Dynamically applies a "SOLD OUT" watermark across the asset
    public String generateSoldOutWatermark(String publicId) {
        return cloudinary.url()
                .secure(true)
                .transformation(new com.cloudinary.Transformation()
                        // l_text: Text layer, Arial font, size 120, bold
                        // co_red: Red text color, g_center: Center placement, a_-45: Angled layout
                        .rawTransformation("l_text:Arial_120_bold:SOLD%20OUT,co_red,g_center,a_-45"))
                .generate(publicId);
    }

    // Downloads assets by tag directly to a local Windows directory (Andreas's Automation)
    public String downloadCampaignAssets(String tag, String windowsDirectory) {
        try {
            // 1. Search Cloudinary for assets with the specific tag
            ApiResponse response = cloudinary.search().expression("tags:" + tag).execute();

            // Fixed: Provided proper type identifiers for the List and Map
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resources = (List<Map<String, Object>>) response.get("resources");

            if (resources == null || resources.isEmpty()) {
                return "No assets found for tag: " + tag;
            }

            // 2. Ensure the Windows directory exists
            Path dirPath = Paths.get(windowsDirectory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 3. Download each file directly to the local folder
            int count = 0;
            // Fixed: Added <String, Object> to the loop variable
            for (Map<String, Object> resource : resources) {
                String secureUrl = (String) resource.get("secure_url");
                String format = (String) resource.get("format");
                String publicId = (String) resource.get("public_id");

                // Handle missing format gracefully and extract filename to prevent folder path errors
                String fileExtension = (format != null) ? format : "jpg";
                String fileName = publicId.substring(publicId.lastIndexOf("/") + 1) + "." + fileExtension;
                Path targetPath = dirPath.resolve(fileName);

                try (InputStream in = new URL(secureUrl).openStream()) {
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    count++;
                }
            }
            return "Success: Downloaded " + count + " assets to " + windowsDirectory;

        } catch (Exception e) {
            return "Failed to download assets: " + e.getMessage();
        }
    }
}