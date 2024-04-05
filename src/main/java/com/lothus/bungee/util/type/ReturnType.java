package com.lothus.bungee.util.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReturnType {

    LEGIT(1),
    UNDEFINED(2),
    CRACK(0);

    int id;
}
