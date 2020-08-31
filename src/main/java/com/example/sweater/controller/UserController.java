package com.example.sweater.controller;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')") //к данным методам только админ попадет (
//для работы аннотации, перейти в файл WebSecurityConfig
public class UserController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", userRepo.findAll());

        return "userList";
    }

    //При получении id пользователя, Spring также подхватывает все данные пользователя без Repositories
    @GetMapping("/{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "userEdit";
    }

    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
            ) {
        user.setUsername(username);

        //перевод ролей из Enum в строковый вид
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        //очистили все роли пользователя
        user.getRoles().clear();
        //получаем список ключей с формы (фронт) и выбираем нужные из них в цикле
        for (String key : form.keySet()){
            if (roles.contains(key)){
                //записали новые роли пользователю
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepo.save(user);

        return "redirect:/user";
    }

    //TODO добавить удаление сообщений, настроить связь между таблицами user&message
    @GetMapping("/delete/{user}")
    public String userDelete(@PathVariable User user){
        //очистили все роли пользователя
        user.getRoles().clear();

        userRepo.delete(user);

        return "redirect:/user";
    }
}
