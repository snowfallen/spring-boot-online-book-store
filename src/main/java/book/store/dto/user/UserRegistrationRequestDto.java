package book.store.dto.user;

import book.store.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@FieldMatch(
        field = "password",
        repeatField = "repeatPassword",
        message = "Password and repeat password should be equals"
)
@Getter
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, max = 32)
    private String password;
    @NotBlank
    @Size(min = 8, max = 32)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
