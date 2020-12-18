package TheFirstTask;/*
 * @created 15/12/2020 - 21:50
 * @project IntelliJ IDEA
 * @author Urecp
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@FunctionalInterface
interface ListSorter {

    /**
     * Сортирует переданный список записей (каждая запись - набор колонок) таблицы по указанной колонке по следующим правилам:
     * <ul>
     *  <li>в колонке могут быть null и пустые значения - строки с null-значениями должны быть первыми, затем строки с пустым значением, затем все остальные,</li>
     *  <li>строка бьется на подстроки следующим образом: выделяем непрерывные максимальные фрагменты строки, состоящие только из цифр, и считаем набором подстрок эти фрагменты и все оставшиеся от такого разбиения фрагменты строки</li>
     *  <li>при сравнении строк осуществляется последовательное сравнение их подстрок до первого несовпадения,</li>
     *  <li>если обе подстроки состоят из цифр - то при сравнении они интерпретируются как целые числа (вначале должно идти меньшее число), в противном случае - как строки,</li>
     *  <li>сортировка должна быть устойчива к исходной сортировке списка - т.е., если строки (в контексте указанных правил сравнения) неразличимы, то сортировка не должна менять их местами.</li>
     * </ul>
     *
     * @param rows список записей таблицы (например, результат sql select), которые нужно отсортировать по указанной колонке
     * @param indexOfColumn индекс колонки, по которой нужно провести сортировку
     */
    void sort(List<String[]> rows, int indexOfColumn);
}

class Implementation implements ListSorter {
    public static final ListSorter instance = new Implementation();

    @Override
    public void sort(final List<String[]> rows, final int indexOfColumn) {
        rows.sort(Comparator.comparing(lines -> lines[indexOfColumn], this::comparingOfColumns));
    }

    private int comparingOfColumns(String firstColumn, String secondColumn) {
        if (firstColumn == null || secondColumn == null) {
            return firstColumn == null ? (secondColumn == null ? 0 : -1) : 1;
        }
        if (firstColumn.isEmpty() || secondColumn.isEmpty()) {
            return firstColumn.isEmpty() ? (secondColumn.isEmpty() ? 0 : -1) : 1;
        }

        Object[] firstToken;
        Object[] secondToken;

        firstToken = sizeOfToken(firstColumn);
        secondToken = sizeOfToken(secondColumn);

        int firstLength = firstToken.length;
        int secondLength = secondToken.length;
        int lim = Math.min(firstLength, secondLength);

        int var = 0;
        while (var < lim) {
            int result = comparingOfTokens(firstToken[var], secondToken[var]);

            if (result != 0) {
                return result;
            }
            var++;
        }
        return firstLength - secondLength;
    }

    private int comparingOfTokens(Object firstToken, Object secondToken) {
        if (firstToken instanceof Long && secondToken instanceof Long) {
            return Long.compare((long) firstToken,(long) secondToken);
        }
        return firstToken.toString().compareTo(secondToken.toString());
    }
    private Object[] sizeOfToken(String value) {
        List<Object> tokens = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        int bufferType = -1;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int contentType = c >= '0' && c <= '9' ? 0 : 1;
            if (contentType != bufferType) {
                if (stringBuilder.length() > 0) {
                    tokens.add(token(stringBuilder.toString(), bufferType));
                    stringBuilder.delete(0, stringBuilder.length());
                }
                bufferType = contentType;
            }
            stringBuilder.append(c);
        }
        if (stringBuilder.length() > 0) {
            tokens.add(token(stringBuilder.toString(), bufferType));
        }

        return tokens.toArray();
    }

    private Object token(String raw, int tokenType) {
        if (tokenType == 0){
            Long.parseLong(raw);
            return tokenType;
        }else {
            return raw;
        }
    }
}

class Main {
    public static void main(String[] args) {
        List<String[]> rows = Arrays.asList(
                new String[] { "6", null, "cdms" },
                new String[] { "4", "12g std saw3", null, "0*71" },
                new String[] { null, "jeb 56 seif", "cmd", "g567" }
        );
        Implementation.instance.sort(rows, 0);
        rows.forEach(e -> {
            Stream.of(e).forEach(System.out::println);
            System.out.println();
        });
    }
}
