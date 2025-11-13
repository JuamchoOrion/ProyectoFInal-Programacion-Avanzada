package co.edu.uniquindio.stayNow.services.implementation;


import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.EmailAlreadyInUseException;
import co.edu.uniquindio.stayNow.exceptions.PasswordNotMatchException;
import co.edu.uniquindio.stayNow.exceptions.UnauthorizedActionException;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.mappers.UserMapper;
import co.edu.uniquindio.stayNow.model.entity.User;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.model.enums.UserStatus;
import co.edu.uniquindio.stayNow.repositories.UserRepository;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.ImageService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    //Esto se llama inyeccion de dependencias
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService; // ⬅️ INYECCIÓN DE DEPENDENCIA

    @Value("${cloudinary.folderName}") // ⬅️ INYECCIÓN DEL VALOR DE CONFIGURACIÓN
    private String cloudinaryFolderName;

    @Override
    public void create(CreateUserDTO userDTO) throws Exception {

        if (isEmailDuplicated(userDTO.email())) {
            throw new EmailAlreadyInUseException("this email is already used.");
        }
        //Con bd se llama al metodo para obtener el optional del usuario, hace la consulta si el email exist
        //si no existe se crea, de lo contrario se lanza exception
        if(userRepository.findByEmail(userDTO.email()).isPresent()){
            throw new EmailAlreadyInUseException("this email is already used.");
        }

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name(userDTO.name())
                .email(userDTO.email())
                .phone(userDTO.phone())
                .role(userDTO.role())
                .dateBirth(userDTO.dateBirth())
                .photoUrl(userDTO.photoUrl())
                .password(passwordEncoder.encode(userDTO.password())) // 🔑 cifrado
                .createdAt(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .build();
            //aca se debe guardar en la bd
        userRepository.save(newUser);
    }

    @Override
    public UserProfileDTO getProfile(String id) throws Exception {
        String currentid = authService.getUserID();
        User currentUser = userRepository.getUserById(currentid)
                .orElseThrow(() -> new UserNotFoundException("Unauthenticated user."));

       if (!currentid.equals(id)) {
          throw new UnauthorizedActionException("can't get other's user profile.");
       }

        return new UserProfileDTO(
                currentUser.getId(),
                currentUser.getPhone(),
                currentUser.getEmail(),
                currentUser.getName(),
                currentUser.getPhotoUrl()

        );
    }
    @Override
    public UserProfileDTO get(String id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found."));
        return new UserProfileDTO(
                user.getId(),
                user.getPhone(),
                user.getEmail(),
                user.getName(),
                user.getPhotoUrl()
        );
    }

    @Override
    public void delete(String id) throws Exception {
        User removedUser = userRepository.findById(id).orElse(null);

        if (removedUser == null) {
            throw new UserNotFoundException("User not found.");
        }
        userRepository.delete(removedUser);
    }

    @Override
    public List<UserDTO> listAll() {
        return userRepository.findAll().stream().map(userMapper::toUserDTO).collect(Collectors.toList());
    }
    @Override
    public void edit(EditUserDTO userDTO, MultipartFile photo) throws Exception {

        String id = authService.getUserID();
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Guardar foto anterior
        String oldPhotoUrl = user.getPhotoUrl();

        // Subir nueva foto si viene en el request
        String newPhotoUrl = oldPhotoUrl;

        if (photo != null && !photo.isEmpty()) {
            Map uploadResult = imageService.upload(photo);
            newPhotoUrl = (String) uploadResult.get("secure_url");
        }

        // Actualizar campos
        user.setName(userDTO.name());
        user.setPhone(userDTO.phone());
        user.setRole(userDTO.role());
        user.setPhotoUrl(newPhotoUrl);

        // Borrar imagen anterior si cambió
        if (oldPhotoUrl != null && newPhotoUrl != null && !oldPhotoUrl.equals(newPhotoUrl)) {
            String publicId = extractPublicId(oldPhotoUrl);
            if (publicId != null) {
                imageService.delete(publicId);
            }
        }

        // Guardar en BD
        userRepository.save(user);
    }

    private boolean isEmailDuplicated(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            return true;
        }
        return false;
    }

    private String encode(String password) {
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
    // ... dentro de UserServiceImpl ...

    @Override
    public boolean isHost(Long userId) {
        // 1. Busca el usuario en la DB por ID.
        return userRepository.findById(String.valueOf(userId))
                .map(user -> user.getRole() == Role.HOST)
                .orElse(false);
    }

    // ⬅️ Método auxiliar para extraer el Public ID
    /**
     * Extrae el Public ID de Cloudinary desde la URL, usando el nombre de carpeta configurado.
     */
    private String extractPublicId(String url) {
        if (url == null || url.isBlank()) return null;
        try {
            String uploadMarker = "/upload/";
            int uploadIndex = url.indexOf(uploadMarker);

            if (uploadIndex == -1) return null;

            String remainingUrl = url.substring(uploadIndex + uploadMarker.length());

            int versionEndIndex = remainingUrl.indexOf('/');
            if (versionEndIndex != -1 && remainingUrl.startsWith("v")) {
                remainingUrl = remainingUrl.substring(versionEndIndex + 1);
            }

            // Eliminar la extensión (.jpg, .png, etc.)
            int dotIndex = remainingUrl.lastIndexOf('.');
            if (dotIndex != -1) {
                remainingUrl = remainingUrl.substring(0, dotIndex);
            }

            // El public ID debe empezar con el nombre de la carpeta: stayNow/id_de_archivo
            if (!remainingUrl.startsWith(cloudinaryFolderName + "/")) {
                return null;
            }

            return remainingUrl;

        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public void changePassword (ChangePasswordRequestDTO newPasswordRequest) throws Exception{
        String id = authService.getUserID();
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(passwordEncoder.matches(newPasswordRequest.currentPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPasswordRequest.newPassword()));
            userRepository.save(user);
        }else {
            throw new PasswordNotMatchException("The given password does not match");
        }
    }
    @Override
    public void becomeHost() throws Exception {
        String userId = authService.getUserID();
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (user.getRole() != Role.GUEST) {
            throw new UnauthorizedActionException("Only GUEST can become  HOST.");
        }

        user.setRole(Role.HOST);
        userRepository.save(user);
    }
    @Override
    public String getAuthenticatedUserId() throws Exception {
        return authService.getUserID();
    }
}
