package gui;

import org.junit.*;

public class BoardMenuTest {

    private BoardMenu menu;
    private ColorTube ct;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before BoardMenuTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After BoardMenuTest.class");
    }

    @Before
    public void initTest() {
        menu = new BoardMenu();
        ct = new ColorTube();
    }

    @After
    public void afterTest() {
        menu = null;
        ct = null;
    }

    @Test
    public void testShow() throws Exception {
        System.out.println("testShow()");
        MainFrame.gameMode = MainFrame.FILL_MODE;
        System.out.println("testing clear is not Visible");
        menu.show(null, 10, 10);
        Assert.assertFalse(menu.clear.isVisible());
        System.out.println("testing clear is Visible");
        menu.show(ct, 10, 10);
        Assert.assertTrue(menu.clear.isVisible());
    }
}