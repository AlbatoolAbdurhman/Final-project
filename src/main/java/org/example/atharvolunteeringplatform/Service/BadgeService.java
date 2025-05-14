package org.example.atharvolunteeringplatform.Service;

import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiException;
import org.example.atharvolunteeringplatform.Model.Badge;
import org.example.atharvolunteeringplatform.Repository.BadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;


    private final String uploadDir = "src/main/resources/static/uploads/badges/";

    public List<Badge> findBadges() {
        return badgeRepository.findAll();
    }

    public void createBadge(Badge badge, MultipartFile imageFile) {
        // Validate that the image file is not null or empty
        if (imageFile == null || imageFile.isEmpty()) {
            throw new ApiException("Image file is required");
        }

        // Generate a unique filename to avoid name conflicts
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        try {
            // Define the directory where images will be saved
            Path dirPath = Paths.get(uploadDir);

            // Create the directory if it does not exist
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Build the full file path and copy the image to the directory
            Path filePath = dirPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save the relative image path to the badge entity
            badge.setImagePath("/uploads/badges/" + fileName); // ✅ only the URL path

            // Save the badge entity to the database
            badgeRepository.save(badge);

        } catch (IOException e) {
            // If any error occurs while saving the file, throw a runtime exception
            throw new RuntimeException("Failed to store image file", e);
        }
    }

    public void updateBadge(Integer badgeId, Badge updatedBadge, MultipartFile imageFile) {
        // Get the existing badge from the database
        Badge existingBadge = badgeRepository.findBadgeById(badgeId);
        if (existingBadge == null) {
            throw new ApiException("Badge not found");
        }

        // Update the basic fields
        existingBadge.setTitle(updatedBadge.getTitle());
        existingBadge.setDescription(updatedBadge.getDescription());
        existingBadge.setCriteria(updatedBadge.getCriteria());

        // If a new image is uploaded, handle file storage and update path
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            try {
                Path dirPath = Paths.get(uploadDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                Path filePath = dirPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Optionally: delete old image here if needed

                existingBadge.setImagePath(uploadDir + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Failed to update image file", e);
            }
        }

        // Save updated badge to the database
        badgeRepository.save(existingBadge);
    }


    public void deleteBadge(Integer badgeId) {
        // Retrieve the badge from the database
        Badge badge = badgeRepository.findBadgeById(badgeId);
        if (badge == null) {
            throw new ApiException("Badge not found");
        }

        // Delete the image file from the filesystem
        if (badge.getImagePath() != null) {
            Path imagePath = Paths.get(badge.getImagePath());
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete badge image file", e);
            }
        }

        // Delete the badge from the database
        badgeRepository.delete(badge);
    }



    //17
    public Badge getBadgeDetails(Integer badgeId) {
        Badge badge = badgeRepository.findBadgeById(badgeId);
        if (badge == null) {
            throw new ApiException("Badge not found");
        }
        return badge;
    }


}
