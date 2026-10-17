package e.e.a;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
/** Existing paging assertions require Android's main Looper for static UI helpers. */
@RunWith(RobolectricTestRunner.class) @Config(manifest=Config.NONE,sdk=28)
public class ExistingFrameworkTest {
    @Test public void rankingPagingContracts() throws Exception { RankingPaging190Test.main(new String[0]); }
}
