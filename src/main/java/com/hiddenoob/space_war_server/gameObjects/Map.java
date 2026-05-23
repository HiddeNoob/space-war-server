package com.hiddenoob.space_war_server.gameObjects;

import com.hiddenoob.Math.Polygons.Rectangle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
public class Map {

    // Oyun dünyasının sınırları ve hücre boyutu
    private final int MAP_WIDTH = 1000;
    private final int MAP_HEIGHT = 1000;
    private final int CELL_SIZE = 50; // Her hücre 500x500 piksel

    private final int cols;
    private final int rows;

    private final List<Asteroid>[][] grid;

    @SuppressWarnings("unchecked")
    public Map() {
        this.cols = MAP_WIDTH / CELL_SIZE;
        this.rows = MAP_HEIGHT / CELL_SIZE;
        this.grid = new List[cols][rows];

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                // TODO ArrayList ekleme cikarma o(1) degil
                grid[x][y] = new CopyOnWriteArrayList<>();
            }
        }
    }

    private int getCellX(double x) {
        int cellX = (int) (x / CELL_SIZE);
        return Math.max(0, Math.min(cellX, cols - 1));
    }

    private int getCellY(double y) {
        int cellY = (int) (y / CELL_SIZE);
        return Math.max(0, Math.min(cellY, rows - 1));
    }


    public void addObject(Asteroid e) {
        if (e == null || e.getPosition() == null) return;

        int cx = getCellX(e.getPosition().x);
        int cy = getCellY(e.getPosition().y);

        grid[cx][cy].add(e);
    }


    public void updateObjectPosition(Asteroid e, double oldX, double oldY) {
        if (e == null || e.getPosition() == null) return;

        int oldCx = getCellX(oldX);
        int oldCy = getCellY(oldY);

        int newCx = getCellX(e.getPosition().x);
        int newCy = getCellY(e.getPosition().y);

        if (oldCx == newCx && oldCy == newCy) {
            return;
        }

        // Hücre değiştirmişse eski hücreden sil, yeniye ekle
        grid[oldCx][oldCy].remove(e);
        grid[newCx][newCy].add(e);
    }

    /**
     * Menzil içindeki asteroidleri hiç kopyalamadan, doğrudan mevcut
     * listedeki referanslar üzerinden işleme alır (Sıfır Çöp / Sıfır
     * Kopyalama).
     */
    public void forEachInRange(double minX, double maxX, double minY,
                               double maxY,
                               Consumer<Asteroid> action) {
        int startX = getCellX(minX);
        int endX = getCellX(maxX);
        int startY = getCellY(minY);
        int endY = getCellY(maxY);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (Asteroid asteroid : grid[x][y]) {
                    action.accept(asteroid);
                }
            }
        }
    }

    public void forEachInCell(int x, int y, Consumer<Asteroid> action) {

        if (x < 0 || x >= cols || y < 0 || y >= rows) return;

        for (Asteroid asteroid : grid[x][y]) {
            action.accept(asteroid);
        }

    }


    public void forEachPairInCellRadius(int x, int y, BiConsumer<Asteroid,
            Asteroid> pairAction) {
        if (x < 0 || x >= cols || y < 0 || y >= rows) return;

        List<Asteroid> currentCell = grid[x][y];
        int size = currentCell.size();
        if (size == 0) return; // Hücre boşsa işlem yapmaya gerek yok

        for (int i = 0; i < size; i++) {
            Asteroid a = currentCell.get(i);
            for (int j = i + 1; j < size; j++) {
                Asteroid b = currentCell.get(j);
                pairAction.accept(a, b);
            }
        }

        checkNeighbor(currentCell, x + 1, y, pairAction);     // Sağ Komşu
        checkNeighbor(currentCell, x, y + 1, pairAction); // Alt Komşu
        checkNeighbor(currentCell, x + 1, y + 1, pairAction); // Sağ-Alt Çapraz
        checkNeighbor(currentCell, x - 1, y + 1, pairAction); // Sol-Alt Çapraz
    }

    private void checkNeighbor(List<Asteroid> currentCell, int targetX,
                               int targetY,
                               BiConsumer<Asteroid, Asteroid> pairAction) {
        if (targetX < 0 || targetX >= cols || targetY < 0 || targetY >= rows)
            return;

        List<Asteroid> targetCell = grid[targetX][targetY];
        if (targetCell.isEmpty()) return;

        for (Asteroid a : currentCell) {
            for (Asteroid b : targetCell) {
                pairAction.accept(a, b);
            }
        }
    }

    public void removeObject(Asteroid e) {
        if (e == null || e.getPosition() == null) return;

        int cx = getCellX(e.getPosition().x);
        int cy = getCellY(e.getPosition().y);

        grid[cx][cy].remove(e);
    }

    public List<Asteroid> queryRange(Rectangle borders) {
        List<Asteroid> results = new ArrayList<>();

        int startX = getCellX(borders.getxMin());
        int endX = getCellX(borders.getxMax());
        int startY = getCellY(borders.getyMin());
        int endY = getCellY(borders.getyMax());

        for (int i = startY; i <= endY; i++) {
            for (int j = startX; j <= endX; j++) {
                results.addAll(grid[i][j]);
            }
        }

        return results;
    }
}