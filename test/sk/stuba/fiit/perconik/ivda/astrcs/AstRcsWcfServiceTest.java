package sk.stuba.fiit.perconik.ivda.astrcs;

import com.gratex.perconik.services.ast.rcs.UserDto;
import junit.framework.TestCase;

public class AstRcsWcfServiceTest extends TestCase {

    public void testGetUser() throws Exception {
        UserDto user = AstRcsWcfService.getInstance().getUser(1);
        System.out.println(user);
    }
}