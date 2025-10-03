package co.edu.uniquindio.stayNow.services.interfaces;

import co.edu.uniquindio.stayNow.dto.EmailDTO;

public interface EmailService {
    void sendMail(EmailDTO emailDTO) throws Exception;
}
