package TheThirdTask;/*
 * @created 16/12/2020 - 11:59
 * @project IntelliJ IDEA
 * @author Urecp
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class StringUtils {
    public static boolean isPalindrome(String text) {
        text = preparingOfText(text);
        final int length;
        final int moietyLength;
        length = text.length();
        moietyLength = length / 2;
        for(int i = 0; i < moietyLength; i++) {
            if(text.charAt(i) != text.charAt(length - i - 1)) {
                return false;
            }
        }
        return true;
    }

    /***
     * Проверка слова/фразы на палиндромность
     *
     * @param text - слово/фраза
     * @return
     */

    public static String preparingOfText(String text) {
        return text.toLowerCase().replaceAll("\\s+", "");
    }
}
@FunctionalInterface
interface InterfaceLogger {
    void info(String text);
}
class Logger implements InterfaceLogger {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
    private final Calendar calendar = Calendar.getInstance();
    private String prefix;
    public Logger(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void info(String text) {
       System.out.printf("[%s] [%s] %s\n", dateFormat.format(calendar.getTime()), this.prefix, text);
    }
}

class Player {
    private final BeginningOfTheGame game = BeginningOfTheGame.getInstance();

    /**
     * Очки за правильное слово
     */
    private int points;

    /**
     * Имя игрока для его идентификации среди других игроков
     */
    private String playerName;
    public Player(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Прибавление n очков игроку
     * @param count - количество очков для прибавления
     */
    public void addingOfPoints(int count) {
        this.points += count;
    }
    public Status sendingOfPhrase(String text) {
        return game.addingOfPhrase(this, text);
    }

    /**
     * Количество очков у игрока
     */
    public int getPoints() { return this.points; }

    /**
     * Имя игрока
     */
    public String getName() { return this.playerName; }

    @Override
    public String toString() {
        return this.getName() + " Количество набранных очков: " + this.getPoints();
    }
}
enum Status {
    SUCCESS(""),
    ALREADY_USED("Слово или фраза уже использовалось"),
    NOT_PALINDROME("Слово или фраза не является палиндромом");

    private final String message;
    Status(String message) {
        this.message = message;
    }
    public String getMessage() { return this.message; }
}

interface Storage {
    boolean savingOfPhrase(Player player, String text);
    void savingOfLeaderBoard(List<Player> leaderBoard);
}

class MemoryOfStorage implements Storage {
    private final Map<Player, Set<String>> phrases = new HashMap<>();
    private final List<Player> leaderBoard = new ArrayList<>(5);

    @Override
    public boolean savingOfPhrase(Player player, String text) {
        return this.phrases.computeIfAbsent(player, (play) -> new HashSet<>()).add(text);
    }
    @Override
    public void savingOfLeaderBoard(List<Player> leaderBoard) {
        this.leaderBoard.clear();
        this.leaderBoard.addAll(leaderBoard);
    }
    public List<Player> getLeaderBoard() { return this.leaderBoard; }
}

class BeginningOfTheGame {
    private static final BeginningOfTheGame instance = new BeginningOfTheGame();
    public static final int sizeOfLeaderBoard = 5;
    private final Storage storage = new MemoryOfStorage();
    public final InterfaceLogger logger = new Logger("Game");
    private List<Player> players;

    public BeginningOfTheGame() {
        this.players = new ArrayList<>();
        logger.info("Игра инициализирована!");
    }
    private void updatingOfLeaderBoard() {
        this.storage.savingOfLeaderBoard(this.players.stream().sorted((a, b) -> {
            return Integer.compare(b.getPoints(), a.getPoints());
        }).limit(sizeOfLeaderBoard).collect(Collectors.toList()));
    }

    /**
     * Подключение нового игрока в игру
     * @param nameOfPlayer - имя игрока
     * @return Объект игрока
     */
    public Player joiningTheGame(String nameOfPlayer) {
        Player player = new Player(nameOfPlayer);
        this.players.add(player);
        this.updatingOfLeaderBoard();
        logger.info(String.format("%s присоединился к игре", nameOfPlayer));
        return player;
    }
    /**
     * Проверка слова или фразы на палиндромность
     */
    private boolean checkingOfPhrase(String text) {
        return StringUtils.isPalindrome(text);
    }

    /***
     * Добавление очков в случае палиндромности слова или фразы
     *
     * @param text - слово или фраза для проверки
     */
    public Status addingOfPhrase(Player player, String text) {
        if(this.checkingOfPhrase(text)) {
            if(this.storage.savingOfPhrase(player, StringUtils.preparingOfText(text))) {
                final int points;
                points = text.length();
                player.addingOfPoints(points);
                this.updatingOfLeaderBoard();
                logger.info(String.format("Игрок %s получил %d очков за правильное слово или фразу.", player.getName(), points));
                return Status.SUCCESS;
            }
            return Status.ALREADY_USED;
        }
        return Status.NOT_PALINDROME;
    }
    public static BeginningOfTheGame getInstance() { return instance; }
    public Storage getStorage() { return this.storage; }
    public List<Player> getPlayers(){
        return this.players; }
}
class Main {
    public static void main(String[] args) {
        BeginningOfTheGame game = BeginningOfTheGame.getInstance();
        Player player = game.joiningTheGame("Fals3R");
        player.sendingOfPhrase("топот");
        Player player1 = game.joiningTheGame("Folsik");
        player1.sendingOfPhrase("топот");
        player1.sendingOfPhrase("а роза упала на лапу Азора");
        Player player2 = game.joiningTheGame("John");
        player2.sendingOfPhrase("а роза упала на лапу Азора");
        System.out.println(((MemoryOfStorage)game.getStorage()).getLeaderBoard());
    }
}
