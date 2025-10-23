package com.example.ScopeIndiaProject.Controller;

import java.io.IOException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.ScopeIndiaProject.Model.ContactMail;
import com.example.ScopeIndiaProject.Model.Registration;
import com.example.ScopeIndiaProject.Repository.ContactRepository;
import com.example.ScopeIndiaProject.Repository.RegistrationRepository;
import com.example.ScopeIndiaProject.Service.EmailService;
import com.example.ScopeIndiaProject.Service.RegistrationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class Maincontroller {
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private RegistrationRepository registrationRepository;

    @GetMapping("/")
    public String showHome() {
        return "home";
    }

    @GetMapping("/about")
    public String showAbout() {
        return "about";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/contact")
    public String showContact() {
        return "contact";
    }

    @GetMapping("/templogin")
    public String showTempLogin() {
        return "generate-password";
    }

    @GetMapping("/temp-password-login")
    public String showTemppasswordlogin() {
        return "temp-password-login";
    }

    @GetMapping("/setpassword")
    public String showSetpassword() {
        return "reset-password";
    }

    @GetMapping("/courses")
    public String showCourses(Model model, HttpSession session) {
        Registration student = (Registration) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        model.addAttribute("stud", student);
        model.addAttribute("courseemail", student.getEmail()); // add this
        return "courses";
    }

    @GetMapping("/editprofile")
    public String showEditProfile(HttpSession session, Model model) {
        Registration student = (Registration) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        model.addAttribute("stud", student);
        return "profile-edit";
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Registration student = (Registration) session.getAttribute("user");
        if (student != null) {
            model.addAttribute("stud", student);
            return "dashboard";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public String registrationSave(@ModelAttribute Registration registration, @RequestParam("file") MultipartFile file)
            throws IOException {
        if (!file.isEmpty()) {
            registration.setData(file.getBytes());
        }
        registrationService.saveRegistration(registration);
        emailService.regMail(registration.getEmail());
        System.out.println("Saved successfully.");
        return "success";
    }

    @PostMapping("/receiveEmail")
    @ResponseBody
    public String sendMail(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String subject,
                          @RequestParam String message) {
        try {
            ContactMail contact = new ContactMail();
            contact.setName(name);
            contact.setEmail(email);
            contact.setSubject(subject);
            contact.setMessage(message);

            contactRepository.save(contact);

            emailService.sendUser(email);
            emailService.sendToAdmin(name, email, subject, message);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR";
        }

    }

    @PostMapping("/sendotp")
    public String sendOTP(Model model, @RequestParam("email") String email, HttpSession session) {
        Registration existingStudent = registrationRepository.findByEmail(email);
        if (existingStudent != null) {
            String newOtp = generateRandomOtp();
            existingStudent.setOtp(newOtp);
            registrationRepository.save(existingStudent);
            emailService.otpMailSend(email, newOtp);
            session.setAttribute("otpEmail", email);
            return "temp-password-login";
        } else {
            model.addAttribute("error", "Email not found");
            return "login";
        }
    }

    @PostMapping("/templogin")
    public String verifyOtp(@RequestParam("email") String email, @RequestParam("otp") String enteredOtp, Model model,
            HttpSession session) {
        Registration student = registrationRepository.findByEmail(email);
        if (student != null && student.getOtp() != null && student.getOtp().equals(enteredOtp)) {
            student.setVerification(true);
            registrationRepository.save(student);
            model.addAttribute("email", email);
            session.setAttribute("passemail", email);
        } else {
            model.addAttribute("error", "invalid OTP");
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String setPassword(@RequestParam String email, @RequestParam String password) {
        Registration existingUser = registrationRepository.findByEmail(email);

        if (existingUser != null) {
            existingUser.setPassword(password);
            registrationService.saveRegistration(existingUser);
            return "redirect:/login";
        } else {
            return "userNotFound";
        }
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String keepLoggedIn,
            HttpSession session,
            HttpServletResponse response,
            Model model) {

        Registration student = registrationService.getStudentByEmail(email);

        if (student != null && student.getPassword() != null && student.getPassword().equals(password)) {
            session.setAttribute("user", student);

            if ("on".equals(keepLoggedIn)) {
                Cookie userCookie = new Cookie("userEmail", email);
                userCookie.setHttpOnly(true);
                userCookie.setMaxAge(60);
                userCookie.setPath("/");
                response.addCookie(userCookie);
            } else {
                Cookie cookie = new Cookie("userEmail", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            return "redirect:/dashboard";

        } else {
            model.addAttribute("errorMessage", "Incorrect email or password");
            return "login";
        }
    }

    @PostMapping("/pick-course")
    public String pickCourse(@RequestParam String email,
            @RequestParam String course,
            @RequestParam String duration,
            @RequestParam String fees,
            HttpSession session) {

        Registration updatedStudent = registrationService.getStudentByEmail(email);
        if (updatedStudent != null) {
            updatedStudent.setCourse(course);
            updatedStudent.setDuration(duration);
            updatedStudent.setFees(fees);
            registrationService.saveRegistration(updatedStudent); // save the updated student
            session.setAttribute("user", updatedStudent);
        }
        return "redirect:/dashboard";
    }

    private String generateRandomOtp() {
        String otp = String.valueOf(new Random().nextInt(90000) + 10000);
        return otp;
    }

    @PostMapping("/editprofile")
    public String saveEditProfile(
            @ModelAttribute("stud") Registration updatedStudent,
            HttpSession session, @RequestParam("avatar") MultipartFile avatarFile) throws IOException {

        Registration existingStudent = registrationService.getStudentByEmail(updatedStudent.getEmail());

        if (existingStudent != null) {
            existingStudent.setFirstname(updatedStudent.getFirstname());
            existingStudent.setLastname(updatedStudent.getLastname());
            existingStudent.setPhone(updatedStudent.getPhone());
            existingStudent.setCountry(updatedStudent.getCountry());
            existingStudent.setCity(updatedStudent.getCity());
            existingStudent.setState(updatedStudent.getState());

            if (updatedStudent.getDob() != null)
                existingStudent.setDob(updatedStudent.getDob());
            if (updatedStudent.getGender() != null)
                existingStudent.setGender(updatedStudent.getGender());
            if (updatedStudent.getCourse() != null)
                existingStudent.setCourse(updatedStudent.getCourse());
            if (updatedStudent.getDuration() != null)
                existingStudent.setDuration(updatedStudent.getDuration());
            if (updatedStudent.getFees() != null)
                existingStudent.setFees(updatedStudent.getFees());
            if (avatarFile != null && !avatarFile.isEmpty())
                existingStudent.setData(avatarFile.getBytes());

            registrationService.saveRegistration(existingStudent);
            session.setAttribute("user", existingStudent);
        }

        return "redirect:/dashboard";
    }

    // @GetMapping("/logout")
    // public String logout(HttpSession session, HttpServletResponse response) {
        // session.invalidate();
        // Cookie cookie = new Cookie("studEmail", null);
        // cookie.setMaxAge(0);
        // cookie.setPath("/");
        // response.addCookie(cookie);
        // return "redirect:/login";
    // }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
    session.invalidate(); // clear session data
    // Clear the correct cookie
    Cookie cookie = new Cookie("userEmail", null); // name must match login cookie
    cookie.setMaxAge(0); // delete cookie
    cookie.setPath("/");
    response.addCookie(cookie);

    return "redirect:/";
    }
    @PostMapping("/resetpassword")
    public String sendOtpCode(Model model, @RequestParam("email") String email, HttpSession session) {
        Registration existingStudent = registrationRepository.findByEmail(email);
        if (existingStudent != null) {
            String newOtp = generateRandomOtp();
            existingStudent.setOtp(newOtp);
            registrationRepository.save(existingStudent);
            emailService.otpCodeMailSend(email, newOtp);
            session.setAttribute("newpassemail", email);
            return "newpassword";
        } else {
            model.addAttribute("error", "Email not found");
            return "forgotpassword";
        }
    }

    @GetMapping("/changepassword")
    public String showChangepassword() {
        return "change-password";
    }

    @GetMapping("/newpass")
    public String showNewpassword() {
        return "newpassword";
    }

    @PostMapping("/newpassword")
    public String checkOtp(
            @RequestParam("email") String email,
            @RequestParam("otp") String enteredOtp,
            @RequestParam("newpassword") String newPassword,
            Model model) {
        Registration student = registrationRepository.findByEmail(email);
        if (student != null && student.getOtp() != null && student.getOtp().equals(enteredOtp)) {
            student.setVerification(true);
            student.setPassword(newPassword);
            registrationRepository.save(student);
            model.addAttribute("email", email);
            return "login";
        } else {
            model.addAttribute("error", "invalid OTP");
            return "resetpassword";
        }
    }
}
