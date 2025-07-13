package ru.kurs.userapp.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kurs.userapp.rest.dto.UserDTO;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Управление пользователями")
public class UserController {

    @Operation(
            summary = "Создать пользователя",
            description = "Создает нового пользователя. Возвращает созданного пользователя с присвоенным идентификатором."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":\"11111111-1111-1111-1111-111111111111\",\"name\":\"Иван\"}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким id уже существует")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUserDTO(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDTO.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"name\":\"Иван\"}"
                            ))
            )
            @RequestBody(required = true) UserDTO userDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по указанному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":\"11111111-1111-1111-1111-111111111111\",\"name\":\"Иван\"}"
                            ))),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID пользователя", required = true, example = "11111111-1111-1111-1111-111111111111")
            @PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет данные существующего пользователя по ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":\"11111111-1111-1111-1111-111111111111\",\"name\":\"Петр\"}"
                            ))),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID пользователя", required = true, example = "11111111-1111-1111-1111-111111111111")
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDTO.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"name\":\"Петр\"}"
                            ))
            )
            @RequestBody UserDTO userDetails) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по указанному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удален, тело ответа отсутствует"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", required = true, example = "11111111-1111-1111-1111-111111111111")
            @PathVariable String id) {
        return ResponseEntity.noContent().build();
    }
}