package co.edu.uniquindio.stayNow.services.implementation;



import co.edu.uniquindio.stayNow.services.interfaces.ImageService;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;
    private final String folderName;

    public ImageServiceImpl(
            @Value("${cloudinary.cloudName}") String cloudName,
            @Value("${cloudinary.apiKey}") String apiKey,
            @Value("${cloudinary.apiSecret}") String apiSecret,
            @Value("${cloudinary.folderName}") String folderName
    ){
        this.folderName = folderName;

        // Configura el objeto Cloudinary usando los valores inyectados
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        cloudinary = new Cloudinary(config);
    }

    @Override
    public Map upload(MultipartFile image) throws Exception {
        File file = convert(image);

        return cloudinary.uploader().upload(file, ObjectUtils.asMap("folder", this.folderName));
    }

    @Override
    public Map delete(String imageId) throws Exception {
        return cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
    }
// este metodo e spaar subir localmente solamente, puede que en edit user cause errores porque
// ese ya es desde la web, con el metodo upload seria suficiente y cambiarlo a donde este este creo
    @Override
    public Map uploadFromPath(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File not found: " + filePath);
        }
        return cloudinary.uploader().upload(file, ObjectUtils.asMap("folder", this.folderName));
    }

    private File convert(MultipartFile image) throws IOException {
        File file = File.createTempFile(image.getOriginalFilename(), null);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(image.getBytes());
        fos.close();
        return file;
    }
}