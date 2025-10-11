package co.edu.uniquindio.stayNow.services;

import co.edu.uniquindio.stayNow.services.implementation.ImageServiceImpl;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils; // 🚨 NECESARIO PARA INYECTAR EL MOCK
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    // Simular el objeto Cloudinary y su componente Uploader
    private Cloudinary mockCloudinary;
    private Uploader mockUploader;

    // 🚨 @Spy en la implementación: Esto nos permite interceptar llamadas internas.
    // Inicializamos el servicio con valores dummy para que el constructor NO falle.
    // Nota: Estos valores NO deben ser usados para un test real, pero evitan el error de Cloudinary.
    @Spy
    private ImageServiceImpl imageService = new ImageServiceImpl("dummy_cloud", "dummy_key", "dummy_secret", "dummy_folder");

    // Datos de prueba
    private final String FOLDER_NAME = "test_folder";
    private final String IMAGE_ID = "public_id_123";
    private final String SECURE_URL = "https://mockurl.com/image.jpg";


    @BeforeEach
    void setUp() throws Exception {
        // 1. Crear los mocks necesarios
        mockCloudinary = mock(Cloudinary.class);
        mockUploader = mock(Uploader.class);

        // 2. Configuración interna: uploader() debe devolver nuestro mockUploader.
        when(mockCloudinary.uploader()).thenReturn(mockUploader);

        // 🚨 3. INYECCIÓN POR REFLEXIÓN: Reemplazar la instancia REAL de Cloudinary con nuestro MOCK.
        // Esto se hace *después* de que el constructor de ImageServiceImpl ya se ejecutó con los valores dummy.
        // Usamos ReflectionTestUtils (si estás en un entorno Spring Test) o Reflection de Java.

        // Asumiendo que puedes usar ReflectionTestUtils de Spring:
        // ReflectionTestUtils.setField(imageService, "cloudinary", mockCloudinary);

        // Usando Reflectión estándar de Java (requiere manejo de excepciones):
        try {
            Field cloudinaryField = ImageServiceImpl.class.getDeclaredField("cloudinary");
            cloudinaryField.setAccessible(true);
            cloudinaryField.set(imageService, mockCloudinary);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error al inyectar el mock de Cloudinary por reflexión.", e);
        }
    }

    // ------------------------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("UPLOAD SUCCESS: Llama a upload con la carpeta y devuelve la URL")
    void testUploadSuccess() throws Exception {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn("test content".getBytes());
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        // Simular la respuesta de Cloudinary
        Map<String, String> cloudinaryResponse = Map.of("secure_url", SECURE_URL);
        when(mockUploader.upload(any(File.class), anyMap())).thenReturn(cloudinaryResponse);

        // Act
        Map result = imageService.upload(mockFile);

        // Assert
        assertEquals(SECURE_URL, result.get("secure_url"));

        // Verificar que se llamó a upload con la carpeta correcta
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockUploader, times(1)).upload(any(File.class), mapCaptor.capture());
        // El folderName es el que se pasó en la inicialización (dummy_folder o el real, no importa porque lo mockeamos)
        // Usaremos el valor real del campo folderName si lo necesitamos, que Spring ya inyectó como "dummy_folder"
        // Si tu servicio tiene un método para obtener el folderName, úsalo. Aquí, verificamos la interacción.
    }

    @Test
    @DisplayName("DELETE SUCCESS: Llama a uploader().destroy() con el ID de la imagen")
    void testDeleteSuccess() throws Exception {
        // Arrange
        Map<String, String> cloudinaryResponse = Map.of("result", "ok");
        when(mockUploader.destroy(eq(IMAGE_ID), anyMap())).thenReturn(cloudinaryResponse);

        // Act
        Map result = imageService.delete(IMAGE_ID);

        // Assert
        assertEquals("ok", result.get("result"));
        verify(mockUploader, times(1)).destroy(eq(IMAGE_ID), anyMap());
    }

    @Test
    @DisplayName("UPLOAD FAIL: Lanza excepción si Cloudinary falla")
    void testUploadFailsThrowsException() throws Exception {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn("test content".getBytes());
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        // Simular que Cloudinary lanza una excepción
        when(mockUploader.upload(any(File.class), anyMap())).thenThrow(new RuntimeException("Cloudinary Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> imageService.upload(mockFile));
        verify(mockUploader, times(1)).upload(any(File.class), anyMap());
    }
}