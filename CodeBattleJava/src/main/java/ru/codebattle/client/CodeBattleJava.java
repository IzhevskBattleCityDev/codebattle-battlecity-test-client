package ru.codebattle.client;


import lombok.SneakyThrows;
import ru.codebattle.client.domain.Action;
import ru.codebattle.client.domain.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static ru.codebattle.client.domain.Elements.*;
import static ru.codebattle.client.domain.Elements.CONSTRUCTION_DESTROYED_DOWN;

public class CodeBattleJava {

    @SneakyThrows
    public static void main(String[] args) {
        String user = System.getProperty("user");
        String code = System.getProperty("code");
        String host = System.getProperty("server");

        CodeBattleJavaLibrary client = new CodeBattleJavaLibrary(host, user, code);

        client.run(new Solver(client));
    }

    public static class Solver implements Consumer<Elements[][]> {
        private static final EnumSet<Elements> BLOCK_TYPES = EnumSet.of(
                BATTLE_WALL,
                CONSTRUCTION,
                CONSTRUCTION_DESTROYED_DOWN,
                CONSTRUCTION_DESTROYED_UP,
                CONSTRUCTION_DESTROYED_LEFT,
                CONSTRUCTION_DESTROYED_RIGHT,
                CONSTRUCTION_DESTROYED_DOWN_TWICE,
                CONSTRUCTION_DESTROYED_UP_TWICE,
                CONSTRUCTION_DESTROYED_LEFT_TWICE,
                CONSTRUCTION_DESTROYED_RIGHT_TWICE,
                CONSTRUCTION_DESTROYED_LEFT_RIGHT,
                CONSTRUCTION_DESTROYED_UP_DOWN,
                CONSTRUCTION_DESTROYED_UP_LEFT,
                CONSTRUCTION_DESTROYED_RIGHT_UP,
                CONSTRUCTION_DESTROYED_DOWN_LEFT,
                CONSTRUCTION_DESTROYED_DOWN_RIGHT,

                HEDGEHOG,

                OTHER_TANK_DOWN,
                OTHER_TANK_LEFT,
                OTHER_TANK_UP,
                OTHER_TANK_RIGHT,
                AI_TANK_DOWN,
                AI_TANK_LEFT,
                AI_TANK_UP,
                AI_TANK_RIGHT);

        private final Random random;
        private CodeBattleJavaLibrary client;
        private int prevX = 0;
        private int prevY = 0;

        Consumer<Action> UP;
        Consumer<Action> DOWN;
        Consumer<Action> LEFT;
        Consumer<Action> RIGHT;

        public Solver(CodeBattleJavaLibrary client) {
            this.client = client;
            random = new Random();

            UP = client::up;
            DOWN = client::down;
            LEFT = client::left;
            RIGHT = client::right;
        }

        private static boolean isBlock(Elements e) {
            return BLOCK_TYPES.contains(e);
        }

        Consumer<Action> preDirection = UP;
        Consumer<Action> direction = preDirection;

        int movesInDirection = 0;

        @Override
        public void accept(Elements[][] map) {
            boolean done = false;

            if (direction == null) {
                direction = randomActionOf(random, UP, RIGHT, DOWN, LEFT);
            }

            if (movesInDirection > 4 + random.nextInt(6)) {
                direction = changeDirection(direction);
            }

            while(hasBarrier(direction, map)) {
                direction = changeDirection(direction);
            }

            direction.accept(Action.BEFORE_TURN);

            if (!done) {
                client.blank();
            }
            prevX = client.getPlayerX();
            prevY = client.getPlayerY();

            if (preDirection == direction) {
                movesInDirection++;
            } else {
                movesInDirection = 0;
            }

            preDirection = direction;
        }

        private Consumer<Action> changeDirection(Consumer<Action> direction) {
            if (direction == UP || direction == DOWN) {
                return randomActionOf(random, RIGHT, LEFT);
            } else {
                return randomActionOf(random, UP, DOWN);
            }
        }

        private Consumer<Action> randomActionOf(Random random, Consumer<Action> first, Consumer<Action> ...others) {
            List<Consumer<Action>> varargs = Arrays.asList(others);
            List<Consumer<Action>> list = new ArrayList<>(varargs);
            list.add(first);

            return list.get(random.nextInt(list.size()));
        }

        private boolean hasBarrier(Consumer<Action> direction, Elements[][] map) {
            if (direction == UP && isBlock(el(map, client.getPlayerX(), client.getPlayerY() - 1))) {
                return true;
            } else if (direction == RIGHT && isBlock(el(map, client.getPlayerX() + 1, client.getPlayerY()))) {
                return true;
            } else if (direction == DOWN && isBlock(el(map, client.getPlayerX(), client.getPlayerY() + 1))) {
                return true;
            } else return (direction == LEFT && isBlock(el(map, client.getPlayerX() - 1, client.getPlayerY())));
        }

        private Elements el(Elements[][] map, int playerX, int playerY) {
            if (playerX >=map.length || playerY >= map[playerX].length) {
                return Elements.BATTLE_WALL;
            }

            return map[playerX][playerY];
        }

    }
}
