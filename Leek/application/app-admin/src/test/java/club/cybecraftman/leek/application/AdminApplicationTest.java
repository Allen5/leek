package club.cybecraftman.leek.application;

import club.cybecraftman.leek.repo.admin.repository.IUserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdminApplicationTest {

    @Autowired
    private IUserRepo userRepo;

    @Test
    public void test() {
        userRepo.findAll();
    }

}