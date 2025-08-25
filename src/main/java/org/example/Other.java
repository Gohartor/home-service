package org.example;

import java.util.ArrayList;
import java.util.List;

public class Other {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        // start[index] -> length


        //page 1 -> start 0 end 2
        //page 2 -> start 3 end 5
        //page 3 -> start 6 end 8
        //page 4 -> start 9 end 11

        //3*(i-1)
        //length * (page -1) = start

        int length = 3;
        int page = 2;
        int start = length * (page - 1);

        for (int i = start; i < start + length; i++) {
            System.out.println(list.get(i));
        }

    }
}
