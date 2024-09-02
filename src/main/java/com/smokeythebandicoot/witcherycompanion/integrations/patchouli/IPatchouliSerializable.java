package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

public interface IPatchouliSerializable {

    String serialize();

    void deserialize(String str);

}
