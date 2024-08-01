package codesquad.global.servlet;

import codesquad.mock.http.MockRequest;
import codesquad.mock.http.MockRequestDispatcher;
import codesquad.mock.http.MockResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;

import java.io.IOException;

class LoginServletTest {
    private MockRequestDispatcher mockRequestDispatcher;
    private MockUserRepository mockUserDao;
    private LoginServlet loginServlet;

    @BeforeEach
    void setUp() {
        mockRequestDispatcher = new MockRequestDispatcher();
        mockUserDao = new MockUserRepository();
        loginServlet = new LoginServlet(mockUserDao);
    }

    @Nested
    @DisplayName("GET /login")
    class IndexServletIs {
        @Test
        @DisplayName("forward to : /WEB-INF/views/user/login.jsp")
        void doGet() throws ServletException, IOException {
            // given
            HttpServletRequest request = new MockRequest("/login", "GET", mockRequestDispatcher);
            HttpServletResponse response = new MockResponse();

            // when
            loginServlet.doGet(request, response);

            // then
            Assertions.assertEquals(mockRequestDispatcher.getPath(), "/WEB-INF/views/user/login.jsp");
            Assertions.assertTrue(mockRequestDispatcher.isForwarded());
        }
    }
}