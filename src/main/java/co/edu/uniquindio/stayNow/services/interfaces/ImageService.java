package co.edu.uniquindio.stayNow.services.interfaces;


import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ImageService {
    Map upload(MultipartFile image) throws Exception;
    Map delete(String imageId) throws Exception;
}