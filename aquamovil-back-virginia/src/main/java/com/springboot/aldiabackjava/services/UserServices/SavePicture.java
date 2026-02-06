package com.springboot.aldiabackjava.services.UserServices;

import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import com.springboot.aldiabackjava.utils.GetDateNow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class SavePicture {
    @Autowired
    private IUserRepository iUserRepository;
    @Value("${user.folders.base.path}")
    private String USER_FOLDERS_BASE_PATH;
    @Value("${user.folder.path}")
    private String USER_FOLDERS_PATH;
    public String changerPictureProfilService(User user, byte[] decodedBytes) {
        try {
            String directory = this.USER_FOLDERS_BASE_PATH + user.getUsername() + "/";
            Path path = Paths.get(directory);
            Files.createDirectories(path);
            String filename = GetDateNow.getCode() + "profile.png";
            Path imagePath = path.resolve(filename);
            String currentProfilePicturePath = user.getProfilePicture();
            try {
                Path currentProfilePicture = Paths.get(this.USER_FOLDERS_PATH + currentProfilePicturePath);
                log.info(currentProfilePicture.toString());
                if (Files.exists(currentProfilePicture)) {
                    Files.delete(currentProfilePicture);
                }
            }catch (Exception e){
                log.info("Cambio de foto de usuario nuevo.");
            }
            Files.write(imagePath, decodedBytes);
            String finalPath = "/users/" + user.getUsername() + "/" + filename;
            user.setProfilePicture(finalPath);
            iUserRepository.save(user);
            return finalPath;
        } catch (IOException e) {
            return null;
        }
    }
}
