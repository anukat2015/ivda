package sk.stuba.fiit.perconik.ivda.astrcs;

import com.gratex.perconik.services.ast.rcs.UserDto;
import junit.framework.TestCase;
import org.junit.Assert;

public class AstRcsWcfServiceTest extends TestCase {

    public void testGetUser() throws Exception {
        UserDto user = AstRcsWcfService.getInstance().getUser(1);
        Assert.assertNotNull(user);
        System.out.println(user);
    }
}