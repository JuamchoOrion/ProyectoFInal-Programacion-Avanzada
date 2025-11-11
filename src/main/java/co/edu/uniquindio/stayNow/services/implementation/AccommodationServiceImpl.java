package co.edu.uniquindio.stayNow.services.implementation;

import co.edu.uniquindio.stayNow.dto.*;
import co.edu.uniquindio.stayNow.exceptions.AccommodationNotFoundException;
import co.edu.uniquindio.stayNow.exceptions.UnauthorizedActionException;
import co.edu.uniquindio.stayNow.exceptions.UserNotFoundException;
import co.edu.uniquindio.stayNow.mappers.AccommodationMapper;
import co.edu.uniquindio.stayNow.mappers.ReservationMapper;
import co.edu.uniquindio.stayNow.model.entity.*;
import co.edu.uniquindio.stayNow.model.enums.AccommodationServiceType;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import co.edu.uniquindio.stayNow.model.enums.ReservationStatus;
import co.edu.uniquindio.stayNow.model.enums.Role;
import co.edu.uniquindio.stayNow.repositories.*;
import co.edu.uniquindio.stayNow.services.interfaces.AccommodationService;
import co.edu.uniquindio.stayNow.services.interfaces.AuthService;
import co.edu.uniquindio.stayNow.services.interfaces.ImageService;
import co.edu.uniquindio.stayNow.services.interfaces.UserService;


import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepo;
    private final ReservationRepository reservationRepo;
    private final ReviewRepository reviewRepo;
    private final UserService userService;
    private final ImageService imageService;
    private final AccommodationMapper accommodationMapper;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final AuthService authService;

    @Value("${cloudinary.folderName}")
    private String cloudinaryFolderName;
/*
    @Override
    public AccommodationDTO create(CreateAccommodationDTO accommodationDTO) throws Exception {

        List<String> uploadedImageUrls = new ArrayList<>();

        for (String localPath : accommodationDTO.images()) {
            Map result = imageService.uploadFromPath(localPath);
            String url = (String) result.get("secure_url");
            uploadedImageUrls.add(url);
            System.out.println("Imagen subida: " + url);
        }

        String id = authService.getUserID();
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (!user.getRole().equals(Role.HOST)) {
            throw new UnauthorizedActionException("User is not a host");
        }


        Accommodation accommodation = accommodationMapper.toEntity(accommodationDTO);


        accommodation.setHost(user);


        if (accommodationDTO.services() != null && !accommodationDTO.services().isEmpty()) {
            Set<AccommodationServiceType> serviceEnums = accommodationDTO.services().stream()
                    .map(String::toUpperCase) // para evitar errores de minúsculas
                    .map(AccommodationServiceType::valueOf)
                    .collect(Collectors.toSet());

            accommodation.setAccommodationServiceTypes(serviceEnums);
        } else {
            accommodation.setAccommodationServiceTypes(Set.of()); // vacío si no hay servicios
        }

        accommodation.setImages(uploadedImageUrls);
        accommodation.setMainImage(uploadedImageUrls.get(0));

        Accommodation saved = accommodationRepo.save(accommodation);

        // 🧠 7️⃣ Convertir la entidad guardada a DTO de respuesta
        return accommodationMapper.toAccommodationDTO(saved);
    }
*/
@Override
public AccommodationDTO create(CreateAccommodationDTO accommodationDTO) throws Exception {

    List<String> uploadedImageUrls = new ArrayList<>();

    // ✅ Subir las imágenes que vienen como MultipartFile
    if (accommodationDTO.images() != null) {
        for (MultipartFile file : accommodationDTO.images()) {
            Map result = imageService.upload(file); // 👈 usa tu método que recibe MultipartFile
            String url = (String) result.get("secure_url");
            uploadedImageUrls.add(url);
            System.out.println("✅ Imagen subida: " + url);
        }
    }

    // 🔐 Validar el usuario autenticado
    String id = authService.getUserID();
    User user = userRepository.getUserById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    if (!user.getRole().equals(Role.HOST)) {
        throw new UnauthorizedActionException("User is not a host");
    }

    // 🧩 MapStruct crea la entidad base
    Accommodation accommodation = accommodationMapper.toEntity(accommodationDTO);
    accommodation.setHost(user);

    // 💡 Servicios (enums)
    if (accommodationDTO.services() != null && !accommodationDTO.services().isEmpty()) {
        Set<AccommodationServiceType> serviceEnums = accommodationDTO.services().stream()
                .map(String::toUpperCase)
                .map(AccommodationServiceType::valueOf)
                .collect(Collectors.toSet());
        accommodation.setAccommodationServiceTypes(serviceEnums);
    } else {
        accommodation.setAccommodationServiceTypes(Set.of());
    }

    // 🖼️ Asignar imágenes subidas
    if (!uploadedImageUrls.isEmpty()) {
        accommodation.setImages(uploadedImageUrls);
        accommodation.setMainImage(uploadedImageUrls.get(0));
    }

    Accommodation saved = accommodationRepo.save(accommodation);
    return accommodationMapper.toAccommodationDTO(saved);
}

    @Override
    public AccommodationDTO get(Long accomodationId) throws Exception {
        String id = authService.getUserID();
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Accommodation accommodation = accommodationRepo.findById(accomodationId)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found"));


        return accommodationMapper.toAccommodationDTO(accommodation);
    }

    // co.edu.uniquindio.stayNow.services.implementation.AccommodationServiceImpl

    @Override
    public AccommodationDTO edit(Long id, EditAccommodationDTO accommodationDTO) throws Exception {

        Accommodation accommodation = accommodationRepo.findById(id)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found"));
        String userId = authService.getUserID();
        User currentUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 🔒 Validar que el usuario sea el propietario
        if (!accommodation.getHost().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You cannot edit an accommodation that does not belong to you");
        }
        // 1. Guardar las URLs de imágenes anteriores (antes de la actualización del mapper)
        // El campo mainImage ahora existe en la entidad.
        List<String> oldImages = new ArrayList<>(accommodation.getImages());
        String oldMainImage = accommodation.getMainImage();

        // 2. Actualizar la entidad con los nuevos datos del DTO
        // (Tu mapper debe mapear 'mainImage' e 'images' del DTO a la entidad)
        accommodationMapper.updateEntity(accommodationDTO, accommodation);

        // 3. Lógica de limpieza en Cloudinary

        // 3.1. Limpieza de la imagen principal antigua
        if (oldMainImage != null && !oldMainImage.isBlank() && !oldMainImage.equals(accommodationDTO.mainImage())) {
            String publicId = extractPublicId(oldMainImage);
            if (publicId != null) {
                imageService.delete(publicId);
            }
        }

        // 3.2. Limpieza de las imágenes secundarias eliminadas
        // Crear un Set con todas las nuevas URLs para una búsqueda eficiente
        Set<String> newImagesAndMain = new java.util.HashSet<>(accommodationDTO.images());
        newImagesAndMain.add(accommodationDTO.mainImage());

        for (String oldUrl : oldImages) {
            // Si la URL antigua no está en ninguna de las nuevas URLs (ni principal ni secundaria)
            if (!newImagesAndMain.contains(oldUrl)) {
                String publicId = extractPublicId(oldUrl);
                if (publicId != null) {
                    imageService.delete(publicId);
                }
            }
        }

        // 4. Guardar los cambios
        accommodationRepo.save(accommodation);

        return accommodationMapper.toAccommodationDTO(accommodation);
    }

    /**
     * Método auxiliar para extraer el Public ID de Cloudinary desde la URL.
     * El public ID es lo que Cloudinary necesita para eliminar el archivo.
     * Se asume que la URL es del formato: .../v[timestamp]/folderName/public_id.[ext]
     */
    private String extractPublicId(String url) {
        if (url == null || url.isBlank()) return null;
        try {
            // La URL de Cloudinary contiene típicamente el public ID después del 'upload/' o 'v[timestamp]/'
            // Asumiremos que el public ID incluye el folderName (que es 'stayNow/' o lo que sea tu config)

            String urlWithoutExtension = url.substring(0, url.lastIndexOf('.'));

            // Buscar el patrón que precede al public ID, típicamente después del '/v' con números o 'upload/'
            String pattern = "/upload/";
            int uploadIndex = urlWithoutExtension.lastIndexOf(pattern);

            if (uploadIndex == -1) {
                // Intento alternativo para URLs con versión (e.g., /v123456789/)
                int lastSlashBeforeFilename = urlWithoutExtension.lastIndexOf('/');
                if (lastSlashBeforeFilename != -1) {
                    String potentialPublicId = urlWithoutExtension.substring(lastSlashBeforeFilename + 1);
                    // Si tienes un folderName definido en Cloudinary (e.g. "stayNow"), el public ID es: folderName/filename
                    String folderName = "stayNow"; // Reemplaza esto con el valor real de tu propiedad 'cloudinary.folderName'
                    return folderName + "/" + potentialPublicId;
                }
            } else {
                // El public ID comienza justo después de '/upload/v[timestamp]/' o '/upload/'
                // Buscar el último '/' después de '/upload/' para encontrar el inicio del public ID
                String remainingUrl = urlWithoutExtension.substring(uploadIndex + pattern.length());
                int versionIndex = remainingUrl.indexOf("/v"); // Buscar el indicador de versión

                if (versionIndex != -1) {
                    // Si hay indicador de versión (ej: /v123456789/folder/file)
                    int nextSlash = remainingUrl.indexOf('/', versionIndex + 2); // Buscar '/' después de /v
                    if (nextSlash != -1) {
                        return remainingUrl.substring(nextSlash + 1); // El public ID es lo que sigue
                    }
                }
            }

            // Si no se encuentra un patrón claro, devuelve null
            return null;

        } catch (Exception e) {
            // En caso de que la URL sea inválida
            return null;
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        Accommodation accommodation = accommodationRepo.findById(id)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation does not exist"));

        String userId = authService.getUserID();
        User currentUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 🔒 Solo el dueño o un admin pueden eliminar
        if (!accommodation.getHost().getId().equals(currentUser.getId())
                && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedActionException("You cannot delete an accommodation that does not belong to you");
        }
        if(accommodation.getReservations().stream().anyMatch(r -> !r.getReservationStatus().equals(ReservationStatus.CANCELED))) {
            throw new AccommodationNotFoundException("Accommodation cannot be deleted because it has active reservations");
        }
        accommodation.setStatus(AccommodationStatus.DELETED);
        accommodationRepo.save(accommodation);

    }

    @Override
    public Page<AccommodationDTO> listAll() throws Exception {
        return null;
    }@Override
    public Page<AccommodationDTO> search(
            String city,
            LocalDateTime checkIn,
            LocalDateTime checkOut,
            Double minPrice,
            Double maxPrice,
            List<String>  services,
            Pageable pageable
    ) throws Exception {

        List<Specification<Accommodation>> filters = new ArrayList<>();

        // Filtrar por ciudad
        if (city != null && !city.isBlank()) {
            filters.add((root, query, cb) -> cb.equal(root.get("city"), city));
        }

        // Filtrar por rango de precios
        if (minPrice != null && maxPrice != null) {
            filters.add((root, query, cb) ->
                    cb.between(root.get("pricePerNight"), minPrice, maxPrice));
        } else if (minPrice != null) {
            filters.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice));
        } else if (maxPrice != null) {
            filters.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice));
        }

        // 🔹 Filtrar por disponibilidad
        if (checkIn != null && checkOut != null) {
            filters.add((root, query, cb) -> {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Reservation> reservationRoot = subquery.from(Reservation.class);
                subquery.select(reservationRoot.get("accommodation").get("id"))
                        .where(
                                cb.and(
                                        cb.equal(reservationRoot.get("accommodation").get("id"), root.get("id")),
                                        cb.lessThan(reservationRoot.get("checkIn"), checkOut),
                                        cb.greaterThan(reservationRoot.get("checkOut"), checkIn)
                                )
                        );
                return cb.not(root.get("id").in(subquery));
            });
        }

        // Filtrar por servicios (enum)
        if (services != null && !services.isEmpty()) {
            Set<AccommodationServiceType> enumServices = services.stream()
                    .map(String::toUpperCase)
                    .map(AccommodationServiceType::valueOf)
                    .collect(Collectors.toSet());

            filters.add((root, query, cb) -> {
                Join<Accommodation, AccommodationServiceType> join = root.joinSet("accommodationServiceTypes");
                query.distinct(true);
                return join.in(enumServices);
            });
        }

        // Combinar filtros dinámicos
        Specification<Accommodation> spec = Specification.allOf(filters);

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }

        Page<Accommodation> page = accommodationRepo.findAll(spec, pageable);

        return page.map(accommodationMapper::toAccommodationDTO);
    }



    @Override
    public Page<ReservationDTO> getReservations(Long accommodationId, LocalDateTime from, LocalDateTime to, List<String> status, Pageable pageable) throws Exception {
        Accommodation accommodation = accommodationRepo.findById(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found"));

        String userId = authService.getUserID();
        User currentUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!accommodation.getHost().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You cannot view reservations for an accommodation that does not belong to you");
        }

        List<Specification<Reservation>> filters = new ArrayList<>();
        //SPECIFICATION ES BASICAMENTE CONSTRUIR FILTROS EN CONSULTAS SQL CON WHERE (AND)
        filters.add((root,query,cb)->cb.equal(root.get("accommodation").get("id"), accommodationId));
        if(from != null ) {
            filters.add((root,query,cb)->cb.greaterThanOrEqualTo(root.get("checkIn"), from));
        }
        if(to != null ) {
            filters.add((root, query, cb)-> cb.lessThanOrEqualTo(root.get("checkOut"), to));
        }
        if(status != null) {
            Set<ReservationStatus> enumStatus = status.stream()
                    .map(String::toUpperCase)
                    .map(ReservationStatus::valueOf)
                    .collect(Collectors.toSet());
            filters.add((root, query, cb) -> {
                Join<Reservation, ReservationStatus> join = root.joinSet("reservationStatus");
                query.distinct(true);
                return join.in(enumStatus);
            });
        }
        Specification<Reservation> spec = Specification.allOf(filters);
        Page<Reservation> page = reservationRepo.findAll(spec,pageable );
        return page.map(reservationMapper::toReservationDTO);
    }

}
