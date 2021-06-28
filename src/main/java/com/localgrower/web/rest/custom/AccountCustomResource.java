package com.localgrower.web.rest.custom;

import com.localgrower.domain.AppUser;
import com.localgrower.domain.User;
import com.localgrower.domain.custom.CertificateUploadUtil;
import com.localgrower.repository.AppUserRepository;
import com.localgrower.repository.UserRepository;
import com.localgrower.security.SecurityUtils;
import com.localgrower.service.AppUserService;
import com.localgrower.service.MailService;
import com.localgrower.service.UserService;
import com.localgrower.service.dto.AppUserDTO;
import com.localgrower.service.dto.custom.CertificateGrowerDTO;
import com.localgrower.service.dto.custom.RegisterDTO;
import com.localgrower.service.mapper.UserMapper;
import com.localgrower.web.rest.AccountResource;
import com.localgrower.web.rest.errors.EmailAlreadyUsedException;
import com.localgrower.web.rest.errors.InvalidAdressException;
import com.localgrower.web.rest.errors.InvalidPasswordException;
import com.localgrower.web.rest.errors.LoginAlreadyUsedException;
import com.localgrower.web.rest.vm.ManagedUserVM;
import java.io.File;
import java.io.IOException;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/custom")
public class AccountCustomResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    @Value("${custom.location}")
    private String location;

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final AppUserService appUserService;

    private final UserMapper userMapper;

    private final AppUserRepository appUserRepository;

    public AccountCustomResource(
        UserRepository userRepository,
        UserService userService,
        MailService mailService,
        AppUserService appUserService,
        UserMapper userMapper,
        AppUserRepository appUserRepository
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.appUserService = appUserService;
        this.userMapper = userMapper;
        this.appUserRepository = appUserRepository;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param registerDTO the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody RegisterDTO registerDTO) {
        if (isPasswordLengthInvalid(registerDTO.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (isAdressLengthInvalid(registerDTO.getAdress())) {
            throw new InvalidAdressException();
        }
        User user = userService.registerUser(registerDTO, registerDTO.getPassword());
        mailService.sendActivationEmail(user);
        //for appUser
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setUser(userMapper.userToUserDTO(user));
        appUserDTO.setCreatedAt(ZonedDateTime.now());
        appUserDTO.setModifiedAt(ZonedDateTime.now());
        appUserDTO.setAdress(registerDTO.getAdress());
        appUserService.save(appUserDTO);
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }

    private static boolean isAdressLengthInvalid(String adress) {
        return (
            StringUtils.isEmpty(adress) ||
            adress.length() < RegisterDTO.ADRESS_MIN_LENGTH ||
            adress.length() > RegisterDTO.ADRESS_MAX_LENGTH
        );
    }

    @GetMapping("/{id}/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id, @PathVariable("filename") String filename) {
        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File("D:/Licenta2k21/src/main/image/certificate-grower/" + id + "/" + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping("/admin/certificate-image")
    public ResponseEntity<List<CertificateGrowerDTO>> getImagePath() {
        List<CertificateGrowerDTO> imagePathList = new ArrayList<>();
        if (userRepository.findAllUsersWithCertificate().isPresent()) {
            List<User> users = userRepository.findAllUsersWithCertificate().get();
            for (User user : users) {
                if (user.getImageUrl() != null && !user.getImageUrl().equals("")) {
                    CertificateGrowerDTO certificateGrowerDTO = new CertificateGrowerDTO();
                    certificateGrowerDTO.setId(user.getId());
                    certificateGrowerDTO.setLogin(user.getLogin());
                    //String uriExtern = uri + user.getId() + "/" + user.getImageUrl();
                    //RestTemplate restTemplate = new RestTemplate();
                    //byte[] resultImage = restTemplate.getForObject(uriExtern, byte[].class);
                    ResponseEntity<byte[]> responseEntity = getImage(user.getId().toString(), user.getImageUrl());
                    certificateGrowerDTO.setCertificate(responseEntity.getBody());

                    imagePathList.add(certificateGrowerDTO);
                }
            }
        }
        Page<CertificateGrowerDTO> page = new PageImpl<>(imagePathList);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/upload/certificate")
    public RedirectView saveUser(User user, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
        User userCurrent = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).get();

        userCurrent.setImageUrl(fileName);
        User savedUser = userRepository.save(userCurrent);

        String uploadDir = location + savedUser.getId();

        CertificateUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return new RedirectView("", true);
    }

    @GetMapping("/current/grower")
    public Long getGrowerId() {
        Long result = Long.valueOf("0");
        if (SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).isPresent()) {
            User user = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).get();
            AppUser appUser = appUserRepository.findAppUserByUserId(user.getId()).get();
            result = appUser.getIdAppUser();
        }

        return result;
    }

    @GetMapping("/current/appuser")
    public AppUser getAccount() {
        User user = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).get();
        AppUser appUser = appUserRepository.findAppUserByUserId(user.getId()).get();
        return appUser;
    }
}
