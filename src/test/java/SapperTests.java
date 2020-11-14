import gui.AppGUI;

public class SapperTests {

    public static void main(String[] args) {
        int n = 100;
        int lostGames = 0;
        for (int i = 0; i < n; i++) {
            AppGUI appGUI = new AppGUI();
            appGUI.main(args);
            if (appGUI.gamer.board.lostTheGame()) {
                lostGames++;
            }
        }
        System.out.println(lostGames);
    }
}
