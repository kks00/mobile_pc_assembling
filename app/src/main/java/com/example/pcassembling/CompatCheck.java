package com.example.pcassembling;

import android.util.Log;

import java.util.ArrayList;

public class CompatCheck {
    public static String get_between(String orig, String left, String right) {
        int start_index = orig.indexOf(left) + left.length();
        int end_index = orig.indexOf(right, start_index);
        return orig.substring(start_index, end_index);
    }

    //  CPU와 메인보드의 소켓 타입 호환여부 확인
    public static boolean cpu_board_check(Item cpu_item, Item board_item) {
        String cpu_socket = get_between(cpu_item.spec, "(소켓", ")");
        String board_socket = get_between(board_item.spec, "(소켓", ")");
        Log.d("cpu_board_check", "CPU Socket: " + cpu_socket + ", Board Socket: " + board_socket);

        if (cpu_socket.indexOf(board_socket) < 0) // CPU의 소켓과 메인보드의 소켓이 일치하지 않으면
            return false;
        return true;
    }

    // 보드의 메모리 소켓 타입과 메모리의 호환여부 확인
    public static boolean memory_type_check(Item board_item, Item memory_item) {
        String memory_type = get_between(memory_item.spec, "데스크탑용/", "/");
        String board_type = get_between(board_item.spec, "메모리/", "/");
        Log.d("memory_type_check", "Memory Type: " + memory_type + ", Board Type: " + board_type);

        if (memory_type.indexOf(board_type) < 0)
            return false;
        return true;
    }

    // 장바구니에 들어있는 아이템들이 서로 호환이 가능한지 여부를 리턴하는 함수
    public static boolean is_compatible(ArrayList<Item> cart_items, StringBuilder error_message) {
        error_message.setLength(0);

        Item cpu_item = null;
        Item memory_item = null;
        Item board_item = null;

        // 장바구니에 있는 아이템에서 카테고리별 제품 찾기
        for (Item item: cart_items) {
            if (item.category =="CPU")
                cpu_item = item;
            else  if (item.category == "메모리")
                memory_item = item;
            else if (item.category == "메인보드")
                board_item = item;
        }
        // 담지 않은 제품이 있을 시
        if (cpu_item == null) {
            error_message.append("CPU가 선택되지 않았습니다.");
            return false;
        }
        if (memory_item == null) {
            error_message.append("메모리가 선택되지 않았습니다.");
            return false;
        }
        if (board_item == null) {
            error_message.append("메인보드가 선택되지 않았습니다.");
            return false;
        }

        if (cpu_board_check(cpu_item, board_item) != true) {
            error_message.append("선택하신 CPU와 메인보드가 호환되지 않습니다.");
            return false;
        }
        if (memory_type_check(board_item, memory_item) != true) {
            error_message.append("선택하신 메인보드와 메모리가 호환되지 않습니다.");
            return false;
        }

        return true; // 모두 호환가능할 때
    }
}
