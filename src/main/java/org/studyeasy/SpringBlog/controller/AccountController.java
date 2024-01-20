package org.studyeasy.SpringBlog.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.studyeasy.SpringBlog.models.Account;
import org.studyeasy.SpringBlog.services.AccountService;
import org.studyeasy.SpringBlog.services.EmailService;
import org.studyeasy.SpringBlog.util.AppUtil;
import org.studyeasy.SpringBlog.util.email.EmailDetails;

import jakarta.validation.Valid;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @Value("${photo_prefix}")
    private String photo_prefix;

    @Value("${site.domain}")
    private String site_domain;

    @Value("${password.token.reset.timeout.minutes}")
    private Integer password_token_timeout;

    @GetMapping("/register")
    public String register(final Model model) {
        final var account = new Account();
        model.addAttribute("account", account);
        return "account_views/register";
    }

    @PostMapping("/register")
    public String register_user(@Valid @ModelAttribute final Account account, final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "account_views/register";
        }
        accountService.save(account);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "account_views/login";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(final Model model, final Principal principal) {
        final var authUser = principal != null ? principal.getName() : "email";
        final var optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            final var account = optionalAccount.get();
            model.addAttribute("account", account);
            model.addAttribute("photo", account.getPhoto());
            return "account_views/profile";
        } else {
            return "redirect:/?error";
        }
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String post_profile(@Valid @ModelAttribute final Account account, final BindingResult bindingResult,
            final Principal principal) {
        if (bindingResult.hasErrors()) {
            return "account_views/profile";
        }
        final var authUser = principal != null ? principal.getName() : "email";
        final var optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            final var optional_account_by_id = accountService.findById(account.getId());
            if(optional_account_by_id.isPresent()) {
                final var account_by_id = optional_account_by_id.get();
                account_by_id.setAge(account.getAge());
                account_by_id.setDateOfBirth(account.getDateOfBirth());
                account_by_id.setFirstName(account.getFirstName());
                account_by_id.setLastName(account.getLastName());
                account_by_id.setGender(account.getGender());
                account_by_id.setPassword(account.getPassword());
                accountService.save(account_by_id);
                SecurityContextHolder.clearContext();
                return "redirect:/";
            } else {
                return "redirect:/?error";
            }
        } else {
            return "redirect:/?error";
        }
    }

    @PostMapping("/update_photo")
    @PreAuthorize("isAuthenticated()")
    public String update_photo(@RequestParam("file") final MultipartFile multipartFile,
            final RedirectAttributes redirectAttributes, final Principal principal) {
        if (multipartFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No file uploaded");
            return "redirect:/profile";
        } else {
            final var fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            try {
                final var length = 10;
                final var useLetters = true;
                final var useNumbers = true;
                final var generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
                final var finalPhotoName = generatedString + fileName;
                final var absoluteFileLocation = AppUtil.get_upload_path(finalPhotoName);
                final var path = Paths.get(absoluteFileLocation);
                Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                redirectAttributes.addFlashAttribute("message", "You successfully uploaded");

                final var authUser = principal != null ? principal.getName() : "email";
                final var optionalAccount = accountService.findOneByEmail(authUser);
                if (optionalAccount.isPresent()) {
                    final var account = optionalAccount.get();
                    final var optional_account_by_id = accountService.findById(account.getId());
                    if (optional_account_by_id.isPresent()) {
                        final var account_by_id = optional_account_by_id.get();
                        final var relativeFileLocation = photo_prefix.replace("**", "uploads/" + finalPhotoName);
                        account_by_id.setPhoto(relativeFileLocation);
                        accountService.save(account_by_id);
                    } else {
                        return "redirect:/profile?error";
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "redirect:/profile";
            } catch (final Exception e) {
                return "redirect:/profile?error";
            }
        }
    }

    @GetMapping("/forgot-password")
    public String forgot_password() {
        return "account_views/forgot_password";
    }

    @PostMapping("/reset-password")
    public String reset_password(@RequestParam("email") final String _email,
            final RedirectAttributes redirectAttributes) {
        final var optionalAccount = accountService.findOneByEmail(_email);
        if (optionalAccount.isPresent()) {
            final var optional_account = accountService.findById(optionalAccount.get().getId());
            if (optional_account.isPresent()) {
                final var account = optional_account.get();
                final var resetToken = UUID.randomUUID().toString();
                account.setToken(resetToken);
                account.setPassword_reset_token_expiry(
                        LocalDateTime.now().plusMinutes(password_token_timeout));
                accountService.save(account);
                final var reset_message = "This is the reset password link: " + site_domain + "change-password?token="
                        + resetToken;
                final var emailDetails = new EmailDetails(account.getEmail(), reset_message,
                        "Reset password StudyEasy demo");
                if (!emailService.sendSimpleEmail(emailDetails)) {
                    redirectAttributes.addFlashAttribute("error", "Error while sending email, contact admin");
                    return "redirect:/forgot-password";
                }
                redirectAttributes.addFlashAttribute("message", "Password reset email sent");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "No user found with the email supplied");
                return "redirect:/forgot-password";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "No user found with the email supplied");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/change-password")
    public String change_password(final Model model, @RequestParam("token") final String token,
            final RedirectAttributes redirectAttributes) {
        if (token.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Invalid Token");
            return "redirect:/forgot-password";
        }
        final var optionalAccount = accountService.findByToken(token);
        if (optionalAccount.isPresent()) {
            final var optional_account = accountService.findById(optionalAccount.get().getId());
            if (optional_account.isPresent()) {
                final var account = optional_account.get();
                final var now = LocalDateTime.now();
                if (now.isAfter(optionalAccount.get().getPassword_reset_token_expiry())) {
                    redirectAttributes.addFlashAttribute("error", "Token Expired");
                    return "redirect:/forgot-password";
                }
                model.addAttribute("account", account);
                return "account_views/change_password";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid token");
                return "redirect:/forgot-password";
            }
        }
        redirectAttributes.addFlashAttribute("error", "Invalid token");
        return "redirect:/forgot-password";
    }

    @PostMapping("/change-password")
    public String post_change_password(@ModelAttribute final Account account,
            final RedirectAttributes redirectAttributes) {
        final var optional_account_by_id = accountService.findById(account.getId());
        if (optional_account_by_id.isPresent()) {
            final var account_by_id = optional_account_by_id.get();
            account_by_id.setPassword(account.getPassword());
            account_by_id.setToken("");
            accountService.save(account_by_id);
            redirectAttributes.addFlashAttribute("message", "Password updated");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Password not updated");
            return "redirect:/login?error";
        }
    }
}
