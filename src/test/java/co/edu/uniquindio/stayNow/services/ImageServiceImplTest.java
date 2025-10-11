package co.edu.uniquindio.stayNow.services;




import co.edu.uniquindio.stayNow.services.implementation.ImageServiceImpl;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    // Simular el objeto Cloudinary (¡ATENCIÓN! Cloudinary es final, lo que puede requerir Mockito-inline)
    // Usaremos @Spy e @InjectMocks para intentar simular la instancia interna

    // 🚨 Dado que no podemos mockear el constructor, vamos a simular los componentes que usa.
    // Para que @InjectMocks funcione, debemos inyectar la instancia Cloudinary o usar un constructor real.

    // Una forma más simple: Probar el servicio con una instancia de Cloudinary simulada.
    private Cloudinary mockCloudinary;
    private Uploader mockUploader;
    private ImageServiceImpl imageService;

    // Datos de prueba
    private final String FOLDER_NAME = "test_folder";

    @BeforeEach
    void setUp() {
        // Inicializar manualmente los mocks internos
        mockCloudinary = mock(Cloudinary.class);
        mockUploader = mock(Uploader.class);

        // Simular que cloudinary.uploader() devuelve nuestro mockUploader
        when(mockCloudinary.uploader()).thenReturn(mockUploader);

        // 🚨 Crear la instancia del servicio inyectando la dependencia Mockeada
        // (Ignorando la lógica del constructor @Value y pasando los valores de configuración como null,
        // ya que la instancia mockCloudinary se usará en lugar de la instancia real creada por el constructor)
        imageService = new ImageServiceImpl("cloudName", "apiKey", "apiSecret", FOLDER_NAME) {
            // Sobrescribir la instancia de Cloudinary con el mock para la prueba

            protected Cloudinary getCloudinaryInstance() { return mockCloudinary; }
        };
    }

    // ------------------------------------------------------------------------------------------------------------------

    // ⭐ MÉTODOS DE PRUEBA ⭐

    @Test
    @DisplayName("UPLOAD SUCCESS: Debe llamar a uploader().upload() con el folder correcto")
    void testUploadSuccess() throws Exception {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        byte[] fileContent = "test content".getBytes();
        when(mockFile.getBytes()).thenReturn(fileContent);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        // Simular la respuesta de Cloudinary
        Map<String, String> cloudinaryResponse = Map.of("secure_url", "http://testurl.com/image.jpg");
        when(mockUploader.upload(any(File.class), anyMap())).thenReturn(cloudinaryResponse);

        // Act
        Map result = imageService.upload(mockFile);

        // Assert
        assertNotNull(result);
        assertEquals("http://testurl.com/image.jpg", result.get("secure_url"));
        // Verificar que se llamó a upload con la configuración de carpeta
        verify(mockUploader, times(1)).upload(any(File.class), argThat(map -> FOLDER_NAME.equals(map.get("folder"))));
    }

    @Test
    @DisplayName("DELETE SUCCESS: Debe llamar a uploader().destroy() con el ID")
    void testDeleteSuccess() throws Exception {
        // Arrange
        String imageId = "public_id_123";

        // Simular la respuesta de Cloudinary
        Map<String, String> cloudinaryResponse = Map.of("result", "ok");
        when(mockUploader.destroy(eq(imageId), anyMap())).thenReturn(cloudinaryResponse);

        // Act
        Map result = imageService.delete(imageId);

        // Assert
        assertNotNull(result);
        assertEquals("ok", result.get("result"));
        // Verificar que se llamó a destroy con el ID correcto
        verify(mockUploader, times(1)).destroy(eq(imageId), anyMap());
    }

    @Test
    @DisplayName("UPLOAD FAIL: Debe lanzar excepción si la subida falla")
    void testUploadFails() throws Exception {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn("test content".getBytes());
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        // Simular que Cloudinary lanza una excepción
        when(mockUploader.upload(any(File.class), anyMap())).thenThrow(new RuntimeException("Cloudinary Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> imageService.upload(mockFile));
    }
}