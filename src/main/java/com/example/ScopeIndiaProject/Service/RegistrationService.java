package com.example.ScopeIndiaProject.Service;

import org.springframework.stereotype.Service;

import com.example.ScopeIndiaProject.Model.Registration;
import com.example.ScopeIndiaProject.Repository.RegistrationRepository;
@Service
public class RegistrationService{
    private RegistrationRepository registrationRepository;

    public RegistrationService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public Registration saveRegistration(Registration registration) {

        return registrationRepository.save(registration);
    }
    public Registration getStudentByEmail(String email) {
        return registrationRepository.findByEmail(email);
    }
    public Registration saveAvatarImage(String email, byte[] imageData) {
    Registration student = registrationRepository.findByEmail(email);
    if (student == null) {
        System.out.println("No user found with email: " + email);
        return null;
    }
    if (imageData == null || imageData.length == 0) {
        System.out.println("Image data is empty");
        return null;
    }
    student.setData(imageData);
    return registrationRepository.save(student);
}
}