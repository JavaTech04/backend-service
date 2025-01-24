/*
 * Author: Nong Hoang Vu || JavaTech
 * Facebook:https://facebook.com/NongHoangVu04
 * Github: https://github.com/JavaTech04
 * Youtube: https://www.youtube.com/@javatech04/?sub_confirmation=1
 */
package vn.vunh.controller;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import vn.vunh.common.Gender;
import vn.vunh.controller.response.UserPageResponse;
import vn.vunh.controller.response.UserResponse;
import vn.vunh.service.JwtService;
import vn.vunh.service.UserService;
import vn.vunh.service.UserServiceDetail;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserServiceDetail userServiceDetail;

    @MockBean
    private JwtService jwtService; // Mocked bean for tests

    private static UserResponse javaTech;
    private static UserResponse johnDoe;

    @BeforeAll
    static void setUp() {
        // Chuẩn bị dữ liệu
        javaTech = new UserResponse();
        javaTech.setId(1L);
        javaTech.setFirstName("Java");
        javaTech.setLastName("Tech");
        javaTech.setGender(Gender.MALE);
        javaTech.setBirthday(new Date());
        javaTech.setEmail("nonghoangvu04@gmail.com");
        javaTech.setPhone("0777049085");
        javaTech.setUsername("javaTech");

        johnDoe = new UserResponse();
        johnDoe.setId(2L);
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        johnDoe.setGender(Gender.FEMALE);
        johnDoe.setBirthday(new Date());
        johnDoe.setEmail("johndoe@gmail.com");
        johnDoe.setPhone("0123456789");
        johnDoe.setUsername("johndoe");
    }

    @Test
    @WithMockUser(authorities = {"admin", "manager"}) // Fake user
    void shouldGetUserList() throws Exception {
        List<UserResponse> userListResponses = List.of(javaTech, johnDoe);

        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(0);
        userPageResponse.setPageSize(20);
        userPageResponse.setTotalPages(1);
        userPageResponse.setTotalElements(2);
        userPageResponse.setUsers(userListResponses);

        when(userService.findAll(null, null, 0, 20)).thenReturn(userPageResponse);

        // Perform the test
        mockMvc.perform(get("/user/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("user list")))
                .andExpect(jsonPath("$.data.totalPages", is(1)))
                .andExpect(jsonPath("$.data.totalElements", is(2)))
                .andExpect(jsonPath("$.data.users[0].id", is(1)))
                .andExpect(jsonPath("$.data.users[0].username", is("javaTech")));
    }

    @Test
    @WithMockUser(authorities = {"admin", "manager"})
    void shouldGetUserDetail() throws Exception {
        when(userService.findById(anyLong())).thenReturn(javaTech);

        // Perform the test
        mockMvc.perform(get("/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("user")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.firstName", is("Java")))
                .andExpect(jsonPath("$.data.lastName", is("Tech")))
                .andExpect(jsonPath("$.data.email", is("nonghoangvu04@gmail.com")));
    }
}