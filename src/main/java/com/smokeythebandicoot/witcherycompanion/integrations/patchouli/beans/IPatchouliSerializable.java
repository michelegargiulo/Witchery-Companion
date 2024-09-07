package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans;

public interface IPatchouliSerializable {

    String serialize();

    void deserialize(String str);

}
